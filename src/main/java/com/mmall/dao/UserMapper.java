package com.mmall.dao;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //查询用户名，看此用户名是否存在
    int checkUsername(String username);

    //查询邮箱，看此邮箱是否存在
    int checkEmail(String email);

    //用户登陆
    //@Param为传入的参数起名字，这样在Dao层可以获得数据
    User selectLogin(@Param("username") String username, @Param("password") String password);//mybatis传递多个参数时要用Param注解

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew") String passwordNew);

    int checkPassword(@Param(value = "password") String password, @Param("userId") Integer userId);

    int checkEmailByUserId(@Param(value = "email") String email, @Param("userId") Integer userId);



}