package com.ssafy.ganhoho.domain.notification.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssafy.ganhoho.domain.member.MemberRepository;
import com.ssafy.ganhoho.domain.member.entity.Member;
import com.ssafy.ganhoho.domain.notification.NotificationMapper;
import com.ssafy.ganhoho.domain.notification.dto.FcmDto;
import com.ssafy.ganhoho.domain.notification.dto.NotificationDto;
import com.ssafy.ganhoho.domain.notification.entity.Notification;
import com.ssafy.ganhoho.domain.notification.repository.DeviceGroupRepository;
import com.ssafy.ganhoho.domain.notification.entity.DeviceGroup;
import com.ssafy.ganhoho.domain.notification.repository.NotificationRepository;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.constant.FcmConstant;
import com.ssafy.ganhoho.global.converter.KoreanToQwertyConverter;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final DeviceGroupRepository deviceGroupRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void changeSubscription(Long memberId, Boolean isSubscribed) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        String notificationKeyName = makeNotificationKeyName(member.getHospital(), member.getWard());
        DeviceGroup deviceGroup = deviceGroupRepository.findByNotificationKeyName(notificationKeyName);
        String message;

        if (deviceGroup == null) {
            if (isSubscribed == false) throw new CustomException(ErrorCode.NOT_EXIST_DEVICE_GROUP);
            message = makeJsonDeviceGroup("", notificationKeyName, member.getAppFcmToken(), "create");
            manageDeviceGroup("", notificationKeyName, message);
        } else {
            if (isSubscribed == false) {
                log.warn("isSubscribed false: {}", isSubscribed);
                message = makeJsonDeviceGroup(deviceGroup.getNotificationKey(), notificationKeyName, member.getAppFcmToken(), "remove");
                manageDeviceGroup(deviceGroup.getNotificationKey(), notificationKeyName, message);
            } else {
                log.warn("isSubscribed true: {}", isSubscribed);
                message = makeJsonDeviceGroup(deviceGroup.getNotificationKey(), notificationKeyName, member.getAppFcmToken(), "add");
                manageDeviceGroup(deviceGroup.getNotificationKey(), notificationKeyName, message);
            }
        }

    }

    @Override
    public List<NotificationDto> getNotifications(Long memberId) {
        List<Notification> notifications = notificationRepository.findAllByMemberId(memberId);
        return NotificationMapper.INSTANCE.notificationListToNotificationDtoList(notifications);
    }

    @Override
    public void sendNotification(NotificationDto notificationSendRequestBody) {
        String notificationKeyName = makeNotificationKeyName(notificationSendRequestBody.getHospital(), notificationSendRequestBody.getWard());
        DeviceGroup deviceGroup = deviceGroupRepository.findByNotificationKeyName(notificationKeyName);
        if(deviceGroup == null) throw new CustomException(ErrorCode.NOT_EXIST_DEVICE_GROUP);
        List<Member> members = memberRepository.findMembersByHospitalAndWard(notificationSendRequestBody.getHospital(), notificationSendRequestBody.getWard());
        sendFcmToServer(deviceGroup.getNotificationKey(), notificationSendRequestBody.getTitle(), notificationSendRequestBody.getMessage());
        for (Member member : members) {
            log.warn("notification member : {}",member);
            Notification notification = Notification.builder()
                    .type(notificationSendRequestBody.getType())
                    .message(notificationSendRequestBody.getMessage())
                    .title(notificationSendRequestBody.getTitle())
                    .memberId(member.getMemberId())
                    .build();
            notificationRepository.save(notification);
        }

    }

    private void manageDeviceGroup(String notificationKey, String notificationKeyName, String message) { // 기기그룹에 토큰 추가하기
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            String accessToken = getAccessToken();
            Request request = new Request.Builder()
                    .url(FcmConstant.FIREBASE_DEVICE_GROUP_URL.value())
                    .post(requestBody)
                    .addHeader("access_token_auth", "true")
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .addHeader("project_id", FcmConstant.FIREBASE_SENDER_ID.value())
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                if (notificationKey.isEmpty()) saveNewDeviceGroup(notificationKeyName, response);
            } else {
                log.error("notification response not success : {}", response.body().string());
                throw new Exception(response.body().string());
            }

        } catch (Exception e) {
            log.error("add manageDeviceGroup error : {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveNewDeviceGroup(String notificationKeyName, Response response) throws IOException, ParseException { // db에 deviceGroup 정보 저장
        String jsonString = response.body().string();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
        Object keyValue = jsonObject.get("notification_key");
        if (keyValue instanceof String notificationKey) {
            DeviceGroup deviceGroup = DeviceGroup.builder()
                    .notificationKey(notificationKey)
                    .notificationKeyName(notificationKeyName)
                    .build();

            deviceGroupRepository.save(deviceGroup);
        } else {
            throw new IllegalStateException("Invalid notification_key type");
        }
    }

    private void sendFcmToServer(String notificationKey, String title, String content) {
        try {
            Map<String, String> data = new HashMap<>(); // key - value로 알림 데이터 넣어서 보내기
            data.put("title", title);
            data.put("content", content);

            FcmDto fcmDto = FcmDto.builder()
                    .message(FcmDto.Message.builder()
                            .token(notificationKey)
                            .data(data)
                            .build())
                    .build();

            String message = objectMapper.writeValueAsString(fcmDto);
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(FcmConstant.FIREBASE_POST_MESSAGE_URL.value())
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() == false) {
                log.error("notification response not success : {}", response.body().string());
                throw new Exception(response.body().string());
            }

        } catch (Exception e) {
            log.error("add sendFcmToServer error : {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FcmConstant.FIREBASE_KEY_FILE.value()).getInputStream())
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeJsonDeviceGroup(String notificationKey, String notificationKeyName, String registrationId, String operation){
        Map<String, Object> map = new HashMap<>();
        map.put("operation", operation);
        if (notificationKey.isEmpty() == false) map.put("notification_key", notificationKey);

        map.put("notification_key_name", notificationKeyName);
        map.put("registration_ids", Arrays.asList(
                registrationId
        ));
        try{
            return objectMapper.writeValueAsString(map);
        }catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    private String makeNotificationKeyName(String hospital, String ward) {
        return KoreanToQwertyConverter.convert(hospital) + "_" + KoreanToQwertyConverter.convert(ward);
    }

}
