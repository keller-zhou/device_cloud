package ren.lollipop.cloud.model;

import cn.bmob.v3.BmobObject;

public class UserContactCloud extends BmobObject {

    private String contact_json;

    private String device_imei;


    public String getContact_json() {
        return contact_json;
    }

    public void setContact_json(String contact_json) {
        this.contact_json = contact_json;
    }

    public String getDevice_imei() {
        return device_imei;
    }

    public void setDevice_imei(String device_imei) {
        this.device_imei = device_imei;
    }
}
