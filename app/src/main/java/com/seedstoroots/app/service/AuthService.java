package com.seedstoroots.app.service;

import com.seedstoroots.app.dto.AuthResponse;
import com.seedstoroots.app.dto.LoginRequest;
import com.seedstoroots.app.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
