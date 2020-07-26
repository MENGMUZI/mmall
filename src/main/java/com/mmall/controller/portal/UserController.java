package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**

* @Author:   mengmuzi
* @Date:   2019-02-27
*/
@Controller  //表示在tomcat启动的时候，把这个类作为一个控制器加载到Spring的Bean工厂
@RequestMapping("/user/") //RequestMapping就是一个映射路径,就是请求地址前面加上/user
public class UserController {
    //按类型进行注入
    //将iUserService注入进来
    @Autowired
    private IUserService iUserService;

    /**
     * @description: 登陆功能
     * @Param username
     * @Param password
     * @Param session
     * @Author: mengmuzi
     * @Date: 2019-02-27
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)//POST将数据放入body里面。保证用户无法直接看到
    @ResponseBody//说明这个方法返回的东西会通过IO流的方式写入到浏览器。自动通过SpingMvc的json插件将返回值转换成json

    //访问地址为login.do 访问方式为POST
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        //service -> mybatis ->dao
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            //如果登陆成功，将用户放入到Session中
            session.setAttribute(Const.CURRENT_USER, response.getData());//用户已登录系统后你就在session中存储了一个用户信息对象
        }
        return response;
    }

    /**
     * @description: 退出登录功能
     * @Param session
     * @Author: mengmuzi
     * @Date: 2019-02-28
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)//GET会导致明文暴露
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * @description: 注册用户
     * @Param user
     * @Author: mengmuzi
     * @Date: 2019-02-28
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
    *
    * @description: 检验输入反馈是否合理
      * @Param str type
    * @Author: mengmuzi
    * @Date:  2019-02-28
    */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str,type);
    }

    /**
    *
    * @description: 获取用户登录信息
      * @Param session
    * @Author: mengmuzi
    * @Date:  2019-02-28
    */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    /**
    *
    * @description: 忘记密码根据用户名获取密码提示问题
      * @Param username
    * @Author: mengmuzi
    * @Date:  2019-02-28
    */

    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){//返回提示问题
        return  iUserService.selectQuestion(username);
    }


    /**
    *
    * @description: 检验问题答案
      * @Param username question answer
    * @Author: mengmuzi
    * @Date:  2019-02-28
    */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
    * 
    * @description: 忘记密码然后重置密码
      * @Param username, passwordNew, forgetToken
    * @Author: mengmuzi
    * @Date:  2019-03-01
    */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
    *
    * @description: 登录状态下的重置密码
      * @Param passwordOld, passwordNew, user
    * @Author: mengmuzi
    * @Date:  2019-03-01
    */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> resetPassword(HttpSession session ,String passwordOld ,String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户没有登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);

    }

    /**
    *
    * @description: 更新用户信息
      * @Param session , user
    * @Author: mengmuzi
    * @Date:  2019-03-01
    */

    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session, User user){
        User currentuser = (User)session.getAttribute(Const.CURRENT_USER);//取出当前用户
        if(currentuser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //更新的信息中是不包括username，userID
        user.setId(currentuser.getId());
        user.setUsername(currentuser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        //判断response若成功，然后更新session
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;

    }

    /**
    *
    * @description: 获取用户信息并加密;若没有登录进行一种强制登录
      * @Param session
    * @Author: mengmuzi
    * @Date:  2019-03-01
    */
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if( currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }









}
