package com.victorengineer.limpiamiciudad;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.WindowManager;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBoldActionBartitle();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }


    public void setBoldActionBartitle() {
        final SpannableString out2 = new SpannableString(getTitle());
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        out2.setSpan(boldSpan, 0, getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setTitle(out2);
    }

    protected void addOrReplaceFragment(Fragment fragment, int containerResId) {
        if (findViewById(containerResId) != null) {
            getSupportFragmentManager().beginTransaction().replace(containerResId, fragment).commit();
        } else if (fragment != null) {
            getSupportFragmentManager().beginTransaction().add(containerResId, fragment).commit();
        }
    }


}
