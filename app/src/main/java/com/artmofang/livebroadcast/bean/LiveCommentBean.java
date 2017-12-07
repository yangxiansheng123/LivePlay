package com.artmofang.livebroadcast.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 * 直播评论内容
 */

public class LiveCommentBean {

    /**
     * code : 10000
     * message : 获取直播评论成功
     * data : [{"id":"32","live_id":"3","uid":"1","addtime":"1512439990","nickname":"沐沐","reply_infor":"好啊","fid":null,"status":"1","type":"0"},{"id":"33","live_id":"3","uid":"1","addtime":"1512439992","nickname":"沐沐","reply_infor":"好啊","fid":null,"status":"1","type":"0"},{"id":"34","live_id":"3","uid":"1","addtime":"1512439993","nickname":"沐沐","reply_infor":"好啊","fid":null,"status":"1","type":"0"},{"id":"35","live_id":"3","uid":"1","addtime":"1512439993","nickname":"沐沐","reply_infor":"好啊","fid":null,"status":"1","type":"0"},{"id":"36","live_id":"3","uid":"1","addtime":"1512439994","nickname":"沐沐","reply_infor":"好啊","fid":null,"status":"1","type":"0"},{"id":"37","live_id":"3","uid":"1","addtime":"1512439995","nickname":"沐沐","reply_infor":"好啊","fid":null,"status":"1","type":"0"},{"id":"38","live_id":"3","uid":"1","addtime":"1512439995","nickname":"沐沐","reply_infor":"好啊","fid":null,"status":"1","type":"0"}]
     */

    private String code;
    private String message;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 32
         * live_id : 3
         * uid : 1
         * addtime : 1512439990
         * nickname : 沐沐
         * reply_infor : 好啊
         * fid : null
         * status : 1
         * type : 0
         */

        private String id;
        private String live_id;
        private String uid;
        private String addtime;
        private String nickname;
        private String reply_infor;
        private String fid;
        private String status;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLive_id() {
            return live_id;
        }

        public void setLive_id(String live_id) {
            this.live_id = live_id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getReply_infor() {
            return reply_infor;
        }

        public void setReply_infor(String reply_infor) {
            this.reply_infor = reply_infor;
        }

        public String getFid() {
            return fid;
        }

        public void setFid(String fid) {
            this.fid = fid;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
