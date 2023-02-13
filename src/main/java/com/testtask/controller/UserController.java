package com.testtask.controller;

import com.testtask.Entity.Quote;
import com.testtask.Entity.QuoteRating;
import com.testtask.Entity.User;
import com.testtask.Service.QuoteRatingService;
import com.testtask.Service.UserService;
import com.testtask.model.CreateUserModel;
import com.testtask.model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController()
@Slf4j
@RequestMapping(value = "/api/v1/users")
public class UserController {
    private final UserService userService;
    private final QuoteRatingService quoteRatingService;

    public UserController(UserService userService, QuoteRatingService quoteRatingService) {
        this.userService = userService;
        this.quoteRatingService = quoteRatingService;
    }


    @GetMapping
    public ResponseEntity findAll() {
        try {
            return ResponseEntity.ok(userService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody CreateUserModel userModel) {
        try {
            User user = User.builder()
                    .username(userModel.getUsername())
                    .password(userModel.getPassword())
                    .roles("ROLE_USER")
                    .createTime(Instant.now())
                    .email(userModel.getEmail())
                    .build();
            UserModel createdUser = userService.save(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity update(@PathVariable Long id, @Valid @RequestBody UserModel userModel) {
        try {
            User user = User.builder()
                    .id(id)
                    .username(userModel.getUsername())
                    .createTime(userModel.getCreateTime())
                    .build();
            UserModel updatedUser = userService.update(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok().body("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }


    @GetMapping("/{id}/lastvotes")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity lastVotes(@PathVariable Long id) {
        try {
            List<QuoteRating> quoteList = quoteRatingService.getLastVotes(id);
            return ResponseEntity.ok(quoteList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }
}
