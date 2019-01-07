package com.riyeyuedu.springsecurity.entity.user;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author THJ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "user")
public class User {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色
     */
    @Transient
    private List<Role> roles;
}