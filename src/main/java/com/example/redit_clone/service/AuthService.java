package com.example.redit_clone.service;

import com.example.redit_clone.dto.AuthenticationResponse;
import com.example.redit_clone.dto.LoginRequest;
import com.example.redit_clone.dto.RegisterRequest;
import com.example.redit_clone.exceptions.MyCustomException;
import com.example.redit_clone.model.NotificationEmail;
import com.example.redit_clone.model.User;
import com.example.redit_clone.model.VerificationToken;
import com.example.redit_clone.repository.UserRepository;
import com.example.redit_clone.repository.VerificationTokenRepository;
import com.example.redit_clone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

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
    private void fetchUserAndEnable(VerificationToken verificationToken) {

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
        return new AuthenticationResponse(token, loginRequest.getUsername());

    }
}
