package com.anghel.investmenthelper.auth.service.auth_user;

import com.anghel.investmenthelper.auth.exception.ResourceAlreadyExistsException;
import com.anghel.investmenthelper.auth.exception.ResourceInactiveException;
import com.anghel.investmenthelper.auth.model.dto.auth_user.RegisterRequestDTO;
import com.anghel.investmenthelper.auth.model.entity.AuthUser;
import com.anghel.investmenthelper.auth.repository.AuthUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthUserServiceValidatorImpl implements AuthUserServiceValidator {

    private final AuthUserRepository authUserRepository;

    public AuthUserServiceValidatorImpl(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    public void checkIfAuthUserExists(RegisterRequestDTO registerRequestDTO) {
        if (authUserRepository.findAuthUserByEmail(registerRequestDTO.getEmail()) != null) {
            throw new ResourceAlreadyExistsException("User with email " + registerRequestDTO.getEmail() + " already exists");
        }
    }

    @Override
    public void checkIfEmailBelongsToAnotherUser(String email, Long currentUserId) {
        if (email == null) {
            return;
        }

        AuthUser existingUser = authUserRepository.findAuthUserByEmail(email);

        if (existingUser != null && !existingUser.getId().equals(currentUserId)) {
            throw new ResourceAlreadyExistsException("User with email " + email + " already exists");
        }
    }

    @Override
    public void checkIfAuthUserEnabled(AuthUser authUser) {
        if (!authUser.isEnabled()) {
            log.warn("Disabled user attempted login [email={}]", authUser.getEmail());
            throw new ResourceInactiveException("Invalid credentials");
        }
    }
}
