package gift.service.auth;

import gift.client.KakaoApiClient;
import gift.config.properties.KakaoProperties;
import gift.dto.kakao.KakaoAuthInformation;
import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.order.GiftOrderResponse;
import gift.exception.InvalidKakaoTokenException;
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

    public void saveKakaoToken(Long memberId, String code) {
        var kakaoTokenResponse = kakaoApiClient.getTokenResponse(code, kakaoProperties.tokenUri());
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(memberId + "를 가진 이용자가 존재하지 않습니다."));
        saveKakaoToken(member, kakaoTokenResponse);
    }

    public KakaoToken saveKakaoToken(Member member, KakaoTokenResponse kakaoTokenResponse) {
        if (kakaoTokenRepository.existsByMemberId(member.getId())) {
            var kakaoToken = kakaoTokenRepository.findByMemberId(member.getId())
                    .orElseThrow(() -> new InvalidKakaoTokenException(member.getId() + "를 가진 이용자의 카카오 토큰 정보가 존재하지 않습니다."));
            kakaoToken.updateToken(kakaoTokenResponse.accessToken(), kakaoTokenResponse.refreshToken());
            return kakaoTokenRepository.save(kakaoToken);
        }
        var kakaoToken = new KakaoToken(member, kakaoTokenResponse.accessToken(), kakaoTokenResponse.refreshToken());
        return kakaoTokenRepository.save(kakaoToken);
    }

    public KakaoAuthInformation getKakaoAuthInformation(KakaoTokenResponse kakaoTokenResponse) {
        var response = kakaoApiClient.getKakaoAuthResponse(kakaoTokenResponse);
        var kakaoAccount = response.kakaoAccount();
        var name = kakaoAccount.profile().name();
        var email = kakaoAccount.email();
        return KakaoAuthInformation.of(name, email);
    }

    public void sendSelfMessageOrder(Long memberId, GiftOrderResponse giftOrderResponse) {
        var kakaoToken = kakaoTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new InvalidKakaoTokenException(memberId + "를 가진 이용자의 카카오 토큰 정보가 존재하지 않습니다."));
        var validatedKakaoToken = tokenValidation(kakaoToken);
        kakaoApiClient.sendSelfMessageOrder(validatedKakaoToken.getAccessToken(), giftOrderResponse);
    }

    public void deleteByMemberId(Long memberId) {
        if (!kakaoTokenRepository.existsByMemberId(memberId)) return;
        kakaoTokenRepository.deleteByMemberId(memberId);
    }

    private KakaoToken tokenValidation(KakaoToken kakaoToken) {
        try {
            kakaoApiClient.canUseKakaoAccessToken(kakaoToken.getAccessToken());
        } catch (InvalidKakaoTokenException exception) {
            var kakaoTokenResponse = kakaoApiClient.getRefreshedTokenResponse(kakaoToken.getRefreshToken());
            kakaoToken = saveKakaoToken(kakaoToken.getMember(), kakaoTokenResponse);
        }
        return kakaoToken;
    }
}
