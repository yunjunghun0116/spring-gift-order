package gift.service.auth;

import gift.config.properties.JwtProperties;
import gift.dto.AuthResponse;
import gift.dto.KakaoAuthInformation;
import gift.dto.KakaoAuthToken;
import gift.dto.LoginRequest;
import gift.dto.RegisterRequest;
import gift.exception.DuplicatedEmailException;
import gift.exception.InvalidLoginInfoException;
import gift.exception.NotFoundElementException;
import gift.model.Member;
import gift.model.MemberRole;
import gift.repository.MemberRepository;
import gift.service.api.KakaoApiService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final KakaoApiService kakaoApiService;
    private final JwtProperties jwtProperties;

    public AuthService(MemberRepository memberRepository, KakaoApiService kakaoApiService, JwtProperties jwtProperties) {
        this.memberRepository = memberRepository;
        this.kakaoApiService = kakaoApiService;
        this.jwtProperties = jwtProperties;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        emailValidation(registerRequest.email());
        var member = saveMemberWithMemberRequest(registerRequest);
        return createAuthResponseWithMember(member);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        var member = memberRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new InvalidLoginInfoException(loginRequest.email() + "를 가진 이용자가 존재하지 않습니다."));
        member.passwordCheck(loginRequest.password());
        return createAuthResponseWithMember(member);
    }

    public AuthResponse kakaoAuth(String code) {
        var kakaoAuthToken = kakaoApiService.getTokenWithCode(code);
        var kakaoAuthInformation = kakaoApiService.getAuthInformationWithToken(kakaoAuthToken.accessToken());
        var member = getMemberWithKakaoAuth(kakaoAuthInformation);
        return createAuthResponseWithOAuth(member, kakaoAuthToken);
    }

    public AuthResponse setKakaoTokenWithMember(Long memberId, String code) {
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(memberId + "를 가진 이용자가 존재하지 않습니다."));
        var kakaoAuthToken = kakaoApiService.getTokenToSetWithCode(code);
        return createAuthResponseWithOAuth(member, kakaoAuthToken);
    }

    private AuthResponse createAuthResponseWithMember(Member member) {
        var token = Jwts.builder()
                .subject(member.getId().toString())
                .claim("name", member.getName())
                .claim("role", member.getRole())
                .claim("kakaoAccessToken", "")
                .claim("kakaoRefreshToken", "")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiredTime()))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes()))
                .compact();
        return AuthResponse.of(token);
    }

    private AuthResponse createAuthResponseWithOAuth(Member member, KakaoAuthToken kakaoAuthToken) {
        var token = Jwts.builder()
                .subject(member.getId().toString())
                .claim("name", member.getName())
                .claim("role", member.getRole())
                .claim("kakaoAccessToken", kakaoAuthToken.accessToken())
                .claim("kakaoRefreshToken", kakaoAuthToken.refreshToken())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiredTime()))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes()))
                .compact();
        return AuthResponse.of(token);
    }

    private void emailValidation(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException("이미 존재하는 이메일입니다.");
        }
    }

    private Member saveMemberWithMemberRequest(RegisterRequest registerRequest) {
        var member = new Member(registerRequest.name(), registerRequest.email(), registerRequest.password(), MemberRole.valueOf(registerRequest.role()));
        return memberRepository.save(member);
    }

    private Member saveMemberWithKakaoAuth(KakaoAuthInformation kakaoAuthInformation) {
        var member = new Member(kakaoAuthInformation.name(), kakaoAuthInformation.email(), MemberRole.MEMBER);
        return memberRepository.save(member);
    }

    private Member getMemberWithKakaoAuth(KakaoAuthInformation kakaoAuthInformation) {
        if (memberRepository.existsByEmail(kakaoAuthInformation.email())) {
            return memberRepository.findByEmail(kakaoAuthInformation.email())
                    .orElseThrow(() -> new NotFoundElementException(kakaoAuthInformation.email() + "을 가진 이용자가 존재하지 않습니다."));
        }
        return saveMemberWithKakaoAuth(kakaoAuthInformation);
    }
}
