package com.cwp.cloud.mapper;

import com.cwp.cloud.bean.user.Roles;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolesMapper {
    List<Roles> getAllRoles();

    List<Roles> getRolesList(@Param("rolesName") String rolesName);

    int addRoles(Roles roles);
}
