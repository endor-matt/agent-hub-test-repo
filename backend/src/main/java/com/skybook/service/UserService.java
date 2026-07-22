package com.skybook.service;

import com.skybook.domain.AuditActions;
import com.skybook.domain.User;
import com.skybook.dto.user.ChangePasswordRequest;
import com.skybook.dto.user.UpdateProfileRequest;
import com.skybook.dto.user.UserResponse;
import com.skybook.exception.BadRequestException;
import com.skybook.exception.ResourceNotFoundException;
import com.skybook.repository.UserRepository;
import com.skybook.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapperService mapperService;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public UserResponse me(UserPrincipal principal) {
        return mapperService.toUserResponse(requireUser(principal.getId()));
    }

    @Transactional
    public UserResponse updateProfile(UserPrincipal principal, UpdateProfileRequest request, HttpServletRequest httpRequest) {
        long start = System.currentTimeMillis();
        User user = requireUser(principal.getId());
        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        auditService.record(
                AuditActions.PROFILE_UPDATE,
                "/api/v1/users/me",
                "PUT",
                200,
                (int) (System.currentTimeMillis() - start),
                "{\"fields\":[\"firstName\",\"lastName\",\"email\",\"phone\"]}",
                httpRequest
        );
        return mapperService.toUserResponse(user);
    }

    @Transactional
    public void changePassword(UserPrincipal principal, ChangePasswordRequest request, HttpServletRequest httpRequest) {
        long start = System.currentTimeMillis();
        User user = requireUser(principal.getId());
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        auditService.record(
                AuditActions.PASSWORD_CHANGE,
                "/api/v1/users/me/password",
                "POST",
                200,
                (int) (System.currentTimeMillis() - start),
                "{\"result\":\"success\"}",
                httpRequest
        );
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(mapperService::toUserResponse).toList();
    }

    private User requireUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
