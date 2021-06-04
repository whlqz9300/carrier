package cn.simonfish.carrier.config;

import cn.simonfish.carrier.controller.api.UserApi;
import cn.simonfish.carrier.filters.LoginInterceptor;
import cn.simonfish.carrier.model.SysUser;
import cn.simonfish.carrier.controller.view.OrderView;
import cn.simonfish.carrier.controller.view.SubscribeView;
import cn.simonfish.carrier.controller.view.UserView;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

/**
 * Created by simon on 2017/9/9.
 */
public class MyConfig extends JFinalConfig {
    @Override
    public void configConstant(Constants constants) {
        constants.setDevMode(true);
        constants.setError404View("/common/404.html");
        constants.setError500View("/common/500.html");
    }

    @Override
    public void configRoute(Routes routes) {
        routes.add("/api/user", UserApi.class,"");

        routes.add("/view/user", UserView.class,"");
        routes.add("/view/order", OrderView.class,"");
        routes.add("/view/subscribe", SubscribeView.class,"");
    }

    @Override
    public void configEngine(Engine engine) {

    }

    @Override
    public void configPlugin(Plugins plugins) {
        PropKit.use("conf.properties");
        DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("jdbc.url"),PropKit.get("jdbc.username"),PropKit.get("jdbc.password"));
        plugins.add(druidPlugin);
        ActiveRecordPlugin arp = new ActiveRecordPlugin("mysql",druidPlugin);

        arp.addMapping("sys_user", SysUser.class);

        plugins.add(arp);
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        interceptors.addGlobalActionInterceptor(new LoginInterceptor());
    }

    @Override
    public void configHandler(Handlers handlers) {

    }
}
