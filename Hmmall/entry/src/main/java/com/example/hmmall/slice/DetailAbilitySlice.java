package com.example.hmmall.slice;
import com.example.hmmall.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.utils.net.Uri;

//本页面为商品详情页，作为搜索跳转和首页点击跳转后的结果页
public class DetailAbilitySlice extends AbilitySlice {
    private DataAbilityHelper dataAbilityHelper;
    private RdbStore rdbStore;
    //创建以下三个变量，作为价格、数目、名称的全局统一
    //作为整个商品的结果信息显示中转变量
    //连接不同的点击事件，起到统一的作用
    private String price_tran;
    private String num_tran;
    private String name_tran;
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        //加载商品详情页的布局文件
        setUIContent(ResourceTable.Layout_ability_detail);
        //将商品ID显示到商品详情页面的product_id_text的文本中
        //获取页面Slice传递的商品ID,名称，价格，简介信息
        String product_Id_tran = (String) intent.getParams().getParam("productId");
        //根据上传过来的ID值，通过数据库中的比对搜索，输出与其同列的各个值的集合
        //在函数内创建数据库帮助
        dataAbilityHelper = DataAbilityHelper.creator(this);
        //接下来根据实际操作定义两个数据库的查询参数
        //查询相关信息需要的URI及查询列等参数，商品信息显示数据库
        Uri uri = Uri.parse("dataability:///com.example.hmmall.ProductDataAbility/shop");
        String[] colums = {"id","name","price","num","itro"};
        DataAbilityPredicates dataAbilityPredicates = new DataAbilityPredicates();
        //购物车插入操作中对shop_car列表进行的插入操作
        Uri uri1 = Uri.parse("dataability:///com.example.hmmall.ShopCarDataAbility/shop_car");
        String[] colums_1 = {"id","num","price","name"};
        DataAbilityPredicates dataAbilityPredicates1 = new DataAbilityPredicates();
        try {
            ResultSet resultSet = dataAbilityHelper.query(uri,colums,dataAbilityPredicates);
            //从rs中获取查询结果
            int rowCount = resultSet.getRowCount();
            System.out.println("----------hell0---------");
            if(rowCount>0){
                //从数据库中的首行起进行搜索，寻找匹配项
                resultSet.goToFirstRow();
                do{
                    //获取商品id行的信息
                    String product_id = resultSet.getString(0);
                    System.out.println(product_id);
                    //如果传到该商品详情页的id与数据库中的id相同，则调用后边
                    if(product_Id_tran.equals(product_id)) {
                        System.out.println(product_id);
                        String product_name = resultSet.getString(1);
                        String product_price = resultSet.getString(2);
                        String product_num = resultSet.getString(3);
                        String product_itro = resultSet.getString(4);
                        name_tran = product_name;
                        price_tran = product_price;
                        //获取XML文件中的各个text
                        Text text1 = (Text) findComponentById(ResourceTable.Id_product_id);
                        Text text2 = (Text) findComponentById(ResourceTable.Id_product_name);
                        Text text3 = (Text) findComponentById(ResourceTable.Id_product_detail_price);
                        Text text4 = (Text) findComponentById(ResourceTable.Id_product_detail_sku);

                        //显示在各个test的内容
                        text1.setText("商品的id是:"+product_id);
                        text2.setText("商品的名称是:"+product_name);
                        text3.setText("商品的价格是: ￥ "+product_price);
                        text4.setText("商品的基本信息:"+product_itro);
                        System.out.println("--wanbi--");
                        break;
                    }else {
                        System.out.println("---wrong---");
                    }
                }while(resultSet.goToNextRow());
            }
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }


        Button btn_sub = (Button) findComponentById(ResourceTable.Id_btn_l);
        Button btn_add = (Button) findComponentById(ResourceTable.Id_btn_r);

        //转换字符串为整数的方式
        //String s = "123456";
        //int b = Integer.parseInt(s);
        Text tf = (Text) findComponentById(ResourceTable.Id_num_text);

        //通过id获取num值，然后将值传到Text文本中
        try {
            ResultSet resultSet = dataAbilityHelper.query(uri,colums,dataAbilityPredicates);
            //从rs中获取查询结果
            int rowCount = resultSet.getRowCount();
            if(rowCount>0){
                resultSet.goToFirstRow();
                do{
                    //获取商品id列信息
                    String product_id = resultSet.getString(0);
                    if(product_Id_tran.equals(product_id)) {
                        String product_num_get = resultSet.getString(3);
                        num_tran = product_num_get;
                        break;
                    }else {
                        System.out.println("---wrong---");
                    }
                }while(resultSet.goToNextRow());
            }
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }
        tf.setText(num_tran);
        System.out.println(num_tran);

        Component.ClickedListener listener = new Component.ClickedListener(){
            @Override
            public void onClick(Component component) {
                Button btn = (Button) component;
                int num = Integer.parseInt(tf.getText());
                System.out.println(num);
                if(btn == btn_sub){
                    num = num>1? num-1:num;
                }else if (btn == btn_add){
                    num = num + 1;
                }
                tf.setText(num+"");
                //得到最终的数量num_tran
                num_tran = num+"";
            }
        };

        btn_add.setClickedListener(listener);
        btn_sub.setClickedListener(listener);

        //添加购物车的同时将数据内容插入到数据库中
        Button add_shop = (Button) findComponentById(ResourceTable.Id_add_btn);
        add_shop.setClickedListener(component -> {
            Intent intent_add = new Intent();
            ValuesBucket valuesBucket = new ValuesBucket();
            valuesBucket.putString("id",product_Id_tran);
            valuesBucket.putString("num",num_tran);
            valuesBucket.putString("price",price_tran);
            valuesBucket.putString("name",name_tran);
            try {
                int i = dataAbilityHelper.insert(Uri.parse("dataability:///com.example.hmmall.ShopCarDataAbility/shop_car"),valuesBucket);
                System.out.println("------->"+i);
                //以下代码为测试内容
                try {
                    ResultSet resultSet = dataAbilityHelper.query(uri1,colums_1,dataAbilityPredicates1);
                    //从rs中获取查询结果
                    int rowCount = resultSet.getRowCount();
                    if(rowCount>0){
                        resultSet.goToFirstRow();
                        do{
                            //获取商品id列信息
                            String product_id = resultSet.getString(0);
                            if(product_Id_tran.equals(product_id)) {
                                System.out.println(product_id);
                                System.out.println("------right-----");
                                String product_num_get = resultSet.getString(1);
                                System.out.println(product_num_get);
                                String product_price_get = resultSet.getString(2);
                                System.out.println(product_price_get);
                                String product_name_get = resultSet.getString(3);
                                System.out.println(product_name_get);
                                System.out.println("--wanbi--");
                                }else {
                                    System.out.println("---wrong---");
                                }
                            }while(resultSet.goToNextRow());
                        }
                    } catch (DataAbilityRemoteException e) {
                        e.printStackTrace();
                    }
            } catch (DataAbilityRemoteException e) {
                e.printStackTrace();
            }
            new ToastDialog(getContext()).setText("已添加到购物车").show();
            System.out.println("success");
            System.out.println("新数据库的输出如上所示");
        });
    }
}
