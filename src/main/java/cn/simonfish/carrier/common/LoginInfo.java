package cn.simonfish.carrier.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by simon on 2017/9/10.
 */
public class LoginInfo {

    public static final String SESSION_LOGIN = "_login_info";
    public static final String SESSION_LAST_URL = "_last_url";

    private String phone;

    private int userId;

    private Set<String> sites = new HashSet<>();

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public Set<String> getSites() {
        return sites;
    }

    public void setSites(Set<String> sites) {
        this.sites = sites;
    }

    public void addSite(String site){
        this.sites.add(site);
    }

    /**
     * 判断是否有订阅权限
     * @param site
     * @return
     */
    public boolean hasSiteAuth(String site){
        if(sites==null){
            return false;
        }else{
            return sites.contains(site);
        }
    }
}
