package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Address;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     *  v1~v3의 경우 컨트롤러까지 커넥트를 유지해야 작동한다.
     *  커넥트를 유지하려면 jpa: open-in-view: true 로 세팅 해야한다.
     *  컨트롤러까지 커넥션을 들고 있지만 리소스 낭비가 심해진다.
     *
     *  jpa: open-in-view: false 로 두게된다면 리소스 낭비가 덜해지지만 영속성이 컨트롤러 까지 유지되지 않기 때문에 엔티티에 대한 정보를 컨트롤러까지 가져오지 못한다.
     *  이를 방지 하기 위해서는 영속성 컨텍스트가 유지되는 서비스 or 리포지토리에서 엔티티 가공 작업(DTO로 바꾸는등)을 해서 넘어와야 한다.
     */
    @GetMapping("/api/v1/orders")
    public List<Order> orderList() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                System.out.println(orderItem.toString());
            }
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
            orderItems = order.getOrderItems().stream().map(o -> new OrderItemDto(o)).collect(toList());
        }
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        List<OrderDto> dtoList = orderRepository.findAll(new OrderSearch()).stream().map(o -> new OrderDto(o)).collect(toList());
        return dtoList;
    }
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<OrderDto> dtoList = orderRepository.findAllWithItem().stream().map(o -> new OrderDto(o)).collect(toList());
        return dtoList;
    }
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(@RequestParam(value = "offset",defaultValue = "0") int offset,
                                       @RequestParam(value = "limit",defaultValue = "100") int limit) {
        List<OrderDto> dtoList = orderRepository.findAllWithMemberDelivery(offset,limit).stream().map(o -> new OrderDto(o)).collect(toList());
        return dtoList;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepository.findAllByDtoOptimization();
    }
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> orderV6() {
        List<OrderFlatDto> flatDtos = orderQueryRepository.findAllByDtoFlat();
        return flatDtos.stream().collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()), mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList()))).entrySet().stream().map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(),e.getValue())).collect(toList());
    }
    @GetMapping("/api/test")
    public List<OrderItem> test() {
        List<OrderItem> orderItems = orderRepository.findOne(1L).getOrderItems();
        return orderItems;
    }

    @Data
    static class OrderItemDto {
        private String name;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.name = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getOrderPrice();
        }
    }
}
