package com.ssafy.ganhoho.domain.member;

import com.ssafy.ganhoho.domain.member.dto.HospitalWardRequestBody;
import com.ssafy.ganhoho.domain.member.dto.MemberInfoResponse;
import com.ssafy.ganhoho.domain.member.service.MemberService;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.ssafy.ganhoho.global.auth.SecurityUtil.getCurrentMemberId;

@RestController
@Slf4j
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/mypage")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(){
        Long memberId = getCurrentMemberId();
        return ResponseEntity.ok(memberService.getMemberInfo(memberId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MemberInfoResponse>> searchMember(@RequestParam String friendLoginId) {
        // 두개중 하나는 포함되는지 검증
        return ResponseEntity.ok(memberService.searchMembers(friendLoginId));
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity withdrawal() {
        Long memberId = getCurrentMemberId();
        memberService.withdrawal(memberId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/mypage/hospital-ward")
    public ResponseEntity<MemberInfoResponse> updateHospitalWard(@RequestBody HospitalWardRequestBody hospitalWardRequestBody) {
        Long memberId = getCurrentMemberId();
        MemberInfoResponse memberInfoResponse = memberService.updateHospitalWard(memberId, hospitalWardRequestBody);
        return ResponseEntity.ok(memberInfoResponse);
    }
}
