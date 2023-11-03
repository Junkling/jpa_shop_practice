package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Address;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Test
    public void join() {
        Member member = new Member("memberA", new Address("cityA", "streetA", "zipcodeA"));
        Long join = memberService.join(member);
        assertThat(member).isEqualTo(memberRepository.find(join));
    }

    @Test
    public void valid() {
        Member memberA1 = new Member("memberA", new Address("cityA", "streetA", "zipcodeA"));
        Member memberA2 = new Member("memberA", new Address("cityA", "streetA", "zipcodeA"));
        memberService.join(memberA1);
        assertThrows(IllegalStateException.class, ()->memberService.join(memberA2), "이미 존재하는 회원명입니다.");
    }

}