package com.cwp.cloud.bean.user;

import com.cwp.cloud.utils.FormatClassInfo;

/**
 * 用户管理表实体类
 * Created by chen_wp on 2019-10-11.
 */
public class User {
    private int id;
    private String username; // 用户名
    private String password; // 密码
    private String name; // 姓名
    private String phoneNumber; // 手机号
    private String roles; // 角色名称
    private String area; // 区域
    private String addTime; // 添加时间
    private String workStatus; // 坐班状态
    private Integer rolesId;// 角色id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public Integer getRolesId() {
        return rolesId;
    }

    public void setRolesId(Integer rolesId) {
        this.rolesId = rolesId;
    }

    @Override
    public String toString() {
        return FormatClassInfo.format(this);
    }
}
