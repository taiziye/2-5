package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.RegisterMember;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class PersonalMsgActivity extends Activity implements View.OnClickListener {

    private final static int SCANNIN_GREQUEST_CODE = 1;
    private Button double_code;
    private Button next;

    private TextView text;

    private EditText user_name;
    private EditText pass;
    private EditText check_pass;
    private EditText referee;
    private EditText storeid;
    private EditText service;
    private TextView link;

    private CheckBox check;
    private ProgressDialog dialog = null;

    private String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.personal_msg_activity);

        init();
    }

    private void init() {
        double_code= (Button) findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);

        text = (TextView) findViewById(R.id.text);
        text.setText(getResources().getString(R.string.personal_info));

        user_name = (EditText) findViewById(R.id.user_name);
        pass = (EditText) findViewById(R.id.pass);
        check_pass = (EditText) findViewById(R.id.check_pass);
        referee = (EditText) findViewById(R.id.referee);
        storeid = (EditText) findViewById(R.id.store_id);
        service = (EditText) findViewById(R.id.service);

        link = (TextView) findViewById(R.id.link);
        link.setOnClickListener(this);

        check = (CheckBox) findViewById(R.id.check);
        check.setOnClickListener(this);

        phone = getIntent().getStringExtra("tel");
    }

    private void postPersonalInfo() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        final String username = user_name.getText().toString();
        final String password = pass.getText().toString();
        String confirm_pass=check_pass.getText().toString();
        if(username.length() == 0||username=="") {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_username));
            return;
        }
        if(password.length() == 0||password=="") {
            Tools.showToast(getApplicationContext(),getString(R.string.please_input_password));
            return;
        }
        if(confirm_pass.length()==0||confirm_pass==""){
            Tools.showToast(getApplicationContext(),getString(R.string.please_confirm_password));
            return;
        }
        if (!TextUtils.equals(password, confirm_pass)) {
            Tools.showToast(getApplicationContext(), getString(R.string.password_not_matched));
            return;
        }
        if(!check.isChecked()){
            Tools.showToast(getApplicationContext(),getString(R.string.please_aggree_register_protocol));
            return;
        }
        final String phone = Configs.getCatchedPhoneNum(PersonalMsgActivity.this);
        String service_center = service.getText().toString();
        String store_id = storeid.getText().toString();
        String referrer = referee.getText().toString();
        String sex = "";
        String birth = "";
        String qq = "";
        String email = "";
        String address = "";
        String bank_account = "";
        String bank_name = "";
        String bank = "中国银行";
        String bank_address = "";

        dialog = ProgressDialog.show(PersonalMsgActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{username, password, phone, service_center, store_id, referrer
                , sex, birth, qq, email, address, bank_account, bank_name, bank, bank_address};
        String params = RegisterMember.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Configs.cleanData(PersonalMsgActivity.this);
                System.out.println(result.toString());
                ToastUtils.showToast(PersonalMsgActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT);

                try {
                    JSONObject object = result.getJSONObject("param");
                    Configs.cacheUser(getApplicationContext(), object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(PersonalMsgActivity.this, RegisterSuccessActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("passwd", password);
                intent.putExtra("tel", phone);
                Tools.gatherActivity(PersonalMsgActivity.this);
                startActivity(intent);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    //Tools.handleResult(PersonalMsgActivity.this, result.getString("status"));
                    if("300".equals(result.getString("status"))) {
                        Tools.showToast(PersonalMsgActivity.this, result.getString("info"));
                    } else if("2".equals(result.getString("status"))) {
                        Tools.showToast(PersonalMsgActivity.this, "格式有误");
                    } else if("9".equals(result.getString("status"))) {
                        Tools.showToast(PersonalMsgActivity.this, getString(R.string.login_timeout));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.double_code:
                Tools.showToast(PersonalMsgActivity.this,getString(R.string.double_code_scan_has_not_open));
                break;
            case R.id.next:
                postPersonalInfo();
                break;
            case R.id.check:
                if (check.isChecked()) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);
                }
                break;
            case R.id.link:
                intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri url = Uri.parse("http://www.51xfzf.com/lfindex/helpinfo.aspx?id=5");
                intent.setData(url);
                startActivity(intent);
                break;
        }
    }
}
