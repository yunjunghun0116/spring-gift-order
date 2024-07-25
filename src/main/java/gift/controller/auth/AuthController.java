package gift.controller.auth;

import gift.dto.auth.AuthResponse;
import gift.dto.auth.LoginRequest;
import gift.dto.auth.RegisterRequest;
import gift.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/oauth/kakao")
    public ResponseEntity<AuthResponse> kakaoAuth(@RequestParam String code) {
        var auth = authService.kakaoAuth(code);
        return ResponseEntity.ok(auth);
    }
}
