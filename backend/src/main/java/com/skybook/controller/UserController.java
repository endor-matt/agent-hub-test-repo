package com.skybook.controller;

import com.skybook.dto.common.MessageResponse;
import com.skybook.dto.user.ChangePasswordRequest;
import com.skybook.dto.user.UpdateProfileRequest;
import com.skybook.dto.user.UserResponse;
import com.skybook.security.UserPrincipal;
import com.skybook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current profile")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.me(principal);
    }

    @PutMapping("/me")
    @Operation(summary = "Update profile")
    public UserResponse update(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest httpRequest
    ) {
        return userService.updateProfile(principal, request, httpRequest);
    }

    @PostMapping("/me/password")
    @Operation(summary = "Change password")
    public MessageResponse changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest httpRequest
    ) {
        userService.changePassword(principal, request, httpRequest);
        return MessageResponse.builder().message("Password updated").build();
    }
}
