package cn.simonfish.carrier.controller.api;

import cn.simonfish.carrier.common.LoginInfo;
import cn.simonfish.carrier.common.Result;
import cn.simonfish.carrier.kit.EmptyKit;
import cn.simonfish.carrier.model.SysUser;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;

import java.util.Date;
import java.util.List;

import static cn.simonfish.carrier.model.SysUser.dao;

/**
 * Created by simon on 2017/9/10.
 */
public class UserApi extends Controller {

    public void login(){
        String phone = getPara("phone");
        String passwd = getPara("password");
        if(EmptyKit.isEmpty(phone)){
            renderJson(new Result(90001,"电话不能为空"));
            return;
        }
        if(EmptyKit.isEmpty(passwd)){
            renderJson(new Result(90001,"密码不能为空"));
            return;
        }
        passwd = HashKit.md5(HashKit.sha256(passwd));
        SysUser user = SysUser.dao.findFirst("select * from sys_user where phone=? and password=?",phone,passwd);
        if(user == null){
            renderJson(new Result(90001,"帐号或密码错误"));
            return;
        }
        user.set("last_login_time",new Date());
        user.update();
        LoginInfo info = new LoginInfo();
        info.setPhone(phone);
        info.setUserId(user.getInt("id"));
        setSessionAttr(LoginInfo.SESSION_KEY,info);
        renderJson(new Result(200,"成功"));
    }

    /**
     *
     */
    public void register(){
        SysUser sysUser = getModel(SysUser.class,"",true);
        String passwd2 = getPara("password2");
        if(EmptyKit.isEmpty(sysUser.get("phone"))){
            renderJson(new Result(90001,"电话不能为空"));
            return;
        }
        if(EmptyKit.isEmpty(sysUser.get("password"))){
            renderJson(new Result(90001,"密码不能为空"));
            return;
        }
        if(!StrKit.equals(sysUser.getStr("password"),passwd2)){
            renderJson(new Result(90001,"密码不一致"));
            return;
        }
        List list = dao.find("select * from sys_user where phone=?",sysUser.get("phone"));
        if(list.isEmpty()==false){
            renderJson(new Result(90001,"电话已经被注册过了:"+sysUser.get("phone")));
            return;
        }
        sysUser.set("create_time",new Date());

        //没有写昵称,即使用手机号码
        if(EmptyKit.isEmpty(sysUser.get("nick_name"))){
            sysUser.set("nick_name", sysUser.get("phone"));
        }
        String sha = HashKit.sha256(sysUser.getStr("password"));
        sysUser.set("password", HashKit.md5(sha));
        sysUser.save();
        renderJson(new Result(200,"注册成功"));
    }


}
