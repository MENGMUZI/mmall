package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author : mengmuzi
 * create at:  2019-03-07  23:08
 * @description:
 */


@Controller
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;


    /**
    *
    * @description: 添加收货地址
    * @Author: mengmuzi
    * @Date:  2019-03-07
    */

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping){//SpringMVC的对象绑定

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.add(user.getId(),shipping);

    }


    /**
    *
    * @description: 删除收货地址
    * @Author: mengmuzi
    * @Date:  2019-03-08
    */
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpSession session, Integer shippingId){//SpringMVC的对象绑定

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.del(user.getId(),shippingId);

    }


    /**
     *
     * @description: 更新收货地址
     * @Author: mengmuzi
     * @Date:  2019-03-08
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session,Shipping shipping){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.update(user.getId(),shipping);
    }


    /**
     *
     * @description: 查询收货地址
     * @Author: mengmuzi
     * @Date:  2019-03-08
     */
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session,Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }


    /**
     *
     * @description: 分页接口
     * @Author: mengmuzi
     * @Date:  2019-03-08
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value ="PageNum",defaultValue ="1") int pageNum,
                                         @RequestParam(value ="PageSize",defaultValue ="10")int pageSize,HttpSession session){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.list(user.getId(),pageNum,pageSize);

    }









}
