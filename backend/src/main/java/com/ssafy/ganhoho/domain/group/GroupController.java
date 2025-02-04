package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.*;
import com.ssafy.ganhoho.global.auth.SecurityUtil;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody GroupCreatRequest request) {
        try {
            //jwt 토큰에서 사용자 추출
            Long memberId = SecurityUtil.getCurrentMemberId();

            //그룹 생성 요청
            GroupCreateResponse response = groupService.createGroup(memberId, request);
            return ResponseEntity.ok()
                    .body(response);
        } catch (CustomException e) {
            if (e.getErrorCode().equals(ErrorCode.NOT_EXIST_MEMBER)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("ACCESS_DENIED");
            }
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            // JWT 관련
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED");
        }
    }

    @GetMapping
    public ResponseEntity<?> getGroupList() {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            List<GroupListResponse> response = groupService.getGroupList(memberId);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED");
        }
    }

    @GetMapping("/link/{groupId}")
    public ResponseEntity<?> getGroupInvite(@PathVariable Long groupId) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            GroupInviteLinkResponse response = groupService.getGroupInviteLink(memberId, groupId);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED");
        }

    }

    @GetMapping("/members/{groupId}")
    public ResponseEntity<?> getGroupMembers(@PathVariable Long groupId) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            List<GroupMemberResponse> responses = groupService.getGroupMembers(memberId, groupId);
            return ResponseEntity.ok(responses);
        } catch (CustomException e) {
            if (e.getErrorCode().equals(ErrorCode.ACCES_DENIED)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("ACCESS_DENIED");
            }
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED");
        }
    }

}
