package jpabook.jpashop.repository.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface SpringDataMemberRepository extends JpaRepository<Member,Long> {
    List<Member> findByName(String name);
}
