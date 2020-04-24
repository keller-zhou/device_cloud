package ren.lollipop.cloud.model;

import cn.bmob.v3.BmobObject;

public class UserCallLogCloud extends BmobObject {

    private String calllog_json;

    private String device_imei;


    public String getCalllog_json() {
        return calllog_json;
    }

    public void setCalllog_json(String calllog_json) {
        this.calllog_json = calllog_json;
    }

    public String getDevice_imei() {
        return device_imei;
    }

    public void setDevice_imei(String device_imei) {
        this.device_imei = device_imei;
    }
}
