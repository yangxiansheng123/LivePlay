package com.artmofang.livebroadcast.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.artmofang.livebroadcast.MainActivity;
import com.artmofang.livebroadcast.R;
import com.artmofang.livebroadcast.base.BaseActivity;
import com.artmofang.livebroadcast.bean.LoginBean;
import com.artmofang.livebroadcast.manager.ArtMoFangManager;
import com.artmofang.livebroadcast.manager.Constants;
import com.artmofang.livebroadcast.manager.RememerConstants;
import com.artmofang.livebroadcast.utils.RxRegTool;
import com.artmofang.livebroadcast.utils.T;
import com.tumblr.remember.Remember;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements TextWatcher {

    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_login_account)
    EditText etLoginAccount;
    @Bind(R.id.et_login_pwd)
    EditText etLoginPwd;
    @Bind(R.id.check_remberPwd)
    CheckBox checkRemberPwd;
    @Bind(R.id.btn_login)
    Button btnLogin;
    private int isRemberPwd = 0;//是否记住密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //文本输入框改变监听，必须在定位完成之后
        etLoginAccount.addTextChangedListener(this);
    }

    /**
     * 登录
     *
     * @param view
     */
    @OnClick({R.id.btn_login, R.id.check_remberPwd})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        String account = etLoginAccount.getText().toString();
        String pwd = etLoginPwd.getText().toString();
        switch (view.getId()) {
            case R.id.btn_login://登录
                if (TextUtils.isEmpty(account)) {
                    T.show(LoginActivity.this, "请输入账号", 1000);
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    T.show(LoginActivity.this, "请输入密码", 1000);
                    return;
                }
//                if (RxRegTool.isMobile(account)) {
//
//                } else  if (RxRegTool.isEmail(account)) {
//
//                }else {
//                    T.show(LoginActivity.this, "请输入正确的手机号码", 1000);
//                    etLoginAccount.setText("");
//                    return;
//                }
                if (pwd.length() >= 6) {

                } else {
                    T.show(LoginActivity.this, "密码不可小于6位数", 1000);
                    return;
                }
                HashMap<String, String> map = new HashMap<>();
                map.put("account", account);
                map.put("password", pwd);
                ArtMoFangManager.getInstance().userLogin(map, getSubscriber(Constants.USERLOGIN));
                break;
            case R.id.check_remberPwd:
                if (isRemberPwd == 0) {
                    if (TextUtils.isEmpty(pwd)) {
                        T.show(LoginActivity.this, "请输入密码", 1000);
                        checkRemberPwd.setChecked(false);
                        return;
                    }
                    checkRemberPwd.setChecked(true);
                    T.show(LoginActivity.this, "记住密码", 1000);
                    Remember.putString(RememerConstants.PASSWORD, pwd);
                    Remember.putString(RememerConstants.ACCOUNT, account);
                    isRemberPwd++;
                } else {
                    isRemberPwd--;
                    T.show(LoginActivity.this, "取消密码", 1000);
                    Remember.putString(RememerConstants.PASSWORD, "");
                    Remember.putString(RememerConstants.ACCOUNT, "");
                }
                break;

        }
    }

    @Override
    public void onCompleted(int what) {
        super.onCompleted(what);
    }


    @Override
    public void onError(Throwable e, int what) {
        super.onError(e, what);
    }


    @Override
    public void onNext(Object object, int what) {
        super.onNext(object, what);
        switch (what) {
            case Constants.USERLOGIN:
                LoginBean loginBean = (LoginBean) object;
                if (loginBean.getCode().equals("10000")) {
                    Intent intent = new Intent();
//                    intent.putExtra("name",loginBean.getData().getName());
//                    intent.putExtra("headPicture",loginBean.getData().getHead_picture());
                    intent.setClass(LoginActivity.this,MainActivity.class);
                    Remember.putString(RememerConstants.USERID, loginBean.getData().getMer_id());
                    Remember.putString("name", loginBean.getData().getName());
                    Remember.putString("headPicture", loginBean.getData().getHead_picture());
                    startActivity(intent);
                } else {
                    T.show(LoginActivity.this, "" + loginBean.getMessage(), 100);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0 || "".equals(s.toString())) {

            btnLogin.setBackground(getResources().getDrawable(R.drawable.btn_login_bg_gray));
            btnLogin.setEnabled(false);

        } else {
            if (RxRegTool.isMobile(s.toString())) {
                btnLogin.setBackground(getResources().getDrawable(R.drawable.btn_login_bg));
                btnLogin.setEnabled(true);
            } else if (RxRegTool.isEmail(s.toString())) {
                btnLogin.setBackground(getResources().getDrawable(R.drawable.btn_login_bg));
                btnLogin.setEnabled(true);
            } else {
                btnLogin.setBackground(getResources().getDrawable(R.drawable.btn_login_bg_gray));
                btnLogin.setEnabled(false);
            }


        }
    }
}
