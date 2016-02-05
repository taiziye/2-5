package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetChatAccount;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemberInfoActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button send;

    private TextView user_name;
    private TextView contact_tel;
    private TextView rel_name;
    //    private EditText member_level;
//    private EditText id_card;
//
//    private TextView bank;
//    private ImageView select_bank;
//
//    private EditText bank_card;
//    private EditText bank_name;
//
    private Member member = null;
//
//    private ProgressDialog dialog=null;

//    private String userid=null;
//    private String password=null;
//
//    private String[] banklist=null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.deleteActivity(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.member_info_activity);
        Tools.gatherActivity(this);
        member = (Member) getIntent().getExtras().getSerializable("member");
//        userid=getIntent().getExtras().getString("userid");
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
//        edit = (Button) findViewById(R.id.edit);
//        edit.setOnClickListener(this);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);

        user_name = (TextView) findViewById(R.id.user_name);
        contact_tel = (TextView) findViewById(R.id.contact_tel);
        rel_name = (TextView) findViewById(R.id.rel_name);
        //member_level = (EditText) findViewById(R.id.member_level);
//        id_card = (EditText) findViewById(R.id.id_card);
//
//        bank = (TextView) findViewById(R.id.bank);
//        bank.setOnClickListener(this);
//        select_bank= (ImageView) findViewById(R.id.select_bank);
//        select_bank.setOnClickListener(this);
//
//        bank_card = (EditText) findViewById(R.id.bank_card);
//        bank_name = (EditText) findViewById(R.id.bank_name);

        user_name.setText(member.getUsername());
        contact_tel.setText(member.getPhone());
        rel_name.setText(member.getName());
        //member_level.setText("");
//        id_card.setText(member.getId_number());
//        bank.setText(member.getBank());
//        bank_card.setText(member.getBank_account());
//        bank_name.setText(member.getBank_name());
//
//        password= MD5Tool.md5(member.getPhone().substring(member.getPhone().length()-6));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
//            case R.id.edit:
//                editMember();
//                break;
            case R.id.send:
                //getAccount();
                break;
        }
    }
}

