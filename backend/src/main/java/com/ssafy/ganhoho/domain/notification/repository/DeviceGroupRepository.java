package com.ssafy.ganhoho.domain.notification.repository;

import com.ssafy.ganhoho.domain.notification.entity.DeviceGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, Long> {
    DeviceGroup findByNotificationKeyName(String notificationKeyName);
}
