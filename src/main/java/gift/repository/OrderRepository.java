package gift.repository;

import gift.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    void deleteAllByOptionId(Long optionId);

    void deleteAllByMemberId(Long memberId);
}
