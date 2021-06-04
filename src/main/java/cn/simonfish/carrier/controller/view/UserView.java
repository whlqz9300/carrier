package cn.simonfish.carrier.controller.view;

import com.jfinal.core.Controller;

/**
 * Created by simon on 2017/9/10.
 */
public class UserView extends Controller {


    public void login(){
        renderFreeMarker("login.html");
    }

    public void register(){
        renderFreeMarker("register.html");
    }
}
