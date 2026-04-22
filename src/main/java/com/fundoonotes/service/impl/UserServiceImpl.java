package com.fundoonotes.service.impl;

import com.fundoonotes.dto.request.*;
import com.fundoonotes.dto.response.*;
import com.fundoonotes.entity.User;
import com.fundoonotes.exception.*;
import com.fundoonotes.repository.UserRepository;
import com.fundoonotes.service.UserService;
import com.fundoonotes.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fundoonotes.dto.request.LoginRequestDto;
import com.fundoonotes.dto.request.UserRegisterRequestDto;
import com.fundoonotes.dto.response.LoginResponseDto;
import com.fundoonotes.dto.response.UserResponseDto;
import com.fundoonotes.entity.User;
import com.fundoonotes.event.EmailEvent;
import com.fundoonotes.exception.InvalidCredentialsException;
import com.fundoonotes.exception.UserAlreadyExistsException;
import com.fundoonotes.exception.UserNotFoundException;
import com.fundoonotes.repository.UserRepository;
import com.fundoonotes.service.RabbitMQProducer;
import com.fundoonotes.service.RedisService;
import com.fundoonotes.service.UserService;
import com.fundoonotes.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;
    private final RedisService redisService;
    private final RabbitMQProducer rabbitMQProducer;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           TokenUtil tokenUtil,
                           RedisService redisService,
                           RabbitMQProducer rabbitMQProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtil = tokenUtil;
        this.redisService = redisService;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Override
    public UserResponseDto register(UserRegisterRequestDto requestDto) {
        log.info("Registering user with email: {}", requestDto.getEmail());

        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        // Save user to MySQL — same as before
        User user = new User();
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User saved = userRepository.save(user);
        log.info("User registered successfully with id: {}", saved.getId());

        // Generate a verification token and store it in Redis for 24 hours.
        // Key stored in Redis will look like: "verify:arjun@gmail.com" -> "token_value"
        // After 24 hours Redis automatically deletes it — no manual cleanup needed.
        String verificationToken = tokenUtil.generateToken(saved.getId());
        redisService.storeVerificationToken(saved.getEmail(), verificationToken);
        log.info("Verification token stored in Redis for: {}", saved.getEmail());

        // Publish welcome email event to RabbitMQ.
        // This line returns INSTANTLY — the email is processed in the background
        // by RabbitMQConsumer without making the user wait.
        EmailEvent emailEvent = new EmailEvent(
                saved.getEmail(),
                saved.getFirstName(),
                "Welcome to Fundoo Notes!",
                "Hi " + saved.getFirstName() + ", your account has been created successfully.",
                "WELCOME"
        );
        rabbitMQProducer.sendEmailEvent(emailEvent);

        return mapToUserResponse(saved);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {
        log.info("Login attempt for email: {}", requestDto.getEmail());

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = tokenUtil.generateToken(user.getId());
        log.info("Login successful for user id: {}", user.getId());

        return new LoginResponseDto(token, "Login successful");
    }

    @Override
    public UserResponseDto getProfile(String token) {
        Long userId = tokenUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    /*
     * Logout — blacklists the token in Redis until it naturally expires.
     * Even if someone has a valid token after logout, isTokenBlacklisted()
     * will return true and the request will be rejected.
     * 86400000ms = 24 hours, matching your JWT expiry in application.properties.
     */
    @Override
    public void logout(String token) {
        redisService.blacklistToken(token, Duration.ofMillis(86400000L));
        log.info("Token blacklisted in Redis successfully");
    }

    private UserResponseDto mapToUserResponse(User user) {
        return new UserResponseDto(user.getId(), user.getFirstName(),
                user.getLastName(), user.getEmail());
    }
}