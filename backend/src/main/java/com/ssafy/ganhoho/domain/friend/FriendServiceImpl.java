package com.ssafy.ganhoho.domain.friend;


import com.ssafy.ganhoho.domain.auth.AuthRepository;
import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import com.ssafy.ganhoho.domain.friend.dto.*;
import com.ssafy.ganhoho.domain.friend.entity.Friend;
import com.ssafy.ganhoho.domain.member.entity.Member;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        // 친구 목록 조회 (양방향 가능하게)
        List<Friend> sentFriends = friendRepository.findAcceptedFriendsByMember(member);
        List<Friend> receivedFriends = friendRepository.friendAcceptedByLoginId(member.getLoginId());

        // 양쪽 병합
        List<Friend> allFriends = new ArrayList<>();
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

            Member friendMember = authRepository.findByLoginId(targetLoginId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));


            return FriendListResponse.builder()
                    .friendId(friend.getFriendId())
                    .memberId(friendMember.getMemberId())
                    .friendLoginId(friendMember.getLoginId())
                    .name(friendMember.getName())
                    .hospital(friendMember.getHospital())
                    .ward(friendMember.getWard()) // ward 추가
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
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        // 친구 관계 확인
        Friend friend = friendRepository.findById(friendId)
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

        Optional<Friend> otherSideFriend = friendRepository.findByMemberLoginIdAndFriendLoginId(
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
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        //친구 요청 목록 조회(단방향 - 받은요청만)
        List<Friend> receivedRequests = friendRepository.findRequestsByLoginId(member.getLoginId());

        //요청 X
        if (receivedRequests.isEmpty()) {
            return List.of();
        }

        // 친구요청목록 -> FriendRequestListResponse 변환.
        return receivedRequests.stream().map(request -> {

            Member otherMember = authRepository.findByLoginId(request.getMember().getLoginId())
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
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 단일 요청 확인
        Friend friendRequest = friendRepository.findById(friendId)
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
            Friend saveRequest = friendRepository.save(friendRequest);

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
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 추가할 회원 존재여부 확인
        Member friend = authRepository.findByLoginId(request.getFriendLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 요청 <- 자기자신 check
        if (member.getLoginId().equals(request.getFriendLoginId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        // 이미 친구(양방향 확인)
        List<Friend> sentAcceptedFriends = friendRepository.findAcceptedFriendsByMember(member);
        List<Friend> receivedAcceptedFriends = friendRepository.friendAcceptedByLoginId(member.getLoginId());


        if (sentAcceptedFriends.stream().anyMatch(f -> f.getFriendLoginId().equals(request.getFriendLoginId())) ||
            receivedAcceptedFriends.stream().anyMatch(f -> f.getMember().getLoginId().equals(request.getFriendLoginId()))) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_EXISTS);
        }

        // 이미 친구요청 보냈는지 확인 (양방향 확인)
        List<Friend> sentRequests = friendRepository.findRequestsByMember(member);
        List<Friend> receivedRequests = friendRepository.findRequestsByLoginId(member.getLoginId());

        if (sentRequests.stream().anyMatch(f -> f.getFriendLoginId().equals(request.getFriendLoginId())) ||
            receivedRequests.stream().anyMatch(f -> f.getMember().getLoginId().equals(request.getFriendLoginId()))) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_EXISTS);
        }
        // 친구 요청 생성 & 저장
        Friend friendRequest = Friend.builder()
                .member(member)
                .friendLoginId(request.getFriendLoginId())
                .requestStatus(RequestStatus.PENDING)
                .isFavorite(false)
                .build();

        friendRepository.save(friendRequest);

        return FriendAddResponse.builder().build();

    }

    // 친구 즐겨찾기 수정
    @Override
    @Transactional
    public FriendFavoriteResponse updateFriendFavorite(Long memberId, Long friendId, FriendFavoriteRequest request) {
        // 유저확인
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        // 존재 여부 확인
        Member friendMember = authRepository.findById(request.getFriendMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        Friend friend = friendRepository.findByMemberAndFriend(
                member.getLoginId(),
                friendMember.getLoginId()
        ).orElseThrow(() -> new CustomException(ErrorCode.NO_MATCHING_FRIEND_REQUESTS));

        friend.setIsFavorite(request.getIsFavorite());
        friendRepository.save(friend);

        return FriendFavoriteResponse.builder().build();
    }
}
