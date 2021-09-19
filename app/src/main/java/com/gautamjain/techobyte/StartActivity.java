package com.gautamjain.techobyte;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private ImageView iconImage;
    private Button id_login, id_register;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        iconImage = findViewById(R.id.id_icon);
        id_login = findViewById(R.id.id_login);
        id_register = findViewById(R.id.id_register);
        linearLayout = findViewById(R.id.id_linear_layout_start_activity);

//      <--------------------------------------- Icon Image Animation ----------------------------------->

        linearLayout.animate().alpha(0f).setDuration(2);

        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -1000);
        animation.setDuration(1100);
        animation.setFillAfter(false);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iconImage.clearAnimation();
                iconImage.setVisibility(View.INVISIBLE);
                linearLayout.animate().alpha(1f).setDuration(2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        iconImage.setAnimation(animation);

        id_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        id_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // This feature enable user from login again and again when opening app
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }
}