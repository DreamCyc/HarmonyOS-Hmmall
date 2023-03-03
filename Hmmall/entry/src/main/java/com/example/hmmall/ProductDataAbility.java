package com.example.hmmall;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.rdb.*;
import ohos.data.resultset.ResultSet;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;
import ohos.utils.PacMap;

import java.io.FileDescriptor;

public class ProductDataAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    private RdbStore rdbStore;
    private StoreConfig config = StoreConfig.newDefaultConfig("product.db");
    //用来关联数据库的文件
    private RdbOpenCallback openCallback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rdbStore) {
            //如果数据表不存在需要先创建数据表
            rdbStore.executeSql("create table if not exists shop(id,name,price,num,itro)");
            rdbStore.executeSql("insert into shop(id,name,price,num,itro) values(0,'保温杯',30,0,'这是保温杯')," +
                    "(1,'玩偶',20,0,'这是玩偶'),(2,'Tshirt',50,0,'这是T恤衫'),(3,'薯片',10,0,'这是薯片')");
        }
        @Override
        public void onUpgrade(RdbStore rdbStore, int i, int i1) {}
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LABEL_LOG, "ProductDataAbility onStart");
        //初始化数据库连接
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        rdbStore = databaseHelper.getRdbStore(config,1,openCallback);
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, "shop");
        ResultSet resultSet = rdbStore.query(rdbPredicates, columns);
        return resultSet;
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        HiLog.info(LABEL_LOG, "ProductDataAbility insert");
        int i = (int) rdbStore.insert("shop",value);
        return i;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, "shop");
        int i = (int) rdbStore.delete(rdbPredicates);
        //这里也强制转换成int类型了
        return i;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, "shop");
        int i = (int) rdbStore.update(value, rdbPredicates);
        return i;
    }


    //以下三个方法主要是对文件进行操作
    @Override
    public FileDescriptor openFile(Uri uri, String mode) {
        return null;
    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}