package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author : mengmuzi
 * create at:  2019-02-27  15:43
 * @description:
 */

//Service表示业务层
//创建iUserService对象，放入到容器中
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    //注入userMapper
    @Autowired
    private UserMapper userMapper;


    @Override
    //登陆
    public ServerResponse<User> login(String username, String password) {
        //先查询用户名，看用户名是否存在
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            //如果查不到的话，用户不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //密码登陆
        //密码需要改成MD5码
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        //通过用户名和密码进行查询
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        ////将密码设置为空
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);


        return ServerResponse.createBySuccess("登录成功",user);

    }

    //注册
    public ServerResponse<String> register(User user){

        //方法一：还可以复用checkValid的代码
        ServerResponse validResponse =  this.checkValid(user.getUsername(),Const.USERNAME);//同一个类中不同方法体中调用函数要加关键字this
        if(!validResponse.isSuccess()){//校验用户名
            return validResponse;
        }
        validResponse =  this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){ //校验邮箱
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);//设置用户角色
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword())); //MD5加密

        int resultCount = userMapper.insert(user);//传入数据库
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");

        /*
        //方法二：原始的判断
        int resultCount = userMapper.checkUsername(user.getUsername());
        if(resultCount > 0 ){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        resultCount = userMapper.checkEmail(user.getEmail());
        if(resultCount > 0 ) {
            return ServerResponse.createByErrorMessage("email已存在");
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5的加密

        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        resultCount = userMapper.insert(user);//传入数据库
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
        */
    }
    //校验
    public ServerResponse<String> checkValid(String str, String type){
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(type)){ //type不是空，才开始校验
            //开始校验
            if(Const.USERNAME.equals(type)){//判断用户名
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0 ){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){  //判断email
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0 ){
                    return ServerResponse.createByErrorMessage("email已存在");
                }

            }

        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    //忘记密码，查询问题
    public ServerResponse<String> selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        //先看下用户是否存在
        if(validResponse.isSuccess()){
            //用户名已存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //存在的话根据用户名查询问题
        String question = userMapper.selectQuestionByUsername(username);
        //当问题不为空的时候返回
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");

    }
    //检查用户回答的答案是否正确
    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        //说明问题及问题答案是这个用户的,并且是正确的
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0){
            //说明问题及问题答案是正确的
            //声明一个token
            String forgetToken = UUID.randomUUID().toString();//生成一个UUID字符串，不可重复的
            //token本地缓存，使用guava缓存实现
            TokenCache.setKey(TokenCache.TOKEN_PRFIX +username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    //忘记密码中的重置密码
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        //先判断是否携带了token
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        //校验一下用户名
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //从缓存中获取用户的token
        String token = TokenCache.getKey(TokenCache.TOKEN_PRFIX +username);
        //获取到看token是否为空
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        //比较token是否相等
        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            //更新密码
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            //如果个数大于1，则更新密码成功
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }else{
                return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
            }
        }
        return ServerResponse.createByErrorMessage("修改密码失败");

    }
    //登陆状态下的重置密码
    public ServerResponse<String> resetPassword(String passwordOld,String passordNew,User user){
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);//选择性的更新，没变化的就不动，变化了的就更新
        if(updateCount > 0){  //更新成功
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    //更新用户信息
    public ServerResponse<User> updateInformation(User user){
        // username是不能被更新的
        // email也要进行一个校验,校验新的email是不是已经存在,并且输入的email已经被其他用户用过了,则此email不能是我们当前的这个用户的.
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");

    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }

    // backend 校验是否是管理员
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
