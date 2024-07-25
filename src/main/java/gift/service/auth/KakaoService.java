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

    public void saveKakaoToken(Long memberId, String code) {
        var kakaoTokenResponse = kakaoApiClient.getTokenResponse(code, kakaoProperties.tokenUri());
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(memberId + "를 가진 이용자가 존재하지 않습니다."));
        saveKakaoToken(member, kakaoTokenResponse);
    }

    public void saveKakaoToken(Member member, KakaoTokenResponse kakaoTokenResponse) {
        if (kakaoTokenRepository.existsByMemberId(member.getId())) {
            var kakaoToken = kakaoTokenRepository.findByMemberId(member.getId())
                    .orElseThrow(() -> new NotFoundElementException(member.getId() + "를 가진 이용자의 카카오 토큰 정보가 존재하지 않습니다."));
            kakaoToken.updateToken(kakaoTokenResponse);
            kakaoTokenRepository.save(kakaoToken);
            return;
        }
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

    public void sendMessageToSelf() {
        // get AccessToken -> findByMemberId
        // 만약 없으면 redirect -> set-token으로
        // 있으면 token유효한지 검사 -> 토큰 유효해?
        // 유효하면 그대로 사용, 유효하지 않으면 refresh요청
        // 만약 refresh기한 넘어갔으면 set-token으로 redirect
        // 다 됐으면 이제 message send
    }

    public void deleteByMemberId(Long memberId) {
        if (!kakaoTokenRepository.existsByMemberId(memberId)) return;
        kakaoTokenRepository.deleteByMemberId(memberId);
    }

    private void tokenValidation(KakaoToken kakaoToken) {

    }
}
