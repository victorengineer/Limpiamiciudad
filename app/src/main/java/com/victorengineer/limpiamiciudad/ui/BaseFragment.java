package com.victorengineer.limpiamiciudad.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    protected OnChangeListener mCallback;
    Boolean mIsReady = false;

    public interface OnChangeListener {
        public void onDataChanged();
        public void onFragmentAttached();
        public void onStartNewActivity(String stringActivity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;

            try {
                mCallback = (OnChangeListener) activity;
                mCallback.onFragmentAttached();
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnDataChangedListener");
            }
        }

    }

    @Deprecated
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnChangeListener) activity;
            mCallback.onFragmentAttached();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDataChangedListener");
        }
    }


    public Boolean isReady() {
        return mIsReady;
    }

    public void setReady(Boolean isready) {
        this.mIsReady = isready;
    }

    public void showMessages() {
        throw new UnsupportedOperationException("Must implement showMessages()");
    }

}

