package cn.simonfish.carrier.controller.view;

import cn.simonfish.carrier.common.Dict;
import cn.simonfish.carrier.common.LoginInfo;
import cn.simonfish.carrier.controller.BaseController;
import cn.simonfish.carrier.data.CoinInfo;
import cn.simonfish.carrier.data.DataCrawling;
import cn.simonfish.carrier.data.Site;
import cn.simonfish.carrier.model.SysSubscribe;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.*;

/**
 * Created by simon on 2017/9/10.
 */
public class SubscribeView extends BaseController{


    public void list(){
        prepare_list_data();
        renderFreeMarker("template/subscribe/list.html");
    }

    /**
     * 准备订阅列表的数据
     */
    private void prepare_list_data(){
        LoginInfo loginInfo = getLoginInfo();
        List<Record> list = Db.find("select * from sys_subscribe o where o.user_id=? order by modify_time desc", loginInfo.getUserId());
        List<Record> currList = new ArrayList<>();
        List<Record> expireList = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Record record:list) {
            if(record.getDate("expire_time").getTime()>now){
                currList.add(record);
            }else{
                expireList.add(record);
            }
        }
        setAttr("currList",currList);
        setAttr("expireList",expireList);
    }



    public void over_view(){
        LoginInfo info = getLoginInfo();
        List list = SysSubscribe.dao.find("select * from sys_subscribe s where s.user_id=? and s.expire_time>?",info.getUserId(),new Date());
        setAttr("projects",list);
        renderFreeMarker("template/subscribe/over_view.html");
    }

    public void data(){
        String sites = getPara("sites");
        LoginInfo info = getLoginInfo();
        if(info.hasSiteAuth(sites)){
            String[] siteArr = sites.split(",");
            if(siteArr.length!=2){
                renderHtml("非法请求参数:"+sites);
                return;
            }
            siteArr[0] = siteArr[0].replace("#","");
            Site site1 = DataCrawling.getSite(siteArr[0]);
            Site site2 = DataCrawling.getSite(siteArr[1]);

            if(site1 == null){
                renderHtml("对不起，没有找到"+siteArr[0]+"网站的数据。");
                return;
            }
            if(site2 == null){
                renderHtml("对不起，没有找到"+siteArr[1]+"网站的数据。");
                return;
            }

            Map<String,CoinInfo[]> coins = calSite(site1,site2);
            setAttr("site1",siteArr[0]);
            setAttr("site2",siteArr[1]);
            setAttr("coins",coins);
            renderFreeMarker("template/subscribe/data.html");
        }else {
            renderHtml("对不起,您还未订阅此项目:"+sites);
        }

    }

    private Map<String,CoinInfo[]> calSite(Site site1, Site site2){
        Map<String,CoinInfo[]> map = new HashMap();
        Set<String> coin1NameSet = site1.keySet();
        Set<String> coin2NameSet = site2.keySet();
        Set<String> names = new HashSet<>();
        names.addAll(coin1NameSet);
        names.retainAll(coin2NameSet);
        double a2b = 0 ,b2a = 0;
        for (String cname : names    ) {
            CoinInfo coin1 = site1.get(cname);
            CoinInfo coin2 = site2.get(cname);
            if(coin1.getAskprice()>0.00000001){
                a2b = Math.max(a2b,coin2.getBidprice()/coin1.getAskprice());
            }
            if(coin2.getAskprice()>0.00000001){
                b2a = Math.max(b2a,coin1.getBidprice()/coin2.getAskprice());
            }
            map.put(cname,new CoinInfo[]{coin1,coin2});
        }
        setAttr("maxA2B",a2b);
        setAttr("maxB2A",b2a);
        setAttr("maxProfit",a2b*b2a);//最大收益
        return map;
    }

    public void select(){
        Object sites = Dict.get("site");
        List<Record> records = Db.find("select WEBSITE from SETTING_WEBSITE where ENABLE=1");
        setAttr("sites",catchField(records,"WEBSITE"));
        setAttr("combos",Dict.get("combo"));
        prepare_list_data();
        renderFreeMarker("template/subscribe/select.html");
    }

    private List catchField(List<Record> records,String field){
        List list = new ArrayList();
        for (Record record:records           ) {
            list.add( record.get(field));
        }
        return list;
    }

}
