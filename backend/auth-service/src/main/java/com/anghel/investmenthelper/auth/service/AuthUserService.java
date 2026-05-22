package com.anghel.investmenthelper.auth.service;

import com.anghel.investmenthelper.auth.model.dto.*;

public interface AuthUserService {

    RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    AuthUserResponseDTO updateUserRole(Long id, RoleDTO roleDTO);
}
