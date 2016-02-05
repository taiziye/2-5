package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.Login;
import com.tangpo.lianfu.utils.CircularImage;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.Tencent;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements View.OnClickListener {

    private EditText user_name;
    private EditText user_pass;
    private Button login;

    private TextView forget;
    private TextView register;

    private CircularImage weibo;
    private CircularImage weixin;
    private CircularImage qq;

    private ProgressDialog pd = null;

    private Intent intent = null;

    //这里是QQ授权的实例的对象
    private static final String TAG = MainActivity.class.getName();
    public static String mAppid;
    private UserInfo mInfo;
    public static Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        String token = Configs.getCatchedToken(this);
        //判断用户是否登录，如果已登录，则跳过该页面
        if (token != null) {  //如果已登录
            //根据登录身份跳转到相应的界面
            Tools.gotoActivity(MainActivity.this, HomePageActivity.class);
            finish();
        }
        init();
    }

    private void init() {
        user_name = (EditText) findViewById(R.id.user_name);
        user_pass = (EditText) findViewById(R.id.user_pass);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        forget = (TextView) findViewById(R.id.forget);
        forget.setOnClickListener(this);
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        weibo = (CircularImage) findViewById(R.id.weibo);
        weibo.setOnClickListener(this);
        weixin = (CircularImage) findViewById(R.id.weixin);
        weixin.setOnClickListener(this);
        qq = (CircularImage) findViewById(R.id.qq);
        qq.setOnClickListener(this);

    }

    private void login() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String name = user_name.getText().toString();
        if (name.equals("")) {
            ToastUtils.showToast(this, getString(R.string.username_cannot_be_null), Toast.LENGTH_SHORT);
            return;
        }
        String pass = user_pass.getText().toString();
        if (pass.equals("")) {
            ToastUtils.showToast(this, getString(R.string.password_cannot_be_null), Toast.LENGTH_SHORT);
            return;
        }
        //String openId="";
        String kvs[] = new String[]{name, pass};

        String params = Login.packagingParam(kvs);

        System.out.println(Escape.unescape(params));

        pd = ProgressDialog.show(MainActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                try {
                    JSONObject jsonObject = result.getJSONObject("param");
                    String sessid = jsonObject.getString("session_id");
                    Configs.cacheToken(getApplicationContext(), sessid);
                    Configs.cacheUser(getApplicationContext(), jsonObject.toString());
                    intent = new Intent(MainActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                System.out.println(Escape.unescape(result.toString()));
                try {
                    if("2".equals(result.getString("status"))) {
                        Tools.showToast(MainActivity.this, getString(R.string.username_or_pwd_error));
                    }else{
                        Tools.showToast(MainActivity.this, getString(R.string.fail_to_login));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    public void register() {
        intent = new Intent(MainActivity.this, RegisterActivity.class);
        Tools.gatherActivity(this);
        startActivity(intent);
        finish();
    }

    public void forgetPassword() {
        intent=new Intent(MainActivity.this,ForgetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                Configs.cleanData(this);
                login();
                break;
            case R.id.register:
                register();
                break;
            case R.id.forget:
                forgetPassword();
                break;
            case R.id.weixin:
                //微信第三方登录
                Tools.showToast(this,getString(R.string.wechat_login_has_not_open));
                break;
            case R.id.weibo:
                //微博第三方登录
                Tools.showToast(this,getString(R.string.weibo_login_has_not_open));
                break;
            case R.id.qq:
                //qq第三方登录
                Tools.showToast(this,getString(R.string.qq_login_has_not_open));
                break;
        }
    }
}
