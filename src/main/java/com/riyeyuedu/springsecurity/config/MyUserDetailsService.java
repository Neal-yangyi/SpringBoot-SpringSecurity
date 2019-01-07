package com.riyeyuedu.springsecurity.config;

import com.riyeyuedu.springsecurity.Service.user.impl.UserServiceImpl;
import com.riyeyuedu.springsecurity.entity.user.Role;
import com.riyeyuedu.springsecurity.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author NealCaffrey
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserServiceImpl userService;

    @Autowired
    public MyUserDetailsService(UserServiceImpl userService) {
        this.userService = userService;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myUser = userService.findByUserName(username);
        if (myUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 根据用户名查找到对于的密码和权限
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Role role : myUser.getRoles()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getRole());
            grantedAuthorities.add(grantedAuthority);
        }
        return new org.springframework.security.core.userdetails.User(
                myUser.getUsername(),
                myUser.getPassword(),
                grantedAuthorities);
    }
}
