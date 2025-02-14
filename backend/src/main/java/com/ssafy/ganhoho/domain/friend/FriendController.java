package com.ssafy.ganhoho.domain.friend;

import com.ssafy.ganhoho.domain.friend.dto.*;
import com.ssafy.ganhoho.global.auth.SecurityUtil;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import com.ssafy.ganhoho.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Friend", description = "친구 API")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 목록 조회", description = "인증된 사용자의 전체 친구 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공 혹은 빈 리스트 반환(없을시)",
                content = @Content(schema = @Schema(implementation = FriendListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다. 다시 시도해 주세요.")
    })
    @GetMapping("/list")
    public ResponseEntity<?> getFriendsList() {
        try {
            // JWT 토큰에서 사용자 ID 추출
            Long memberId = SecurityUtil.getCurrentMemberId();
            List<FriendListResponse> friendList = friendService.getFriendsList(memberId);

            return ResponseEntity.ok(friendList);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            // JWT 관련 예외나 기타 예외 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }

    }

    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다(양방향 삭제)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 삭제 성공",
                content = @Content(schema = @Schema(implementation = FriendDeleteResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 유효하지 않을 시)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다. (회원 ID가 존재하지 않을 시)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다. 다시 시도해 주세요. (서버 오류 발생시)")
    })
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> deleteFriend(
            @Parameter(description = "삭제할 친구 관계의 ID")
            @PathVariable("friendId") Long friendId) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();

            // FriendDeleteResponse 호출
            FriendDeleteResponse response = friendService.deleteFriend(memberId, friendId);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    @Operation(summary = "친구 요청 목록 조회", description = "인증된 사용자의 전체 친구 요청 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 목록 조회 성공(요청없을시 빈리스트)",
            content = @Content(schema = @Schema(implementation = FriendRequestListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다 (JWT 토큰 누락 혹은 유효 하지 않을 시)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다. (회원 ID나 요청 보낸 회원이 존재하지 않을시)")
    })
    @GetMapping("/requests/list")
    public ResponseEntity<?> getFriendRequestList() {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();

            //친구 요청 목록 조회
            List<FriendRequestListResponse> requestList = friendService.getFriendRequestList(memberId);
            return ResponseEntity.ok(requestList);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    @Operation(summary = "친구 요청 승인/거절", description = "받은 친구 요청을 승인하거나 거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 처리 성공",
                content = @Content(schema = @Schema(implementation = FriendRequestStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (요청 상태가 PENDING이 아닐시)"),
            @ApiResponse(responseCode = "401", description = "인증받지 않은 요청입니다. (요청을 받은 사람이 아닐 시)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원이거나 친구 요청이 존재하지 않습니다. (회원 혹은 요청 찾을 수 없을 시)")
    })
    @PostMapping("/{friendId}/response")
    public ResponseEntity<?> handleFriendRequest(
            @PathVariable("friendId") Long friendId,
            @RequestBody FriendRequestStatusRequest request) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();

            FriendRequestStatusResponse response = friendService.handleFriendRequest(memberId, friendId, request);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    @Operation(summary = "친구 추가 요청", description = "다른 사용자에게 친구 요청을 보냄")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 추가 요청 성공",
                content = @Content(schema = @Schema(implementation = FriendAddResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다. (자기 자신에게 요청 시)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (요청한 회원이나 대상 회원이 존재하지 않을 시)"),
            @ApiResponse(responseCode = "409", description = "이미 친구 요청이 존재하거나 이미 친구상태입니다.")
    })
    @PostMapping("/request")
    public ResponseEntity<?> addFriend(
            @RequestBody FriendAddRequest request) {

        try {
            Long memberId = SecurityUtil.getCurrentMemberId();

            FriendAddResponse response = friendService.addFriend(memberId, request);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    @Operation(summary = "친구 즐겨찾기 수정", description = "특정 친구를 즐겨찾기에 추가(true) 혹은 제거(false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "즐겨찾기 수정 성공",
                content = @Content(schema = @Schema(implementation = FriendFavoriteResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 유효하지 않을 시)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원이거나 친구관계가 존재하지 않습니다.(회원이나 친구 관계를 찾을 수 없을시)")
    })
    @PatchMapping("/favorite")
    public ResponseEntity<?> updateFriendFavorite(
            @RequestBody FriendFavoriteRequest request) {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            FriendFavoriteResponse response = friendService.updateFriendFavorite(
              memberId,
              request.getFriendMemberId(), // DTO 에서 friendId 가져오기
              request
            );
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }

    }
}
