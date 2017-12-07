package com.artmofang.livebroadcast.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.artmofang.livebroadcast.R;
import com.artmofang.livebroadcast.bean.LiveCommentBean;
import com.artmofang.livebroadcast.bean.LiveRoomBean;
import com.artmofang.livebroadcast.utils.DateUtil;
import com.bumptech.glide.Glide;
import com.tumblr.remember.Remember;

import java.util.List;

/**
 * Created by imahe001 on 2015/9/29.
 */
public class LiveRoomListviewAdapter extends BaseAdapter {

    private final String name;
    private Context context;
    private List<LiveCommentBean.DataBean> listData;


    public LiveRoomListviewAdapter(Context context) {
        this.context = context;
        name = Remember.getString("name",null);
    }

    public void setListData(List<LiveCommentBean.DataBean> listData) {
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size() > 0 ? listData.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_live_room_pattern, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


//        Glide.with(context)
//                .load(listDta.get(position).getAvatar())
//                .centerCrop()
//                .dontAnimate()//防止设置placeholder导致第一次不显示网络图片,只显示默认图片的问题
//                .placeholder(R.mipmap.default_glide)
//                .error(R.mipmap.default_glide)
//                .into(holder.img_evaluatePerson);
        String type = listData.get(position).getType();//type=0代表该评论使用户发送给机构的,1代表是当前机构发送的
        if (type.equals("0")) {
            holder.tv_commentName.setText(listData.get(position).getNickname());
            holder.tv_studentComment.setText(listData.get(position).getReply_infor());
            holder.tv_liveTime.setText(DateUtil.getDateToString2(Long.parseLong(listData.get(position).getAddtime())));
            holder.ll_reply.setVisibility(View.GONE);
            holder.img_isLiveMaster.setVisibility(View.GONE);
            holder.tv_studentComment.setVisibility(View.VISIBLE);
        } else if (type.equals("1")) {
            holder.ll_reply.setVisibility(View.VISIBLE);
            holder.img_isLiveMaster.setVisibility(View.VISIBLE);
            holder.tv_commentName.setText(name);
            holder.tv_replyName.setText("" + listData.get(position).getNickname() + " : ");
            holder.tv_replyContent.setText(listData.get(position).getReply_infor());
            holder.tv_liveTime.setText(DateUtil.getDateToString2(Long.parseLong(listData.get(position).getAddtime())));
            holder.tv_studentComment.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView img_isLiveMaster;//是否是主播
        TextView tv_commentName;
        TextView tv_liveTime;//时间
        TextView tv_studentComment;//学生评论
        LinearLayout ll_reply;//主播回复评论(visible,gone)
        TextView tv_replyContent;//主播回复内容
        TextView tv_replyName;//主播回复的人

        public ViewHolder(View view) {
            img_isLiveMaster = (ImageView) view.findViewById(R.id.img_isLiveMaster);
            tv_commentName = (TextView) view.findViewById(R.id.tv_commentName);
            tv_liveTime = (TextView) view.findViewById(R.id.tv_liveTime);
            tv_studentComment = (TextView) view.findViewById(R.id.tv_studentComment);
            ll_reply = (LinearLayout) view.findViewById(R.id.ll_reply);
            tv_replyContent = (TextView) view.findViewById(R.id.tv_replyContent);
            tv_replyName = (TextView) view.findViewById(R.id.tv_replyName);
        }
    }


}
