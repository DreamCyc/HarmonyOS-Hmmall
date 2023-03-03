package com.example.hmmall.provider;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.PageSliderProvider;

import java.util.List;

public class TabPageSliderProvider extends PageSliderProvider {
    //定义私有变量布局列表及页面
    private List<Integer> layoutFileIds;
    private AbilitySlice abilitySlice;
    //对定义的变量进行赋值，赋值页面
    public TabPageSliderProvider(List<Integer> layoutFileIds, AbilitySlice abilitySlice) {
        this.layoutFileIds = layoutFileIds;
        this.abilitySlice = abilitySlice;
    }

    //返回列表大小
    @Override
    public int getCount() {
        return layoutFileIds.size();
    }

    //对组件进行添加操作
    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {
        Integer id = layoutFileIds.get(i);
        Component component = LayoutScatter.getInstance(abilitySlice).parse(id,null,false);
        componentContainer.addComponent(component);
        return component;
    }

    //对组件进行销毁操作
    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {
        componentContainer.removeComponent((Component) o);
    }

    //对是否匹配该对象进行判断，返回布尔结果
    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        return true;
    }
}
