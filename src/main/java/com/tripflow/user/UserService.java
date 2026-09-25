package com.tripflow.user;

import com.tripflow.user.dto.CreateUserRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(CreateUserRequest request) {
        User user = new User(
                request.username(),
                request.email()
        );

        return userRepository.save(user);
    }
}