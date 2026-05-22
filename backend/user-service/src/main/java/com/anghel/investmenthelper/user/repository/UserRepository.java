package com.anghel.investmenthelper.user.repository;

import com.anghel.investmenthelper.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findUserById(Long id);
}
