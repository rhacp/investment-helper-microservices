package com.anghel.investmenthelper.user.service.user;

import com.anghel.investmenthelper.user.client.AuthServiceClient;
import com.anghel.investmenthelper.user.model.dto.user.UserDTO;
import com.anghel.investmenthelper.user.model.dto.user.UserInputDTO;
import com.anghel.investmenthelper.user.model.dto.user.UserUpdateDTO;
import com.anghel.investmenthelper.user.model.entity.User;
import com.anghel.investmenthelper.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final UserQueryService userQueryService;

    private final AuthServiceClient authServiceClient;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, UserQueryService userQueryService, AuthServiceClient authServiceClient) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userQueryService = userQueryService;
        this.authServiceClient = authServiceClient;
    }

    @Transactional
    @Override
    public UserDTO createUser(UserInputDTO userInputDTO) {
        User user = modelMapper.map(userInputDTO, User.class);

        user.setId(null);
        user.setAuthUserId(userInputDTO.getAuthUserId());
        User savedUser = userRepository.save(user);
        log.info("User created [id={}]", savedUser.getId());

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
    public UserDTO getUserByAuthUserId(Long authUserId) {
        User user = userQueryService.getValidUserByAuthUserId(authUserId);
        log.debug("User retrieved [id={}]", authUserId);

        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional
    @Override
    public void deactivateUserByAuthUserId(Long authUserId) {
        User user = userQueryService.getValidUserByAuthUserId(authUserId);

        authServiceClient.disableAuthUser(authUserId);
        user.setActive(false);

        log.info("User deactivated [id={}]", authUserId);
    }

    @Transactional
    @Override
    public UserDTO updateUserByAuthUserId(UserUpdateDTO userUpdateDTO, Long authUserId) {
        User user = userQueryService.getValidUserByAuthUserId(authUserId);

        updateUserFromDTO(user, userUpdateDTO);
        User savedUser = userRepository.save(user);
        log.info("User updated [id={}]", savedUser.getId());

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

        if (userUpdateDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        }
    }
}
