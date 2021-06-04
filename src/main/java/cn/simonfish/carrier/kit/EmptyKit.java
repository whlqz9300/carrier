package cn.simonfish.carrier.kit;

/**
 * Created by simon on 2017/9/10.
 */
public class EmptyKit {

    public static boolean isEmpty(Object obj){
        return obj == null || "".equals(obj);
    }
}
