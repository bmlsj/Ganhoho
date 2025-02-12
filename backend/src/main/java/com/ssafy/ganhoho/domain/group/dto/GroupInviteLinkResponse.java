package com.ssafy.ganhoho.domain.group.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupInviteLinkResponse {
    private String groupInviteLink;
}
