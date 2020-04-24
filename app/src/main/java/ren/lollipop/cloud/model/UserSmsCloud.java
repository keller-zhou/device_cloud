package ren.lollipop.cloud.model;

import cn.bmob.v3.BmobObject;

public class UserSmsCloud extends BmobObject {

    private String sms_json;

    private String device_imei;


    public String getSms_json() {
        return sms_json;
    }

    public void setSms_json(String sms_json) {
        this.sms_json = sms_json;
    }

    public String getDevice_imei() {
        return device_imei;
    }

    public void setDevice_imei(String device_imei) {
        this.device_imei = device_imei;
    }
}
