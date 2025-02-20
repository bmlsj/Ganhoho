package com.ssafy.ganhoho.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "그룹 탈퇴 응답")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupLeaveResponse {
    @Schema(description = "탈퇴 처리 성공 여부", example = "true")
    @Builder.Default
    private boolean success = true;
}
