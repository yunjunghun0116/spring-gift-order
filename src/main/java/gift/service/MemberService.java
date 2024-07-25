package gift.service;

import gift.exception.NotFoundElementException;
import gift.repository.MemberRepository;
import gift.service.auth.KakaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final WishProductService wishProductService;
    private final KakaoService kakaoService;

    public MemberService(MemberRepository memberRepository, WishProductService wishProductService, KakaoService kakaoService) {
        this.memberRepository = memberRepository;
        this.wishProductService = wishProductService;
        this.kakaoService = kakaoService;
    }

    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundElementException("존재하지 않는 이용자의 ID 입니다.");
        }
        kakaoService.deleteByMemberId(memberId);
        wishProductService.deleteAllByMemberId(memberId);
        memberRepository.deleteById(memberId);
    }
}
