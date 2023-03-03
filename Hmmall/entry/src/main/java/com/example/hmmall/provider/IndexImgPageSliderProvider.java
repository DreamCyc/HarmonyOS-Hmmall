package com.example.hmmall.provider;

import com.example.hmmall.ResourceTable;
import com.example.hmmall.beans.Img;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;

import java.util.List;

public class IndexImgPageSliderProvider extends PageSliderProvider {
    //传进图片信息，设置页面与图片列表变量
    private List<Img> list;
    private AbilitySlice abilitySlice;

    //设置变量值，list，abilitySlice
    public IndexImgPageSliderProvider(List<Img> list, AbilitySlice abilitySlice) {
        this.list = list;
        this.abilitySlice = abilitySlice;
    }

    //返回list的大小
    @Override
    public int getCount() {
        return list.size();
    }

    //创建容器，将图片map插入到轮播图中
    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {
        Img img = list.get(i);
        //layout是刚刚轮播图设置的每一部分的组件
        DirectionalLayout layout = (DirectionalLayout) LayoutScatter.getInstance(abilitySlice).parse(ResourceTable.Layout_index_img_item,null,false);
        Image image = (Image) layout.findComponentById(ResourceTable.Id_index_image);
        image.setPixelMap(img.getIndexResourceId());
        //容器添加操作
        componentContainer.addComponent(layout);
        return layout;
    }

    //容器销毁操作
    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {
        componentContainer.removeComponent((Component) o);
    }

    //判断操作，返回布尔值
    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        return true;
    }
}
