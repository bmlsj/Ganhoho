package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.auth.AuthRepository;

import com.ssafy.ganhoho.domain.group.dto.*;
import com.ssafy.ganhoho.domain.member.dto.MemberDto;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupParticipationRepository groupParticipationRepository;
    private final AuthRepository authRepository;

    @Override
    @Transactional
    public GroupCreateResponse createGroup(Long memberId, GroupCreatRequest request) {
        // 해당 유저 확인
        MemberDto member = authRepository.findById((memberId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 그룹 생성
        GroupDto group = GroupDto.builder()
                .groupName(request.getGroupName())
                .groupIconType(request.getGroupIconType())
                .groupMemberCount(1) // 생성자가 첫 멤버
                .groupInviteLink(generateInviteLink())
                .build();

        // 그룹 저장
        GroupDto savedGroup = groupRepository.save(group);

        // 생성자 그룹 참여자로 추가
        GroupParticipationDto participation = GroupParticipationDto.builder()
                .memberId(member.getMemberId())
                .groupId(savedGroup.getGroupId())
                .build();
        // 그룹 생성자 참여 정보 DB에 저장
        try {
            groupParticipationRepository.save(participation);
            // 저장 성공시 회우너 ID 그룹 ID 로그에 기록
            log.info("Group participation added: memberId={}, groupId={}",
                    member.getMemberId(), savedGroup.getGroupId());
        }catch (Exception e) {
            log.error(("Failed to add group participation: " + e.getMessage()));
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
        // 생성시 로그
        log.info("Group created: groupId={}, groupName={}, creator={}",
                savedGroup.getGroupId(), group.getGroupName(), member.getLoginId());

        // Response
        return GroupCreateResponse.builder()
                .groupId(savedGroup.getGroupId())
                .groupName(savedGroup.getGroupName())
                .groupIconType(savedGroup.getGroupIconType())
                .groupMemberCount(savedGroup.getGroupMemberCount())
                .groupInviteLink(savedGroup.getGroupInviteLink())
                .build();

    }
    // 초대링크 생성
    private String generateInviteLink() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public List<GroupListResponse> getGroupList(Long memberId) {
        // member Id 기준으로 그룹 목록 조회하여 List에 담는다.
        List<GroupDto> groups = groupRepository.findGroupByMemberId(memberId);

        return groups.stream()
                .map(group -> GroupListResponse.builder()
                        .groupId(group.getGroupId())
                        .groupName(group.getGroupName())
                        .groupIconType(group.getGroupIconType())
                        .groupMemberCount(group.getGroupMemberCount())
                        .build())
                .collect(Collectors.toList());
    }

}
