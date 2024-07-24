package gift.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.properties.KakaoProperties;
import gift.dto.KakaoAuthInformation;
import gift.dto.KakaoAuthResponse;
import gift.dto.KakaoAuthToken;
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

    public KakaoAuthToken getTokenWithCode(String code) {
        return getTokenResponse(code, kakaoProperties.redirectUrl());
    }

    public KakaoAuthToken getTokenToSetWithCode(String code) {
        return getTokenResponse(code, kakaoProperties.setUrl());
    }

    public KakaoAuthInformation getAuthInformationWithToken(String accessToken) {
        var response = getKakaoAuthResponse(accessToken);
        var name = response.kakaoAccount().profile().name();
        var email = response.kakaoAccount().email();
        return KakaoAuthInformation.of(name, email);
    }

    private KakaoAuthToken getTokenResponse(String code, String redirect_uri) {
        var url = "https://kauth.kakao.com/oauth/token";
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", kakaoProperties.grantType());
        body.add("client_id", kakaoProperties.restApiKey());
        body.add("redirect_uri", redirect_uri);
        body.add("code", code);

        var response = client.post()
                .uri(URI.create(url))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);

        return convertDtoWithJsonString(response, KakaoAuthToken.class);
    }

    private KakaoAuthResponse getKakaoAuthResponse(String accessToken) {
        var url = "https://kapi.kakao.com/v2/user/me";
        var header = "Bearer " + accessToken;

        var response = client.get()
                .uri(URI.create(url))
                .header("Authorization", header)
                .retrieve()
                .body(String.class);

        return convertDtoWithJsonString(response, KakaoAuthResponse.class);
    }

    public <T> T convertDtoWithJsonString(String response, Class<T> returnTypeClass) {
        try {
            return objectMapper.readValue(response, returnTypeClass);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(returnTypeClass.getName() + "의 데이터를 DTO 로 변환하는 과정에서 예외가 발생했습니다.", exception);
        }
    }
}
