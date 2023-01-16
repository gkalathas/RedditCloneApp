package com.example.redit_clone.security;

import com.example.redit_clone.exceptions.MyCustomException;
import com.example.redit_clone.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;

import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;


@Service
public class JwtProvider {

    private KeyStore keyStore;

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    public JwtProvider(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }


    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            //file
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        }catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new MyCustomException("Exception occurred while loading keystore");
        }
    }

    public String generateToken(Authentication authentication) {
        User principal = (User)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUserName())
                .signWith(getPrivateKey())
                .setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public String generateTokenWithUserName(String username) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtExpirationInMillis))
                .subject(username)
                .claim("scope", "ROLE_USER")
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

//    public String generateTokenWithUserName(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(from(Instant.now()))
//                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
//                .compact();
//    }

    private PrivateKey getPrivateKey() {
        try{
            return (PrivateKey)keyStore.getKey("springblog", "secret".toCharArray());
        }catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new MyCustomException("Exception occurred while retrieving public key from keystore");
        }
    }

    public boolean valildateToken(String jwt) {
        parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        }catch (KeyStoreException e) {
            throw new MyCustomException("Exception occurred while retrieving public key");
        }
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}
