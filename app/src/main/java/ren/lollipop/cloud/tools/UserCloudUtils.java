package ren.lollipop.cloud.tools;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import ren.lollipop.cloud.model.UserCloud;

public class UserCloudUtils {

    public static UserCloud readDevice(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String cellphone = tm.getLine1Number();//手机号码
        String device_version = android.os.Build.VERSION.RELEASE;
        String device_name = Build.MODEL;
        String device_imei = "";
        try {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                device_imei = tm.getDeviceId();
            }else {
                Method method = tm.getClass().getMethod("getImei");
                device_imei = (String) method.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String device_ip = getLocalIpAddress();

//        Log.d("-----------", "cellphone"+cellphone+"---imei"+imei+"--------"+phoneModel+"-------"+phoneVersion+"----------"+ip);
        UserCloud userCloudBean = new UserCloud();
        userCloudBean.setCellphone(cellphone);
        userCloudBean.setDevice_name(device_name);
        userCloudBean.setDevice_version(device_version);
        userCloudBean.setDevice_imei(device_imei);
        userCloudBean.setDevice_ip(device_ip);
        return userCloudBean;
    }


    //GPRS连接下的ip
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return "";
    }



}
