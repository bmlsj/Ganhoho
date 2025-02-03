package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.GroupCreatRequest;
import com.ssafy.ganhoho.domain.group.dto.GroupCreateResponse;
import com.ssafy.ganhoho.domain.group.dto.GroupInviteLinkResponse;
import com.ssafy.ganhoho.domain.group.dto.GroupListResponse;

import java.util.List;

public interface GroupService {
    //그룹 생성
    GroupCreateResponse createGroup(Long memberId, GroupCreatRequest request);

    // 그룹 목록 조회
    List<GroupListResponse> getGroupList(Long memberId);

    // 그룹 초대 링킄 조회
    GroupInviteLinkResponse getGroupInviteLink(Long memberId, Long groupId);
}
