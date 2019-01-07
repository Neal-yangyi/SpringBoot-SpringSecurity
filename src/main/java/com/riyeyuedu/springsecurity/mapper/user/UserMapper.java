package com.riyeyuedu.springsecurity.mapper.user;

import com.riyeyuedu.springsecurity.entity.user.User;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author NealCaffrey
 */
public interface UserMapper extends Mapper<User> {
    User selectByUserName(String username);
}