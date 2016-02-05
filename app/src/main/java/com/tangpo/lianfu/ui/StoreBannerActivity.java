package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ShopImgAdapter;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.DeleteStorePicture;
import com.tangpo.lianfu.parms.UploadStorePicture;
import com.tangpo.lianfu.utils.Tools;
import com.tangpo.lianfu.utils.UploadImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by shengshoubo on 2016/1/18.
 */
public class StoreBannerActivity extends Activity implements View.OnClickListener{
    private Button back;
    private Button add;
    private Button delete;

    private ImageView imageView=null;

    private String user_id=null;
    private String store_id=null;

    private ProgressDialog dialog=null;
    private String serverImgPath=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_page);

        Intent intent=getIntent();
        serverImgPath= intent.getStringExtra("imgPath");
        user_id=intent.getStringExtra("user_id");
        store_id=intent.getStringExtra("store_id");
        init();
    }

    private void init(){
        imageView= (ImageView) findViewById(R.id.ad);
        imageView.setOnClickListener(this);
        Tools.setPhoto(this, serverImgPath, imageView);
        back= (Button) findViewById(R.id.back);
        add= (Button) findViewById(R.id.add);
        delete= (Button) findViewById(R.id.delete);
        back.setOnClickListener(this);
        add.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    private void deleteStoreImg(){
        if(!Tools.checkLAN()) {
            Tools.showToast(this, "网络未连接，请联网后重试");
            return;
        }
        dialog=ProgressDialog.show(this,getString(R.string.connecting),getString(R.string.please_wait));
        String[] kvs=new String[]{store_id,user_id,"0","0"};
        String param= DeleteStorePicture.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                imageView.setImageResource(R.drawable.logo);
                Tools.showToast(StoreBannerActivity.this,getString(R.string.delete_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if("1".equals(status)){
                        Tools.showToast(StoreBannerActivity.this,getString(R.string.delete_failed));
                    }else if("10".equals(status)){
                        Tools.showToast(StoreBannerActivity.this, getString(R.string.server_exception));
                    }else {
                        Tools.showToast(StoreBannerActivity.this, getString(R.string.input_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },param);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.back:
                this.finish();
                break;
            case R.id.add:
                Tools.showToast(StoreBannerActivity.this,getString(R.string.can_not_add_pic));
                break;
            case R.id.delete:
                deleteStoreImg();
                break;
            case R.id.ad:
                break;
        }
    }
}
