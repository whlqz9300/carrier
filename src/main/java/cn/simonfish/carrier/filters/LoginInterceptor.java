package cn.simonfish.carrier.filters;

import cn.simonfish.carrier.common.LoginInfo;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by simon on 2017/9/10.
 */
public class LoginInterceptor implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Set<String> escapeKeys = new HashSet(Arrays.asList(
            "/view/user/login",
            "/view/user/register"
            ));

    @Override
    public void intercept(Invocation invocation) {
        String actionKey = invocation.getActionKey();
        if(actionKey.startsWith("/view/") && !escapeKeys.contains(actionKey)){
            Controller controller = invocation.getController();
            LoginInfo loginInfo = (LoginInfo)controller.getSessionAttr(LoginInfo.SESSION_LOGIN);
            //如果还没有登录,就跳转到登录页面
            if(loginInfo == null){
                controller.setSessionAttr(LoginInfo.SESSION_LAST_URL,invocation.getActionKey());
                logger.info("未登录，跳转到登录页面。原页面为:{}",invocation.getActionKey());
                controller.redirect("/view/user/login");
                return;
            }
        }
        invocation.invoke();
    }
}
