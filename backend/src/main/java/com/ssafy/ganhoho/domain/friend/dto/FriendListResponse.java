package com.ssafy.ganhoho.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "친구 목록 응답 DTO")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendListResponse {
    @Schema(description = "친구 관계 고유 ID (친구 등록 순서대로 부여 & 양방향)", example = "1")
    private Long friendId;

    @Schema(description = "친구의 회원 ID", example = "2")
    private Long memberId;

    @Schema(description = "친구의 로그인 ID", example = "newUser2")
    private String friendLoginId;

    @Schema(description = "친구 이름", example = "김간호")
    private String name;

    @Schema(description = "친구의 소속 병원 정보", example = "싸피병원")
    private String hospital;

    @Schema(description = "친구의 소속 병동", example = "외과병동")
    private String ward;

    @Schema(description = "즐겨찾기 여부", example = "true")
    private Boolean isFavorite;
}
