package com.tang.updateappproject;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zhitongcai.updateapp.UpdateAppBean;
import com.zhitongcai.updateapp.UpdateAppManager;
import com.zhitongcai.updateapp.UpdateCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
    }

    private void test() {
        String mUpdateUrl = String.format("%s%s", "http://v100.ztcjapi.com/updatehints/index.html?",
                "token=c96f2a4a1a548205389a8f5af5b80c1f&tradition_chinese=0&platform=android");

        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(this)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(mUpdateUrl)
                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(false)
                .build()
                .checkNewApp(new UpdateCallback() {
                    @Override
                    protected UpdateAppBean parseJson(String json, Activity mActivity) {
                        return getBean(json);
                    }

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        Log.e(TAG, "hasNewApp--------------");
                    }

                    @Override
                    protected void noNewApp() {
                        Log.e(TAG, "noNewApp--------------");
                        super.noNewApp();
                    }

                });
    }

    private UpdateAppBean getBean(String json) {
        UpdateAppBean updateAppBean = new UpdateAppBean();
        try {
            JSONObject jObj = new JSONObject(json);
            JSONObject jsonObject = jObj.getJSONObject("data");

            boolean update = jsonObject.optInt("version_code") > 1;

            updateAppBean
                    //（必须）是否更新Yes,No
                    .setUpdate(update ? "Yes" : "No")
                    //（必须）新版本号，
                    .setVersionName(jsonObject.optString("version"))
                    .setVersionCode(jsonObject.optInt("version_code"))
                    //（必须）下载地址
                    .setApkFileUrl(jsonObject.optString("apk_file_url"))
                    //（必须）更新内容
                    .setUpdateLog(jsonObject.optString("content"))
                    //大小，不设置不显示大小，可以不设置
                    .setTargetSize(jsonObject.optString("target_size"))
                    //是否强制更新，可以不设置
                    .setConstraint(false)
                    //设置md5，可以不设置
                    .setNewMd5(jsonObject.optString("new_md51"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateAppBean;
    }


}