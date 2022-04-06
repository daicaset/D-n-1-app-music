package com.media.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.media.musicplayer.R;
import com.media.musicplayer.fragment.IntroOneFragment;
import com.media.musicplayer.fragment.IntroThreeFragment;
import com.media.musicplayer.fragment.IntroTwoFragment;
import com.media.musicplayer.share_pre.AppPre;

import me.relex.circleindicator.CircleIndicator;
public class IntroActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private AppCompatButton btnStart;
    private ViewPagerAdapter adapter;
    private AppPre appPre;
    private int page = 0;
    private Handler handler;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (adapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }
            viewPager.setCurrentItem(page, true);
            new Handler().postDelayed(this, 3500);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        viewPager = findViewById(R.id.view_pager);
        circleIndicator = findViewById(R.id.circle_indicator);
        btnStart = findViewById(R.id.btn_start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        appPre = AppPre.getInstance(this);
        handler = new Handler();
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager);

        btnStart.setOnClickListener(view -> {
            appPre.putBoolean("isFirst", true);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter  {
        public ViewPagerAdapter(@NonNull FragmentManager fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new IntroOneFragment();
                case 1:
                    return new IntroTwoFragment();
                case 2:
                    return new IntroThreeFragment();
                default:
                    return new IntroOneFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}