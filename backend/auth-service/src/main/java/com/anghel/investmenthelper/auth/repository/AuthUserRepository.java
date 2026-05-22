package com.anghel.investmenthelper.auth.repository;

import com.anghel.investmenthelper.auth.model.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser,Long> {

    AuthUser findAuthUserByEmail(String username);

    AuthUser findAuthUsersById(Long id);
}
