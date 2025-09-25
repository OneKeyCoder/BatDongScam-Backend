package com.se100.bds.services.auth.impl;

import com.se100.bds.entities.auth.JwtToken;
import com.se100.bds.exceptions.NotFoundException;
import com.se100.bds.repositories.auth.JwtTokenRepository;
import com.se100.bds.services.MessageSourceService;
import com.se100.bds.services.auth.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtTokenRepository jwtTokenRepository;

    private final MessageSourceService messageSourceService;

    /**
     * Find a JWT token by user id and refresh token.
     *
     * @param id           UUID
     * @param refreshToken String
     * @return JwtToken
     */
    @Override
    public JwtToken findByUserIdAndRefreshToken(UUID id, String refreshToken) {
        return jwtTokenRepository.findByUserIdAndRefreshToken(id, refreshToken)
                .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                        new String[]{messageSourceService.get("token")})));
    }

    /**
     * Find a JWT token by token or refresh token.
     *
     * @param token String
     * @param refreshToken String
     * @return JwtToken
     */
    @Override
    public JwtToken findByTokenOrRefreshToken(String token, String refreshToken) {
        Optional<JwtToken> jwtToken = jwtTokenRepository.findByTokenOrRefreshToken(token, refreshToken);
        if (jwtToken.isEmpty()) {
            throw new NotFoundException("Token not found");
        }
        return jwtToken.get();
    }

    /**
     * Save a JWT token.
     *
     * @param jwtToken JwtToken
     */
    @Override
    public void save(JwtToken jwtToken) {
        jwtTokenRepository.save(jwtToken);
    }

    /**
     * Delete a JWT token.
     *
     * @param jwtToken JwtToken
     */
    @Override
    public void delete(JwtToken jwtToken) {
        jwtTokenRepository.delete(jwtToken);
        log.info("Deleted token: {}", jwtToken);
    }
}