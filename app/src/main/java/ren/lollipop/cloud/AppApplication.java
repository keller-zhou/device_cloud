package ren.lollipop.cloud;

import android.app.Application;

import cn.bmob.v3.Bmob;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        Bmob.initialize(this, "6b5ac813b41e9d7280213ae366f085ab");
        super.onCreate();
    }
}
