package gift.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.properties.KakaoProperties;
import gift.dto.kakao.KakaoAuthInformation;
import gift.dto.kakao.KakaoAuthResponse;
import gift.dto.kakao.KakaoAuthToken;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Service
public class KakaoApiService {

    private final RestClient client = RestClient.builder().build();
    private final KakaoProperties kakaoProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoApiService(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    public KakaoAuthToken getKakaoAuthTokenToAccess(String code) {
        var response = getTokenResponse(code, kakaoProperties.tokenUri());
        return convertDtoWithJsonString(response, KakaoAuthToken.class);
    }

    public KakaoAuthInformation getAuthInformationWithToken(String code) {
        var accessToken = getKakaoAuthTokenToAuth(code).accessToken();
        var response = getKakaoAuthResponse(accessToken);
        var kakaoAccount = convertDtoWithJsonString(response, KakaoAuthResponse.class).kakaoAccount();
        var name = kakaoAccount.profile().name();
        var email = kakaoAccount.email();
        return KakaoAuthInformation.of(name, email);
    }

    private KakaoAuthToken getKakaoAuthTokenToAuth(String code) {
        var response = getTokenResponse(code, kakaoProperties.redirectUri());
        return convertDtoWithJsonString(response, KakaoAuthToken.class);
    }

    private String getTokenResponse(String code, String redirectUri) {
        var url = "https://kauth.kakao.com/oauth/token";
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", kakaoProperties.grantType());
        body.add("client_id", kakaoProperties.restApiKey());
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        return client.post()
                .uri(URI.create(url))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    private String getKakaoAuthResponse(String accessToken) {
        var url = "https://kapi.kakao.com/v2/user/me";
        var header = "Bearer " + accessToken;

        return client.get()
                .uri(URI.create(url))
                .header("Authorization", header)
                .retrieve()
                .body(String.class);
    }

    private <T> T convertDtoWithJsonString(String response, Class<T> returnTypeClass) {
        try {
            return objectMapper.readValue(response, returnTypeClass);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(returnTypeClass.getName() + "의 데이터를 DTO 로 변환하는 과정에서 예외가 발생했습니다.", exception);
        }
    }
}
