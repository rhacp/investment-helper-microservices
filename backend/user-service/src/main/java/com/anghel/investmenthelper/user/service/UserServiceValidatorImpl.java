package com.anghel.investmenthelper.user.service;

import com.anghel.investmenthelper.user.exception.ResourceAlreadyExistsException;
import com.anghel.investmenthelper.user.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.user.model.dto.UserInputDTO;
import com.anghel.investmenthelper.user.model.entity.User;
import com.anghel.investmenthelper.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceValidatorImpl implements UserServiceValidator {

    private final UserRepository userRepository;

    public UserServiceValidatorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void checkIfUserExists(UserInputDTO userInputDTO) {
        if(userRepository.findUserByEmail(userInputDTO.getEmail()) != null) {
            throw new ResourceAlreadyExistsException("User with email " + userInputDTO.getEmail() + " already exists");
        }
    }

    @Override
    public User getValidUser(Long id) {
        User user =  userRepository.findUserById(id);

        if (user == null) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }

        return user;
    }

    @Override
    public void checkIfEmailBelongsToAnotherUser(String email, Long currentUserId) {
        if (email == null) {
            return;
        }

        User existingUser = userRepository.findUserByEmail(email);

        if (existingUser != null && !existingUser.getId().equals(currentUserId)) {
            throw new ResourceAlreadyExistsException("User with email " + email + " already exists");
        }
    }
}
