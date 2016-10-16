package com.blue.sky.common.utils;

/**
 * Created by Administrator on 2014/11/22.
 */
public class EnumUtil {

    public enum Login
    {
        Email("电子邮件"),
        Mobile("手机"),
        QQ("QQ"),
        Sina("新浪微博");
        // 成员变量
        private String name;

        // 构造方法
        private Login(String name) {
            this.name = name;
        }
    }
}
