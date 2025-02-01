package com.ssafy.ganhoho.domain.friend;


import com.ssafy.ganhoho.domain.auth.AuthRepository;
import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import com.ssafy.ganhoho.domain.friend.dto.*;
import com.ssafy.ganhoho.domain.member.dto.MemberDto;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final AuthRepository authRepository;

    // 친구 목록
    @Override
    public List<FriendListResponse> getFriendsList(Long memberId) {
        // 현재 유저 확인 (없으면 예외밣생(orElseThrow)
        MemberDto member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        // 친구 목록 조회 (양방향 가능하게)
        List<FriendDto> sentFriends = friendRepository.findAcceptedFriendsByMember(member);
        List<FriendDto> receivedFriends = friendRepository.friendAcceptedByLoginId(member.getLoginId());

        // 양쪽 병합
        List<FriendDto> allFriends = new ArrayList<>();
        allFriends.addAll(sentFriends);
        allFriends.addAll(receivedFriends);

        // 빈 목록일 경우 빈 리스트 반환
        if (allFriends.isEmpty()) {
            return List.of();
        }
        // map = 친구목록 -> FriendListResponse 변환
        // stream을 이용해 FriendDto 순회
        return allFriends.stream().map(friend -> {
            // 관계에 따른 적절한 멤버 가져오기
            String targetLoginId = friend.getMember().getLoginId().equals(member.getLoginId())
                    ? friend.getFriendLoginId()
                    : friend.getMember().getLoginId();

            MemberDto friendMember = authRepository.findByLoginId(targetLoginId)
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

    // 친구 삭제
    @Override
    @Transactional //readOnly = false
    public FriendDeleteResponse deleteFriend(Long memberId, Long friendId) {
        // 유저 확인
        MemberDto member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        // 친구 관계 확인
        FriendDto friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        //확인한 유저 의 친구가 맞는지 (양방향 권한 확인)
        if (!friend.getMember().getLoginId().equals(member.getLoginId()) &&
            !friend.getFriendLoginId().equals(member.getLoginId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 친구 삭제
        friendRepository.delete(friend);

        // 삭제 대상의 친구관계도 찾아서 삭제
        String otherLoginId = friend.getMember().getLoginId().equals(member.getLoginId())
                ? friend.getFriendLoginId()
                : friend.getMember().getLoginId();

        Optional<FriendDto> otherSideFriend = friendRepository.findByMemberLoginIdAndFriendLoginId(
                otherLoginId, member.getLoginId());

        otherSideFriend.ifPresent(friendRepository::delete);

        return FriendDeleteResponse.builder()
                .friendId(friendId)
                .build();
    }
    // 친구 요청 목록 확인
    @Override
    public List<FriendRequestListResponse> getFriendRequestList(Long memberId) {
        // 유저 확인
        MemberDto member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        //친구 요청 목록 조회(양방향)
        List<FriendDto> receivedRequests = friendRepository.findRequestsByLoginId(member.getLoginId());
        List<FriendDto> sentRequests = friendRepository.findRequestsByMember(member);

        List<FriendDto> allRequests = new ArrayList<>();
        allRequests.addAll(receivedRequests);
        allRequests.addAll(sentRequests);


        //요청 X
        if (allRequests.isEmpty()) {
            return List.of();
        }

        // 친구요청목록 -> FriendRequestListResponse 변환.
        return allRequests.stream().map(request -> {

            // 관계에 따른 멤버 가져오기
            String targetLoginId = request.getMember().getLoginId().equals(member.getLoginId())
                    ? request.getFriendLoginId()
                    : request.getMember().getLoginId();

            MemberDto otherMember = authRepository.findByLoginId(targetLoginId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

            return FriendRequestListResponse.builder()
                    .friendRequestId(request.getFriendId())
                    .friendLoginId(otherMember.getLoginId())
                    .name(otherMember.getName())
                    .hospital(otherMember.getHospital())
                    .ward(otherMember.getWard())
                    .requestStatus(request.getRequestStatus())
                    .build();
        })
        .collect(Collectors.toList());
    }
    // 친구 요청 승인 및 거절
    @Override
    @Transactional
    public FriendRequestStatusResponse handleFriendRequest(Long memberId, Long friendId, FriendRequestStatusRequest request) {
        // 유저 확인
        MemberDto member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 단일 요청 확인
        FriendDto friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_MATCHING_FRIEND_REQUESTS));

        // 요청 받은 사람인지 확인
        if (!friendRequest.getFriendLoginId().equals(member.getLoginId())) {
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

    // 친구 추가
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
        // 이미 친구(양방향 확인)
        List<FriendDto> sentAcceptedFriends = friendRepository.findAcceptedFriendsByMember(member);
        List<FriendDto> receivedAcceptedFriends = friendRepository.friendAcceptedByLoginId(member.getLoginId());


        if (sentAcceptedFriends.stream().anyMatch(f -> f.getFriendLoginId().equals(request.getFriendLoginId())) ||
            receivedAcceptedFriends.stream().anyMatch(f -> f.getMember().getLoginId().equals(request.getFriendLoginId()))) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_EXISTS);
        }

        // 이미 친구요청 보냈는지 확인 (양방향 확인)
        List<FriendDto> sentRequests = friendRepository.findRequestsByMember(member);
        List<FriendDto> receivedRequests = friendRepository.findRequestsByLoginId(member.getLoginId());

        if (sentRequests.stream().anyMatch(f -> f.getFriendLoginId().equals(request.getFriendLoginId())) ||
            receivedRequests.stream().anyMatch(f -> f.getMember().getLoginId().equals(request.getFriendLoginId()))) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_EXISTS);
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
