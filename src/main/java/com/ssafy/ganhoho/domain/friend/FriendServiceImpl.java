package com.ssafy.ganhoho.domain.friend;


import com.ssafy.ganhoho.domain.auth.AuthRepository;
import com.ssafy.ganhoho.domain.friend.dto.FriendDto;
import com.ssafy.ganhoho.domain.friend.dto.FriendListResponse;
import com.ssafy.ganhoho.domain.member.dto.MemberDto;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
