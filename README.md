# SpringBoot+SpringSecurity简易教程

### 序言
参考一些网上的资料整理出来的东西，有什么好的建议或批评，请务必issue下。<br/>
项目地址：https://github.com/Neal-yangyi/SpringBoot-SpringSecurity

### 准备工作
在开始本教程之前，请保证已经熟悉以下几点。<br/>
* Spring Boot 基本语法，至少要懂得Controller、RestController、Autowired等这些基本注释。
* MySql的基本语法
* thymeleaf的基本使用方法，本项目采用模板渲染的方式。
### 程序逻辑
1.我们POST用户名与密码到/login进行登入，在数据库中获取用户信息和权限。<br/>
2.之后用户访问每一个需要权限的网址请求都必须在WebSecurityConfig配置类中进行拦截。<br/>
3.后台会进行权限的校验，如果有误会报错
### 准备Maven文件
新建一个Maven工程，添加相关的dependencies。<br/>
```Java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.riyeyuedu.springsecurity</groupId>
    <artifactId>security</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>security</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/tk.mybatis/mapper-spring-boot-starter -->
        <dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper-spring-boot-starter</artifactId>
            <version>2.0.4</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>


    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

### 构建简易的数据源
| id | username | password |
| --- | --- | --- |
| 1 | user | $10$f/hUEUjoW8oGxhsh1gkdHO/NfhKUb7jdQKCNdhlCB.qNKsFFm0Nie |

这个是一个简单的用户表，表中的密码是进过加密后的密码，真实密码为123456。

| id | role |
| --- | --- |
| 1 | ROLE_USER |

这是权限表。这里的role字段是SpringSecurity的一个大坑也是很多程序员要吐槽的一点，权限必须以ROLE_开头。

| id | user_id | role_id |
| --- | --- | --- |
| 1 | 1 | 1 |

这是用户和权限的关联表，没什么好说的。

### 之后再构建一个UserService来模拟数据库查询，并且把结果放到User实体类之中。
UserServiceImpl.java
```Java
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUserName(String username) {
        return userMapper.selectByUserName(username);
    }
}
```
### 对应的UserMapper
UserMapper.java
```Java
public interface UserMapper extends Mapper<User> {
    User selectByUserName(String username);
}
```
### 对应的XML文件
UserMapper.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.riyeyuedu.springsecurity.mapper.user.UserMapper">
  <resultMap id="BaseResultMap" type="com.riyeyuedu.springsecurity.entity.user.User">
    <!--
      WARNING - @mbg.generated
    -->
    <id column="id" jdbcType="INTEGER" property="id" />
    <result column="username" jdbcType="VARCHAR" property="username" />
    <result column="password" jdbcType="VARCHAR" property="password" />
  </resultMap>

  <resultMap id="RoleResultMap" type="com.riyeyuedu.springsecurity.entity.user.User">
    <!--
      WARNING - @mbg.generated
    -->
    <result column="id" jdbcType="INTEGER" property="id" />
    <result column="username" jdbcType="VARCHAR" property="username" />
      <result column="password" jdbcType="VARCHAR" property="password" />
    <collection property="roles" ofType="com.riyeyuedu.springsecurity.entity.user.Role">
      <id column="role_id" property="id"/>
      <result column="role_role" property="role"/>
    </collection>
  </resultMap>

    <select id="selectByUserName" resultMap="RoleResultMap">
    SELECT a.id, a.username, a.password,
      r.id AS role_id, r.role AS role_role
      FROM user a
      LEFT JOIN user_role ur ON ur.user_id = a.id
      LEFT JOIN role r ON ur.role_id = r.id
      WHERE a.username = #{username}
  </select>
</mapper>
```
### User和Role的实体类
User.java
```Java
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
```
Role.java
```Java
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
```
### 配置SpringSecurity
我们写一个继承WebSecurityConfigurerAdapter的配置类
```Java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private MyUserDetailsService myUserDetailsService;

    @Autowired
    public void setMyUserDetailsService(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    /**
     * 匹配 "/user" 及其以下所有路径，都需要 "USER" 权限
     * 登录地址为 "/login"，登录成功默认跳转到页面 "/user"
     * 退出登录的地址为 "/logout"，退出成功后跳转到页面 "/login"
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/user/**").hasRole("USER")
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/user")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login");
    }

    /**
     * 配置匹配用户密码规则
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 添加 UserDetailsService， 实现自定义登录校验
     */
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception{
        builder.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
```
在configure方法中按照自己的需求在拦截和开放URL。
### 写我们自定义的校验
```Java
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
```
### URL结构
| URL | 作用 |
| --- | --- |
| /login | 登入 |
| /logout | 登出 |
| /user | 查看登入后用户信息 |
### Controller
UserController.java
```Java
@Controller
public class UserController {

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal Principal principal, Model model){
        model.addAttribute("username", principal.getName());
        return "user/user";
    }

}
```
HomeController.java
```Java
@Controller
public class HomeController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

}
```
### 注意事项
UserMapper是继承了MyBatis的通用Mapper并且是在启动类中通过@MapperScan扫描注入到Spring的，大家可根据自己需要来操作。
### 总结
这里只是使用Spring Security最简单基本的一些操作，并没有一些骚操作，可以当作一个简单的demo看看
