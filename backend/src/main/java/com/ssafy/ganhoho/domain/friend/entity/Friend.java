package com.ssafy.ganhoho.domain.friend.entity;


import com.ssafy.ganhoho.domain.friend.constant.RequestStatus;
import com.ssafy.ganhoho.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId;

    @ManyToOne(fetch = FetchType.LAZY) // 지연로딩
    @JoinColumn(name = "member_id")
    private Member member; // 친구관계 소유회원(자기자신)

    private String friendLoginId; //친구 로그인ID

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    @Column(name="is_favorite")
    private Boolean isFavorite;

    @Column(insertable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
