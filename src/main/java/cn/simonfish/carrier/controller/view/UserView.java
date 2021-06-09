package cn.simonfish.carrier.controller.view;

import cn.simonfish.carrier.common.LoginInfo;
import cn.simonfish.carrier.controller.BaseController;
import cn.simonfish.carrier.model.SysUser;

/**
 * Created by simon on 2017/9/10.
 */
public class UserView extends BaseController {


    public void profile(){
        SysUser user = SysUser.dao.findById(getLoginInfo().getUserId());
        setAttr("user",user);
        renderFreeMarker("template/user/profile.html");
    }

    public void login(){
        setAttr("isLogin",(getLoginInfo()!=null));
        renderFreeMarker("template/user/login.html");
    }

    public void logout(){
        setSessionAttr(LoginInfo.SESSION_LOGIN,null);
        setSessionAttr(LoginInfo.SESSION_LAST_URL,null);
        redirect("/index.html");
    }

    public void register(){
        renderFreeMarker("template/user/register.html");
    }
}
