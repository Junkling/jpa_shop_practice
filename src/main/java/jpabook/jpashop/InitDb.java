package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Address;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() {
            Member member = createMember("userA", new Address("서울", "1", "111"));
            em.persist(member);
            Book jpa1_book = createBook("Jpa1 book", 10000,100, "123", "123");
            Book jpa2_book = createBook("Jpa2 book", 20000,100, "123", "123");
            em.persist(jpa1_book);
            em.persist(jpa2_book);

            OrderItem orderItem1 = OrderItem.createOrderItem(jpa1_book, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(jpa2_book, 20000, 2);

            Order order = Order.createOrder(member, new Delivery(member.getAddress()), orderItem1, orderItem2);
            em.persist(order);

        }
        public void dbInit2() {
            Member member = createMember("userB", new Address("부산", "122", "12211"));
            em.persist(member);
            Book spring1_book = createBook("Spring1 book", 20000,200, "123", "123");
            Book spring2_book = createBook("Spring2 book", 40000,300, "123", "123");
            em.persist(spring1_book);
            em.persist(spring2_book);

            OrderItem orderItem1 = OrderItem.createOrderItem(spring1_book, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(spring2_book, 40000, 4);

            Order order = Order.createOrder(member, new Delivery(member.getAddress()), orderItem1, orderItem2);
            em.persist(order);

        }

        private static Book createBook(String Jpa1_book, int price, int stock, String author, String isbn) {
            Book book = new Book(Jpa1_book, price, stock,author,isbn);
            return book;
        }

        private static Member createMember(String name, Address address) {
            Member member = new Member(name, address);
            return member;
        }

    }
}
