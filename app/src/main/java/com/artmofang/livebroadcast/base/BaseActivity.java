package com.artmofang.livebroadcast.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.artmofang.livebroadcast.manager.OnSubscriber;
import com.artmofang.livebroadcast.manager.RememerConstants;
import com.tumblr.remember.Remember;

import java.io.ByteArrayOutputStream;
import java.io.File;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public class BaseActivity extends AppCompatActivity implements OnSubscriber{


    private static Toast toast;
    public MySubscriber subscriber;

    public MySubscriber getSubscriber(int what) {
        return subscriber = new MySubscriber(what);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 显示提示信息
     *
     * @param msg
     */
    public void showToast(String msg) {

        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }


    @Override
    public void onCompleted(int what) {

    }

    @Override
    public void onError(Throwable e, int what) {
        if (e instanceof HttpException) {
            if (((HttpException) e).response().code() == 401) {
//                Remember.clear();
//                this.startActivity(new Intent(this, LoginActivity.class));
            }
        } else {//服务器返回的错误
            // ToastUtils.showShort((this,"服务器错误");
//            ToastUtils.showShort((this, ((HttpException) e).message());
        }
    }

    @Override
    public void onNext(Object object, int what) {

    }

   

    public class MySubscriber extends Subscriber {

        private int what = -1;

        public MySubscriber() {
        }

        public MySubscriber(int what) {
            this.what = what;
        }

        @Override
        public void onCompleted() {
//            Log.e("baseActivity","----------completed");
            BaseActivity.this.onCompleted(what);
        }

        @Override
        public void onError(Throwable e) {
            BaseActivity.this.onError(e, what);
        }

        @Override
        public void onNext(Object object) {
//            Log.e("baseActivity","----------onNext");
            BaseActivity.this.onNext(object, what);

        }
    }

    /**
     * 获取用户token
     *
     * @return
     */
    public String getToken() {
        String token = Remember.getString(RememerConstants.USER_TOKEN, "");
        if (token.equals("")) {

            return "";
        }
        return Remember.getString(RememerConstants.USER_TOKEN, "");
    }

    /**
     * 用户
     *
     * @return
     */
    public String getAccount() {
        String token = Remember.getString(RememerConstants.ACCOUNT, "");
        if (token.equals("")) {

            return "";
        }
        return Remember.getString(RememerConstants.ACCOUNT, "");
    }


    /**
     * 用户id
     *
     * @return
     */
    public String getUid() {
        String token = Remember.getString(RememerConstants.USERID, "");
        if (token.equals("")) {

            return "";
        }
        return Remember.getString(RememerConstants.USERID, "");
    }

   
    /**
     * 背景透明度设置
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }
}
