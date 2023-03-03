package com.example.hmmall.slice;
import com.example.hmmall.ResourceTable;
import com.example.hmmall.beans.Img;
import com.example.hmmall.provider.IndexImgPageSliderProvider;
import com.example.hmmall.provider.TabPageSliderProvider;
import com.example.hmmall.utils.DataBaseUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.resultset.ResultSet;
import ohos.utils.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainAbilitySlice<tabList> extends AbilitySlice {
    //创建dataability中数据库适配器
    private DataAbilityHelper dataAbilityHelper;
    //创建私有变量表示从登录界面中传来的用户名，供全局使用
    private String user_login;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        //加载主页面的布局文件，作为布局的基础
        super.setUIContent(ResourceTable.Layout_ability_main);
        //获取从登录界面中传来的参数值，通过类中的私有变量表示，方便调用
        String userName = (String) intent.getParams().getParam("userName");
        user_login = userName;

        //1. 初始化页面底部的TabList菜单（在加载的布局文件中）
        TabList tabList = (TabList) findComponentById(ResourceTable.Id_tab_list);
        //设置底部的四个按钮的内容
        String[] tabListTags = {"首页","广场","购物车","我的"};
        //将按钮的内容添加到tabList菜单中
        for (int i = 0; i < tabListTags.length; i++){
            TabList.Tab tab = tabList.new Tab(this);
            tab.setText(tabListTags[i]);
            tabList.addTab(tab);
        }

        //2. 初始化pageslider数组，提供滑动功能
        List<Integer> layoutFiledIds = new ArrayList<>();
        //在pageslider组件中加入四个ui的xml布局文件
        layoutFiledIds.add(ResourceTable.Layout_ability_main_index);
        layoutFiledIds.add(ResourceTable.Layout_ability_main_square);
        layoutFiledIds.add(ResourceTable.Layout_ability_main_shopcart);
        layoutFiledIds.add(ResourceTable.Layout_ability_main_user_center);
        //将pageslider四个页面添加到ability_main的布局中
        PageSlider pageSlider = (PageSlider) findComponentById(ResourceTable.Id_page_slider);
        //调用provider/TabPageSliderProvider中的函数进行构造
        pageSlider.setProvider(new TabPageSliderProvider(layoutFiledIds,this));

        //曾出现的错误操作：把下边代码写在onstart方法之外
        //3. 设置Tablist and PageSlider联动
        tabList.addTabSelectedListener(new TabList.TabSelectedListener() {
            @Override
            public void onSelected(TabList.Tab tab) {
                //获取点击的菜单的索引
                int index = tab.getPosition();
                //设置pageslider的索引与菜单的索引一致，并封装初始化页面的函数
                pageSlider.setCurrentPage(index);
                if(index == 0){
                    //首页
                    initIndex(pageSlider);
                }else if(index == 1){
                    //广场
                    initSquare(pageSlider);
                }else if(index == 2){
                    //购物车
                    initShopcart(pageSlider);
                }else if(index == 3){
                    //我的
                    initUserCenter(pageSlider);
                }
            }
            @Override
            public void onUnselected(TabList.Tab tab) { }
            @Override
            public void onReselected(TabList.Tab tab) { }
        });
        pageSlider.addPageChangedListener(new PageSlider.PageChangedListener() {
            @Override
            public void onPageSliding(int i, float v, int i1) { }
            @Override
            public void onPageSlideStateChanged(int i) { }
            @Override
            public void onPageChosen(int i) {
                //参数i就表示当前PageSlider的索引
                if(tabList.getSelectedTabIndex() != i){
                    tabList.selectTabAt(i);
                }
            }
        });

        //4. tablist默认选中第一个菜单，加载pageslider的第一个页面（默认）
        tabList.selectTabAt(0);


    }



    //以下四个函数为定义的四个初始化方法
    //每个页面可以加载其本身和相邻两个的页面组件
    private void initIndex(PageSlider pageSlider) {
        //监听首页的搜索输入框，点击输入框则调转到SearchAbilitySlice中
        TextField searchTextField = (TextField) findComponentById(ResourceTable.Id_index_search_textfield);
        searchTextField.setFocusChangedListener((component,b)->{
            //获得焦点时b为true
            //当搜索输入框获得焦点，导航到SearchAbilitySlice中
            if(b){
                present(new SearchAbilitySlice(),new Intent());
            }
        });

        //初始化轮播图的数据
        PageSlider pageSlider_img = (PageSlider) findComponentById(ResourceTable.Id_index_image_page_slider);
        List<Img> list = getData();
        //调用IndexImgPageSliderProvider进行图片属性设置
        IndexImgPageSliderProvider pageSliderProvider = new IndexImgPageSliderProvider(list, this);
        pageSlider_img.setProvider(pageSliderProvider);

        //渲染数据，通过接口从后台获取数据，并显示到ability_main_index.xml布局文件的组件中
        List<Component> product_list = new ArrayList<>();

        Component product_1 = pageSlider.findComponentById(ResourceTable.Id_layout_product01);
        Component product_2 = pageSlider.findComponentById(ResourceTable.Id_layout_product02);
        Component product_3 = pageSlider.findComponentById(ResourceTable.Id_layout_product03);
        Component product_4 = pageSlider.findComponentById(ResourceTable.Id_layout_product04);
        product_list.add(product_1);
        product_list.add(product_2);
        product_list.add(product_3);
        product_list.add(product_4);


        product_1.setClickedListener(component -> {
            Intent intent = new Intent();
            String product_id = getId("保温杯");
            intent.setParam("productId",product_id);
            this.present(new DetailAbilitySlice(),intent);
            System.out.println("--wanbi--");
        });

        product_2.setClickedListener(component -> {
            Intent intent = new Intent();
            String productId = getId("玩偶");
            intent.setParam("productId",productId);
            this.present(new DetailAbilitySlice(), intent);
        });

        product_3.setClickedListener(component -> {
            Intent intent = new Intent();
            String productId = getId("Tshirt");
            intent.setParam("productId",productId);
            this.present(new DetailAbilitySlice(), intent);
        });

        product_4.setClickedListener(component -> {
            Intent intent = new Intent();
            String productId = getId("薯片");
            intent.setParam("productId",productId);
            this.present(new DetailAbilitySlice(), intent);
        });

        System.out.println("-------chuandi---wanbi-------");

    }

    //在首页中需要用到的函数方法
    private List<Img> getData() {
        List<Img> list = new ArrayList<>();
        list.add(new Img(ResourceTable.Media_baowenbei));
        list.add(new Img(ResourceTable.Media_wanou));
        list.add(new Img(ResourceTable.Media_Tshirt));
        list.add(new Img(ResourceTable.Media_shupian));
        return list;
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

    private void initSquare(PageSlider pageSlider) {
        System.out.println("hl");
        //跳转到此页面后，首先设置上边的directionlayout点击事件
        Component information = (Component) findComponentById(ResourceTable.Id_new_information);
        //实现点击跳转到信息的详情页
        //使用同一个详情页，根据传递的id值不同进行跳转
        information.setClickedListener(component -> {
            Intent intent_information = new Intent();
            intent_information.setParam("info",1);
            this.present(new InformationAbilitySlice(),intent_information);
        });

        Component discount = (Component) findComponentById(ResourceTable.Id_discount);
        discount.setClickedListener(component -> {
            Intent intent_discount = new Intent();
            intent_discount.setParam("info",2);
            this.present(new InformationAbilitySlice(),intent_discount);
        });
    }

    private void initShopcart(PageSlider pageSlider) {
        //打开后进行数据库查询，显示ShopCarDataAbility数据的信息
        //数据库的列分别为id,num,price,name
        //此集合用于存储用户选择的购物车id
        List<String> cids = new ArrayList<>();

        //对于获取的购物车信息，将其显示到Tablelayout的布局中
        getUITaskDispatcher().asyncDispatch(()->{
            TableLayout container = (TableLayout) findComponentById(ResourceTable.Id_shop_list_table);
            //读数据库，读一行，写一行
            Uri uri1 = Uri.parse("dataability:///com.example.hmmall.ShopCarDataAbility/shop_car");
            String[] colums_1 = {"id","num","price","name"};
            DataAbilityPredicates dataAbilityPredicates1 = new DataAbilityPredicates();
            //在线程内部创建帮助类
            dataAbilityHelper = DataAbilityHelper.creator(this);
            try {
                ResultSet resultSet = dataAbilityHelper.query(uri1,colums_1,dataAbilityPredicates1);
                //从rs中获取查询结果
                int rowCount = resultSet.getRowCount();
                if(rowCount>0){
                    resultSet.goToFirstRow();
                    do{
                        //获取0商品id,1商品数量，2商品价格，3商品名称
                        String product_id = resultSet.getString(0);
                        String product_num = resultSet.getString(1);
                        System.out.println(product_num);
                        System.out.println("------right-----");
                        String product_name = resultSet.getString(3);
                        String product_price = resultSet.getString(2);

                        //获取XML文件中的各个text
                        Component template = LayoutScatter.getInstance(this).parse(ResourceTable.Layout_shopcart_item,null,false);
                        Checkbox checkbox = (Checkbox) template.findComponentById(ResourceTable.Id_check_box); //复选框
                        Text text1 = (Text) template.findComponentById(ResourceTable.Id_shopcart_item_product_name_text);  //商品名称
                        Text text2 = (Text) template.findComponentById(ResourceTable.Id_shopcart_item_product_price_text);  //单价
                        Text text3 = (Text) template.findComponentById(ResourceTable.Id_shopcart_item_product_number_text);  //数目
                        Text text4 = (Text) template.findComponentById(ResourceTable.Id_shopcart_item_total_price_text);  //总价

                            //显示在各个test的内容
                        text1.setText("商品:"+product_name);
                        text2.setText("单价:￥"+product_price);
                        text3.setText("数量:"+product_num);
                        text4.setText("总价:￥"+(Integer.parseInt(product_price))*(Integer.parseInt(product_num))+"");
                        System.out.println("--wanbi--");
                        container.addComponent(template);
                        System.out.println("添加成功");

                        //监听复选框
                        checkbox.setCheckedStateChangedListener((btn,b)->{
                            //参数b表示复选框的状态
                            if(b){
                                cids.add(product_id);
                            }else {
                                cids.remove(new Integer(Integer.parseInt(product_id)));
                            }
                        });
                    }while(resultSet.goToNextRow());
                } else {
                    System.out.println("暂无商品");
                }
            } catch (DataAbilityRemoteException e) {
                e.printStackTrace();
            }
            //获取数据库的插入数量
            //Integer length = getLength();
        });

        //为购物车中的添加购物车按钮添加点击事件
        Button btn = (Button) findComponentById(ResourceTable.Id_shopcart_add_button);
        btn.setClickedListener(component -> {
            if(cids.size()>0){
                Intent intent = new Intent();
                intent.setParam("cids", String.join(",", cids));
                present(new OrderAddAbilitySlice(),intent);
            }else {
                new ToastDialog(this).setText("请选择需要结算的商品").show();
            }

        });
    }

    private Integer getLength(){
        Uri uri1 = Uri.parse("dataability:///com.example.hmmall.ShopCarDataAbility/shop_car");
        String[] colums_1 = {"id","num","price","name"};
        DataAbilityPredicates dataAbilityPredicates1 = new DataAbilityPredicates();
        //查询数据表格信息，确定已有的数据行数目
        ResultSet resultSet = null;
        try {
            resultSet = dataAbilityHelper.query(uri1,colums_1,dataAbilityPredicates1);
        } catch (DataAbilityRemoteException e) {
            e.printStackTrace();
        }
        //从rs中获取查询结果
        int rowCount = resultSet.getRowCount();
        System.out.println(rowCount);
        return rowCount;
    }



    private void initUserCenter(PageSlider pageSlider) {
        Component layout = pageSlider.findComponentById(ResourceTable.Id_layout_product01);

        Text user_name = (Text) findComponentById(ResourceTable.Id_userName);
        user_name.setText(user_login);

        Button return_btn = (Button) findComponentById(ResourceTable.Id_return_btn);
        return_btn.setClickedListener(component -> {


            Intent intent = new Intent();
            present(new LoginAbilitySlice(),intent);
            new ToastDialog(getContext()).setText("退出成功").show();
        });
    }




    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}