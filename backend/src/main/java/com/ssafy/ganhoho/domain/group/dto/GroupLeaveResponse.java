package com.ssafy.ganhoho.domain.group.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupLeaveResponse {
    @Builder.Default
    private boolean success = true;
}
