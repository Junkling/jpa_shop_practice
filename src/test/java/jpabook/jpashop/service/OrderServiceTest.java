package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Address;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void order() {
        Member member = createMember();

        Book book = createBook();

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order order = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, order.getStatus());
        assertEquals(1, order.getOrderItems().size());
        assertEquals(10000 * orderCount, order.getTotalPrice());
        assertEquals(8, book.getStockQuantity());

    }

    @Test
    public void list() {
        Member member = createMember("userB", "진주", "2", "2222");
        em.persist(member);

        Book book1 = createBook("SPRING1 BOOK", 20000, 200);
        em.persist(book1);

        Book book2 = createBook("SPRING2 BOOK", 40000, 300);
        em.persist(book2);

        OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
        OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

        Delivery delivery = createDelivery(member);
        Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
        em.persist(order);

        Order one = orderRepository.findOne(order.getId());
        List<OrderItem> orderItems = one.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            System.out.println(orderItem.toString());
        }
        assertEquals(order.getOrderItems().size(), 2);

    }

    private Member createMember(String name, String city, String street, String zipcode) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book1 = new Book();
        book1.setName(name);
        book1.setPrice(price);
        book1.setStockQuantity(stockQuantity);
        return book1;
    }

    private Delivery createDelivery(Member member) {
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        return delivery;
    }

    @Test
    public void cancel() {
        Member m = createMember();
        Book b = createBook();
        int orderCount = 5;
        Long orderId = orderService.order(m.getId(), b.getId(), orderCount);
        orderService.cancelOrder(orderId);
        assertEquals(orderRepository.findOne(orderId).getStatus(), OrderStatus.CANCEL);
        assertEquals(b.getStockQuantity(), 10);
    }

    @Test
    public void stock() {
        Member m = createMember();
        Book b = createBook();

        int orderCount = 11;

        assertThrows(NotEnoughStockException.class, () -> orderService.order(m.getId(), b.getId(), orderCount));
    }


    private Book createBook() {
        Book book = new Book("jpa", 10000, 10, "김준혁", "1234");
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member("회원1", new Address("서울", "강남", "123-123"));
        em.persist(member);
        return member;
    }
}