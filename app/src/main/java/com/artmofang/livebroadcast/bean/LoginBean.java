package com.artmofang.livebroadcast.bean;

/**
 * Created by Administrator on 2017/11/28.
 */

public class LoginBean {


    /**
     * code : 10000
     * message : 登录艺魔方直播APP成功
     * data : {"mer_id":"703","name":"东跆跆拳道","head_picture":""}
     */

    private String code;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * mer_id : 703
         * name : 东跆跆拳道
         * head_picture : 
         */

        private String mer_id;
        private String name;
        private String head_picture;

        public String getMer_id() {
            return mer_id;
        }

        public void setMer_id(String mer_id) {
            this.mer_id = mer_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHead_picture() {
            return head_picture;
        }

        public void setHead_picture(String head_picture) {
            this.head_picture = head_picture;
        }
    }
}
