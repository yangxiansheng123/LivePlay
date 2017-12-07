package com.artmofang.livebroadcast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.artmofang.livebroadcast.activity.LoginActivity;
import com.artmofang.livebroadcast.manager.RememerConstants;
import com.tumblr.remember.Remember;

public class StartAnimationAcitvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_animation_acitvity);
        ImageView img = (ImageView) findViewById(R.id.startAnimation);

        AlphaAnimation start = new AlphaAnimation(0.3f, 1.0f);
        start.setDuration(3000);
        img.startAnimation(start);
        start.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startTo() {

        String pwd = Remember.getString(RememerConstants.PASSWORD, "");
        String xPhone = Remember.getString(RememerConstants.ACCOUNT, "");
        if (xPhone.equals("")) {
            Intent intent = new Intent(StartAnimationAcitvity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 是判断自动登录的
            if ((!pwd.equals(""))) {
                Intent intent = new Intent(StartAnimationAcitvity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(StartAnimationAcitvity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
