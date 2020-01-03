package com.cwp.cloud.service;

import com.cwp.cloud.bean.user.Roles;
import com.cwp.cloud.bean.user.User;
import com.cwp.cloud.mapper.RolesMapper;
import com.cwp.cloud.mapper.UserMapper;
import com.cwp.cloud.utils.IdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作员业务逻辑类
 * Created by chen_wp on 2019-10-11.
 */
@Component
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RolesMapper rolesMapper;

    @Autowired
    private IdWorker idWorker;

    public User login(String username, String password) {
        return this.userMapper.login(username, password);
    }

    public User login(String username) {
        return this.userMapper.loginByUserName(username);
    }

    public boolean addUser(User user) {
        // 前端这边已经判空了，这里就不需要在判断了
        // 先判断用户名是否重复添加
        User u = userMapper.loginByUserName(user.getUsername());
        if (u != null) {
            // 日志
            return false;
        }
        // 用户姓名和手机号是否也需要判空?
        // TODO 设置默认的坐班状态是签退
        String id = String.valueOf(idWorker.nextId()).substring(10);
        user.setId(Integer.parseInt(id));
        int result = userMapper.addUser(user);
        return result > 0;
    }

    public boolean modifyUser(User user) {
        // 前端这边已经判空了，这里就不需要在判断了
        int result = userMapper.modifyUser(user);
        return result > 0;
    }

    public PageInfo<User> getUserList(String username, String phoneNumber, Integer page, Integer pageSize) {
        // 页数和页数码在前端判读
        PageHelper.startPage(page, pageSize);
        List<User> userList = userMapper.getUserList(username, phoneNumber);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return pageInfo;
    }

    public List<Roles> getRolesList() {
        return rolesMapper.getAllRoles();
    }

    public PageInfo<Roles> getRolesList(String rolesName, Integer page, Integer pageSize) {
        // 页数和页数码在前端判读
        PageHelper.startPage(page, pageSize);
        List<Roles> rolesList = rolesMapper.getRolesList(rolesName);
        PageInfo<Roles> pageInfo = new PageInfo<>(rolesList);
        return pageInfo;
    }

    public boolean addRoles(String rolesName) {
        // 先判断角色名是否存在
        List<Roles> rolesList = rolesMapper.getRolesList(rolesName);
        if (rolesList != null && rolesList.size() > 0) {
            return false;
        }
        Roles roles = new Roles();
        roles.setRolesName(rolesName);
        String id = String.valueOf(idWorker.nextId()).substring(10);
        roles.setId(Integer.parseInt(id));
        int result = rolesMapper.addRoles(roles);
        return result > 0;
    }

    public boolean checkIn(Integer userId) {
        // TODO 更新用户签到表
        int result = userMapper.checkIn(userId);
        return result > 0;
    }

    public boolean checkOut(Integer userId) {
        // TODO 更新用户签退表
        int result = userMapper.checkOut(userId);
        return result > 0;
    }

    public User getUserById(Integer userId) {
        return userMapper.loginById(userId);
    }
}
