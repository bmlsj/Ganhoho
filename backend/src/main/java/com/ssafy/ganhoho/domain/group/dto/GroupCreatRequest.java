package com.ssafy.ganhoho.domain.group.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCreatRequest {
    private String groupName;
    private Integer groupIconType;
    private String groupInviteLink;
}

