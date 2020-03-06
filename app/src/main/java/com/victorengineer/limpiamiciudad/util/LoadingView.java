package com.victorengineer.limpiamiciudad.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.victorengineer.limpiamiciudad.R;

public class LoadingView extends RelativeLayout {
    private RelativeLayout mContentView;
    private RelativeLayout mAuxView;
    private ProgressBar mProgressBar;
    private int mProgresscolor = Color.GREEN;
    private int mProgressBackgroundColor = Color.TRANSPARENT;
    private boolean mIsLoading = true;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attr) {
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0);
        mContentView = new RelativeLayout(getContext());
        mContentView.setLayoutParams(lp);
        mContentView.setId(Helpers.generateViewId());
        mContentView.setVisibility(View.GONE);
        addView(mContentView);

        mAuxView = new RelativeLayout(getContext());
        mAuxView.setGravity(Gravity.CENTER);
        mAuxView.setLayoutParams(lp);
        mAuxView.setId(Helpers.generateViewId());
        addView(mAuxView);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0);
        mProgressBar = new ProgressBar(getContext());
        mProgressBar.setLayoutParams(params);

        TypedArray ta = context.obtainStyledAttributes(attr, R.styleable.LoadingView, 0, 0);
        try {
            mProgresscolor = ta.getColor(R.styleable.LoadingView_progressColor, Color.GREEN);
            mProgressBackgroundColor =
                    ta.getColor(R.styleable.LoadingView_progressBackgroundColor, Color.TRANSPARENT);
            mIsLoading = ta.getBoolean(R.styleable.LoadingView_isProgressLoading, true);
        } finally {
            ta.recycle();
        }

        mAuxView.setBackgroundColor(mProgressBackgroundColor);
        mProgressBar.setIndeterminate(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(mProgresscolor));
            mProgressBar.setProgressTintList(ColorStateList.valueOf(mProgresscolor));

        } else {
            mProgressBar
                    .getIndeterminateDrawable()
                    .setColorFilter(mProgresscolor, PorterDuff.Mode.SRC_IN);
        }

        mAuxView.addView(mProgressBar);

        setLoading(mIsLoading);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        final int id = child.getId();

        if (id == mContentView.getId() || id == mAuxView.getId()) {
            super.addView(child, index, params);
        } else {
            mContentView.addView(child, index, params);
        }
    }

    public void setLoading(Boolean loading) {
        mAuxView.setVisibility(loading ? View.VISIBLE : View.GONE);
        mContentView.setVisibility(loading ? View.GONE : View.VISIBLE);
        mIsLoading = loading;
    }

    public Boolean isLoading() {
        return mIsLoading;
    }
}
