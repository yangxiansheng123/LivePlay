package com.artmofang.livebroadcast.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/29.
 * 直播列表
 */

public class LiveBroadcastListBean {

    /**
     * code : 10000
     * message : 获取直播列表成功
     * data : [{"live_id":"1","live_state":2,"live_img":"/upload/store/000/000/500/ym_20170905210204.jpeg","live_name":"万圣节面具+南瓜灯 | 学创意绘画&amp;手工","live_category":"架子鼓","live_keyword":"南瓜灯","live_star_time":"2017-11-02 09:13:00","live_end_time":"2017-11-23 09:13:00","live_push":"\trtmp://16355.livepush.myqcloud.com/live/16355_c609c34407?bizid=16355&amp;txSecret=7863db1f9741537ffc3efa372f630800&amp;txTime=5A29657F","live_play":"http://16355.liveplay.myqcloud.com/live/16355_c609c34407.m3u8","live_price":null,"live_num":"5"},{"live_id":"3","live_state":2,"live_img":"/upload/store_icon/000/000/023/5a0d023225f41.jpg","live_name":"奇妙的宇宙 | 学独特绘画技巧&amp;手工","live_category":"手工DIY","live_keyword":"宇宙","live_star_time":"2017-10-31 17:51:00","live_end_time":"2017-10-31 17:51:00","live_push":"1111111","live_play":"http://1255507447.vod2.myqcloud.com/ca093c67vodgzp1255507447/3475e4944564972818576723254/playlist.m3u8","live_price":null,"live_num":"10"}]
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
         * live_id : 1
         * live_state : 2
         * live_img : /upload/store/000/000/500/ym_20170905210204.jpeg
         * live_name : 万圣节面具+南瓜灯 | 学创意绘画&amp;手工
         * live_category : 架子鼓
         * live_keyword : 南瓜灯
         * live_star_time : 2017-11-02 09:13:00
         * live_end_time : 2017-11-23 09:13:00
         * live_push : 	rtmp://16355.livepush.myqcloud.com/live/16355_c609c34407?bizid=16355&amp;txSecret=7863db1f9741537ffc3efa372f630800&amp;txTime=5A29657F
         * live_play : http://16355.liveplay.myqcloud.com/live/16355_c609c34407.m3u8
         * live_price : null
         * live_num : 5
         */

        private String live_id;
        private String live_state;
        private String live_img;
        private String live_name;
        private String live_category;
        private String live_keyword;
        private String live_star_time;
        private String live_end_time;
        private String live_push;
        private String live_play;
        private String live_price;
        private String live_num;

        public String getLive_id() {
            return live_id;
        }

        public void setLive_id(String live_id) {
            this.live_id = live_id;
        }

        public void setLive_state(String live_state) {
            this.live_state = live_state;
        }

        public void setLive_price(String live_price) {
            this.live_price = live_price;
        }

        public String getLive_img() {
            return live_img;
        }

        public void setLive_img(String live_img) {
            this.live_img = live_img;
        }

        public String getLive_name() {
            return live_name;
        }

        public void setLive_name(String live_name) {
            this.live_name = live_name;
        }

        public String getLive_category() {
            return live_category;
        }

        public void setLive_category(String live_category) {
            this.live_category = live_category;
        }

        public String getLive_keyword() {
            return live_keyword;
        }

        public void setLive_keyword(String live_keyword) {
            this.live_keyword = live_keyword;
        }

        public String getLive_star_time() {
            return live_star_time;
        }

        public void setLive_star_time(String live_star_time) {
            this.live_star_time = live_star_time;
        }

        public String getLive_end_time() {
            return live_end_time;
        }

        public void setLive_end_time(String live_end_time) {
            this.live_end_time = live_end_time;
        }

        public String getLive_push() {
            return live_push;
        }

        public void setLive_push(String live_push) {
            this.live_push = live_push;
        }

        public String getLive_play() {
            return live_play;
        }

        public void setLive_play(String live_play) {
            this.live_play = live_play;
        }

        public String getLive_state() {
            return live_state;
        }

        public String getLive_price() {
            return live_price;
        }

        public String getLive_num() {
            return live_num;
        }

        public void setLive_num(String live_num) {
            this.live_num = live_num;
        }
    }
}
