package com.anghel.investmenthelper.user.service.user;

import com.anghel.investmenthelper.user.model.dto.user.UserDTO;
import com.anghel.investmenthelper.user.model.dto.user.UserInputDTO;
import com.anghel.investmenthelper.user.model.dto.user.UserUpdateDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(UserInputDTO userInputDTO);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    void deleteUserById(Long id);

    UserDTO updateUserById(UserUpdateDTO userUpdateDTO, Long id);
}
