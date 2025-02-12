package com.ssafy.ganhoho.domain.group.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupMemberResponse {
    private String loginId;
    private String name;
    private String hospital;
    private String ward;
}
