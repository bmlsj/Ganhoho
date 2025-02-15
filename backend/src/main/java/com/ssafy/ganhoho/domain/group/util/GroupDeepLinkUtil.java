package com.ssafy.ganhoho.domain.group.util;

// 목표 딥링크 형식: ssafyd209://ganhoho/group?groupId=[그룹ID]
public class GroupDeepLinkUtil {

    // 앱 스킴 - 앱 식별 고유값
    private static final String SCHEME = "ssafyd209";

    // 앱내 도메인 구분값
    private static final String HOST = "ganhoho";

    // 그룹 관련 경로
    private static final String GROUP_PATH = "/group";

    // 기본 딥링크 형식
    private static final String BASE_DEEP_LINK = SCHEME + "://" + HOST;

    // 그룹 ID 포함해 딥링크 생성하기
    public static String createGroupDeepLink(String inviteLink) {
        return String.format("%s%s?groupCode=%s",
                BASE_DEEP_LINK,
                GROUP_PATH,
                inviteLink);
    }

}
