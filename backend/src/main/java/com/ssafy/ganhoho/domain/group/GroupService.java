package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.dto.GroupCreatRequest;
import com.ssafy.ganhoho.domain.group.dto.GroupCreateResponse;

public interface GroupService {
    GroupCreateResponse createGroup(Long memberId, GroupCreatRequest request);
}
