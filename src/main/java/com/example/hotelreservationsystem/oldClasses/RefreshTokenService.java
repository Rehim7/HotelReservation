//package com.example.hotelreservationsystem.service;
//
//import com.example.hotelreservationsystem.dto.request.RefreshTokenRequest;
//import com.example.hotelreservationsystem.exceptions.TokenRefreshException;
//import com.example.hotelreservationsystem.model.RefreshToken;
//import com.example.hotelreservationsystem.repository.RefreshTokenRepository;
//import com.example.hotelreservationsystem.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.security.auth.Refreshable;
//import java.sql.Ref;
//import java.time.Instant;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class RefreshTokenService {
//
//    @Value("${security.jwt.refresh-token.token-expiration}")
//    private long refrestExpirationTime;
//
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final UserRepository userRepository;
//    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
//        this.refreshTokenRepository = refreshTokenRepository;
//        this.userRepository = userRepository;
//    }
//
//
//    public RefreshToken createToken(String  email) {
//        var user = userRepository.findByEmail(email).orElseThrow();
//        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
//
//        RefreshToken refreshToken = RefreshToken.builder()
//                .user(user)
//                .token(UUID.randomUUID().toString())
//                .expiryDate(Instant.now().plusMillis(refrestExpirationTime))
//                .build();
//        return  refreshTokenRepository.save(refreshToken);
//    }
//
//    public Optional<RefreshToken> findByToken(String token) {
//        return refreshTokenRepository.findByToken(token);
//    }
//
//    public RefreshToken refreshTokenExpiration(RefreshToken refreshToken) {
//        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
//            refreshTokenRepository.delete(refreshToken);
//            throw new TokenRefreshException(refreshToken.getToken(), "Refresh token's time expired.Please log in again");
//        }
//        return refreshToken;
//    }
//
//}
//
//
//
