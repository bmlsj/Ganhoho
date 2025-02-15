package com.ssafy.ganhoho.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "그룹 초대 링크 조회 응답(비회원용)")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPublicInviteLinkResponse {
    @Schema(description = "그룹 딥링크", example = "[스킴]://[호스트]/[경로]?[파라미터]")
    private String groupDeepLink;
}
