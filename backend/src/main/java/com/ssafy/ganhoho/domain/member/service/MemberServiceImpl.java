package com.ssafy.ganhoho.domain.member.service;

import com.ssafy.ganhoho.domain.member.MemberMapper;
import com.ssafy.ganhoho.domain.member.MemberRepository;
import com.ssafy.ganhoho.domain.member.entity.Member;
import com.ssafy.ganhoho.domain.member.dto.MemberInfoResponse;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.ssafy.ganhoho.global.auth.SecurityUtil.getCurrentMemberId;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    @Override
    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
        return MemberMapper.INSTANCE.memberInfoToMemberInfoResponse(member);
    }

    @Override
    public List<MemberInfoResponse> searchMembers(String loginId) {
        Long memberId = getCurrentMemberId();

        List<Member> members = memberRepository.findMemberByLoginIdContainingIgnoreCase(loginId);
        return MemberMapper.INSTANCE.membersToMemberInfoResponses(members.stream().filter(member -> member.getMemberId().equals(memberId) == false).collect(Collectors.toList()));
    }

    @Override
    public void withdrawal(Long memberId) {
        memberRepository.deleteMemberByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));
    }
}
