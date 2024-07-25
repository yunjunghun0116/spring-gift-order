package gift.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.properties.KakaoProperties;
import gift.dto.kakao.KakaoAuthInformation;
import gift.dto.kakao.KakaoAuthResponse;
import gift.dto.kakao.KakaoAuthToken;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
public class KakaoApiClient {

    private final RestClient client = RestClient.builder().build();
    private final KakaoProperties kakaoProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoApiClient(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    public KakaoAuthToken getKakaoAuthTokenToAccess(String code) {
        return getTokenResponse(code, kakaoProperties.tokenUri());
    }

    public KakaoAuthToken getKakaoAuthTokenToRefresh(String refreshToken) {
        return getRefreshedTokenResponse(refreshToken);
    }

    public KakaoAuthInformation getAuthInformationWithToken(String code) {
        var accessToken = getKakaoAuthTokenToAuth(code).accessToken();
        var response = getKakaoAuthResponse(accessToken);
        var kakaoAccount = response.kakaoAccount();
        var name = kakaoAccount.profile().name();
        var email = kakaoAccount.email();
        return KakaoAuthInformation.of(name, email);
    }

    private KakaoAuthToken getKakaoAuthTokenToAuth(String code) {
        return getTokenResponse(code, kakaoProperties.redirectUri());
    }

    private KakaoAuthToken getTokenResponse(String code, String redirectUri) {
        var url = "https://kauth.kakao.com/oauth/token";
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.restApiKey());
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        var response = client.post()
                .uri(URI.create(url))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);

        return convertDtoWithJsonString(response, KakaoAuthToken.class);
    }

    private KakaoAuthToken getRefreshedTokenResponse(String refreshToken) {
        var url = "https://kauth.kakao.com/oauth/token";
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", kakaoProperties.restApiKey());
        body.add("refresh_token", refreshToken);

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

    private <T> T convertDtoWithJsonString(String response, Class<T> returnTypeClass) {
        try {
            return objectMapper.readValue(response, returnTypeClass);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(returnTypeClass.getName() + "의 데이터를 DTO 로 변환하는 과정에서 예외가 발생했습니다.", exception);
        }
    }
}
