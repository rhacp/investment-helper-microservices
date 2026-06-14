package com.anghel.investmenthelper.auth.service.auth_user;

import com.anghel.investmenthelper.auth.model.dto.auth_user.*;

public interface AuthUserService {

    RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    AuthUserResponseDTO updateUserRole(Long id, RoleDTO roleDTO);

    void disableAuthUser(Long authUserId);
}
