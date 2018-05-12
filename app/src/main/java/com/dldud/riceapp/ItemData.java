package com.dldud.riceapp;

import net.daum.mf.map.api.MapReverseGeoCoder;

/**
 * Created by dldud on 2018-04-05.
 */

public class ItemData {
    public String strLike;
    public String strShare;
    public String strReply;
    public String strUserId;
    public String strUserImage;
    public String strThumbnailImage;
    public String strVideo;
    public String strUserName;
    public String strPingLike;
    public String strContent;
    public double douLatitude;
    public double douLongitude;

    private int iMarkerIndex = -1;
    private String strAddress = null;
    private MapReverseGeoCoder reverseGeoCoder = null;


    public int getiMarkerIndex() {
        return iMarkerIndex;
    }

    public void setiMarkerIndex(int iMarkerIndex) {
        this.iMarkerIndex = iMarkerIndex;
    }

    public String getStrAddress() {
        return strAddress;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public void setReverseGeoCoder(MapReverseGeoCoder reverseGeoCoder) {
        this.reverseGeoCoder = reverseGeoCoder;
    }

    public MapReverseGeoCoder getReverseGeoCoder() {
        return reverseGeoCoder;
    }

    //Getters and Setters
    public String getStrLike() {
        return strLike;
    }

    public void setStrLike(String strLike) {
        this.strLike = strLike;
    }

    public String getStrShare() {
        return strShare;
    }

    public void setStrShare(String strShare) {
        this.strShare = strShare;
    }

    public String getStrReply() {
        return strReply;
    }

    public void setStrReply(String strReply) {
        this.strReply = strReply;
    }

    public String getStrUserId() {
        return strShare;
    }

    public void setStrUserId(String strUserId) {
        this.strUserId = strUserId;
    }

    public String getStrUserImage() {
        return strUserImage;
    }

    public void setStrUserImage(String strUserImage) {
        this.strUserImage = strUserImage;
    }

    public String getStrThumbnailImage() {
        return strThumbnailImage;
    }

    public void setStrThumbnailImage(String strThumbnailImage) {
        this.strThumbnailImage = strThumbnailImage;
    }

    public String getStrVideo() {
        return strVideo;
    }

    public void setStrVideo(String strVideo) {
        this.strVideo = strVideo;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName){
        this.strUserName = strUserName;
    }

    public String getStrPingLike(){
        return strPingLike;
    }

    public void setStrPingLike(String strPingLike){
        this.strPingLike = strPingLike;
    }

    public String getStrContent(){
        return strContent;
    }

    public void setStrContent(String strContent){
        this.strContent = strContent;
    }

    public double getDouLatitude(){
        return  douLatitude;
    }

    public void setDouLatitude(double douLatitude) {
        this.douLatitude = douLatitude;
    }

    public double getDouLongitude(){
        return douLongitude;
    }

    public void setDouLongitude(double douLongitude) {
        this.douLongitude = douLongitude;
    }

    /*
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof ItemData)
        {
            if()
        }
    }*/
}
