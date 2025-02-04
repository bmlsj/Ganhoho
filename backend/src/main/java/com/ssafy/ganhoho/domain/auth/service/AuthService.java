package com.ssafy.ganhoho.domain.auth.service;

import com.ssafy.ganhoho.domain.auth.dto.LoginRequest;
import com.ssafy.ganhoho.domain.auth.dto.LoginResponse;
import com.ssafy.ganhoho.domain.auth.dto.SignUpRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    boolean signUp(SignUpRequest signUpRequest);

    boolean isIdDuplicate(String loginId);

    LoginResponse login(LoginRequest loginRequest);
}
