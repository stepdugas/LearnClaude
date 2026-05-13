package com.learnclaudeai.app.controller;

import com.learnclaudeai.app.dto.UserDto;
import com.learnclaudeai.app.entity.User;
import com.learnclaudeai.app.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(UserDto.from(user));
    }

    @PutMapping("/notifications")
    @Transactional
    public ResponseEntity<?> updateNotifications(@AuthenticationPrincipal User user,
                                                  @RequestBody Map<String, Boolean> body) {
        if (user == null) return ResponseEntity.status(401).build();

        Boolean notify = body.get("notifyNewLessons");
        if (notify == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "notifyNewLessons is required"));
        }

        user.setNotifyNewLessons(notify);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("notifyNewLessons", user.isNotifyNewLessons()));
    }
}
