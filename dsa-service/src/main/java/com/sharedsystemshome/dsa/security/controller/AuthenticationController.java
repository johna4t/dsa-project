package com.sharedsystemshome.dsa.security.controller;

import com.sharedsystemshome.dsa.controller.BadRequestException;
import com.sharedsystemshome.dsa.security.service.AuthenticationService;
import com.sharedsystemshome.dsa.security.dto.UserAuthentication;
import com.sharedsystemshome.dsa.security.dto.UserLogin;
import com.sharedsystemshome.dsa.security.dto.UserRegistration;
import com.sharedsystemshome.dsa.security.util.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
//@RequestMapping(value = "${sharedsystemshome.dsa.request.authentication}")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    //    @PostMapping("${sharedsystemshome.dsa.request.authentication.register}")
    @PostMapping("/register")
    public ResponseEntity<UserAuthentication> register(
            RequestEntity<UserRegistration> request){

        try {
            return ResponseEntity.status(200).body(this.authService.register(request.getBody()));
        }catch(Exception e){

            throw new BadRequestException(e.getMessage(), e);

        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserAuthentication> authenticate(RequestEntity<UserLogin> request){

        try{
            return ResponseEntity.status(200).body(this.authService.authenticate(request.getBody()));
        }catch(Exception e){

            throw new AuthenticationException(e.getMessage(), e);

        }

    }
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
         this.authService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        this.authService.logout(request, response, authentication);
        return ResponseEntity.ok().build();
    }

}
