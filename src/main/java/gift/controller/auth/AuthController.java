package gift.controller.auth;

import gift.dto.AuthResponse;
import gift.dto.LoginRequest;
import gift.dto.RegisterRequest;
import gift.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        var auth = authService.register(registerRequest);
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var auth = authService.login(loginRequest);
        return ResponseEntity.ok(auth);
    }

    @GetMapping("/oauth")
    public ResponseEntity<AuthResponse> kakaoAuth(@RequestParam String code) {
        var auth = authService.kakaoAuth(code);
        return ResponseEntity.ok(auth);
    }

    @GetMapping("/set-token")
    public ResponseEntity<AuthResponse> setKakaoTokenWithMember(@RequestAttribute("memberId") Long memberId, @RequestParam String code) {
        var auth = authService.setKakaoTokenWithMember(memberId, code);
        return ResponseEntity.ok(auth);
    }
}
