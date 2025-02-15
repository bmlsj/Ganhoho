package com.ssafy.ganhoho.domain.group;

import com.ssafy.ganhoho.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    // memberId를 기준으로 사용자가 참여하고 있는 그룹들 조회
    @Query("SELECT g FROM Group g " +
            "JOIN GroupParticipation gp ON g.groupId = gp.groupId " +
            "WHERE gp.memberId = :memberId")
    List<Group> findGroupByMemberId(@Param("memberId") Long memberId);

    Optional<Group> findByGroupInviteLink(String groupInviteLink);
}
