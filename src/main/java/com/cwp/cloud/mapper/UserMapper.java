package com.cwp.cloud.mapper;

import com.cwp.cloud.bean.user.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作员数据库操作接口
 * Created by chen_wp on 2019-10-11.
 */
@Repository
public interface UserMapper {

    int addUser(User user);

    User login(@Param("username") String username, @Param("password") String password);

    User loginByUserName(String username);

    User loginById(@Param("id") Integer id);

    int modifyUser(User user);

    List<User> getUserList(@Param("username") String username, @Param("phoneNumber") String phoneNumber);

    int checkIn(Integer userId);

    int checkOut(Integer userId);

    List<User> getCheckInUser();

    User getUserById(@Param("userId") Integer userId);
}
