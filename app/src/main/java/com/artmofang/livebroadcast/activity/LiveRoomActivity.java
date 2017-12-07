package com.artmofang.livebroadcast.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.artmofang.livebroadcast.R;
import com.artmofang.livebroadcast.adapter.LiveRoomListviewAdapter;
import com.artmofang.livebroadcast.base.BaseActivity;
import com.artmofang.livebroadcast.bean.LiveCommentBean;
import com.artmofang.livebroadcast.bean.ReplyCommentsBean;
import com.artmofang.livebroadcast.manager.ArtMoFangManager;
import com.artmofang.livebroadcast.manager.Constants;
import com.artmofang.livebroadcast.utils.LoadingDialogUtils;
import com.artmofang.livebroadcast.utils.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.bottomdialog.BottomDialog;

/**
 * 直播间
 */
public class LiveRoomActivity extends BaseActivity implements TextWatcher {

    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_comment)
    TextView tvComment;
    @Bind(R.id.img_liveBg)//播放背景img
            ImageView imgLiveBg;
    @Bind(R.id.lv_liveInteraction)
    ListView lvLiveInteraction;
    @Bind(R.id.tv_listenNumber)//收听人数
            TextView tvListenNumber;
    private BottomDialog bootomDialog;
    private EditText et_comment;
    private TextView publish;
    private LiveRoomListviewAdapter adapter;
    private List<LiveCommentBean.DataBean> listData;
    private String liveId = "", fld = "", nickName = "";
    private Dialog loadingDialogUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        ButterKnife.bind(this);
        liveId = getIntent().getStringExtra("live_id");
        initCtrl();
        toolbarTitle.setText("直播间互动");
        initView();
        loadingDialogUtils = LoadingDialogUtils.createLoadingDialog(LiveRoomActivity.this, "加载数据中...");
        postCommmentData();
    }

    private void initCtrl() {
        //头部toolbar的  设置
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveRoomActivity.this.finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化数据
     */
    private void initView() {
        listData = new ArrayList<>();
        adapter = new LiveRoomListviewAdapter(this);
        adapter.setListData(listData);
        lvLiveInteraction.setAdapter(adapter);

        lvLiveInteraction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                nickName = l
                showDialog();
                liveId = listData.get(position).getLive_id();
                fld = listData.get(position).getFid();
                nickName = listData.get(position).getNickname();
                if (fld == null) {
                    fld = "";
                }
            }
        });
    }


    /**
     * 评论
     *
     * @param view
     */
    @OnClick({R.id.tv_comment, R.id.fl_living})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_comment://评论
                showDialog();
                nickName = "All";
                break;
            case R.id.fl_living://进入直播
                Intent intent = new Intent(LiveRoomActivity.this, LiveBroadcastActivity.class);
                intent.putExtra("liveRoom", "liveRoom");
                startActivity(intent);
                break;

        }
    }

    /**
     * 评论
     */
    private void showDialog() {
        bootomDialog = (BottomDialog) BottomDialog.create(getSupportFragmentManager())
                .setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        initViewBottom(v);
                    }
                })
                .setLayoutRes(R.layout.activity_comment_pattern)
                .setDimAmount(0.5f)
                .setTag("BottomDialog")
                .setCancelOutside(false)
                .show();
    }

    /**
     * 评论
     *
     * @param v
     */
    private void initViewBottom(View v) {
        et_comment = (EditText) v.findViewById(R.id.et_comment);
        if (nickName != null) {
            et_comment.setHint("@" + nickName);
        } else {
            et_comment.setHint("@" + nickName);
        }
        String replyComment = et_comment.getText().toString();
        //文本输入框改变监听，必须在定位完成之后
        et_comment.addTextChangedListener(this);
        publish = (TextView) v.findViewById(R.id.tv_publish);//发布
        ImageView imgDownClose = (ImageView) v.findViewById(R.id.img_downClose);
        imgDownClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bootomDialog.dismiss();
                et_comment.setText("");
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_comment.getText().toString().equals("")) {
                    fld = "";
                    postReplyCommmentData(et_comment.getText().toString());
                } else {
                    T.show(LiveRoomActivity.this, "请输入发布评论内容", 1000);
                }
            }
        });
    }


    /**
     * 直播评论回复列表
     */
    private void postCommmentData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("liveId", liveId);
        map.put("merId", getUid());
        ArtMoFangManager.getInstance().liveCommentBean(map, getSubscriber(Constants.LIVECONTENT));
    }


    /**
     * 回复评论
     *
     * @param replyComment
     */
    private void postReplyCommmentData(String replyComment) {
        HashMap<String, String> map = new HashMap<>();
        map.put("merId", getUid());//机构id
//        map.put("userId", "");//提交评论的用户id
        map.put("replyContent", replyComment);//回复内容
        map.put("fld", fld);//回复对象id
        map.put("liveId", liveId);//直播间id
//        map.put("nickName", "@" + nickName);//提交评论的普通用户昵称(机构不填)
        map.put("type", "1");//type 1 代表机构回复
        ArtMoFangManager.getInstance().replyCommentsBean(map, getSubscriber(Constants.REPLYCOMMENT));
    }


    @Override
    public void onCompleted(int what) {
        super.onCompleted(what);
        LoadingDialogUtils.closeDialog(loadingDialogUtils);
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
            case Constants.LIVECONTENT:
                LiveCommentBean liveCommentBean = (LiveCommentBean) object;
                if (liveCommentBean.getCode().equals("10000")) {
//                    T.show(LiveRoomActivity.this, "" + liveCommentBean.getMessage(), 1000);
                    if (liveCommentBean.getData().size() > 0) {
                        listData.clear();
                        listData.addAll(liveCommentBean.getData());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    T.show(LiveRoomActivity.this, "" + liveCommentBean.getMessage(), 1000);
                }
                break;
            case Constants.REPLYCOMMENT:
                ReplyCommentsBean replyComment = (ReplyCommentsBean) object;
                if (replyComment.getCode().equals("10000")) {
                    T.show(LiveRoomActivity.this, "" + replyComment.getMessage(), 1000);
                    bootomDialog.dismiss();
                    lvLiveInteraction.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    postCommmentData();
                } else {
                    T.show(LiveRoomActivity.this, "" + replyComment.getMessage(), 1000);
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
            publish.setBackground(getResources().getDrawable(R.drawable.solid_rectangle_shape_gray));
        } else {
            publish.setBackground(getResources().getDrawable(R.drawable.solid_rectangle_shape_blue));
            et_comment.setTextColor(getResources().getColor(R.color.text_color_000000));
        }
    }
}
