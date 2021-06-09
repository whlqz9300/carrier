package cn.simonfish.carrier.controller.api;

import cn.simonfish.carrier.common.LoginInfo;
import cn.simonfish.carrier.common.Result;
import cn.simonfish.carrier.common.web.SessionMap;
import cn.simonfish.carrier.common.web.UserSessionMap;
import cn.simonfish.carrier.controller.BaseController;
import cn.simonfish.carrier.kit.EmptyKit;
import cn.simonfish.carrier.model.SysSubscribe;
import cn.simonfish.carrier.model.SysUser;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

import static cn.simonfish.carrier.model.SysUser.dao;

/**
 * Created by simon on 2017/9/10.
 */
public class UserApi extends BaseController {

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
        //初始化已授权的网站
        List<SysSubscribe> list = SysSubscribe.dao.find("select * from sys_subscribe s where s.user_id=? and s.expire_time>?",info.getUserId(),new Date());
        for (SysSubscribe ss: list             ) {
            info.addSite("#" + ss.getStr("site1") +","+ss.getStr("site2")  );
        }

        setSessionAttr(LoginInfo.SESSION_LOGIN,info);
        Result result = new Result(200,"成功");
        String url = getSessionAttr(LoginInfo.SESSION_LAST_URL);
        result.setData( url==null||url.length()<1 ? "/index.html" : url);
        processSession(info);
        renderJson(result);
    }

    /**
     * 处理Session事项,如果登录了,取出该电话是否有其他Session,如果有,则置空
     * @param info
     */
    private void processSession(LoginInfo info){
        //通过用户,取了Session信息
        String sid = UserSessionMap.get(info.getPhone());
        HttpSession httpSession = SessionMap.get(sid);
        if(httpSession!=null){
            httpSession.setAttribute(LoginInfo.SESSION_LOGIN,null);
        }
        //保存此次登录的信息
        UserSessionMap.register(info.getPhone(),getSession().getId());
    }

    public void logout(){
        setSessionAttr(LoginInfo.SESSION_LOGIN,null);
        redirect("/index.html");
    }

    /**
     * 新密码
     */
    public void new_passwd(){
        String old_pwd = getPara("old_password");
        String new_pwd = getPara("new_password");
        String new_pwd2 = getPara("new_password2");
        if(EmptyKit.isEmpty(old_pwd)||EmptyKit.isEmpty(new_pwd)||EmptyKit.isEmpty(new_pwd2)){
            renderJson(new Result(90001,"旧密码错误"));
            return;
        }
        if(!StrKit.equals(new_pwd,new_pwd2)){
            renderJson(new Result(90001,"新密码不一致"));
            return;
        }
        old_pwd = HashKit.md5(HashKit.sha256(old_pwd));
        SysUser user = SysUser.dao.findFirst("select * from sys_user where phone=? and password=?",getLoginInfo().getPhone(),old_pwd);
        if(user == null){
            renderJson(new Result(90001,"帐号或密码错误"));
            return;
        }
        new_pwd  = HashKit.md5(HashKit.sha256(new_pwd));
        user.set("password",new_pwd);
        user.update();
        renderJson(new Result(200,"已修改新密码"));
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
