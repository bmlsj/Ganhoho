package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.*;
import com.ssafy.ganhoho.domain.group.entity.Group;
import com.ssafy.ganhoho.global.auth.SecurityUtil;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import com.ssafy.ganhoho.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Group", description = "그룹 API")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupRepository groupRepository;

    @Operation(summary = "그룹 생성", description = "새로운 그룹을 생성하고 생성자를 멤버로 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹 생성 성공",
            content = @Content(schema = @Schema(implementation = GroupCreateResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 유효하지 않을 시)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다. (회원 ID가 존재하지 않을 시)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다. 다시 시도해주세요. (서버 오류 발생시)")
    })
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
            // 멤버없음 에러 발생시 접근 거부됨으로 변환하여 처리
            if (e.getErrorCode().equals(ErrorCode.NOT_EXIST_MEMBER)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(ErrorCode.ACCES_DENIED));
            }
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            // JWT 관련
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }
    @Operation(summary = "그룹 목록 조회", description = "인증된 사용자의 전체 그룹 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹 목록 조회 성공",
                content = @Content(schema = @Schema(implementation = GroupListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 유효하지 않을 시)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다. (회원 ID가 존재하지 않을 시)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다. 다시 시도해주세요. (서버 오류 발생시)")
    })
    @GetMapping
    public ResponseEntity<?> getGroupList() {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            List<GroupListResponse> response = groupService.getGroupList(memberId);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    @Operation(summary = "그룹 초대 링크 조회", description = "특정 그룹의 초대 링크를 조회 ( 해당 그룹 멤버만 조회 가능)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "초대 링크 조회 성공",
            content = @Content(schema = @Schema(implementation = GroupInviteLinkResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 유효하지 않을 시)"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 그룹이거나 접근 권한이 없습니다. (그룹이 존재하지 않거나 해당 그룹의 멤버가 아닐 시)")

    })
    @GetMapping("/link/{groupId}")
    public ResponseEntity<?> getGroupInvite(@PathVariable Long groupId) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            GroupInviteLinkResponse response = groupService.getGroupInviteLink(memberId, groupId);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }

    }
    @Operation(summary = "그룹원 정보 전체 조회", description = "특정 그룹의 모든 멤버 정보를 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "그룹원 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = GroupMemberResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 유효하지 않을 시)"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원이거나 그룹입니다. (회원이나 그룹을 찾을 수 없을 시)")
    })
    @GetMapping("/members/{groupId}")
    public ResponseEntity<?> getGroupMembers(@PathVariable Long groupId) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            List<GroupMemberResponse> responses = groupService.getGroupMembers(memberId, groupId);
            return ResponseEntity.ok(responses);
        } catch (CustomException e) {
            if (e.getErrorCode().equals(ErrorCode.ACCES_DENIED)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(ErrorCode.ACCES_DENIED));
            }
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }
    //잘되어라!
    @Operation(summary = "그룹 탈퇴", description = "특정 그룹에서 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹 탈퇴 성공",
                content = @Content(schema = @Schema(implementation = GroupLeaveResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 되지 않은 요청입니다. (JWT 토큰 누락 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 그룹이거나 접근 권한이 없음. (그룹이 없거나 해당 그룹의 멤버가 아닐 시)")
    })
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> leaveGroup(@PathVariable Long groupId) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            GroupLeaveResponse response = groupService.getGroupLeave(memberId, groupId);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            if (e.getErrorCode().equals(ErrorCode.ACCES_DENIED)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(ErrorCode.ACCES_DENIED));
            }
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    @Operation(summary = "그룹 초대 수락", description = "그룹 초대 링크를 통해 회원 가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹 가입 성공",
                content = @Content(schema = @Schema(implementation = GroupAcceptResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락시)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원이거나 그룹입니다. (회원 ID 또는 그룹을 찾을 수 없을 시)"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 그룹입니다.(해당 그룹에 이미 가입된 회원일 시)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생햇습니다. 다시 시도해주세요(서버 오류 발생시")
    })
    @PostMapping("/{inviteLink}")
    public ResponseEntity<?> acceptGroupInvitation(@PathVariable String inviteLink) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();

            // inviteLink로 그룹 찾기 추가
            Group group = groupRepository.findByGroupInviteLink(inviteLink)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_GROUP));

            List<GroupAcceptResponse> responses = groupService.acceptGroupInvitation(memberId, group.getGroupId());
            return ResponseEntity.ok(responses);
        } catch (CustomException e) {
            if (e.getErrorCode().equals(ErrorCode.ACCES_DENIED)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(ErrorCode.ACCES_DENIED));
            }
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    @GetMapping("/schedules/{groupId}")
    public ResponseEntity<?> getGroupSchedules(
            @PathVariable Long groupId,
            @RequestParam String yearMonth) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            List<GroupScheduleResponse> response = groupService.getGroupSchedules(memberId, groupId, yearMonth);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            if (e.getErrorCode().equals(ErrorCode.ACCES_DENIED)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(ErrorCode.ACCES_DENIED));
            }
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    @Operation(summary = "그룹 초대 링크 조회 (비회원용)", description = "초대 링크를 통해 그룹 딥링크 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "딥링크 조회 성공",
                content = @Content(schema = @Schema(implementation = GroupPublicInviteLinkResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 초대 링크입니다")
    })
    @GetMapping("/invite")
    public ResponseEntity<?> getGroupInviteByCode(
            @Schema(description = "초대링크", example = "abc123de")
            @RequestParam String inviteLink) {
        try {
            GroupPublicInviteLinkResponse response = groupService.getGroupInviteLinkByCode(inviteLink);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        }
    }


}
