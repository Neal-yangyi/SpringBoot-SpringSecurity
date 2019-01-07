package com.riyeyuedu.springsecurity.entity.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author THJ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "role")
public class Role {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 角色
     */
    private String role;
}