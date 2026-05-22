package com.anghel.investmenthelper.auth.service;

import com.anghel.investmenthelper.auth.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.auth.model.entity.AuthUser;
import com.anghel.investmenthelper.auth.repository.AuthUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthUserQueryServiceImpl implements AuthUserQueryService {

    private final AuthUserRepository authUserRepository;

    public AuthUserQueryServiceImpl(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    public AuthUser getValidAuthUser(Long id) {
        AuthUser authUser = authUserRepository.findAuthUsersById(id);

        if (authUser == null) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }

        return authUser;
    }

    @Override
    public AuthUser getValidAuthUser(String email) {
        AuthUser authUser = authUserRepository.findAuthUserByEmail(email);

        if (authUser == null) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        return authUser;
    }
}
