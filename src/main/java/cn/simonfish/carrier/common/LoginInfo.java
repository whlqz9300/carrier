package cn.simonfish.carrier.common;

/**
 * Created by simon on 2017/9/10.
 */
public class LoginInfo {

    public static final String SESSION_KEY = "_login_info";

    private String phone;

    private int userId;

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
}
