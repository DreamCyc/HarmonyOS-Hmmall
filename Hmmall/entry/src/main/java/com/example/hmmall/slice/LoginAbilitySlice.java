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
import ohos.data.resultset.ResultSet;
import ohos.utils.net.Uri;

//本页面实现登录功能
public class LoginAbilitySlice extends AbilitySlice {
    //创建dataAbility帮助类，进行数据库的调用
    private DataAbilityHelper dataAbilityHelper;
    //定义整体变量
    //进行登录结果查询，用0/1作为查询结果的反馈
    private int query_answer = 0;
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        //加载登录界面的xml文件
        setUIContent(ResourceTable.Layout_ability_login);
        //表示登录界面中的两个输入文本框和按钮
        TextField user_id = (TextField) findComponentById(ResourceTable.Id_login_name_textfield);
        TextField user_pwd = (TextField) findComponentById(ResourceTable.Id_login_pwd_textfield);
        Button submitBtn = (Button) findComponentById(ResourceTable.Id_login_btn);
        Button enrollBtn = (Button) findComponentById(ResourceTable.Id_enroll_btn);

        //点击注册按钮，跳转到注册页中
        enrollBtn.setClickedListener(component -> {
            System.out.println("----jump to the Enroll----");
            present(new EnrollAbilitySlice(), new Intent());
        });

        //点击登录按钮
        submitBtn.setClickedListener(component -> {
            //获取文本框中输入的用户名和密码
            String userName_login = user_id.getText();
            String pwd_login = user_pwd.getText();
            //创建线程内的适配器
            dataAbilityHelper = DataAbilityHelper.creator(this);
            //执行查询数据库的操作，查询列为用户名和密码
            //URI，column，dataabilitypredicates为查询函数的参数
            Uri uri = Uri.parse("dataability:///com.example.hmmall.HmmallDataAbility/Hmmall_info");
            String[] colums = {"username","pwd"};
            DataAbilityPredicates dataAbilityPredicates = new DataAbilityPredicates();
            //将查询操作放入try-catch结构中
            try {
                //从数据库中获取查询结果，resultSet
                ResultSet resultSet = dataAbilityHelper.query(uri,colums,dataAbilityPredicates);
                //查询得到数据库的总行数rowCount
                int rowCount = resultSet.getRowCount();
                System.out.println("---getRowCount---");
                //当数据库中存在数据时进行查询
                if(rowCount>0){
                    //查询操作从第一行起执行
                    resultSet.goToFirstRow();
                    //do while循环表示数据库未结束时继续查询
                    do{
                        String userName = resultSet.getString(0);
                        String pwd = resultSet.getString(1);
                        //将数据库中查询的用户名和密码与输入的进行比照
                        if(userName.equals(userName_login) && pwd.equals(pwd_login)) {
                            System.out.println("------right-----");
                            //在匹配的情况下进行页面跳转，将用户名传到MainAbilitySlice
                            Intent intent_user = new Intent();
                            intent_user.setParam("userName",userName);
                            present(new MainAbilitySlice(), intent_user);
                            //设置用户的查询反馈，1表示匹配成功
                            query_answer = 1;
                            new ToastDialog(getContext()).setText("登录成功").show();
                            System.out.println("--wanbi--");
                            break;
                        }
                    }while(resultSet.goToNextRow());
                    //若对数据库进行遍历后没有得到结果
                    if(query_answer == 0){
                        //给出弹框，匹配出现错误，请重试
                        new ToastDialog(getContext()).setText("用户名或密码错误请重新输入").show();
                        System.out.println("---login wrong---");
                    }
                }
            } catch (DataAbilityRemoteException e) {
                e.printStackTrace();
            }
            System.out.println("---login end---");
        });
    }
}
