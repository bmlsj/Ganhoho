package com.ssafy.ganhoho.domain.group.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCreateResponse {
    private Long groupId;
    private String groupName;
    private Integer groupIconType;
    private Integer groupMemberCount;
    private String groupInviteLink;
}
