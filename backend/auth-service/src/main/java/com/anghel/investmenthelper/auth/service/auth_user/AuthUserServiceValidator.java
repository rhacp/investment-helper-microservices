package com.anghel.investmenthelper.auth.service.auth_user;

import com.anghel.investmenthelper.auth.model.dto.auth_user.RegisterRequestDTO;
import com.anghel.investmenthelper.auth.model.entity.AuthUser;

public interface AuthUserServiceValidator {

    void checkIfAuthUserExists(RegisterRequestDTO registerRequestDTO);

    void checkIfEmailBelongsToAnotherUser(String email, Long currentUserId);

    void checkIfAuthUserEnabled(AuthUser authUser);
}
