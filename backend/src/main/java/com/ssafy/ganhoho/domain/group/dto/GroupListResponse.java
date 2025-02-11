package com.ssafy.ganhoho.domain.group.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// [GET] 그룹 목록 조회
public class GroupListResponse {
    private Long groupId;
    private String groupName;
    private Integer groupIconType;
    private Integer groupMemberCount;
}
