package com.ssafy.ganhoho.domain.auth;

import com.ssafy.ganhoho.domain.auth.dto.LoginRequest;
import com.ssafy.ganhoho.domain.auth.dto.LoginResponse;
import com.ssafy.ganhoho.domain.auth.dto.SignUpRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    void signUp(SignUpRequest signUpRequest);

    boolean isIdDuplicate(String loginId);

    LoginResponse login(LoginRequest loginRequest);
}
