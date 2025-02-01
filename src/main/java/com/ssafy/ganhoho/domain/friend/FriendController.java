package com.ssafy.ganhoho.domain.friend;

import com.ssafy.ganhoho.domain.friend.dto.*;
import com.ssafy.ganhoho.global.auth.SecurityUtil;
import com.ssafy.ganhoho.global.auth.jwt.JWTUtil;
import com.ssafy.ganhoho.global.error.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 친구 목록조회
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
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> deleteFriend(
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
}
