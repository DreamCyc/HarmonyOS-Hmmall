package com.example.hmmall.beans;
//定义轮播图实体类
public class Img {
    private int indexImageId;
    private int indexResourceId; //本地资源中的Id
    private String indexImagePth;
    private String indexImgType; //0商品；1类别
    private String indexImgUri;

    public Img(){
    }

    public Img(int indexResourceId){
        this.indexResourceId = indexResourceId;
    }

    public void setIndexResourceId(int indexResourceId) {
        this.indexResourceId = indexResourceId;
    }

    public Img(int indexImageId, int indexResourceId, String indexImagePth, String indexImgType, String indexImgUri) {
        this.indexImageId = indexImageId;
        this.indexResourceId = indexResourceId;
        this.indexImagePth = indexImagePth;
        this.indexImgType = indexImgType;
        this.indexImgUri = indexImgUri;
    }


    public void setIndexImagePth(String indexImagePth) {
        this.indexImagePth = indexImagePth;
    }


    public int getIndexImageId() {
        return indexImageId;
    }


    public String getIndexImgType() {
        return indexImgType;
    }

    public void setIndexImgType(String indexImgType) {
        this.indexImgType = indexImgType;
    }

    public String getIndexImgUri() {
        return indexImgUri;
    }

    public void setIndexImgUri(String indexImgUri) {
        this.indexImgUri = indexImgUri;
    }

    public int getIndexResourceId() {
        return indexResourceId;
    }

    public String getIndexImagePth() {
        return indexImagePth;
    }
}
