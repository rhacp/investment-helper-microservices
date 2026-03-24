package com.anghel.investmenthelper.user.service;

import com.anghel.investmenthelper.user.model.dto.UserDTO;
import com.anghel.investmenthelper.user.model.dto.UserInputDTO;
import com.anghel.investmenthelper.user.model.dto.UserUpdateDTO;
import com.anghel.investmenthelper.user.model.entity.User;
import com.anghel.investmenthelper.user.repository.UserRepository;
import com.anghel.investmenthelper.user.util.enumeration.Role;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserServiceValidator userServiceValidator;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserServiceValidator userServiceValidator) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userServiceValidator = userServiceValidator;
    }

    @Transactional
    @Override
    public UserDTO createUser(UserInputDTO userInputDTO) {
        userServiceValidator.checkIfUserExists(userInputDTO);

        User user = modelMapper.map(userInputDTO, User.class);
        user.setPasswordHash(passwordEncoder.encode(userInputDTO.getPassword()));
        user.setCreationDate(LocalDate.now());
        user.setCreationTime(LocalTime.now());
        updateUserRole(user, userInputDTO.getRole());

        User savedUser = userRepository.save(user);
        log.info("User created [id={}, email={}]", savedUser.getId(), savedUser.getEmail());

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.debug("Retrieved all users [count={}]", users.size());

        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userServiceValidator.getValidUser(id);
        log.debug("User retrieved [id={}]", id);

        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        userServiceValidator.getValidUser(id);
        userRepository.deleteById(id);
        log.info("User deleted [id={}]", id);
    }

    @Transactional
    @Override
    public UserDTO updateUserById(UserUpdateDTO userUpdateDTO, Long id) {
        User user = userServiceValidator.getValidUser(id);
        userServiceValidator.checkIfEmailBelongsToAnotherUser(userUpdateDTO.getEmail(), id);

        updateUserFromDTO(user, userUpdateDTO);
        User savedUser = userRepository.save(user);
        log.info("User updated [id={}, email={}]", savedUser.getId(), savedUser.getEmail());

        return modelMapper.map(savedUser, UserDTO.class);
    }

    private void updateUserFromDTO(User user,
                                   UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getFirstName() != null) {
            user.setFirstName(userUpdateDTO.getFirstName());
        }

        if (userUpdateDTO.getLastName() != null) {
            user.setLastName(userUpdateDTO.getLastName());
        }

        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }

        if (userUpdateDTO.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        if (userUpdateDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        }

        if (userUpdateDTO.getRole() != null) {
            updateUserRole(user, userUpdateDTO.getRole());
        }
    }

    private void updateUserRole(User user, String role) {
        if (role == null) {
            return;
        }

        switch (role.toUpperCase()) {
            case "ROLE_ADMIN" -> user.setRole(Role.ROLE_ADMIN);
            case "ROLE_USER" -> user.setRole(Role.ROLE_USER);
        }
    }
}
