package com.example.hmmall.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import com.example.hmmall.ResourceTable;
import ohos.agp.components.Button;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.resultset.ResultSet;
import ohos.utils.net.Uri;

public class SearchAbilitySlice extends AbilitySlice {
    private DataAbilityHelper dataAbilityHelper;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_search);

        Button search_btn = (Button) findComponentById(ResourceTable.Id_search_btn);
        TextField search_text = (TextField) findComponentById(ResourceTable.Id_search_textfield);

        search_btn.setClickedListener(component -> {
            //得到search商品的文字描述
            String search_info = search_text.getText();
            //对search内容进行设计，与数据库进行比对获取商品id
            String search_product_id = getId(search_info);
            //得到商品id后完成向商品详情页面的跳转

            Intent intent_search = new Intent();
            intent_search.setParam("productId",search_product_id);
            this.present(new DetailAbilitySlice(),intent_search);

        });
    }

    private String getId(String get_name){
        //查询部分代码
        dataAbilityHelper = DataAbilityHelper.creator(this);
        //查询相关信息
        Uri uri = Uri.parse("dataability:///com.example.hmmall.ProductDataAbility/shop");
        String[] colums = {"id","name","price","num","itro"};
        DataAbilityPredicates dataAbilityPredicates = new DataAbilityPredicates();

        try {
            ResultSet resultSet = dataAbilityHelper.query(uri,colums,dataAbilityPredicates);
            //从rs中获取查询结果
            int rowCount = resultSet.getRowCount();
            System.out.println("hell0---------");
            if(rowCount>0){
                resultSet.goToFirstRow();
                do{
                    //获取并上传商品id
                    String product_id = resultSet.getString(0);
                    String product_name = resultSet.getString(1);
                    if(product_name.equals(get_name)) {
                        System.out.println(product_id);
                        System.out.println("------right-----");
                        return product_id;
                    }
                }while(resultSet.goToNextRow());
            }
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }
        new ToastDialog(getContext()).setText("暂时没有该商品").show();
        System.out.println("---wrong---");
        return null;
    }
}
