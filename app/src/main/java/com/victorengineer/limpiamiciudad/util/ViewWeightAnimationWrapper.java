package com.victorengineer.limpiamiciudad.util;

import android.view.View;
import android.widget.LinearLayout;

public class ViewWeightAnimationWrapper {
    private View view;

    public ViewWeightAnimationWrapper() {
    }

    public void setWeight(float weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = weight;
        view.getParent().requestLayout();
    }

    public float getWeight() {
        return ((LinearLayout.LayoutParams) view.getLayoutParams()).weight;
    }
}