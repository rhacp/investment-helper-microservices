package com.anghel.investmenthelper.user.service;

import com.anghel.investmenthelper.user.model.dto.UserInputDTO;
import com.anghel.investmenthelper.user.model.entity.User;

public interface UserServiceValidator {

    void checkIfUserExists(UserInputDTO userInputDTO);

    User getValidUser(Long id);

    void checkIfEmailBelongsToAnotherUser(String email, Long currentUserId);
}
