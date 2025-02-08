package com.ssafy.ganhoho.domain.group.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_participation")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 그룹 참여 정보
public class GroupParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_participation_id")
    private Long groupParticipationId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
