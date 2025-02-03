package com.ssafy.ganhoho.domain.member;

import com.ssafy.ganhoho.domain.member.dto.MemberDto;
import com.ssafy.ganhoho.domain.member.dto.MemberInfoResponse;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    @Override
    public MemberInfoResponse getMemberInfo(Long memberId) {
        MemberDto memberDto = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        return MemberInfoResponse.builder()
                .memberId(memberDto.getMemberId())
                .loginId(memberDto.getLoginId())
                .name(memberDto.getName())
                .ward(memberDto.getWard())
                .hospital(memberDto.getHospital())
                .build();
    }
}
