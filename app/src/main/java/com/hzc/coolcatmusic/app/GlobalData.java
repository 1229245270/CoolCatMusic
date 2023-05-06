package com.hzc.coolcatmusic.app;

public class GlobalData {


    public static Boolean isDebug = true;

    public static String URL(){

        String TEST_URL = "http://192.168.212.19:8081/";
        String SIM_URL = "http://10.0.2.2:8081/";
        String RELEASE_URL = "http://www.huangzhichao.xyz:8081/";
        if(isDebug){
            return TEST_URL;
        }else{
            return RELEASE_URL;
        }
    }
}
