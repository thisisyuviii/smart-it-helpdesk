package com.helpdesk.sys.service;

import com.helpdesk.sys.dto.request.LoginRequest;
import com.helpdesk.sys.dto.request.RegisterRequest;
import com.helpdesk.sys.dto.response.JwtAuthResponse;
import com.helpdesk.sys.dto.response.UserSummaryResponse;

public interface AuthService {
    JwtAuthResponse login(LoginRequest loginRequest);
    UserSummaryResponse register(RegisterRequest registerRequest);
}
