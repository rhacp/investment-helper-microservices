package com.anghel.investmenthelper.auth.service;

import com.anghel.investmenthelper.auth.model.dto.RegisterRequestDTO;
import com.anghel.investmenthelper.auth.model.entity.AuthUser;

public interface AuthUserServiceValidator {

    void checkIfAuthUserExists(RegisterRequestDTO registerRequestDTO);

    void checkIfEmailBelongsToAnotherUser(String email, Long currentUserId);

    void checkIfAuthUserEnabled(AuthUser authUser);
}
