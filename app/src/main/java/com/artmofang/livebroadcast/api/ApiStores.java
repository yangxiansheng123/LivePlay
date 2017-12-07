package com.artmofang.livebroadcast.api;


import com.artmofang.livebroadcast.bean.LiveBroadcastListBean;
import com.artmofang.livebroadcast.bean.LiveCommentBean;
import com.artmofang.livebroadcast.bean.LoginBean;
import com.artmofang.livebroadcast.bean.ReplyCommentsBean;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;


/**
 * Created by Administrator on 2017/9/13.
 */

public interface ApiStores {
    public static String COMMEN = "app.php?";

    //登录
    @POST(COMMEN + "c=Organizationlogin_app&a=liveplay_login")
    Observable<LoginBean> userLogin(@Body RequestBody data);

    //MainActivity直播列表
    @POST(COMMEN + "c=Liveplay_app&a=get_merchant_liveplay_list")
    Observable<LiveBroadcastListBean> liveBroadcastListBean(@Body RequestBody data);

    //直播评论内容
    @POST(COMMEN + "c=Liveplay_app&a=get_liveplay_comments")
    Observable<LiveCommentBean> liveCommentBean(@Body RequestBody data);

    //回复评论
    @POST(COMMEN + "c=Liveplay_app&a=reply_comment")
    Observable<ReplyCommentsBean> replyCommentsBean(@Body RequestBody data);

}
