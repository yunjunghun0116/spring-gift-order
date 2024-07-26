package gift.service.auth;

import gift.config.properties.JwtProperties;
import gift.dto.auth.LoginRequest;
import gift.dto.auth.RegisterRequest;
import gift.dto.kakao.KakaoAuthInformation;
import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.order.OrderResponse;
import gift.exception.DuplicatedEmailException;
import gift.exception.InvalidLoginInfoException;
import gift.model.KakaoToken;
import gift.model.Member;
import gift.model.MemberRole;
import gift.reflection.AuthTestReflectionComponent;
import gift.repository.MemberRepository;
import gift.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
class AuthServiceTest {

    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private MemberService memberService;
    @Autowired
    private AuthTestReflectionComponent authTestReflectionComponent;
    private KakaoService kakaoService = Mockito.mock(KakaoService.class);

    @BeforeEach
    void mockKakaoServiceSetUp() {
        authService = new AuthService(memberRepository, kakaoService, jwtProperties);
        Mockito.doNothing().when(kakaoService).saveKakaoToken(any(Long.class), any(String.class));
        Mockito.doNothing().when(kakaoService).sendSelfMessageOrder(any(Long.class), any(OrderResponse.class));
        Mockito.doNothing().when(kakaoService).deleteByMemberId(any(Long.class));

        var mockMember = new Member("MOCK", "MOCK@naver.com", MemberRole.MEMBER);
        var mockKakaoTokenResponse = new KakaoTokenResponse("ACCESSTOKEN", "REFRESHTOKEN");
        var mockKakaoToken = new KakaoToken(mockMember, "ACCESSTOKEN", "REFRESHTOKEN");
        var mockKakaoAuthInformation = new KakaoAuthInformation("MOCK", "MOCK@naver.com");

        Mockito.when(kakaoService.saveKakaoToken(any(Member.class), any(KakaoTokenResponse.class)))
                .thenReturn(mockKakaoToken);
        Mockito.when(kakaoService.getKakaoTokenResponse(any(String.class)))
                .thenReturn(mockKakaoTokenResponse);
        Mockito.when(kakaoService.getKakaoAuthInformation(any(KakaoTokenResponse.class)))
                .thenReturn(mockKakaoAuthInformation);
    }

    @Test
    @DisplayName("회원가입 시도하기 - 성공")
    void successRegister() {
        //given
        var registerRequest = new RegisterRequest("테스트", "test@naver.com", "testPassword", "MEMBER");
        //when
        var auth = authService.register(registerRequest);
        var role = authTestReflectionComponent.getMemberRoleWithToken(auth.token());
        //then
        Assertions.assertThat(role).isEqualTo(MemberRole.MEMBER);

        var id = authTestReflectionComponent.getMemberIdWithToken(auth.token());
        memberService.deleteMember(id);
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시도하기 - 실패")
    void failRegisterWithDuplicatedEmail() {
        //given
        var registerRequest = new RegisterRequest("테스트", "test@naver.com", "testPassword", "MEMBER");
        var auth = authService.register(registerRequest);
        var id = authTestReflectionComponent.getMemberIdWithToken(auth.token());
        //when, then
        Assertions.assertThatThrownBy(() -> authService.register(registerRequest)).isInstanceOf(DuplicatedEmailException.class);

        memberService.deleteMember(id);
    }

    @Test
    @DisplayName("로그인 실행하기 - 성공")
    void successLogin() {
        //given
        var registerRequest = new RegisterRequest("테스트", "test@naver.com", "testPassword", "MEMBER");
        authService.register(registerRequest);
        var loginRequest = new LoginRequest("test@naver.com", "testPassword");
        //when
        var auth = authService.login(loginRequest);
        var role = authTestReflectionComponent.getMemberRoleWithToken(auth.token());
        //then
        Assertions.assertThat(role).isEqualTo(MemberRole.MEMBER);

        var id = authTestReflectionComponent.getMemberIdWithToken(auth.token());
        memberService.deleteMember(id);
    }

    @Test
    @DisplayName("로그인 실행하기 - 실패")
    void failLoginWithWrongPassword() {
        //given
        var registerRequest = new RegisterRequest("테스트", "test@naver.com", "testPasswords", "MEMBER");
        var auth = authService.register(registerRequest);
        var loginRequest = new LoginRequest("test@naver.com", "testPassword");
        //when, then
        Assertions.assertThatThrownBy(() -> authService.login(loginRequest)).isInstanceOf(InvalidLoginInfoException.class);

        var id = authTestReflectionComponent.getMemberIdWithToken(auth.token());
        memberService.deleteMember(id);
    }

    @Test
    @DisplayName("카카오 회원가입하기")
    void successKakaoLogin() {
        //given
        var code = "인가코드";
        //when
        var auth = authService.kakaoAuth(code);
        //then
        var role = authTestReflectionComponent.getMemberRoleWithToken(auth.token());
        var id = authTestReflectionComponent.getMemberIdWithToken(auth.token());

        Assertions.assertThat(role).isEqualTo(MemberRole.MEMBER);
        Assertions.assertThat(id).isNotNull();

        memberService.deleteMember(id);
    }
}
