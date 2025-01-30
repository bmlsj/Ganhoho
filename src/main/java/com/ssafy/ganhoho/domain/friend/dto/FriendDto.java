package com.ssafy.ganhoho.domain.friend.dto;


import com.ssafy.ganhoho.domain.member.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId;

    @ManyToOne(fetch = FetchType.LAZY) // 지연로딩
    @JoinColumn(name = "member_id")
    private MemberDto member; // 친구관계 소유회원(자기자신)

    private String friendLoginId; //친구 로그인ID

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private String requestStatus;

    @Column(name="is_favorite")
    private Boolean isFavorite;

    @Column(insertable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
