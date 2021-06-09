package cn.simonfish.carrier.controller.view;

import cn.simonfish.carrier.common.Dict;
import cn.simonfish.carrier.common.LoginInfo;
import cn.simonfish.carrier.controller.BaseController;
import cn.simonfish.carrier.kit.NumberKit;
import cn.simonfish.carrier.kit.PayKit;
import cn.simonfish.carrier.model.SysOrder;
import cn.simonfish.carrier.model.SysOrderItem;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.Map;

/**
 * Created by simon on 2017/9/10.
 */
public class OrderView extends BaseController {

    public void pay(){
        int id = getParaToInt("id");
        SysOrder order = SysOrder.dao.findById(id);
        if(order == null){
            renderHtml("找不到订单.");
        }else {
            setAttr("order",order);
            renderFreeMarker("template/order/pay.html");
        }
    }

    public void list(){
        LoginInfo loginInfo = getLoginInfo();
        List<Record> list = Db.find("select * from sys_order o where o.user_id=? order by create_time desc", loginInfo.getUserId());
        long now = System.currentTimeMillis();
        for (Record order : list   ) {
            order.set("is_timeout", (now - order.getDate("create_time").getTime())/(PropKit.getInt("order.pay.timeout")*1000) > 1);
        }
        setAttr("orders",list);
        renderFreeMarker("template/order/list.html");
    }

    public void preview(){
        String json = HttpKit.readData(getRequest());
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray itemArray = jsonObject.getJSONArray("items");
        Map combo = Dict.getByFilter("combo","code",jsonObject.getString("combo"));
        Double price = NumberKit.toDouble( combo.get("price") );
        combo.put("amount", price * itemArray.size());
        setAttr("combo",combo);
        setAttr("items",itemArray);
        renderFreeMarker("template/order/preview.html");
    }


    public void detail(){
        int orderId = getParaToInt("order_id");
        Record order = Db.findFirst("select * from sys_order o where o.user_id=? and id = ?",getLoginInfo().getUserId(),orderId);
        if(order == null){
            renderHtml("找不到此订单数据");
        }else{
            long now = System.currentTimeMillis();
            order.set("is_timeout", (now - order.getDate("create_time").getTime())/(PropKit.getInt("order.pay.timeout")*1000) > 1);
            List items = SysOrderItem.dao.find("select * from sys_order_item i where i.order_id = ?",orderId);
            setAttr("items",items);
            setAttr("order",order);
            renderFreeMarker("template/order/detail.html");
        }

    }

    public void to_pay(){
        int order_id = getParaToInt("order_id");
        SysOrder order = SysOrder.dao.findById(order_id);
        if(order!=null){
            try {
                String body = "订单:"+order.getStr("order_no")+";金额:"+order.getDouble("amount")+"元";
                String html = PayKit.submit(body,order.getStr("order_no"),order.getDouble("amount"),body);
                renderHtml(html);
            }catch (Throwable ex){
                renderHtml("系统异常");
            }
        }else{
            renderHtml("订单不存在");
        }
    }
}
