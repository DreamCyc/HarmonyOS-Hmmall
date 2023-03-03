package com.example.hmmall.slice;
import com.example.hmmall.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.ToastDialog;

//本页面为广场页的内容设置
public class InformationAbilitySlice extends AbilitySlice {
    //定义获取的运气积分变量lucky
    private Integer lucky;
    //判断是否可以获取lucky的变量judge
    //judge=1表示可以获取
    private Integer judge = 1;
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        //通过页面跳转的传值确定点击的不同button信息
        Integer information_id = (Integer) intent.getParams().getParam("info");
        //若传值为1，则是新闻页面；若传值为2，则是获取运气值页面
        if(information_id == 1){
            //跳转到新闻显示页面中
            super.setUIContent(ResourceTable.Layout_information_new);
        }else if(information_id == 2){
            //跳转到运气值获取页面中，加载对应的布局文件
            super.setUIContent(ResourceTable.Layout_ability_discount);
            //定义获取运气值按钮和运气值结果文本框
            Button get_lucky = (Button) findComponentById(ResourceTable.Id_discount);
            TextField lucky_show = (TextField) findComponentById(ResourceTable.Id_discount_get);

            //设置按钮点击事件
            get_lucky.setClickedListener(component -> {
                //若judge变量值为1，即可获取状态，进行获取操作
                if(judge == 1){
                    //设置随机减少值，调用函数获取返回结果
                    lucky = getlucky();
                    //判断获取值变量设置为0
                    judge = 0;
                }
                //在lucky中获取的值作为结果进行展示并弹窗显示
                lucky_show.setText(lucky+"积分");
                new ToastDialog(getContext()).setText("已获取幸运值！").show();
            });

        }

    }

    //随机函数
    private Integer getlucky(){
        //采用随机生成1-10间的随机整数，得到返回结果
        Integer lucky_num = (int)(Math.random() *10) + 1;
        System.out.println(lucky_num);
        return lucky_num;
    }

}

