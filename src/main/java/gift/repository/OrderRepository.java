package gift.repository;

import gift.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    void deleteAllByOptionId(Long optionId);

    void deleteAllByMemberId(Long memberId);

    List<Order> findAllByMemberId(Long memberId, Pageable pageable);
}
