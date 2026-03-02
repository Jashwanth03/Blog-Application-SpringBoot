package com.jash.blog.controller;


import com.jash.blog.domain.Dto.AuthResponse;
import com.jash.blog.domain.Dto.LoginRequest;
import com.jash.blog.domain.entities.User;
import com.jash.blog.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private AuthenticationService authenticationService;


    @PostMapping
    public ResponseEntity<AuthResponse> login (@RequestBody LoginRequest loginRequest){

        UserDetails userDetails = authenticationService.authenticate(
                loginRequest.email(),
                loginRequest.password()
        );

        String  tokenValue = authenticationService.generateToken(userDetails);

       AuthResponse authResponse = new AuthResponse(
               tokenValue,
               86400
       );

       return ResponseEntity.ok(authResponse);
    }
}
