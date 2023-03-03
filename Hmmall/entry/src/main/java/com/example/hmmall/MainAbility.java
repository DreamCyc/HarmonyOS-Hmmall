package com.example.hmmall;

import com.example.hmmall.slice.LoginAbilitySlice;
import com.example.hmmall.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        //注册页面为进入应用后加载的页面
        super.setMainRoute(LoginAbilitySlice.class.getName());
    }
}
