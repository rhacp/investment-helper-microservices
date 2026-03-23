package com.anghel.investmenthelper.user.repository;

import com.anghel.investmenthelper.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findUserByEmail(String email);

    User findUserById(Long id);

    List<User> findAllUsersByEmail(String email);
}
