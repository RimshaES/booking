package com.rimsha.service;

import com.rimsha.model.dto.request.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}
