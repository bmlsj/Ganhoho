package com.ssafy.ganhoho.domain.group.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "`group`")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 그룹 기본 정보
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", length = 50, nullable = false)
    private String groupName;

    @Column(name = "group_invite_link", length = 50)
    private String groupInviteLink;

    @Column(name = "group_deep_link", length = 500)
    private String groupDeepLink;

    @Column(name = "group_member_count")
    private Integer groupMemberCount;

    @Column(name = "group_icon_type")
    private Integer groupIconType;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
