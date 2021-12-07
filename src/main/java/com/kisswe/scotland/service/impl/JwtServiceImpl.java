package com.kisswe.scotland.service.impl;

import com.kisswe.scotland.config.AuthClaimConfig;
import com.kisswe.scotland.config.AuthSigningKeyConfig;
import com.kisswe.scotland.database.User;
import com.kisswe.scotland.service.JwtService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {
    private final AuthClaimConfig claimConfig;
    private final AuthSigningKeyConfig signingKeyConfig;
    private Key signingSk;

    @PostConstruct
    public void init() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance(signingKeyConfig.getType());
        InputStream keyStoreStream = ClassLoader.getSystemResourceAsStream(signingKeyConfig.getFilename());
        keyStore.load(keyStoreStream, signingKeyConfig.getPassword().toCharArray());
        signingSk = keyStore.getKey(signingKeyConfig.getAlias(), signingKeyConfig.getPassword().toCharArray());
    }

    @Override
    public Mono<String> createAccessToken(User user) {
        Instant now = Instant.now();
        return Mono.just(Jwts.builder()
                .setIssuer(claimConfig.getIssuer())
                .setAudience(claimConfig.getAudience())
                .setSubject(user.getId().toString())
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(now.plus(claimConfig.getDuration())))
                .claim("roles", user.getRoles())
                .signWith(signingSk)
                .compact());
    }
}
