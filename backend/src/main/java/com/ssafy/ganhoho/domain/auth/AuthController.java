package com.ssafy.ganhoho.domain.auth;

import com.ssafy.ganhoho.domain.auth.dto.LoginRequest;
import com.ssafy.ganhoho.domain.auth.dto.LoginResponse;
import com.ssafy.ganhoho.domain.auth.dto.SignUpRequest;
import com.ssafy.ganhoho.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Boolean> signUp(@RequestBody SignUpRequest signUpRequest) {
        Boolean isSuccess = authService.signUp(signUpRequest);
        return ResponseEntity.ok(isSuccess);
    }

    @GetMapping("/duplicate-check")
    public ResponseEntity<Boolean> checkIdDuplicate(@RequestParam(name = "loginId") String loginId) {
        return ResponseEntity.ok(authService.isIdDuplicate(loginId));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
