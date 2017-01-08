package com.szh.myphoto;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Message;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Button;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FullscreenActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private MyViewPager mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private View frameLayout;
    private Button dummyButton;
    private int size;
    private final Handler mHideHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    onTitleChanged(String.format("%s/%s", msg.arg1 + 1, size), Color.BLACK);
                    dummyButton.setText(ListsInfo.list.get(msg.arg1));
                    break;
            }
        }
    };
    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = (MyViewPager) findViewById(R.id.fullscreen_content);
        frameLayout = findViewById(R.id.fullscreen_activity);
        dummyButton = (Button) findViewById(R.id.dummy_button);
        int position = this.getIntent().getIntExtra("Position", 0);
        size = ListsInfo.list.size();
        List<BaseFragment> list = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            BaseFragment fragment = new BaseFragment();
            list.add(fragment);
        }
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), size, list);
        mContentView.setAdapter(pagerAdapter);
        mContentView.setCurrentItem(position);
        Message message = Message.obtain();
        message.what = 0;
        message.arg1 = position;
        mHideHandler.sendMessage(message);
        mContentView.addOnPageChangeListener(new MyViewPager.SimpleOnPageChangeListener(){

            @Override
            public void onPageSelected(int position) {
                Glide.get(FullscreenActivity.this).clearMemory();
                Message message = Message.obtain();
                message.what = 0;
                message.arg1 = position;
                mHideHandler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContentView.clearOnPageChangeListeners();
        Glide.get(FullscreenActivity.this).clearMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggle() {
        if (mVisible) {
            frameLayout.setBackgroundResource(android.R.color.background_dark);
            hide();
        } else {
            frameLayout.setBackgroundResource(android.R.color.background_light);
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }
}
