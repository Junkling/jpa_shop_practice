package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Address;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    public void cancel() {
        Member m = createMember();
        Book b = createBook();
        int orderCount = 5;
        Long orderId = orderService.order(m.getId(), b.getId(), orderCount);
        orderService.cancel(orderId);
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