package ren.lollipop.cloud.model;

import cn.bmob.v3.BmobObject;

public class UserAppCloud extends BmobObject {

    private String app_json;

    private String device_imei;

    public String getApp_json() {
        return app_json;
    }

    public void setApp_json(String app_json) {
        this.app_json = app_json;
    }

    public String getDevice_imei() {
        return device_imei;
    }

    public void setDevice_imei(String device_imei) {
        this.device_imei = device_imei;
    }
}
