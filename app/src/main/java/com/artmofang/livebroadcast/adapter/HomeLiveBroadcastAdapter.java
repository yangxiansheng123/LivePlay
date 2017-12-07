package com.artmofang.livebroadcast.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.artmofang.livebroadcast.R;
import com.artmofang.livebroadcast.bean.LiveBroadcastListBean;
import com.artmofang.livebroadcast.utils.DateUtil;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/9/15.
 * 直播fragment
 */

public class HomeLiveBroadcastAdapter extends BaseQuickAdapter<LiveBroadcastListBean.DataBean, BaseViewHolder> {
    private Context mContext;
    private BeginToShowListener beginToShowListener;
    private String live_state;

    public void setBeginToShowListener(BeginToShowListener beginToShowListener) {
        this.beginToShowListener = beginToShowListener;
    }

    public HomeLiveBroadcastAdapter(@LayoutRes int layoutResId, @Nullable List<LiveBroadcastListBean.DataBean> data, Context context) {
        super(layoutResId, data);
        mContext = context;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, LiveBroadcastListBean.DataBean data) {
        TextView tv_liveBoradState = baseViewHolder.getView(R.id.tv_liveBoradState);//播放状态
        ImageView img_liveBroadPng = baseViewHolder.getView(R.id.img_liveBroadPng);//背景
        ImageView img_liveBoradState = baseViewHolder.getView(R.id.img_liveBoradState);//直播状态
        TextView tv_instName = baseViewHolder.getView(R.id.tv_instName);//机构直播主题
        TextView tv_courseType0 = baseViewHolder.getView(R.id.tv_courseType0);//机构直播类型
        TextView tv_courseType1 = baseViewHolder.getView(R.id.tv_courseType1);//机构直播类型
        TextView tv_courseType2 = baseViewHolder.getView(R.id.tv_courseType2);//机构直播类型
        TextView tv_beginToShow = baseViewHolder.getView(R.id.tv_beginToShow);//开播
        TextView tv_liveBroTime = baseViewHolder.getView(R.id.tv_liveBroTime);//开播时间
        TextView tv_joinPnum = baseViewHolder.getView(R.id.tv_joinPnum);//参与人数

//        tv_liveBoradState.setText(data.getCode());
        live_state = data.getLive_state();
        if (data.getLive_img() != null) {
            Glide.with(mContext)
//                .load("http://img4.imgtn.bdimg.com/it/u=3178868727,931461577&fm=200&gp=0.jpg")
                    .load(data.getLive_img())
                    .centerCrop()
                    .dontAnimate()//防止设置placeholder导致第一次不显示网络图片,只显示默认图片的问题
                    .error(R.mipmap.ic_launcher)
                    .into(img_liveBroadPng);
        }
        //0:预告,1:直播，2:其他
        if (live_state.equals("0")) {
            img_liveBoradState.setBackgroundResource(R.mipmap.livepage_foreshow);
        } else if (live_state.equals("1")) {
            img_liveBoradState.setBackgroundResource(R.mipmap.livepage_living);
        } else {
        }
        tv_instName.setText(data.getLive_name());
        tv_courseType0.setText(data.getLive_category());
//        tv_liveBroTime.setText(DateUtil.getDateToString2(Long.parseLong(data.getLive_end_time())));
        tv_liveBroTime.setText(data.getLive_end_time());
        tv_joinPnum.setText(data.getLive_num() + "人参与");

        tv_beginToShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginToShowListener.beginToShow(data.getLive_push(),data.getLive_id());
            }
        });

    }


    public interface BeginToShowListener {
        public void beginToShow(String livePush, String live_id);//直播
    }

}
