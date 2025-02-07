package com.ssafy.ganhoho.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssafy.ganhoho.domain.member.MemberRepository;
import com.ssafy.ganhoho.domain.member.entity.Member;
import com.ssafy.ganhoho.domain.notification.dto.NotificationSendRequestBody;
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

import static com.ssafy.ganhoho.global.auth.SecurityUtil.getCurrentMemberId;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final DeviceGroupRepository deviceGroupRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void changeSubscription(Long memberId, Boolean isSubscribe) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        String notificationKeyName = KoreanToQwertyConverter.convert(member.getHospital()) + "_"+ KoreanToQwertyConverter.convert(member.getWard());
        DeviceGroup deviceGroup = deviceGroupRepository.findByNotificationKeyName(notificationKeyName);
        String message;
        try{
            if(deviceGroup == null) {
                if(isSubscribe == false) throw new CustomException(ErrorCode.BAD_REQUEST);
                message = makeJsonDeviceGroup("", notificationKeyName, member.getAppFcmToken(), "create");
                manageDeviceGroup("", notificationKeyName, message);
            }
            else {
                if(isSubscribe == false) {
                    message = makeJsonDeviceGroup(deviceGroup.getNotificationKey(), notificationKeyName, member.getAppFcmToken(), "remove");
                    manageDeviceGroup(deviceGroup.getNotificationKey(), notificationKeyName, message);
                } else {
                    message = makeJsonDeviceGroup(deviceGroup.getNotificationKey(), notificationKeyName, member.getAppFcmToken(), "add");
                    manageDeviceGroup(deviceGroup.getNotificationKey(), notificationKeyName, message);
                }
            }
        }catch (Exception e){
            throw new CustomException(ErrorCode.MISSING_REQUIRED_FIELDS);
        }
    }

    @Override
    public List<Notification> getAllNotifications(Long memberId) {
        return List.of();
    }

    @Override
    public void saveNotification(NotificationSendRequestBody notificationSendRequestBody) {
        // 우선 몽고디비 연결 위해 저장하는 것만 하는 함수 생성, 이후 메세지를 보낸 후 저장하는 것으로 로직 변경 예정
        Long memberId = getCurrentMemberId();
        Notification notification = Notification.builder()
                .type(notificationSendRequestBody.getType())
                .message(notificationSendRequestBody.getMessage())
                .title(notificationSendRequestBody.getTitle())
                .memberId(memberId)
                .build();
        notificationRepository.save(notification);
    }

    public void manageDeviceGroup(String notificationKey, String notificationKeyName, String message) { // 기기그룹에 토큰 추가하기
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(FcmConstant.FIREBASE_DEVICE_GROUP_URL.value())
                    .post(requestBody)
                    .addHeader("access_token_auth", "true")
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .addHeader("project_id", FcmConstant.FIREBASE_SENDER_ID.value())
                    .build();

            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                if(notificationKey.isEmpty()) saveNewDeviceGroup(notificationKeyName, response);
            }
            else log.error("notification response not success : {}", response.body().string());

        }catch (Exception e) {
            log.error("add NewMemberDeviceGroup error : {}",e.getMessage());
        }
    }

    public void saveNewDeviceGroup(String notificationKeyName, Response response) throws IOException, ParseException { // db에 deviceGroup 정보 저장
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

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FcmConstant.FIREBASE_KEY_FILE.value()).getInputStream())
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeJsonDeviceGroup(String notificationKey, String notificationKeyName, String registrationId, String operation) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("operation", operation);
        if(notificationKey.isEmpty() == false) map.put("notification_key", notificationKey);

        map.put("notification_key_name", notificationKeyName);
        map.put("registration_ids", Arrays.asList(
                registrationId
        ));

        return objectMapper.writeValueAsString(map);
    }

}
