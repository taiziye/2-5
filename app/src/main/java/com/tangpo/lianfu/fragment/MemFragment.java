package com.tangpo.lianfu.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.ui.BoundOrRegister;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.ui.PersonalInfoActivity;
import com.tangpo.lianfu.ui.UpdatePasswordActivity;
import com.tangpo.lianfu.utils.CircularImage;
import com.tangpo.lianfu.utils.Tools;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.Tencent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemFragment extends Fragment implements View.OnClickListener {

    private final static int GET_STORE_INFO = 2;

    private final static int GET_OPENID = 3;

    public static final int REQUEST_CODE = 7;


    private Button double_code;
    private Button chat;
    private Button login_out;

    private CircularImage img;
    private ImageView next;

    private TextView power;
    private TextView name;
    private TextView user_name;
    private LinearLayout personal_info;
    private LinearLayout modify_pass;
    private TextView remainder;
    private UserEntity user;

    private SharedPreferences preferences=null;
    private String logintype=null;

    private ProgressDialog dialog=null;
    private FindStore store=null;

    private String user_id=null;
    private Gson gson=null;

    private Button bind_weibo;
    private Button bind_weixin;
    private Button bind_qq;

    //这里是微博授权的实例对象


    //这里是QQ授权的实例的对象
    private static final String TAG = MainActivity.class.getName();
    public static String mAppid;
    private UserInfo mInfo;
    public static Tencent mTencent;

    //这里是微信授权的实例的对象

    private String isbindwb;
    private String isbindwx;
    private String isbindqq;

//    @Override
//    public void onDestroyOptionsMenu() {
//        super.onDestroyOptionsMenu();
//        Tools.closeActivity();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (view == null) {
            view = inflater.inflate(R.layout.member_fragment, container, false);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        init(view);
        return view;
    }

    private void init(View view) {
        gson=new Gson();
        preferences=getActivity().getSharedPreferences(Configs.APP_ID, Context.MODE_PRIVATE);
        logintype=preferences.getString(Configs.KEY_LOGINTYPE, "");
//        userEntity= (UserEntity) getArguments().getSerializable("user");
//        user_id=userEntity.getUser_id();
        String str = preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject = new JSONObject(str);
            user = gson.fromJson(jsonObject.toString(), UserEntity.class);
            user_id=user.getUser_id();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double_code = (Button) view.findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        chat = (Button) view.findViewById(R.id.chat);
        chat.setOnClickListener(this);
        login_out = (Button) view.findViewById(R.id.login_out);
        login_out.setOnClickListener(this);

        img = (CircularImage) view.findViewById(R.id.img);

        power = (TextView) view.findViewById(R.id.power);
        name = (TextView) view.findViewById(R.id.name);
        user_name = (TextView) view.findViewById(R.id.user_name);
        personal_info = (LinearLayout) view.findViewById(R.id.personal_info);
        personal_info.setOnClickListener(this);
        modify_pass = (LinearLayout) view.findViewById(R.id.modify_pass);
        modify_pass.setOnClickListener(this);
        remainder = (TextView) view.findViewById(R.id.remainder);

        Tools.setPhoto(getActivity(), user.getPhoto(), img);
        name.setText(user.getName());
        if (logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
            power.setText("游客");
        }else {
            power.setText("会员");
        }

        user_name.setText("");

        float remain_money=Float.parseFloat(user.getMoney());
        DecimalFormat formatter=new DecimalFormat("##0.00");
        remainder.setText(formatter.format(remain_money));

        bind_weibo= (Button) view.findViewById(R.id.bind_weibo);
        bind_weixin= (Button) view.findViewById(R.id.bind_wexin);
        bind_qq= (Button) view.findViewById(R.id.bind_qq);

        bind_weibo.setOnClickListener(this);
        bind_weixin.setOnClickListener(this);
        bind_qq.setOnClickListener(this);

        if("1".equals(user.getBindwb())){
            bind_weibo.setText(getString(R.string.unbind));
            bind_weibo.setBackgroundResource(R.drawable.unbind);
        }else{
            bind_weibo.setText(getString(R.string.bind));
            bind_weibo.setBackgroundResource(R.drawable.bind);
        }
        if("1".equals(user.getBindwx())){
            bind_weixin.setText(getString(R.string.unbind));
            bind_weixin.setBackgroundResource(R.drawable.unbind);
        }else{
            bind_weixin.setText(getString(R.string.bind));
            bind_weixin.setBackgroundResource(R.drawable.bind);
        }
        if("1".equals(user.getBindqq())){
            bind_qq.setText(getString(R.string.unbind));
            bind_qq.setBackgroundResource(R.drawable.unbind);
        }else{
            bind_qq.setText(getString(R.string.bind));
            bind_qq.setBackgroundResource(R.drawable.bind);
        }

        isbindwb=user.getBindwb();
        isbindwx=user.getBindwx();
        isbindqq=user.getBindqq();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.double_code:
                //扫描二维码
                Tools.showToast(getActivity(),getString(R.string.double_code_scan_has_not_open));
                break;
            case R.id.chat:
                Tools.showToast(getActivity(),getString(R.string.chat_has_not_open));
                break;
            case R.id.next:
                break;
            case R.id.personal_info:
                intent = new Intent(getActivity(), PersonalInfoActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("flag", "1");
//                if(logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
//                    intent=new Intent(getActivity(),BoundOrRegister.class);
//                }else{
//                    intent = new Intent(getActivity(), PersonalInfoActivity.class);
//                    intent.putExtra("user", userEntity);
//                }
                startActivity(intent);
                //Tools.showToast(getActivity(), "请期待下一个版本");
                break;
            case R.id.modify_pass:
                if(logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
                    intent=new Intent(getActivity(),BoundOrRegister.class);
                }else{
                    intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                    intent.putExtra("user", user);
                }
                startActivity(intent);
                break;
            case R.id.login_out:
                Configs.cleanData(getActivity());
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;

            case R.id.bind_weibo:
                Tools.showToast(getActivity(),getString(R.string.weibo_login_has_not_open));
                break;
            case R.id.bind_wexin:
                Tools.showToast(getActivity(),getString(R.string.wechat_login_has_not_open));
                break;
            case R.id.bind_qq:
                Tools.showToast(getActivity(),getString(R.string.qq_login_has_not_open));
                break;
        }
    }
}
