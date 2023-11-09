package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long join = memberService.join(member);
        return new CreateMemberResponse(join);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member(request.getName());
        Long join = memberService.join(member);
        return new CreateMemberResponse(join);
    }

    @PutMapping("/api/v2/members/{memberId}")
    public UpdateMemberResponse updateMemberV2(@PathVariable Long memberId, @RequestBody @Valid UpdateMemberRequest request) {
        memberService.changeName(memberId, request.getName());
        Member findMember = memberService.findOne(memberId);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        List<Member> members = memberService.findMembers();
        return members;
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<MemberDto> list = memberService.findMembers().stream().map(m -> new MemberDto(m.getId(), m.getName())).collect(Collectors.toList());
        return new Result(list.size(),list);
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private Long id;
        private String name;
    }
}
