package com.artmofang.livebroadcast;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.artmofang.livebroadcast.activity.LiveBroadcastActivity;
import com.artmofang.livebroadcast.activity.LiveRoomActivity;
import com.artmofang.livebroadcast.adapter.HomeLiveBroadcastAdapter;
import com.artmofang.livebroadcast.base.BaseActivity;
import com.artmofang.livebroadcast.bean.LiveBroadcastListBean;
import com.artmofang.livebroadcast.manager.ArtMoFangManager;
import com.artmofang.livebroadcast.manager.Constants;
import com.artmofang.livebroadcast.utils.CircleImageView;
import com.artmofang.livebroadcast.utils.LoadingDialogUtils;
import com.artmofang.livebroadcast.utils.T;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tumblr.remember.Remember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements HomeLiveBroadcastAdapter.BeginToShowListener {

    @Bind(R.id.img_head)
    CircleImageView imgHead;
    @Bind(R.id.tv_account)
    TextView tvAccount;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipe_to_load)
    SwipeToLoadLayout swipeToLoad;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private boolean isRefresh = false;
    private int nextPage = 1;
    private HomeLiveBroadcastAdapter adapter;
    private long firstTime = 0;
    private String name, headPicture;
    private Dialog loadingDialogUtils;
    private List<LiveBroadcastListBean.DataBean> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
//        String sdkver = TXLiveBase.getSDKVersionStr();
//        Log.e("1111111111111111111", "liteav sdk version is : " + sdkver);
        name = Remember.getString("name",null);
        headPicture = Remember.getString("headPicture",null);
        toolbarTitle.setText("直播列表");
        tvAccount.setText(name);

        if (headPicture != null) {
            Glide.with(this)
                    .load(headPicture)
                    .centerCrop()
                    .dontAnimate()//防止设置placeholder导致第一次不显示网络图片,只显
                    .error(R.mipmap.ic_launcher)
                    .into(imgHead);
        }
        Remember.putString("name", name);
        loadingDialogUtils = LoadingDialogUtils.createLoadingDialog(MainActivity.this, "加载数据中...");
        postData();
    }

    private void initCtrl() {
        //头部toolbar的  设置
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        listData = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        swipeTarget.addItemDecoration(new DividerItemDecoration(this, 0));
        swipeTarget.setLayoutManager(manager);
        adapter = new HomeLiveBroadcastAdapter(R.layout.activity_live_broadcast_pattern, listData, this);
        adapter.setBeginToShowListener(this);
        swipeTarget.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        swipeToLoad.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isRefresh = true;
                        nextPage = 1;
                        listData.clear();
                        postData();
                        swipeToLoad.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        swipeToLoad.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isRefresh = false;
                        nextPage++;
                        postData();
                        swipeToLoad.setLoadingMore(false);
                    }
                }, 1000);
            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("live_id", listData.get(position).getLive_id());
                intent.setClass(MainActivity.this, LiveRoomActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 请求数据
     */
    private void postData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("merId", getUid());
        ArtMoFangManager.getInstance().liveBroadcastListBean(map, getSubscriber(Constants.HOMELIVEBROADCAST));
    }


    @Override
    public void onCompleted(int what) {
        super.onCompleted(what);
    }

    @Override
    public void onError(Throwable e, int what) {
        super.onError(e, what);
        LoadingDialogUtils.closeDialog(loadingDialogUtils);
    }

    @Override
    public void onNext(Object object, int what) {
        super.onNext(object, what);
        LoadingDialogUtils.closeDialog(loadingDialogUtils);
        switch (what) {
            case Constants.HOMELIVEBROADCAST:
                LiveBroadcastListBean listBean = (LiveBroadcastListBean) object;
                if (listBean.getCode().equals("10000")) {
                    if (listBean.getData().size() > 0) {
                        listData.clear();
                        listData.addAll(listBean.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                       
                    }
                } else {
                    T.show(MainActivity.this, "" + listBean.getMessage(), 1000);
                }
                break;
        }
    }

    /**
     * 直播
     *
     * @param live_id
     * @param live_push
     */
    @Override
    public void beginToShow(String live_push, String live_id) {

        Intent intent = new Intent();
        intent.putExtra("liveRoom", "zb");
        intent.putExtra("live_push", live_push);
        intent.putExtra("live_id", live_id);
        intent.setClass(MainActivity.this, LiveBroadcastActivity.class);
        startActivity(intent);
    }


    /**
     * 监听手机物理键（返回）
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                T.showShort(MainActivity.this, "再按一次退出程序");
                firstTime = secondTime;//更新firstTime
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
