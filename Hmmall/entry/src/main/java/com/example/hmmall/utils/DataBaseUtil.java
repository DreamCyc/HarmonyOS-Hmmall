package com.example.hmmall.utils;

import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.app.Context;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.resultset.ResultSet;
import ohos.utils.net.Uri;

public class DataBaseUtil {
    public static String getValue(String key,Context context){
        //初始化为null，若不存在则返回
        String value = null;
        //-----开始数据库查询过程------
        //访问本地数据库，是否存在用户信息，如果存在则加载Layout_ability_main,
        //如果不存在则加载login_ability
        DataAbilityHelper dataAbilityHelper = DataAbilityHelper.creator(context);
        //从config.json中获取访问路径
        //三个斜杠表示本地路径，在后边添加所建立的数据库名称
        Uri uri = Uri.parse("dataability:///com.example.hmmall.HmmallDataAbility/Hmmall_info");
        //查询字段
        String[] colums = {"id","key","value"};
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        //查询key=token
        predicates.equalTo("key","token");

        //执行查询
        try {
            ResultSet rs = dataAbilityHelper.query(uri, colums, predicates);
            if(rs.getRowCount() > 0){
                //跳转到第一行
                rs.goToFirstRow();
                //得到查询结果
                value = rs.getString(0);
            }
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }
        //-----数据库查询结束------
        return value;
    }
}
