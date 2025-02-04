package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.*;

import java.util.List;

public interface GroupService {
    //그룹 생성
    GroupCreateResponse createGroup(Long memberId, GroupCreatRequest request);

    // 그룹 목록 조회
    List<GroupListResponse> getGroupList(Long memberId);

    // 그룹 초대 링킄 조회
    GroupInviteLinkResponse getGroupInviteLink(Long memberId, Long groupId);

    // 그룹원 정보 전체 조회
    List<GroupMemberResponse> getGroupMembers(Long memberId, Long groupId);

}
