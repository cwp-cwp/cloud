package com.cwp.cloud.api;

import com.cwp.cloud.bean.user.Roles;
import com.cwp.cloud.bean.user.User;
import com.cwp.cloud.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户操作接口类
 * Created by chen_wp on 2019-10-11.
 */
@Component
public class UserApi {

    private final UserService userService;

    @Autowired
    public UserApi(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回登陆的用户对象
     */
    public User login(String username, String password) {
        return this.userService.login(username, password);
    }

    public User login(String username) {
        return this.userService.login(username);
    }

    /**
     * 添加用户
     *
     * @param user 用户对象
     * @return true or false
     */
    public boolean addUser(User user) {
        return this.userService.addUser(user);
    }

    /**
     * 修改用户
     *
     * @param user 用户对象
     * @return true or false
     */
    public boolean modifyUser(User user) {
        return this.userService.modifyUser(user);
    }

    /**
     * 分页获取用户列表，兼并各个查询
     *
     * @param username 用户名（为空查全部）
     * @param phoneNumber 手机号（为空查全部）
     * @param page 页码
     * @param pageSize 每页显示页数
     * @return PageInfo<User>
     */
    public PageInfo<User> getUserList(String username, String phoneNumber, Integer page, Integer pageSize) {
        return this.userService.getUserList(username, phoneNumber, page, pageSize);
    }

    /**
     * 获取全部角色列表
     *
     * @return List<Roles>
     */
    public List<Roles> getAllRolesList() {
        return this.userService.getRolesList();
    }

    /**
     * 获取角色列表
     *
     * @return PageInfo<Roles>
     */
    public PageInfo<Roles> getRolesList(String rolesName, int page, int pageSize) {
        return this.userService.getRolesList(rolesName, page, pageSize);
    }

    /**
     * 添加角色列表
     *
     * @return PageInfo<Roles>
     */
    public boolean addRoles(String rolesName) {
        return this.userService.addRoles(rolesName);
    }

    /**
     * 签到
     *
     * @return true or false
     */
    public boolean checkIn(Integer userId) {
        return this.userService.checkIn(userId);
    }

    /**
     * 签退
     *
     * @return true or false
     */
    public boolean checkOut(Integer userId) {
        return this.userService.checkOut(userId);
    }
}
