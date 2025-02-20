package com.ssafy.ganhoho.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "그룹원 정보 응답")
@Getter
@Builder
public class GroupMemberResponse {
    @Schema(description = "로그인ID", example = "minseok1234")
    private String loginId;

    @Schema(description = "회원 이름", example = "강민석")
    private String name;

    @Schema(description = "소속 병원", example = "싸피 병원")
    private String hospital;

    @Schema(description = "소속 병동", example = "B201")
    private String ward;
}
