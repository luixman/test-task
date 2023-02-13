package com.testtask.Service;

import com.testtask.Entity.User;
import com.testtask.exception.EmailAlreadyExistsException;
import com.testtask.exception.NoPermissionException;
import com.testtask.exception.UsernameAlreadyExistsException;
import com.testtask.model.UserModel;
import com.testtask.repo.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserModel> findAll() {
        List<User> users = userRepo.findAll();

        return users.stream().map(this::toModel).collect(Collectors.toList());
    }

    public UserModel findById(Long id) {
        return toModel(userRepo.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public UserModel save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (isUsernameExists(user.getUsername()))
            throw new UsernameAlreadyExistsException("username already exists");
        if (isEmailExists(user.getEmail()))
            throw new EmailAlreadyExistsException("Email already exists");

        return toModel(userRepo.save(user));
    }

    public UserModel update(Long id, User user) {
        User existingUser = userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!checkPermissions(existingUser))
            throw new NoPermissionException("no permissions");

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setCreateTime(user.getCreateTime());

        userRepo.save(existingUser);

        return toModel(existingUser);
    }

    public void deleteById(Long id) {
        User currentUser = userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!checkPermissions(currentUser))
            throw new NoPermissionException("no permissions");

        userRepo.deleteById(id);
    }

    public UserModel toModel(User user) {
        return UserModel.builder()
                .id(user.getId())
                .username(user.getUsername())
                .createTime(user.getCreateTime())
                .build();
    }

    public boolean isUsernameExists(String username) {
        return userRepo.existsByUsername(username);
    }

    public boolean isEmailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    private boolean checkPermissions(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getName().equals(user.getUsername()))
            return true;

        if (authentication.getAuthorities().equals("ROLE_ADMIN"))
            return true;

        return false;

    }

    protected User findUserByUsername(String username) {
        return userRepo.findUserByUsername(username).orElseThrow(()->new EntityNotFoundException("Entity not found"));
    }
}
