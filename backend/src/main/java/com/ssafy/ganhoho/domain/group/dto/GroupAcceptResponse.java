package com.ssafy.ganhoho.domain.group.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 그룹 초대 수락
public class GroupAcceptResponse {
    private String loginId;
    private String name;
    private String hospital;
    private String ward;
}
