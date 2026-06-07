package com.anghel.investmenthelper.user.service.user;

import com.anghel.investmenthelper.user.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.user.model.entity.User;
import com.anghel.investmenthelper.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    public User getValidUser(Long id) {
//        User user =  userRepository.findUserById(id);
//
//        if (user == null) {
//            throw new ResourceNotFoundException("User with id " + id + " not found");
//        }
//
//        return user;
//    }

    @Override
    public User getValidUserByAuthUserId(Long authUserId) {
        User user = userRepository.findUserByAuthUserId(authUserId);
        if (user == null) {
            throw new ResourceNotFoundException("User with authUserId " + authUserId + " not found");
        }

        return user;
    }
}
