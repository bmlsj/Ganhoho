package com.ssafy.ganhoho.domain.notification.repository;

import com.ssafy.ganhoho.domain.notification.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    @Query(value = "{'memberId': ?0}")
    List<Notification> findAllByMemberId(Long memberId);
}
