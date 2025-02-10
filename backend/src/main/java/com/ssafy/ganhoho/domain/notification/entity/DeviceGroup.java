package com.ssafy.ganhoho.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="device_group")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_group_id")
    private Long deviceGroupId;

    @Column(name = "notification_key")
    private String notificationKey;
    @Column(name = "notification_key_name")
    private String notificationKeyName;

}
