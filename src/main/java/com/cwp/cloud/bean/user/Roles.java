package com.cwp.cloud.bean.user;

/**
 * 角色管理表实体类
 * Created by chen_wp on 2019-10-11.
 */
public class Roles {
    private int id;
    private String rolesName; // 角色名

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRolesName() {
        return rolesName;
    }

    public void setRolesName(String rolesName) {
        this.rolesName = rolesName;
    }
}
