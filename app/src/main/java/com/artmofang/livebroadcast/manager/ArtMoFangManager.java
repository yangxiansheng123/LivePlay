package com.artmofang.livebroadcast.manager;

import android.support.annotation.NonNull;
import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.artmofang.livebroadcast.api.ApiStores;
import com.artmofang.livebroadcast.bean.LiveBroadcastListBean;
import com.artmofang.livebroadcast.bean.LiveCommentBean;
import com.artmofang.livebroadcast.bean.LoginBean;
import com.artmofang.livebroadcast.bean.ReplyCommentsBean;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by jjs1 on 2017/6/21.
 */

public class ArtMoFangManager {
    private static ArtMoFangManager instance;

    public static ArtMoFangManager getInstance() {
        if (null == instance) {
            instance = new ArtMoFangManager();
        }
        return instance;
    }

    private ArtMoFangManager() {

    }


    /**
     * 登录
     */
    public Subscriber<LoginBean> userLogin(@NonNull Map<String, String> map, @NonNull Subscriber<LoginBean> subscribe) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = builder.build();
        Observable<LoginBean> observable = NetServiceUtils.getService(ApiStores.class).userLogin(requestBody);
        new NetServiceUtils<LoginBean>().invoke(observable, userLogin, subscribe);
        return subscribe;
    }

    /**
     *
     */
    private Action1<LoginBean> userLogin = LoginBean -> {
        Log.e("========== ", "" + JSON.toJSONString(LoginBean));
    };


    /**
     * MainActivity直播列表
     */
    public Subscriber<LiveBroadcastListBean> liveBroadcastListBean(@NonNull Map<String, String> map, @NonNull Subscriber<LiveBroadcastListBean> subscribe) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = builder.build();
        Observable<LiveBroadcastListBean> observable = NetServiceUtils.getService(ApiStores.class).liveBroadcastListBean(requestBody);
        new NetServiceUtils<LiveBroadcastListBean>().invoke(observable, liveBroadcastListBean, subscribe);
        return subscribe;
    }

    /**
     *MainActivity直播列表
     */
    private Action1<LiveBroadcastListBean> liveBroadcastListBean = LiveBroadcastListBean -> {
        Log.e("========== ", "" + JSON.toJSONString(LiveBroadcastListBean));
    };



    /**
     * 直播评论内容
     */
    public Subscriber<LiveCommentBean> liveCommentBean(@NonNull Map<String, String> map, @NonNull Subscriber<LiveCommentBean> subscribe) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = builder.build();
        Observable<LiveCommentBean> observable = NetServiceUtils.getService(ApiStores.class).liveCommentBean(requestBody);
        new NetServiceUtils<LiveCommentBean>().invoke(observable, liveCommentBean, subscribe);
        return subscribe;
    }

    /**
     *直播评论内容
     */
    private Action1<LiveCommentBean> liveCommentBean = LiveCommentBean -> {
        Log.e("========== ", "" + JSON.toJSONString(LiveCommentBean));
    };



    /**
     * 回复评论
     */
    public Subscriber<ReplyCommentsBean> replyCommentsBean(@NonNull Map<String, String> map, @NonNull Subscriber<ReplyCommentsBean> subscribe) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = builder.build();
        Observable<ReplyCommentsBean> observable = NetServiceUtils.getService(ApiStores.class).replyCommentsBean(requestBody);
        new NetServiceUtils<ReplyCommentsBean>().invoke(observable, replyCommentsBean, subscribe);
        return subscribe;
    }

    /**
     *回复评论
     */
    private Action1<ReplyCommentsBean> replyCommentsBean = ReplyCommentsBean -> {
        Log.e("========== ", "" + JSON.toJSONString(ReplyCommentsBean));
    };

}
