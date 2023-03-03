package com.example.hmmall.slice;

import com.example.hmmall.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.utils.net.Uri;

//本页面实现注册功能
public class EnrollAbilitySlice extends AbilitySlice {
    //创建dataAbility帮助类，进行数据库的调用
    private DataAbilityHelper dataAbilityHelper;
    //设置全局变量
    //用该变量的值0/1作为查询数据表结果的反馈
    private int query_answer = 0;
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        //设置页面的布局为ability_enroll
        setUIContent(ResourceTable.Layout_ability_enroll);
        //创建函数内的帮助类
        dataAbilityHelper = DataAbilityHelper.creator(this);
        //调用页面布局中的注册按钮和三个输入框
        Button enroll_btn = (Button) findComponentById(ResourceTable.Id_enroll_btn);
        TextField user_id = (TextField) findComponentById(ResourceTable.Id_enroll_name_textfield);
        TextField user_pwd = (TextField) findComponentById(ResourceTable.Id_enroll_pwd_textfield);
        TextField pwd_confirm = (TextField) findComponentById(ResourceTable.Id_enroll_pwd_configure);

        //对注册按钮进行事件监听
        enroll_btn.setClickedListener(component -> {
            //设置查询函数中的三个参数内容，数据表为Hmmall_info,查询列为用户名和密码
            Uri uri = Uri.parse("dataability:///com.example.hmmall.HmmallDataAbility/Hmmall_info");
            String[] colums = {"username","pwd"};
            DataAbilityPredicates dataAbilityPredicates = new DataAbilityPredicates();

            //获取本次输入的用户名，并查询数据库中是否已经存在
            String userName = user_id.getText();
            try {
                ResultSet resultSet = dataAbilityHelper.query(uri,colums,dataAbilityPredicates);
                //从rs中获取查询结果
                int rowCount = resultSet.getRowCount();
                System.out.println("hell0---------");
                //若数据库中存在信息，逐行遍历比照
                if(rowCount>0){
                    resultSet.goToFirstRow();
                    do{
                        //从首行开始，获取第一列的内容，即用户名
                        String name = resultSet.getString(0);
                        //当表格内容和输入用户名相同时，设置查询变量的值为1，跳出循环
                        if(name.equals(userName)) {
                            System.out.println("------right-----");
                            query_answer = 1;
                            break;
                        }
                    }while(resultSet.goToNextRow());
                    //若数据库中已存在该用户信息
                    if(query_answer == 1){
                        //显示该用户已注册，请登录；并跳转到登录页面
                        new ToastDialog(getContext()).setText("该用户已注册，请登录").show();
                        present(new LoginAbilitySlice(),new Intent());
                        System.out.println("---wrong---");
                    }
                }
            } catch (DataAbilityRemoteException e) {
                e.printStackTrace();
            }
            //在确认数据库中没有该用户信息后，将用户信息写入数据库中
            String pwd = user_pwd.getText();
            String pwd_confirmText = pwd_confirm.getText();
            //如果两次输入的内容一致，则将该用户信息插入数据库中
            if(pwd.equals(pwd_confirmText)){
                //访问数据库dataAbility(Hmmall),通过dataAbilityHelper
                //调用insert方法，可以访问任何一个dataAbility的insert，通过URI调用
                //设置插入值盛放容器，将写入的用户名和密码添加到容器中
                ValuesBucket valuesBucket = new ValuesBucket();
                valuesBucket.putString("username",userName);
                valuesBucket.putString("pwd",pwd);
                try {
                    //进行插入操作
                    int i = dataAbilityHelper.insert(Uri.parse("dataability:///com.example.hmmall.HmmallDataAbility//Hmmall_info"),valuesBucket);
                    System.out.println("------->"+i);
                } catch (DataAbilityRemoteException e) {
                    e.printStackTrace();
                }
                System.out.println("---insert---success---");
                //显示注册成功并跳转到登录页面
                new ToastDialog(getContext()).setText("注册成功").show();
                present(new LoginAbilitySlice(), new Intent());
            }else {
                //当两次输入的信息不一致时，弹窗显示
                new ToastDialog(getContext()).setText("两次密码输入不一致").show();
            }
        });
    }
}
