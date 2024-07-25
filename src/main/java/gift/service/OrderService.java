package gift.service;

import gift.dto.option.OptionInformation;
import gift.dto.order.OrderRequest;
import gift.dto.order.OrderResponse;
import gift.exception.NotFoundElementException;
import gift.model.Option;
import gift.model.Order;
import gift.repository.MemberRepository;
import gift.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
    }

    public OrderResponse addOrder(Long memberId, Option option, OrderRequest orderRequest) {
        var order = saveOrderWithOrderRequest(memberId, option, orderRequest);
        return getOrderResponseFromOrder(order);
    }

    private Order saveOrderWithOrderRequest(Long memberId, Option option, OrderRequest orderRequest) {
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(memberId + "를 가진 이용자가 존재하지 않습니다."));
        var order = new Order(member, option, orderRequest.quantity(), orderRequest.message());
        return orderRepository.save(order);
    }

    private OrderResponse getOrderResponseFromOrder(Order order) {
        var optionInformation = OptionInformation.of(order.getId(), order.getOption().getProduct().getName(), order.getOption().getProduct().getPrice(), order.getOption().getName());
        return OrderResponse.of(order.getId(), optionInformation, order.getQuantity(), order.getCreatedDate(), order.getMessage());
    }
}
