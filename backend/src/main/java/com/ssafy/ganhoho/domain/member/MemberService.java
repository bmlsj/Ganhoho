package com.ssafy.ganhoho.domain.member;

import com.ssafy.ganhoho.domain.member.dto.MemberInfoResponse;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    MemberInfoResponse getMemberInfo(Long memberId);
}
