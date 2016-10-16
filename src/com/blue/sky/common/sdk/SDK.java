package com.blue.sky.common.sdk;

import android.content.Context;
import com.blue.sky.common.utils.NetWorkHelper;
import com.wandoujia.ads.sdk.Ads;


/**
 * Created by sky on 2014/11/1.
 */
public class SDK {

    public static final String WDJ_AD_APP_ID = "100009963";
    public static final String WDJ_AD_APP_Secret_Key = "c77dcdba55541b8fc8f916b82d2d4d97";
    public static final String WDJ_AD_APP_LIST = "d48df11a3b66b12093d70706f1232e7f";
    public static final String WDJ_AD_APP_LIST_GAME = "91954f9cd6ab11affce44966176ea5fd";
    public static final String WDJ_AD_APP_LIST_APP = "3c0a7405fc375e35ddcd1a86c9cf8aed";

    public static void initWDJSDK(Context context) {
        try {
            if (NetWorkHelper.isConnect(context)) {
                Ads.init(context, SDK.WDJ_AD_APP_ID, SDK.WDJ_AD_APP_Secret_Key);
            }
        } catch (Exception e) {

        }
    }
}
