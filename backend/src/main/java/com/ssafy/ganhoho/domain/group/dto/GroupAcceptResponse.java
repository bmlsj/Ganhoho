package com.ssafy.ganhoho.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "그룹 초대 수락")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupAcceptResponse {
    @Schema(description = "그룹 ID", example = "1")
    private Long groupId;

    @Schema(description = "그룹 이름", example = "간호호호홓")
    private String groupName;

    @Schema(description = "그룹 아이콘 타입", example = "1")
    private Integer groupIconType;

    @Schema(description = "그룹 멤버 수", example = "1")
    private Integer groupMemberCount;


}
