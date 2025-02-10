package com.ssafy.ganhoho.domain.notification;

import com.ssafy.ganhoho.domain.notification.dto.NotificationDto;
import com.ssafy.ganhoho.domain.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    List<NotificationDto> notificationListToNotificationDtoList(List<Notification> notificationList);
}
