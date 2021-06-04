package cn.simonfish.carrier.common;

/**
 * Created by simon on 2017/9/10.
 */
public class Result {


    private String message ;

    private int code;

    private Object data;

    public Result(int code ,String message){
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return code == 200;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
