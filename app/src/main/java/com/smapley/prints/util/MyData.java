package com.smapley.prints.util;

import android.text.format.Time;

/**
 * Created by smapley on 2015/5/20.
 */
public class MyData {
    public static final String SP_USER = "user";
    public static final String SP_TABLE = "table";
    private static final String BASE_URL = "http://120.25.208.188/dayin/";
    public static final String URL_UPDATEEDU = BASE_URL + "updateEdu.php";
    public static final String URL_ADDDATA = BASE_URL + "addData.php";
    public static final String URL_INDEX = BASE_URL + "xiazhu.php";
    public static final String URL_TUIMA = BASE_URL + "tuima.php";
    public static final String URL_GETJILU1 = BASE_URL + "getJilu1.php";
    public static final String URL_updateZt1 = BASE_URL + "updateZt1.php";
    public static final String URL_getSoudj = BASE_URL + "getSoudj.php";
    public static final String URL_updateZt2 = BASE_URL + "updateZt2.php";

    /**
     * 获取服务器加密码
     * key
     *
     * @return
     */
    public static int getKey() {
        int key = 0;
        key = 1 + (int) (Math.random() * 999);
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // 取得系统时间。
        int date = t.monthDay;
        return key * 789 * date;
    }



}
