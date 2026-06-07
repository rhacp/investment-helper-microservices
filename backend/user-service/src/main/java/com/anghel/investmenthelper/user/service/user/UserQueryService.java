package com.anghel.investmenthelper.user.service.user;

import com.anghel.investmenthelper.user.model.entity.User;

public interface UserQueryService {

    User getValidUserByAuthUserId(Long authUserId);
}
