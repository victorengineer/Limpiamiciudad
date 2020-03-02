package com.victorengineer.limpiamiciudad.util;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.victorengineer.limpiamiciudad.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAlertDialog <I> extends DialogFragment {

    private static final int MIN_FRAGMENT_COUNT = 1;

    private Button positiveButton;
    private Button negativeButton;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView imageView;
    private Builder builder;
    private View fragmentContainer;
    private ResultListener<I> resultListener;
    private LoaderView loaderView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_alert_dialog, container, false);

        titleTextView = view.findViewById(R.id.title);
        descriptionTextView = view.findViewById(R.id.description);
        positiveButton = view.findViewById(R.id.positive_button);
        negativeButton = view.findViewById(R.id.negative_button);
        imageView = view.findViewById(R.id.image);
        fragmentContainer = view.findViewById(R.id.fragment_container);
        loaderView = view.findViewById(R.id.loader);
        descriptionTextView.setMovementMethod(new ScrollingMovementMethod());

        if(builder != null){
            build();
        }

        return view;
    }

    protected void build(){
        if(builder.title == null){
            titleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(builder.title);
            titleTextView.setGravity(builder.titleGravity);
        }

        if(builder.description == null){
            descriptionTextView.setVisibility(View.GONE);
        } else {
            descriptionTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setText(builder.description);
            descriptionTextView.setGravity(builder.descriptionGravity);
        }

        if(builder.positiveButtonText == null){
            positiveButton.setVisibility(View.GONE);
        } else {
            positiveButton.setVisibility(View.VISIBLE);
            positiveButton.setText(builder.positiveButtonText);
            positiveButton.setOnClickListener(builder.positiveListener);
            positiveButton.setEnabled(builder.positiveButtonEnabled);
        }

        if(builder.negativeButtonText == null){
            negativeButton.setVisibility(View.GONE);
        } else {
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setText(builder.negativeButtonText);
            negativeButton.setOnClickListener(builder.negativeListener);
            negativeButton.setEnabled(builder.negativeButtonEnabled);
        }

        if(builder.fragments.isEmpty()){
            fragmentContainer.setVisibility(View.GONE);
        }else{
            if(builder.fragments.size() == MIN_FRAGMENT_COUNT){
                fragmentContainer.setVisibility(View.VISIBLE);
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, (Fragment) builder.fragments.get(0))
                        .commit();
            }else{
                fragmentContainer.setVisibility(View.GONE);
            }
        }

        if(builder.imageId == 0){
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setBackgroundResource(builder.imageId);
        }

        getDialog().setCanceledOnTouchOutside(builder.cancelabeAtOutside);
        getDialog().setCancelable(builder.cancelable);
        setCancelable(builder.cancelable);

        if(builder.loading){
            showLoader(
                    builder.loadingText != null ?
                            builder.loadingText:
                            getString(R.string.dialog_loading));
        }

        if(getResultListener() == null){
            setResultListener(builder.resultListener);
        }
    }

    public Button getPositiveButton(){
        return positiveButton;
    }

    public Button getNegativeButton(){
        return negativeButton;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        try{
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        return dialog;
    }

    public void setResultListener(ResultListener<I> resultListener){
        this.resultListener = resultListener;
    }

    public ResultListener<I> getResultListener() {
        return resultListener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void enablePositiveButton(boolean enabled){
        if(positiveButton != null){
            positiveButton.setEnabled(enabled);
        }
    }

    public void enableNegativeButton(boolean enabled){
        if(negativeButton != null){
            negativeButton.setEnabled(enabled);
        }
    }


    public void showLoader(){
        showLoader(getString(R.string.dialog_loading));
    }

    public void showLoader(String loadingText){
        loaderView.setLoadingText(loadingText);
        loaderView.setVisibility(View.VISIBLE);
    }

    protected void hideLoader(){
        loaderView.setVisibility(View.GONE);
    }

    protected void setBuilder(Builder builder){
        this.builder = builder;
    }

    public static class Builder <I, T>{
        private int imageId;
        private int descriptionGravity;
        private int titleGravity;
        private boolean positiveButtonEnabled;
        private boolean negativeButtonEnabled;
        private boolean cancelable = true;
        private boolean cancelabeAtOutside = true;
        private boolean loading;
        private String loadingText;
        private String title;
        private String description;
        private String positiveButtonText;
        private String negativeButtonText;
        private List<Fragment> fragments;
        private View.OnClickListener positiveListener;
        private View.OnClickListener negativeListener;
        private ResultListener<I> resultListener;

        {
            fragments = new ArrayList<>();
        }

        public Builder setTitle(String title){
            setTitle(title, Gravity.START);
            return this;
        }

        public Builder setTitle(String title, int gravity){
            this.title = title;
            this.titleGravity = gravity;
            return this;
        }

        public Builder setImage(int imageId){
            this.imageId = imageId;
            return this;
        }

        public Builder setDescription(String description){
            setDescription(description, Gravity.START);
            return this;
        }

        public Builder setDescription(String description, int gravity){
            this.description = description;
            this.descriptionGravity = gravity;
            return this;
        }


        public Builder addFragment(Fragment fragment){
            this.fragments.add(fragment);
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, View.OnClickListener positiveListener) {
            setPositiveButton(positiveButtonText, positiveListener, true);
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, View.OnClickListener negativeListener) {
            setNegativeButton(negativeButtonText, negativeListener, true);
            return this;
        }

        public Builder setPositiveButton(
                String positiveButtonText,
                View.OnClickListener positiveListener,
                boolean enabled) {
            this.positiveButtonText = positiveButtonText;
            this.positiveListener = positiveListener;
            this.positiveButtonEnabled = enabled;
            return this;
        }

        public Builder setNegativeButton(
                String negativeButtonText,
                View.OnClickListener negativeListener,
                boolean enabled) {
            this.negativeButtonText = negativeButtonText;
            this.negativeListener = negativeListener;
            this.negativeButtonEnabled = enabled;
            return this;
        }

        public Builder setCancelable(boolean cancelable){
            this.cancelable = cancelable;
            return this;
        }

        public Builder setCancelableAtOutside(boolean cancelabeAtOutside){
            this.cancelabeAtOutside = cancelabeAtOutside;
            return this;
        }

        public Builder setLoading(boolean loading, String loadingText){
            this.loading = loading;
            this.loadingText = loadingText;
            return this;
        }

        public Builder setResultListener(ResultListener<I> resultListener){
            this.resultListener = resultListener;
            return this;
        }

        public CustomAlertDialog build(){
            CustomAlertDialog dialog = new CustomAlertDialog();
            dialog.setBuilder(this);
            return dialog;
        }
    }

}
