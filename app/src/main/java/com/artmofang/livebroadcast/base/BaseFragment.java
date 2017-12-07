package com.artmofang.livebroadcast.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.artmofang.livebroadcast.manager.OnSubscriber;
import com.artmofang.livebroadcast.manager.RememerConstants;
import com.tumblr.remember.Remember;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/9/14.
 */

public class BaseFragment extends Fragment implements OnSubscriber {


    public MySub sub;

    public MySub getSubscriber(int what) {
        return sub = new MySub(what);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCompleted(int what) {

    }

    @Override
    public void onError(Throwable e, int what) {

    }

    @Override
    public void onNext(Object object, int what) {

    }

    private class MySub extends Subscriber {

        private int what = -1;

        public MySub() {
        }

        public MySub(int what) {
            this.what = what;
        }

        @Override
        public void onCompleted() {
            Log.e("basefragment", "----------completed");
            BaseFragment.this.onCompleted(what);
        }

        @Override
        public void onError(Throwable e) {
            BaseFragment.this.onError(e, what);
            if (what == 50) {
                return;
            }
            if (e instanceof HttpException) {
                Log.e("eeeeeeeeeee", "" + ((HttpException) e).response().code());
                if (((HttpException) e).response().code() == 401) {
                    Remember.clear();
                }
            } else {//服务器返回的错误
//                ToastUtils.showShort("服务器错误");
            }
        }

        @Override
        public void onNext(Object object) {
            BaseFragment.this.onNext(object, what);
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

}