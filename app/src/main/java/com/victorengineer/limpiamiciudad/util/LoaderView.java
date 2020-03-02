package com.victorengineer.limpiamiciudad.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.victorengineer.limpiamiciudad.R;

public class LoaderView extends FrameLayout implements Animation.AnimationListener{

    private TextView loadingTextView;
    private View v;
    private Animation inAnimation;
    private Animation outAnimation;
    private View backGroundView;
    private boolean isLoading;

    {
        inAnimation = new AlphaAnimation(0.0f, 1.0f);
        inAnimation.setDuration(0);
        inAnimation.setFillAfter(true);

        outAnimation = new AlphaAnimation(1.0f, 0.0f);
        outAnimation.setDuration(800);
        outAnimation.setFillAfter(true);

        inAnimation.setAnimationListener(this);
        outAnimation.setAnimationListener(this);
        setClickable(false);
        setFocusable(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return isLoading;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isLoading;
    }

    public LoaderView(Context context) {
        super(context);
        initView();
    }

    public LoaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        v = inflate(getContext(), R.layout.layout_loader, null);
        addView(v);
        v.setClickable(false);
        v.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isLoading;
            }
        });
        loadingTextView = v.findViewById(R.id.loading_text);
        backGroundView = v.findViewById(R.id.background);
    }

    public void setLoadingText(String text){
        loadingTextView.setText(text);
    }

    public void setLoadingText(int text){
        loadingTextView.setText(text);
    }


    public void setInAnimation(Animation inAnimation) {
        this.inAnimation = inAnimation;
    }

    public void setOutAnimation(Animation outAnimation) {
        this.outAnimation = outAnimation;
    }

    public void show(){
        isLoading = true;
        startAnimation(inAnimation);
    }

    public void hide(){
        isLoading = false;
        loadingTextView.setText(getContext().getString(R.string.dialog_loading));
        startAnimation(outAnimation);
    }

    public void show(String message){
        loadingTextView.setText(message);
        show();
    }

    public void show(int time){
        inAnimation.setDuration(time);
        show();
    }

    public void show(String message, int time){
        loadingTextView.setText(message);
        show(time);
    }

    public boolean isLoading(){
        return isLoading;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if(isLoading) {
            setVisibility(VISIBLE);
        }
    }

    public void setBackground(int colorId){
        backGroundView.setBackgroundColor(ContextCompat.getColor(getContext(), colorId));
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(!isLoading){
            setVisibility(GONE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

