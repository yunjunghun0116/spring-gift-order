package gift.repository;

import gift.model.OAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KakaoTokenRepository extends JpaRepository<OAuthToken, Long> {

    boolean existsByMemberId(Long memberId);

    Optional<OAuthToken> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}
