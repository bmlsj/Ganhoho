package com.ssafy.ganhoho.domain.member.service;

import com.ssafy.ganhoho.domain.member.dto.MemberInfoResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MemberService {
    MemberInfoResponse getMemberInfo(Long memberId);

    List<MemberInfoResponse> searchMembers(String loginId);
}
