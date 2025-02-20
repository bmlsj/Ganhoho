package com.ssafy.ganhoho.domain.auth.service;

import com.ssafy.ganhoho.domain.auth.AuthRepository;
import com.ssafy.ganhoho.domain.auth.dto.LoginRequest;
import com.ssafy.ganhoho.domain.auth.dto.LoginResponse;
import com.ssafy.ganhoho.domain.auth.dto.SignUpRequest;
import com.ssafy.ganhoho.domain.member.entity.Member;
import com.ssafy.ganhoho.global.auth.jwt.JWTToken;
import com.ssafy.ganhoho.global.auth.jwt.JWTUtil;
import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    @Override
    public boolean signUp(SignUpRequest signUpRequest) {
        try{
            if(isIdDuplicate(signUpRequest.getLoginId())) {
                throw new CustomException(ErrorCode.EXIST_ID);
            } else {
                Member member = Member.builder()
                        .loginId(signUpRequest.getLoginId())
                        .password(passwordEncoder.encode(signUpRequest.getPassword()))
                        .name(signUpRequest.getName())
                        .hospital(signUpRequest.getHospital())
                        .ward(signUpRequest.getWard())
                        .appFcmToken(signUpRequest.getFcmToken())
                        .hospitalLng(signUpRequest.getHospitalLng())
                        .hospitalLat(signUpRequest.getHospitalLat())
                        .build();

                authRepository.save(member);
                return true;
            }
        } catch (Exception e){
            log.debug(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isIdDuplicate(String loginId) {
        return authRepository.existsByLoginId(loginId);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Member member = authRepository.findByLoginId(loginRequest.getLoginId()).orElseThrow(() -> new CustomException(ErrorCode.AUTH_FAILURE));

        boolean matchPassword = passwordEncoder.matches(loginRequest.getPassword(), member.getPassword());

        if(matchPassword == true) {
            JWTToken jwtToken = jwtUtil.createTokens(member.getMemberId());
            if(loginRequest.getFcmToken() != null) updateFcmToken(loginRequest.getFcmToken(), member.getMemberId());

            return LoginResponse.builder()
                    .memberId(member.getMemberId())
                    .loginId(member.getLoginId())
                    .name(member.getName())
                    .hospital(member.getHospital())
                    .ward(member.getWard())
                    .accessToken(jwtToken.getAccessToken())
                    .refreshToken(jwtToken.getRefreshToken())
                    .hospitalLat(member.getHospitalLat())
                    .hospitalLng(member.getHospitalLng())
                    .build();
        } else {
            throw new CustomException(ErrorCode.AUTH_FAILURE);
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
