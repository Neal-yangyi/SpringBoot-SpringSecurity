package com.riyeyuedu.springsecurity.Service.user;

import com.riyeyuedu.springsecurity.entity.user.User;

/**
 * @author NealCaffrey
 */
public interface UserService {
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return user
     */
    User findByUserName(String username);
}
