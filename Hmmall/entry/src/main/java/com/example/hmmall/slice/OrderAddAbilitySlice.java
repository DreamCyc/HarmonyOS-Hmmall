package com.example.hmmall.slice;

import com.example.hmmall.MainAbility;
import com.example.hmmall.location.LocationBean;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import com.example.hmmall.ResourceTable;
import ohos.agp.components.*;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.bundle.IBundleManager;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.resultset.ResultSet;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.*;
import ohos.utils.net.Uri;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//结算页页面，包括定位、支付信息、商品列表、商品总价、提交按钮等内容
public class OrderAddAbilitySlice extends AbilitySlice {
    //通过DataAbilityHelper调用数据库信息
    private DataAbilityHelper dataAbilityHelper;
    //支付方式选择 0微信支付，1支付宝，2银联支付，默认值为3
    private int payType = 3;

    //定位功能需要的变量
    private static final String TAG = MainAbility.class.getSimpleName();
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD000F00, TAG);
    private static final int EVENT_ID = 0x12;
    private static final String PERM_LOCATION = "ohos.permission.LOCATION";
    private final LocatorResult locatorResult = new LocatorResult();
    private Context context;
    private Locator locator;
    private GeoConvert geoConvert;
    private List<GeoAddress> gaList;
    private LocationBean locationDetails;
    private Text locationInfoText;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        //加载提交订单页面的布局文件ability_order_add
        setUIContent(ResourceTable.Layout_ability_order_add);
        //如果页面跳转后传过来的值不为空
        if(intent!=null){
            //获取页面传值的字符串str
            String str = (String) intent.getParams().getParam("cids");
            System.out.println(str);
            //将字符串转换成列表，用逗号分割
            List<String> cids = Arrays.asList(str.split(","));
            //输出转换后的cids及其中的第一个值进行测试
            System.out.println(cids);
            System.out.println(cids.get(0));
            //在线程里设置功能操作
            getUITaskDispatcher().asyncDispatch(()->{
                //对需要逐行添加的商品列表信息，设置固定的布局模式
                //调用order_add_list_table布局文件作为显示布局
                TableLayout table = (TableLayout) findComponentById(ResourceTable.Id_order_add_list_table);
                table.removeAllComponents();
                //设置显示结算商品总价的文本变量
                Text priceText = (Text) findComponentById(ResourceTable.Id_total_price);
                //初始化商品总价为0，根据循环数据库中的待结算商品进行总价计算
                Integer totalPrice = 0;
                //遍历整个cids列表
                for (int i = 0; i<cids.size(); i++){
                    //将cids中的每一个值与数据库的id项进行比较
                    //如果比对成功，则显示该页面，加载页面模板
                    //首先提取cid中的元素cids.get(i)
                    //其中tran为中转变量
                    String tran = cids.get(i);
                    //在线程中需要重新创建DataAbilityHelper
                    //若不重新创建该变量，则没有商品被添加至购物车时会出现闪退现象
                    dataAbilityHelper = DataAbilityHelper.creator(this);
                    //对购物车所在shop_car数据库进行查询操作
                    //设置查询操作需要定义的URI、列等
                    Uri uri1 = Uri.parse("dataability:///com.example.hmmall.ShopCarDataAbility/shop_car");
                    String[] colums_1 = {"id","num","price","name"};
                    DataAbilityPredicates dataAbilityPredicates1 = new DataAbilityPredicates();
                    try {
                        //执行查询操作获取结果集resultSet
                        ResultSet resultSet = dataAbilityHelper.query(uri1,colums_1,dataAbilityPredicates1);
                        //从rs中获取查询结果，获得行数
                        int rowCount = resultSet.getRowCount();
                        System.out.println("----------row get---------");
                        //若行数大于0即数据库中存在数据，则从首行开始逐行遍历
                        if(rowCount>0){
                            resultSet.goToFirstRow();
                            do{
                                //获取 0商品id,1商品数量，2商品价格，3商品名称
                                String product_id = resultSet.getString(0);
                                //若cids列表中的第i项内容tran的值与product_id相同，则进行if内的操作
                                if(product_id.equals(tran)){
                                    //得到数据库中的第1,2,3列内容分别表示商品数量，商品价格，商品名称
                                    String product_num = resultSet.getString(1);
                                    String product_name = resultSet.getString(3);
                                    String product_price = resultSet.getString(2);

                                    //获取XML文件中的各个text
                                    //其中template为逐行添加的模板，采用order_item_template布局文件
                                    Component template = LayoutScatter.getInstance(this).parse(ResourceTable.Layout_order_item_template,null,false);
                                    //获取template布局文件中的四个text变量
                                    //分别表示商品名称Text1，商品单价Text2，商品数量Text3，单个商品总价Text4
                                    Text text1 = (Text) template.findComponentById(ResourceTable.Id_order_item_product_name_text);
                                    Text text2 = (Text) template.findComponentById(ResourceTable.Id_order_item_product_price_text);
                                    Text text3 = (Text) template.findComponentById(ResourceTable.Id_order_item_product_number_text);
                                    Text text4 = (Text) template.findComponentById(ResourceTable.Id_order_item_total_price_text);

                                    //将数据库中的结果按顺序显示在各个test中，采用setText方法
                                    text1.setText(product_name);
                                    text2.setText("单价:￥"+product_price);
                                    text3.setText("数量:"+product_num);
                                    //由于set值为字符类型，进行价格计算时需要进行整数型转换
                                    //并执行 单价*数量=总价 的计算操作，结果为Int类型
                                    Integer price = Integer.parseInt(product_price)*Integer.parseInt(product_num);
                                    //将结果以字符串的形式设置到text4中
                                    text4.setText("总价:￥"+price+"");
                                    //在整体的页面布局中曾设置table列表变量
                                    //在此处添加本次设置的template模板
                                    table.addComponent(template);
                                    System.out.println("添加成功");
                                    //在逐次添加的过程中不断更新总价格的值
                                    totalPrice = totalPrice +price;
                                }
                                //逐行遍历，对符合要求的内容进行相同的插入操作
                            }while(resultSet.goToNextRow());
                        }
                    } catch (DataAbilityRemoteException e) {
                        e.printStackTrace();
                    }
                }
                priceText.setText("￥ "+totalPrice);

            });
        }else {
            //若intent传值中的对象为空，给与弹窗显示
            System.out.println("购物车中暂无商品哦");
        }

        //监听支付方式选择的单选按钮
        RadioContainer radioContainer = (RadioContainer) findComponentById(ResourceTable.Id_pay_type);
        radioContainer.setMarkChangedListener((container,i)->{
            //将选择框与payType变量的值进行绑定
            //对于该候选框，只能进行三选一操作，且必选一个
            payType = i;
        });

        //提交订单按钮获取并设置事件监听
        Button btn = (Button) findComponentById(ResourceTable.Id_order_add_button);
        btn.setClickedListener(component -> {
            //设置判断，确认提交方式已经选择
            if(payType != 3){
                new ToastDialog(getContext()).setText("提交成功").show();
            }else {
                //若没有选择，进行弹框提示
                new ToastDialog(getContext()).setText("请选择支付方式").show();
            }
        });

        //加载定位功能函数，并在本页面中创建定位功能确认信息
        initComponents();
        register(this);
    }


    //在onstart函数外开始设置定位功能，
    //通过调用initComponents进行初始化操作
    private void initComponents() {
        //对开始定位按钮进行变量设置
        Component startLocatingButton = findComponentById(ResourceTable.Id_get_location);
        //设置按钮监听事件
        startLocatingButton.setClickedListener(component -> registerLocationEvent());
        //对显示内容进行变量设置
        locationInfoText = (Text) findComponentById(ResourceTable.Id_location_information);
    }

    private void notifyLocationChange(LocationBean locationDetails) {
        update(locationDetails);
    }

    //设置位置信息显示内容的添加操作
    private void update(LocationBean locationDetails) {
        //对该文本内容进行清空
        locationInfoText.setText("");
        //在文本中依次添加经纬度、国家地区路段等位置信息
        locationInfoText.append("Latitude : " + locationDetails.getLatitude() + System.lineSeparator());
        locationInfoText.append("Longitude : " + locationDetails.getLongitude() + System.lineSeparator());
        locationInfoText.append(
                "SubAdministrative : " + locationDetails.getSubAdministrative() + System.lineSeparator());
        locationInfoText.append("RoadName : " + locationDetails.getRoadName() + System.lineSeparator());
        locationInfoText.append("Locality : " + locationDetails.getLocality() + System.lineSeparator());
        locationInfoText.append("Administrative : " + locationDetails.getAdministrative() + System.lineSeparator());
        locationInfoText.append("CountryName : " + locationDetails.getCountryName());
    }

    private final EventHandler handler = new EventHandler(EventRunner.current()) {
        @Override
        protected void processEvent(InnerEvent event) {
            if (event.eventId == EVENT_ID) {
                notifyLocationChange(locationDetails);
            }
        }
    };

    private void register(Context ability) {
        context = ability;
        requestPermission();
    }

    //按钮监听事件的函数操作
    private void registerLocationEvent() {
        //若已经获取权限
        if (hasPermissionGranted()) {
            int timeInterval = 0;
            int distanceInterval = 0;
            locator = new Locator(context);
            RequestParam requestParam = new RequestParam(RequestParam.PRIORITY_ACCURACY, timeInterval, distanceInterval);
            //开始定位并获取结果
            locator.startLocating(requestParam, locatorResult);
        }
    }

    private void unregisterLocationEvent() {
        if (locator != null) {
            locator.stopLocating(locatorResult);
        }
    }

    private boolean hasPermissionGranted() {
        //权限判断，返回布尔值
        return context.verifySelfPermission(PERM_LOCATION) == IBundleManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        //设置请求申请操作
        if (context.verifySelfPermission(PERM_LOCATION) != IBundleManager.PERMISSION_GRANTED) {
            context.requestPermissionsFromUser(new String[] {PERM_LOCATION}, 0);
        }
    }

    private class LocatorResult implements LocatorCallback {
        @Override
        public void onLocationReport(Location location) {
            HiLog.info(LABEL_LOG, "%{public}s",
                    "onLocationReport : " + location.getLatitude() + "-" + location.getAltitude());
            setLocation(location);
        }

        @Override
        public void onStatusChanged(int statusCode) {
            HiLog.info(LABEL_LOG, "%{public}s", "MyLocatorCallback onStatusChanged : " + statusCode);
        }

        @Override
        public void onErrorReport(int errorCode) {
            HiLog.info(LABEL_LOG, "%{public}s", "MyLocatorCallback onErrorReport : " + errorCode);
        }
    }

    private void setLocation(Location location) {
        if (location != null) {
            Date date = new Date(location.getTimeStamp());
            locationDetails = new LocationBean();
            locationDetails.setTime(date.toString());
            locationDetails.setLatitude(location.getLatitude());
            locationDetails.setLongitude(location.getLongitude());
            locationDetails.setPrecision(location.getAccuracy());
            locationDetails.setSpeed(location.getSpeed());
            locationDetails.setDirection(location.getDirection());
            fillGeoInfo(locationDetails, location.getLatitude(), location.getLongitude());
            handler.sendEvent(EVENT_ID);
            gaList.clear();
        } else {
            HiLog.info(LABEL_LOG, "%{public}s", "EventNotifier or Location response is null");
            new ToastDialog(context).setText("EventNotifier or Location response is null").show();
        }
    }

    private void fillGeoInfo(LocationBean locationDetails, double geoLatitude, double geoLongitude) {
        if (geoConvert == null) {
            geoConvert = new GeoConvert();
        }
        if (geoConvert.isGeoAvailable()) {
            try {
                gaList = geoConvert.getAddressFromLocation(geoLatitude, geoLongitude, 1);
                if (!gaList.isEmpty()) {
                    GeoAddress geoAddress = gaList.get(0);
                    setGeo(locationDetails, geoAddress);
                }
            } catch (IllegalArgumentException | IOException e) {
                HiLog.error(LABEL_LOG, "%{public}s", "fillGeoInfo exception");
            }
        }
    }

    private void setGeo(LocationBean locationDetails, GeoAddress geoAddress) {
        locationDetails.setRoadName(checkIfNullOrEmpty(geoAddress.getRoadName()));
        locationDetails.setLocality(checkIfNullOrEmpty(geoAddress.getLocality()));
        locationDetails.setSubAdministrative(checkIfNullOrEmpty(geoAddress.getSubAdministrativeArea()));
        locationDetails.setAdministrative(checkIfNullOrEmpty(geoAddress.getAdministrativeArea()));
        locationDetails.setCountryName(checkIfNullOrEmpty(geoAddress.getCountryName()));
    }

    private String checkIfNullOrEmpty(String value) {
        if (value == null || value.isEmpty()) {
            return "NA";
        }
        return value;
    }
}

