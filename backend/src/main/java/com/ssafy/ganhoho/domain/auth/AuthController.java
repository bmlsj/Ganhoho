package com.ssafy.ganhoho.domain.auth;

import com.ssafy.ganhoho.domain.auth.dto.LoginRequest;
import com.ssafy.ganhoho.domain.auth.dto.LoginResponse;
import com.ssafy.ganhoho.domain.auth.dto.SignUpRequest;
import com.ssafy.ganhoho.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody SignUpRequest signUpRequest) {

        try {
            authService.signUp(signUpRequest);
            return ResponseEntity.ok("회원가입 성공");

        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/duplicate-check")
    public ResponseEntity checkIdDuplicate(@RequestParam(name = "loginId") String loginId) {
        return ResponseEntity.ok(authService.isIdDuplicate(loginId));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
