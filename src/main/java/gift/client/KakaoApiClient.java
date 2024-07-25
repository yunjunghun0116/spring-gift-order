package gift.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.properties.KakaoProperties;
import gift.dto.kakao.KakaoAuthResponse;
import gift.dto.kakao.KakaoTokenResponse;
import gift.exception.InvalidKakaoTokenException;
import org.springframework.http.HttpStatus;
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

    public KakaoTokenResponse getTokenResponse(String code, String redirectUri) {
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

        return convertDtoWithJsonString(response, KakaoTokenResponse.class);
    }

    public KakaoTokenResponse getRefreshedTokenResponse(String refreshToken) {
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
                .onStatus(statusCode -> statusCode.equals(HttpStatus.UNAUTHORIZED), (req, res) -> {
                    throw new InvalidKakaoTokenException("유효하지 않은 토큰값입니다. 갱신이 필요합니다.");
                })
                .body(String.class);

        return convertDtoWithJsonString(response, KakaoTokenResponse.class);
    }

    public KakaoAuthResponse getKakaoAuthResponse(KakaoTokenResponse kakaoTokenResponse) {
        var url = "https://kapi.kakao.com/v2/user/me";
        var header = "Bearer " + kakaoTokenResponse.accessToken();

        var response = client.get()
                .uri(URI.create(url))
                .header("Authorization", header)
                .retrieve()
                .body(String.class);

        return convertDtoWithJsonString(response, KakaoAuthResponse.class);
    }

    public void canUseKakaoAccessToken(String accessToken) {
        var url = "https://kapi.kakao.com/v1/user/access_token_info";
        var header = "Bearer " + accessToken;

        client.get()
                .uri(URI.create(url))
                .header("Authorization", header)
                .retrieve().onStatus(statusCode -> statusCode.equals(HttpStatus.UNAUTHORIZED), (req, res) -> {
                    throw new InvalidKakaoTokenException(accessToken + "이 유효하지 않습니다. AccessToken 의 갱신이 필요합니다.");
                });
    }

    private <T> T convertDtoWithJsonString(String response, Class<T> returnTypeClass) {
        try {
            return objectMapper.readValue(response, returnTypeClass);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(returnTypeClass.getName() + "의 데이터를 DTO 로 변환하는 과정에서 예외가 발생했습니다.", exception);
        }
    }
}
