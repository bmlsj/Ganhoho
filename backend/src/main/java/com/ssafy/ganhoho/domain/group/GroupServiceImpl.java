package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.auth.AuthRepository;

import com.ssafy.ganhoho.domain.group.dto.*;
import com.ssafy.ganhoho.domain.group.entity.Group;
import com.ssafy.ganhoho.domain.group.entity.GroupParticipation;
import com.ssafy.ganhoho.domain.group.entity.GroupSchedule;
import com.ssafy.ganhoho.domain.group.util.GroupDeepLinkUtil;
import com.ssafy.ganhoho.domain.member.entity.Member;
import com.ssafy.ganhoho.domain.schedule.entity.WorkSchedule;
import com.ssafy.ganhoho.domain.schedule.repository.WorkScheduleRepository;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
    private final WorkScheduleRepository workScheduleRepository;
    private final GroupScheduleRepository groupScheduleRepository;

    // 멤버의 근무 스케줄을 그룹 스케줄ㄹ과 연결
    @Override
    @Transactional
    public void linkMemberSchedulesToGroup(Long groupId, Long memberId, String yearMonth) {
        //그룹 존재 여부 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_GROUP));

        // 해당 그룹 해당 월 그룹스케줄 조회
        Optional<GroupSchedule> existingSchedule = groupScheduleRepository
            .findByGroupIdAndYearMonth(groupId, yearMonth);

        GroupSchedule groupSchedule;
        if (existingSchedule.isPresent()) {
            groupSchedule = existingSchedule.get();
        } else {
            // 없으면 새로 생성
            GroupSchedule newSchedule = GroupSchedule.builder()
                .groupId(groupId)
                .scheduleMonth(yearMonth)
                .build();
            groupSchedule = groupScheduleRepository.save(newSchedule);
        }

        // 해당 멤버 월 근무스케줄 찾아서 그룹 스케줄과 연결
        List<WorkSchedule> memberSchedules = workScheduleRepository
                .findByMemberIdAndMonthYear(memberId, yearMonth);

        for (WorkSchedule schedule : memberSchedules) {
            schedule.setWorkScheduleDetailId(groupSchedule.getWorkScheduleDetailId());
            workScheduleRepository.save(schedule);
        }

    }

    @Override
    @Transactional
    public GroupCreateResponse createGroup(Long memberId, GroupCreatRequest request) {
        // 해당 유저 확인
        Member member = authRepository.findById((memberId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 그룹 생성
        Group group = Group.builder()
                .groupName(request.getGroupName())
                .groupIconType(request.getGroupIconType())
                .groupMemberCount(1) // 생성자가 첫 멤버
                .groupInviteLink(generateInviteLink())
                .build();

        // 그룹 저장
        Group savedGroup = groupRepository.save(group);

        // ID 발급 후 딥링크 생성 및 업데이트
        String deepLink = GroupDeepLinkUtil.createGroupDeepLink(savedGroup.getGroupId());
        savedGroup.setGroupDeepLink(deepLink);
        savedGroup = groupRepository.save(savedGroup);

        // 생성자 그룹 참여자로 추가
        GroupParticipation participation = GroupParticipation.builder()
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
                .groupDeepLink(savedGroup.getGroupDeepLink()) // 딥링크 추가
                .build();

    }
    // 초대링크 생성 (임시방편.)
    private String generateInviteLink() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public List<GroupListResponse> getGroupList(Long memberId) {
        // member Id 기준으로 그룹 목록 조회하여 List에 담는다.
        List<Group> groups = groupRepository.findGroupByMemberId(memberId);

        return groups.stream()
                .map(group -> GroupListResponse.builder()
                        .groupId(group.getGroupId())
                        .groupName(group.getGroupName())
                        .groupIconType(group.getGroupIconType())
                        .groupMemberCount(group.getGroupMemberCount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public GroupInviteLinkResponse getGroupInviteLink(Long memberId, Long groupId) {
        // 그룹 존재 여부 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_GROUP));


        boolean isMember = groupParticipationRepository.existsByMemberIdAndGroupId(memberId, groupId);
        if (!isMember) {
            throw new CustomException(ErrorCode.NOT_EXIST_GROUP);
        }


        // 해당 그룹 초대링크가 없으면 생성
        if (group.getGroupInviteLink() == null) {
            group.setGroupInviteLink(generateInviteLink());
            groupRepository.save(group);
        }

        return GroupInviteLinkResponse.builder()
                .groupInviteLink(group.getGroupInviteLink())
                .build();

    }

    @Override
    public List<GroupMemberResponse> getGroupMembers(Long memberId, Long groupId) {
        // 그룹 존재 여부 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_GROUP));

        // 사용자가 해당 그룹 멤버인지
        boolean isMember = groupParticipationRepository.existsByMemberIdAndGroupId(memberId, groupId);
        if (!isMember) {
            throw new CustomException(ErrorCode.ACCES_DENIED);
        }

        // 조회 후 멤버 정보를 매핑한다.
        List<GroupParticipation> participations = groupParticipationRepository.findByGroupId(groupId);

        //dto로 변환
        return participations.stream()
                .map(participation -> {
                    Member member = authRepository.findById(participation.getMemberId())
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
                    return GroupMemberResponse.builder()
                            .loginId(member.getLoginId())
                            .name(member.getName())
                            .hospital(member.getHospital())
                            .ward(member.getWard())
                            .build();
                })
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public GroupLeaveResponse getGroupLeave(Long memberId, Long groupId) {
        // 그룹 존재 여부 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_GROUP));

        // 사용자가 해당 그룹 멤버인지
        boolean isMember = groupParticipationRepository.existsByMemberIdAndGroupId(memberId, groupId);
        if (!isMember) {
            throw new CustomException(ErrorCode.ACCES_DENIED);
        }

        // 그룹 참여 정보 삭제
        groupParticipationRepository.deleteByMemberIdAndGroupId(memberId, groupId);

        // 그룹 멤버 수 감소
        group.setGroupMemberCount(group.getGroupMemberCount() - 1);
        groupRepository.save(group);

        // 로그 생성
        log.info("User left group: memberId={}, groupId={}", memberId, groupId);

        return GroupLeaveResponse.builder().build();
    }

    @Override
    @Transactional
    public List<GroupAcceptResponse> acceptGroupInvitation(Long memberId, Long groupId) {
        // 사용자가 실제로 있는지
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 그룹 존재 여부 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_GROUP));

        // 이미 그룹원인지 확인
        boolean isMember = groupParticipationRepository.existsByMemberIdAndGroupId(memberId, groupId);
        if (isMember) {
            throw new CustomException(ErrorCode.ACCES_DENIED);
        }

        // 참여 정보 저장...
        GroupParticipation participation = GroupParticipation.builder()
                .memberId(memberId)
                .groupId(groupId)
                .build();
        groupParticipationRepository.save(participation);

        // 멤버 수를 증가시킨다.
        group.setGroupMemberCount(group.getGroupMemberCount() + 1);
        groupRepository.save(group);

        // [추가]현재 월의 스케줄 연결
        String currentYearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        linkMemberSchedulesToGroup(groupId, memberId, currentYearMonth);

        // 업데이트 이후 목록 반환
        List<GroupParticipation> participations = groupParticipationRepository.findByGroupId(groupId);

        return  participations.stream()
                .map(p -> {
                    Member memberDto = authRepository.findById(p.getMemberId())
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
                    return GroupAcceptResponse.builder()
                            .loginId(memberDto.getLoginId())
                            .name(memberDto.getName())
                            .hospital(memberDto.getHospital())
                            .ward(memberDto.getWard())
                            .build();
                })
                .collect(Collectors.toList());

    }

    @Override
    public List<GroupScheduleResponse> getGroupSchedules(Long memberId, Long groupId, String yearMonth) {
        // 사용자가 실제로 있는지
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 그룹 존재 여부 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_GROUP));

        // 이미 그룹원인지 확인
        boolean isMember = groupParticipationRepository.existsByMemberIdAndGroupId(memberId, groupId);
        if (!isMember) {
            throw new CustomException(ErrorCode.ACCES_DENIED);
        }

        // 해당 그룹 월 스케줄 조회
        List<WorkSchedule> workSchedules = groupScheduleRepository.findWorkScheduleByGroupIdAndYearMonth(groupId, yearMonth);

        // 그룹 멤버 목록 조회
        List<GroupParticipation> participations = groupParticipationRepository.findByGroupId(groupId);

        // 멤버별 스케줄 정보 매핑
        return participations.stream()
                .map(participation -> {
                    Member memberDto = authRepository.findById(participation.getMemberId())
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
                    // 그룹 멤버 스케줄 정보만 필터링하고, ScheduleInfo 형태로 변환
                    List<GroupScheduleResponse.ScheduleInfo> schedules = workSchedules.stream()
                            .filter(ws -> ws.getMemberId().equals(participation.getMemberId()))
                            .map(ws -> GroupScheduleResponse.ScheduleInfo.builder()
                                    .workDate(ws.getWorkDate())
                                    .workType(ws.getWorkType())
                                    .build())
                            .collect(Collectors.toList());

                    return GroupScheduleResponse.builder()
                            .memberId(memberDto.getMemberId())
                            .name(memberDto.getName())
                            .loginId(memberDto.getLoginId())
                            .hospital(memberDto.getHospital())
                            .ward(memberDto.getWard())
                            .schedules(schedules)
                            .build();
                })
                .collect(Collectors.toList());

    }


}
