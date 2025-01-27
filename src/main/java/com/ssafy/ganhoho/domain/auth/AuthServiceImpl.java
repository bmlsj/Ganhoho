package com.ssafy.ganhoho.domain.auth;

import com.ssafy.ganhoho.domain.auth.dto.LoginRequest;
import com.ssafy.ganhoho.domain.auth.dto.LoginResponse;
import com.ssafy.ganhoho.domain.member.dto.MemberDto;
import com.ssafy.ganhoho.domain.auth.dto.SignUpRequest;
import com.ssafy.ganhoho.global.auth.jwt.JWTToken;
import com.ssafy.ganhoho.global.auth.jwt.JWTUtil;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    @Override
    public void signUp(SignUpRequest signUpRequest) {
        MemberDto memberDto = MemberDto.builder()
                .loginId(signUpRequest.getLoginId())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .name(signUpRequest.getName())
                .hospital(signUpRequest.getHospital())
                .ward(signUpRequest.getWard())
                .appFcmToken(signUpRequest.getFcmToken())
                .build();

        authRepository.save(memberDto);
    }

    @Override
    public boolean isIdDuplicate(String loginId) {
        return authRepository.existsByLoginId(loginId);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        MemberDto memberDto = authRepository.findByLoginId(loginRequest.getLoginId()).orElseThrow(() -> new UsernameNotFoundException("아이디가 존재하지 않습니다"));

        boolean matchPassword = passwordEncoder.matches(loginRequest.getPassword(), memberDto.getPassword());

        if(matchPassword == true) {
            JWTToken jwtToken = jwtUtil.createTokens(memberDto.getMemberId());
            updateFcmToken(loginRequest.getFcmToken(), memberDto.getMemberId());

            return LoginResponse.builder()
                    .memberId(memberDto.getMemberId())
                    .loginId(memberDto.getLoginId())
                    .name(memberDto.getName())
                    .hospital(memberDto.getHospital())
                    .ward(memberDto.getWard())
                    .accessToken(jwtToken.getAccessToken())
                    .refreshToken(jwtToken.getRefreshToken())
                    .build();
        } else {
            throw new UsernameNotFoundException("비밀번호가 틀렸습니다");
        }
    }

    public void updateFcmToken(String fcmToken, Long memberId) {
        try {
            authRepository.updateFcmToken(fcmToken, memberId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_FCM_TOKEN);
        }
    }

}
