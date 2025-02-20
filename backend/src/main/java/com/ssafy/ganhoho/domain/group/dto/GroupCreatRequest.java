package com.ssafy.ganhoho.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "그룹 생성 요청")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCreatRequest {

    @Schema(description = "그룹 이름", example = "간호호호홓")
    private String groupName;

    @Schema(description = "그룹 아이콘 타입", example = "1")
    private Integer groupIconType;

}

