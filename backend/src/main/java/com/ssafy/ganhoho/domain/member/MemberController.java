package com.ssafy.ganhoho.domain.member;

import com.ssafy.ganhoho.domain.member.dto.MemberInfoResponse;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.ssafy.ganhoho.global.auth.SecurityUtil.getCurrentMemberId;

@RestController
@Slf4j
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    //하이
    @GetMapping("/mypage")
    public ResponseEntity getMemberInfo(){
        Long memberId = getCurrentMemberId();
        return ResponseEntity.ok(memberService.getMemberInfo(memberId));
    }

}
