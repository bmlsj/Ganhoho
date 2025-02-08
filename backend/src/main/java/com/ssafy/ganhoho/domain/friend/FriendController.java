package com.ssafy.ganhoho.domain.friend;

import com.ssafy.ganhoho.domain.friend.dto.*;
import com.ssafy.ganhoho.global.auth.SecurityUtil;
import com.ssafy.ganhoho.global.error.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
                    .body(e.getMessage());
        } catch (Exception e) {
            // JWT 관련 예외나 기타 예외 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing authentication token.");
        }

    }

    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다(양방향 삭제)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 삭제 성공",
                content = @Content(schema = @Schema(implementation = FriendDeleteResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 유효하지 않을 시)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다. (회원 ID가 존재하지 않을 시"),
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
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing authentication token.");
        }
    }

    @Operation(summary = "친구 요청 목록 조회", description = "인증된 사용자의 전체 친구 요청 목록 조회")
    @GetMapping("/requests/list")
    public ResponseEntity<?> getFriendRequestList() {
        try {
            Long memberId = SecurityUtil.getCurrentMemberId();

            //친구 요청 목록 조회
            List<FriendRequestListResponse> requestList = friendService.getFriendRequestList(memberId);
            return ResponseEntity.ok(requestList);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing authentication token.");
        }
    }

    // 친구요청 승인/거절
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
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing authentication token.");
        }
    }

    // 친구 추가 요청
    @PostMapping("/request")
    public ResponseEntity<?> addFriend(
            @RequestBody FriendAddRequest request) {

        try {
            Long memberId = SecurityUtil.getCurrentMemberId();

            FriendAddResponse response = friendService.addFriend(memberId, request);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing authentication token.");
        }
    }

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
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or missing authentication token.");
        }

    }
}
