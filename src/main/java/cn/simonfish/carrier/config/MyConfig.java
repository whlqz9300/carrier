package cn.simonfish.carrier.config;

import cn.simonfish.carrier.common.freemarker.DictMappingDirective;
import cn.simonfish.carrier.controller.AlipayController;
import cn.simonfish.carrier.controller.api.OrderApi;
import cn.simonfish.carrier.controller.api.UserApi;
import cn.simonfish.carrier.controller.view.IndexView;
import cn.simonfish.carrier.controller.view.OrderView;
import cn.simonfish.carrier.controller.view.SubscribeView;
import cn.simonfish.carrier.controller.view.UserView;
import cn.simonfish.carrier.data.DataCrawling;
import cn.simonfish.carrier.filters.LoginInterceptor;
import cn.simonfish.carrier.model.SysOrder;
import cn.simonfish.carrier.model.SysOrderItem;
import cn.simonfish.carrier.model.SysSubscribe;
import cn.simonfish.carrier.model.SysUser;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.FreeMarkerRender;
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

        FreeMarkerRender.getConfiguration().setSharedVariable("mapping",new DictMappingDirective());
    }

    @Override
    public void configRoute(Routes routes) {
        routes.add("/api/user", UserApi.class,"");
        routes.add("/api/order", OrderApi.class,"");

        routes.add("/view/user", UserView.class,"");
        routes.add("/view/order", OrderView.class,"");
        routes.add("/view/subscribe", SubscribeView.class,"");

        routes.add("/alipay", AlipayController.class,"");
        routes.add("/", IndexView.class,"");
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
        arp.addMapping("sys_order", SysOrder.class);
        arp.addMapping("sys_order_item", SysOrderItem.class);
        arp.addMapping("sys_subscribe", SysSubscribe.class);

        plugins.add(arp);


        //插件,初始化定时器抓取数据
        DataCrawling.initialize();
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        interceptors.addGlobalActionInterceptor(new LoginInterceptor());
    }

    @Override
    public void configHandler(Handlers handlers) {

    }
}
