package ren.lollipop.cloud.model;

import cn.bmob.v3.BmobObject;

//用户云基本信息
public class UserCloud extends BmobObject {

    //基本信息
    private String name;//姓名
    private String cellphone;//手机号
    private String device_name;//设备名称
    private String device_version;//设备版本
    private String device_ip;//ip地址
    private String device_imei;//收集imei地址

    //额外信息
    private String contact_object_id;//通讯录所在表id
    private String appinstall_object_id;//App安装所在表id
    private String smsmsg_object_id;//短信所在表id
    private String celllog_object_id;//通话记录所在表id
    private String media_object_id;//多媒体所在表id（未开发）


    public UserCloud() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_version() {
        return device_version;
    }

    public void setDevice_version(String device_version) {
        this.device_version = device_version;
    }

    public String getDevice_ip() {
        return device_ip;
    }

    public void setDevice_ip(String device_ip) {
        this.device_ip = device_ip;
    }

    public String getDevice_imei() {
        return device_imei;
    }

    public void setDevice_imei(String device_imei) {
        this.device_imei = device_imei;
    }

    public String getContact_object_id() {
        return contact_object_id;
    }

    public void setContact_object_id(String contact_object_id) {
        this.contact_object_id = contact_object_id;
    }

    public String getAppinstall_object_id() {
        return appinstall_object_id;
    }

    public void setAppinstall_object_id(String appinstall_object_id) {
        this.appinstall_object_id = appinstall_object_id;
    }

    public String getSmsmsg_object_id() {
        return smsmsg_object_id;
    }

    public void setSmsmsg_object_id(String smsmsg_object_id) {
        this.smsmsg_object_id = smsmsg_object_id;
    }

    public String getCelllog_object_id() {
        return celllog_object_id;
    }

    public void setCelllog_object_id(String celllog_object_id) {
        this.celllog_object_id = celllog_object_id;
    }

    public String getMedia_object_id() {
        return media_object_id;
    }

    public void setMedia_object_id(String media_object_id) {
        this.media_object_id = media_object_id;
    }
}
