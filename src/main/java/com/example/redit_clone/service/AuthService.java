package com.example.redit_clone.service;

import com.example.redit_clone.dto.AuthenticationResponse;
import com.example.redit_clone.dto.LoginRequest;
import com.example.redit_clone.dto.RefreshTokenRequest;
import com.example.redit_clone.dto.RegisterRequest;
import com.example.redit_clone.exceptions.MyCustomException;
import com.example.redit_clone.model.NotificationEmail;
import com.example.redit_clone.model.User;
import com.example.redit_clone.model.VerificationToken;
import com.example.redit_clone.repository.UserRepository;
import com.example.redit_clone.repository.VerificationTokenRepository;
import com.example.redit_clone.security.JwtProvider;
import io.jsonwebtoken.Jwt;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Data
@Service
@AllArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    private RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signUp(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUserName(registerRequest.getUsername());
        //encrypting the password for every new user
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        //send verification email when save the new user to db with the token
        String token = generateVerificationToken(user);

        //sending welcoming email
        mailService.sendEmail(new NotificationEmail("Please Activate your Account",
                user.getEmail(),"Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
                //when user clicks this url redirects him in our server, we also get the token and look into
                //our db if exists to enable the user
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        //we need to process in the db
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
       Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
       verificationToken.orElseThrow(() -> new MyCustomException("Invalid Token"));
       fetchUserAndEnable(verificationToken.get());

    }

    @Transactional
    void fetchUserAndEnable(VerificationToken verificationToken) {

        String userName = verificationToken.getUser().getUserName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new MyCustomException("User not found with username: " + userName));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername()
                        , loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token =  jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();

    }


//    public User getCurrentUser(String userName) {
//        Optional<User> temp = userRepository.findByUsername(userName);
//        return User.builder().userId(temp.get().getUserId()).userName(temp.get().getUserName())
//                .email(temp.get().getEmail()).password(temp.get().getPassword()).enabled(temp.get().isEnabled())
//                .created(temp.get().getCreated()).build();
//    }

    @Transactional()
    public User getCurrentUser() {
        Jwt principal = (Jwt) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getBody().getClass().getName())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " +
                        principal.getBody().getClass().getName()));
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

}
