package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.order.SpringDataMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final SpringDataMemberRepository springDataMemberRepository;

    @Transactional
    public Long join(Member member) {
        validateDuplicate(member);
//        return memberRepository.save(member);
        springDataMemberRepository.save(member);
        return member.getId();
    }

    //Member.username 에 유니크 제약 조건 추가 권장
    private void validateDuplicate(Member member) {
//        List<Member> byName = memberRepository.findByName(member.getName());
        List<Member> byName = springDataMemberRepository.findByName(member.getName());
        if (!byName.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원명입니다.");
        }
    }

    //    @Transactional(readOnly = true)
    public List<Member> findMembers() {
//        return memberRepository.findAll();
        return springDataMemberRepository.findAll();
    }

    //    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
//        return memberRepository.find(memberId);
        return springDataMemberRepository.findById(memberId).orElseThrow();
    }

    @Transactional
    public void changeName(Long memberId, String name) {
//        Member member = memberRepository.find(memberId);
        Member member = springDataMemberRepository.findById(memberId).orElseThrow();
        member.setName(name);
    }
}
