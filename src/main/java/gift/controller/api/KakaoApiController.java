package gift.controller.api;

import gift.client.KakaoApiClient;
import gift.config.properties.KakaoProperties;
import gift.dto.kakao.KakaoAuthToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/kakao")
public class KakaoApiController {

    private final KakaoApiClient kakaoApiClient;
    private final KakaoProperties kakaoProperties;

    public KakaoApiController(KakaoApiClient kakaoApiClient, KakaoProperties kakaoProperties) {
        this.kakaoApiClient = kakaoApiClient;
        this.kakaoProperties = kakaoProperties;
    }

    @GetMapping("/get-token")
    public ResponseEntity<Void> redirectGetToken() {
        var headers = getRedirectHeader(kakaoProperties.tokenUri());
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("/token")
    public ResponseEntity<KakaoAuthToken> getToken(@RequestParam String code) {
        var token = kakaoApiClient.getKakaoAuthTokenToAccess(code);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/get-oauth")
    public ResponseEntity<Void> redirectOAuth() {
        var headers = getRedirectHeader(kakaoProperties.redirectUri());
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<KakaoAuthToken> refreshToken(@RequestParam String refreshToken) {
        var token = kakaoApiClient.getKakaoAuthTokenToRefresh(refreshToken);
        return ResponseEntity.ok(token);
    }

    private HttpHeaders getRedirectHeader(String redirectUri) {
        var headers = new HttpHeaders();
        String redirectLocation = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + kakaoProperties.restApiKey() + "&redirect_uri=" + redirectUri;
        headers.setLocation(URI.create(redirectLocation));
        return headers;
    }
}
