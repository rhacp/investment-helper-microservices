package com.anghel.investmenthelper.user.service;

import com.anghel.investmenthelper.user.model.entity.User;

public interface UserQueryService {

    User getValidUser(Long id);
}
