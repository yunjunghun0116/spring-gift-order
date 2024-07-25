package gift.service.auth;

import gift.client.KakaoApiClient;
import gift.config.properties.KakaoProperties;
import gift.dto.kakao.KakaoAuthInformation;
import gift.dto.kakao.KakaoTokenResponse;
import gift.exception.NotFoundElementException;
import gift.model.KakaoToken;
import gift.model.Member;
import gift.repository.KakaoTokenRepository;
import gift.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KakaoService {

    private final MemberRepository memberRepository;
    private final KakaoTokenRepository kakaoTokenRepository;
    private final KakaoApiClient kakaoApiClient;
    private final KakaoProperties kakaoProperties;

    public KakaoService(MemberRepository memberRepository, KakaoTokenRepository kakaoTokenRepository, KakaoApiClient kakaoApiClient, KakaoProperties kakaoProperties) {
        this.memberRepository = memberRepository;
        this.kakaoTokenRepository = kakaoTokenRepository;
        this.kakaoApiClient = kakaoApiClient;
        this.kakaoProperties = kakaoProperties;
    }

    public KakaoTokenResponse getKakaoTokenResponse(String code) {
        return kakaoApiClient.getTokenResponse(code, kakaoProperties.redirectUri());
    }

    public void setKakaoToken(Long memberId, String code) {
        var kakaoTokenResponse = kakaoApiClient.getTokenResponse(code, kakaoProperties.tokenUri());
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(memberId + "를 가진 이용자가 존재하지 않습니다."));
        var kakaoToken = new KakaoToken(member, kakaoTokenResponse.accessToken(), kakaoTokenResponse.refreshToken());
        kakaoTokenRepository.save(kakaoToken);
    }

    public void setKakaoToken(Member member, KakaoTokenResponse kakaoTokenResponse) {
        var kakaoToken = new KakaoToken(member, kakaoTokenResponse.accessToken(), kakaoTokenResponse.refreshToken());
        kakaoTokenRepository.save(kakaoToken);
    }

    public KakaoAuthInformation getKakaoAuthInformation(KakaoTokenResponse kakaoTokenResponse) {
        var response = kakaoApiClient.getKakaoAuthResponse(kakaoTokenResponse);
        var kakaoAccount = response.kakaoAccount();
        var name = kakaoAccount.profile().name();
        var email = kakaoAccount.email();
        return KakaoAuthInformation.of(name, email);
    }
}
