package gift.service.api;

import gift.config.properties.KakaoProperties;
import gift.dto.KakaoAuthInformation;
import gift.dto.KakaoAuthToken;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Service
public class KakaoApiService {

    private final RestClient client = RestClient.builder().build();
    private final KakaoProperties kakaoProperties;

    public KakaoApiService(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    public KakaoAuthToken getTokenWithCode(String code) {
        var response = getTokenResponse(code, kakaoProperties.redirectUrl());
        return getTokenWithResponse(response);
    }

    public KakaoAuthToken getTokenToSetWithCode(String code) {
        var response = getTokenResponse(code, kakaoProperties.setUrl());
        return getTokenWithResponse(response);
    }

    public KakaoAuthInformation getAuthInformationWithToken(String accessToken) {
        var response = getKakaoAuthResponse(accessToken);
        var kakaoAccount = (Map<String, Object>) response.get("kakao_account");
        var profile = (Map<String, Object>) kakaoAccount.get("profile");
        var name = (String) profile.get("nickname");
        var email = (String) kakaoAccount.get("email");
        return KakaoAuthInformation.of(name, email);
    }

    private Map getTokenResponse(String code, String redirect_uri) {
        var url = "https://kauth.kakao.com/oauth/token";
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", kakaoProperties.grantType());
        body.add("client_id", kakaoProperties.restApiKey());
        body.add("redirect_uri", redirect_uri);
        body.add("code", code);

        return client.post()
                .uri(URI.create(url))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toEntity(Map.class)
                .getBody();
    }

    private Map getKakaoAuthResponse(String accessToken) {
        var url = "https://kapi.kakao.com/v2/user/me";
        var header = "Bearer " + accessToken;

        return client.get()
                .uri(URI.create(url))
                .header("Authorization", header)
                .retrieve()
                .toEntity(Map.class)
                .getBody();
    }

    private KakaoAuthToken getTokenWithResponse(Map response) {
        var accessToken = (String) response.get("access_token");
        var refreshToken = (String) response.get("refresh_token");
        return KakaoAuthToken.of(accessToken, refreshToken);
    }
}
