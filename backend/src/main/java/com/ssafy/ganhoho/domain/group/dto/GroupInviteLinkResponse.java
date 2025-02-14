package com.ssafy.ganhoho.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "그룹 초대 링크 조회 응답")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupInviteLinkResponse {
    @Schema(description = "그룹 초대 링크", example = "abc123de")
    private String groupInviteLink;
}
