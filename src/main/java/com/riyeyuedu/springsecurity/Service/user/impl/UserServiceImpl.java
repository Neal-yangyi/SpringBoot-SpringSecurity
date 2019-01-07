package com.riyeyuedu.springsecurity.Service.user.impl;

import com.riyeyuedu.springsecurity.Service.user.UserService;
import com.riyeyuedu.springsecurity.entity.user.User;
import com.riyeyuedu.springsecurity.mapper.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author NealCaffrey
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUserName(String username) {
        return userMapper.selectByUserName(username);
    }
}
