package com.se100.bds.services.auth;

import com.se100.bds.entities.auth.JwtToken;

import java.util.UUID;

public interface JwtTokenService {
    JwtToken findByUserIdAndRefreshToken(UUID id, String refreshToken);

    JwtToken findByTokenOrRefreshToken(String token, String refreshToken);

    void save(JwtToken jwtToken);

    void delete(JwtToken jwtToken);
}