package gift.controller.api;

import gift.dto.kakao.KakaoAuthToken;
import gift.service.api.KakaoApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kakao")
public class KakaoApiController {

    private final KakaoApiService kakaoApiService;

    public KakaoApiController(KakaoApiService kakaoApiService) {
        this.kakaoApiService = kakaoApiService;
    }

    @GetMapping("/token")
    public ResponseEntity<KakaoAuthToken> getToken(@RequestParam String code) {
        var token = kakaoApiService.getKakaoAuthTokenToAccess(code);
        return ResponseEntity.ok(token);
    }
}
