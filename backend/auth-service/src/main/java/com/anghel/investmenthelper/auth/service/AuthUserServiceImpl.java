package com.anghel.investmenthelper.auth.service;

import com.anghel.investmenthelper.auth.exception.ResourceMismatchException;
import com.anghel.investmenthelper.auth.model.dto.*;
import com.anghel.investmenthelper.auth.model.entity.AuthUser;
import com.anghel.investmenthelper.auth.repository.AuthUserRepository;
import com.anghel.investmenthelper.auth.util.enumeration.Role;
import com.anghel.investmenthelper.auth.util.property.JwtProperties;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;

    private final AuthUserServiceValidator authUserServiceValidator;

    private final AuthUserQueryService authUserQueryService;

    private final JwtService jwtService;

    private final JwtProperties  jwtProperties;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    public AuthUserServiceImpl(AuthUserRepository authUserRepository, AuthUserServiceValidator authUserServiceValidator, AuthUserQueryService authUserQueryService, JwtService jwtService, JwtProperties jwtProperties, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.authUserServiceValidator = authUserServiceValidator;
        this.authUserQueryService = authUserQueryService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        authUserServiceValidator.checkIfAuthUserExists(registerRequestDTO);

        AuthUser authUser = modelMapper.map(registerRequestDTO, AuthUser.class);
        authUser.setPasswordHash(passwordEncoder.encode(registerRequestDTO.getPassword()));
        authUser.setRole(Role.ROLE_USER);
        authUser.setEnabled(true);

        AuthUser savedAuthUser = authUserRepository.save(authUser);
        log.info("User created [id={}, email={}]", savedAuthUser.getId(), savedAuthUser.getEmail());

        return modelMapper.map(savedAuthUser, RegisterResponseDTO.class);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        AuthUser authUser = authUserQueryService.getValidAuthUser(loginRequestDTO.getEmail());
        log.debug("User retrieved [email={}]", authUser.getEmail());

        authUserServiceValidator.checkIfAuthUserEnabled(authUser);
        checkIfPasswordCorrect(loginRequestDTO.getPassword(), authUser.getPasswordHash());

        String accessToken = jwtService.generateAccessToken(authUser);
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                accessToken,
                "Bearer",
                jwtProperties.getAccessTokenExpirationSeconds()
        );

        return loginResponseDTO;
    }

    @Transactional
    @Override
    public AuthUserResponseDTO updateUserRole(Long id, RoleDTO roleDTO) {
        AuthUser authUser = authUserQueryService.getValidAuthUser(id);
        log.debug("User retrieved [id={}]", authUser.getId());

        authUser.setRole(getRole(roleDTO.getRole()));
        AuthUser savedAuthUser = authUserRepository.save(authUser);
        log.info("User updated [id={}, email={}]", savedAuthUser.getId(), savedAuthUser.getEmail());

        return modelMapper.map(savedAuthUser, AuthUserResponseDTO.class);
    }

    private void checkIfPasswordCorrect(String receivedPasswordHash, String existingPasswordHash) {
        if (!passwordEncoder.matches(receivedPasswordHash, existingPasswordHash)) {
            throw new ResourceMismatchException("Invalid credentials");
        }
    }

    private Role getRole(String roleName) {
        return Role.valueOf(roleName);
    }
}
