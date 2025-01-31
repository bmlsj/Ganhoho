package com.ssafy.ganhoho.domain.friend;


import com.ssafy.ganhoho.domain.auth.AuthRepository;
import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import com.ssafy.ganhoho.domain.friend.dto.*;
import com.ssafy.ganhoho.domain.member.dto.MemberDto;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final AuthRepository authRepository;

    @Override
    public List<FriendListResponse> getFriendsList(Long memberId) {
        // 현재 유저 확인 (없으면 예외밣생(orElseThrow)
        MemberDto member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        // 친구 목록 조회
        List<FriendDto> friends = friendRepository.findAcceptedFriendsByMember(member);

        // 친구 없을경우 오류 발생
        if (friends.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_MEMBER);
        }
        // map = 친구목록 -> FriendListResponse 변환
        // stream을 이용해 FriendDto 순회
        return friends.stream().map(friend -> {
            MemberDto friendMember = authRepository.findByLoginId(friend.getFriendLoginId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

            return FriendListResponse.builder()
                    .friendId(friend.getFriendId())
                    .friendLoginId(friendMember.getLoginId())
                    .name(friendMember.getName())
                    .hospital(friendMember.getHospital())
                    .isFavorite(friend.getIsFavorite())
                    .build();
        })
        .collect(Collectors.toList()); // 최종 FriendListResponse 반환

    }
    @Override
    @Transactional //readOnly = false
    public FriendDeleteResponse deleteFriend(Long memberId, Long friendId) {
        // 유저 확인
        MemberDto member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        // 친구 관계 확인
        FriendDto friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        //확인한 유저 의 친구가 맞는지
        if (!friend.getMember().getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 친구 삭제
        friendRepository.delete(friend);

        return FriendDeleteResponse.builder()
                .friendId(friendId)
                .build();
    }
    @Override
    public List<FriendRequestListResponse> getFriendRequestList(Long memberId) {
        // 유저 확인
        MemberDto member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        //친구 요청 목록 조회(대기중인것만)
        List<FriendDto> friendRequests = friendRepository.findRequestsByMember(member);

        //요청 X
        if (friendRequests.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_MEMBER);
        }

        // 친구요청목록 -> FriendRequestListResponse 변환.
        return friendRequests.stream().map(request -> {
            MemberDto requestSender = authRepository.findByLoginId(request.getFriendLoginId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

            return FriendRequestListResponse.builder()
                    .friendRequestId(request.getFriendId())
                    .friendLoginId(requestSender.getLoginId())
                    .name(requestSender.getName())
                    .hospital(requestSender.getHospital())
                    .ward(requestSender.getWard())
                    .requestStatus(request.getRequestStatus())
                    .build();
        })
        .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public FriendRequestStatusResponse handleFriendRequest(Long memberId, Long friendId, FriendRequestStatusRequest request) {
        // 유저 확인
        MemberDto member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 단일 요청 확인
        FriendDto friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        //요청 대상자인지
        if (!friendRequest.getMember().getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        //요청상태가 대기중인지
        if (!friendRequest.getRequestStatus().equals(RequestStatus.PENDING)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        if (request.getRequestStatus().equals(RequestStatus.ACCEPTED)) {
            // 수락처리
            friendRequest.setRequestStatus(RequestStatus.ACCEPTED);
            FriendDto saveRequest = friendRepository.save(friendRequest);

            return FriendRequestStatusResponse.builder()
                    .friendId(saveRequest.getFriendId())
                    .requestStatus(saveRequest.getRequestStatus())
                    .build();
        } else {
            // 거절 처리 - 요청 삭제
            friendRepository.delete(friendRequest);
            return FriendRequestStatusResponse.builder()
                    .friendId(friendId)
                    .requestStatus(RequestStatus.PENDING) // 차피 삭제되니 대기중으로..?
                    .build();
        }
    }

    @Override
    @Transactional
    public FriendAddResponse addFriend(Long memberId, FriendAddRequest request) {
        // 유저 확인
        MemberDto member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 추가할 회원 존재여부 확인
        MemberDto friend = authRepository.findByLoginId(request.getFriendLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 요청 <- 자기자신 check
        if (member.getLoginId().equals(request.getFriendLoginId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        // 이미 친구
        List<FriendDto> acceptedFriends = friendRepository.findAcceptedFriendsByMember(member);
        if (acceptedFriends.stream().anyMatch(f -> f.getFriendLoginId().equals(request.getFriendLoginId()))) {
            throw new CustomException(ErrorCode.UNAUTHORIZED); // 원래는 CONFLICT
        }

        // 이미 친구요청 보냈는지 확인
        List<FriendDto> pendingRequests = friendRepository.findRequestsByMember(member);
        if (pendingRequests.stream().anyMatch(f -> f.getFriendLoginId().equals(request.getFriendLoginId()))) {
            throw new CustomException(ErrorCode.UNAUTHORIZED); // 원래는 CONFLICT
        }
        // 친구 요청 생성 & 저장
        FriendDto friendRequest = FriendDto.builder()
                .member(member)
                .friendLoginId(request.getFriendLoginId())
                .requestStatus(RequestStatus.PENDING)
                .isFavorite(false)
                .build();
        friendRepository.save(friendRequest);

        return FriendAddResponse.builder().build();
    }
}
