package com.deepan.orderpay.auth.service;

import com.deepan.orderpay.auth.dto.RegisterRequest;
import com.deepan.orderpay.auth.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
}
