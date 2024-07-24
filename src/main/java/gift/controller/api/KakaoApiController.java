package gift.controller.api;

import gift.config.properties.KakaoProperties;
import gift.dto.kakao.KakaoAuthToken;
import gift.service.api.KakaoApiService;
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

    private final KakaoApiService kakaoApiService;
    private final KakaoProperties kakaoProperties;

    public KakaoApiController(KakaoApiService kakaoApiService, KakaoProperties kakaoProperties) {
        this.kakaoApiService = kakaoApiService;
        this.kakaoProperties = kakaoProperties;
    }

    @GetMapping("/token")
    public ResponseEntity<KakaoAuthToken> getToken(@RequestParam String code) {
        var token = kakaoApiService.getKakaoAuthTokenToAccess(code);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/get-oauth")
    public ResponseEntity<?> redirectOAuth() {
        var headers = new HttpHeaders();
        String redirectLocation = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + kakaoProperties.restApiKey() + "&redirect_uri=" + kakaoProperties.redirectUri();
        headers.setLocation(URI.create(redirectLocation));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("/get-token")
    public ResponseEntity<?> redirectGetToken() {
        var headers = new HttpHeaders();
        String redirectLocation = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + kakaoProperties.restApiKey() + "&redirect_uri=" + kakaoProperties.tokenUri();
        headers.setLocation(URI.create(redirectLocation));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
