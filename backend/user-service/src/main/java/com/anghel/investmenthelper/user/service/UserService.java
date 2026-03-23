package com.anghel.investmenthelper.user.service;

import com.anghel.investmenthelper.user.model.dto.UserDTO;
import com.anghel.investmenthelper.user.model.dto.UserInputDTO;
import com.anghel.investmenthelper.user.model.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(UserInputDTO userInputDTO);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    void deleteUserById(Long id);

    UserDTO updateUserById(UserUpdateDTO userUpdateDTO, Long id);
}
