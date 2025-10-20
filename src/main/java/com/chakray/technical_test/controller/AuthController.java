package com.chakray.technical_test.controller;

import com.chakray.technical_test.dto.LoginDto;
import com.chakray.technical_test.response.ApiResponse;
import com.chakray.technical_test.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {
    private final UserService svc;
    public AuthController(UserService svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginDto loginRequest){
        try{
            boolean authenticated = svc.login(loginRequest.getTaxId(),loginRequest.getPassword());

            if(!authenticated){
                return ResponseEntity.status(401)
                        .body(ApiResponse.failure("Invalid tax_id or password"));
            }

            String fakeToken = "TEST_TOKEN_" + loginRequest.getTaxId();
            return ResponseEntity.ok(ApiResponse.success(fakeToken,"Login successful"));

        }catch(Exception e){
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.failure("Error during login: "+e.getMessage()));
        }
    }
}