package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/12/16.
 */
public class DiscountEditActivity extends Activity implements View.OnClickListener {
    private Button back;
    private Button commit;
    private EditText name;
    private EditText discount;

    private ProgressDialog dialog;

    private String user_id;
    private String store_id;
    private Discount dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_editdiscount);
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);
        name = (EditText) findViewById(R.id.name);
        discount = (EditText) findViewById(R.id.discount);

        if(getIntent() != null) {
            user_id = getIntent().getStringExtra("userid");
            store_id = getIntent().getStringExtra("storeid");
            dis = (Discount) getIntent().getSerializableExtra("discount");

            name.setText(dis.getDesc());
            discount.setText(dis.getDiscount());
        } else {
            name.setText("");
            discount.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.commit:
                String nameStr = name.getText().toString().trim();
                String discountStr = discount.getText().toString().trim();
                updateDiscount(nameStr, discountStr);
                break;
        }
    }

    private void updateDiscount(final String name, final String discount) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }


        String kvs[] = new String[]{user_id, store_id, dis.getId(), name, discount};
        String params = com.tangpo.lianfu.parms.EditDiscount.packagingParam(getApplicationContext(), kvs);
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                //
                Tools.showToast(getApplicationContext(), getString(R.string.edit_success));
                Intent intent = new Intent();
                dis.setDesc(name);
                dis.setDiscount(discount);
                intent.putExtra("discount", dis);
                setResult(RESULT_OK, intent);
                DiscountEditActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if(status.equals("1")){
                        ToastUtils.showToast(DiscountEditActivity.this,result.getString("info"), Toast.LENGTH_SHORT);
                    }else if(status.equals("2")){
                        ToastUtils.showToast(DiscountEditActivity.this,getString(R.string.format_error),Toast.LENGTH_SHORT);
                    }else if(status.equals("9")){
                        ToastUtils.showToast(DiscountEditActivity.this, getString(R.string.login_timeout), Toast.LENGTH_SHORT);
                        Configs.cleanData(DiscountEditActivity.this);
                        Intent intent = new Intent(DiscountEditActivity.this, MainActivity.class);
                        startActivity(intent);
                        DiscountEditActivity.this.finish();
                    }else{
                        ToastUtils.showToast(DiscountEditActivity.this,result.getString("info"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }
}
