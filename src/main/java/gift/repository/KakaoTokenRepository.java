package gift.repository;

import gift.model.KakaoToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KakaoTokenRepository extends JpaRepository<KakaoToken, Long> {

    boolean existsByMemberId(Long memberId);

    Optional<KakaoToken> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}
