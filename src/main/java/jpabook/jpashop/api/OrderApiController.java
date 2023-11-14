package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Address;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> orderList() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
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

            orderItems = order.getOrderItems().stream().map(o -> new OrderItemDto(o)).collect(Collectors.toList());
        }
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        List<OrderDto> dtoList = orderRepository.findAll(new OrderSearch()).stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return dtoList;
    }
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<OrderDto> dtoList = orderRepository.findAllWithItem().stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return dtoList;
    }

    @GetMapping("/api/test")
    public List<OrderItem> test() {
        List<OrderItem> orderItems = orderRepository.findOne(1L).getOrderItems();

        return orderItems;
    }

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
