package com.testtask.Service;

import com.testtask.Entity.User;
import com.testtask.exception.EmailAlreadyExistsException;
import com.testtask.exception.UsernameAlreadyExistsException;
import com.testtask.model.UserModel;
import com.testtask.repo.UserRepo;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<UserModel> findAll() {
        List<User> users = userRepo.findAll();

        return users.stream().map(this::toModel).collect(Collectors.toList());
    }

    public UserModel findById(Long id) {
        return toModel(userRepo.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public UserModel save(User user) {
        if (isUsernameExists(user.getUsername()))
            throw new UsernameAlreadyExistsException("username already exists");
        if (isEmailExists(user.getEmail()))
            throw new EmailAlreadyExistsException("Email already exists");

        return toModel(userRepo.save(user));
    }

    public UserModel update(Long id, User user) {
        User existingUser = userRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("User not found"));

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setCreateTime(user.getCreateTime());

        userRepo.save(existingUser);

        return toModel(existingUser);
    }

    public void deleteById(Long id) {
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

    private boolean isEmailExists(String email) {
        return userRepo.existsByEmail(email);
    }

}
