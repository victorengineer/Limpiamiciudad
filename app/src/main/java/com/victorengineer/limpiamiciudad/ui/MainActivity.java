package com.victorengineer.limpiamiciudad.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.victorengineer.limpiamiciudad.BaseActivity;
import com.victorengineer.limpiamiciudad.R;
import com.victorengineer.limpiamiciudad.adapters.ChatroomRecyclerAdapter;
import com.victorengineer.limpiamiciudad.models.Chatroom;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import static com.victorengineer.limpiamiciudad.Constants.ERROR_DIALOG_REQUEST;


public class MainActivity extends BaseActivity implements BaseFragment.OnChangeListener,
        View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener
{

    private static final String TAG = "MainActivity";

    //widgets
    private ProgressBar mProgressBar;
    private BottomNavigationView bottomNavigationView;

    //vars
    private FirebaseFirestore mDb;
    private ReportFragment reportFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progressBar);

        mDb = FirebaseFirestore.getInstance();

        setTitle("Reportar");


        reportFragment = ReportFragment.newInstance(this);
        addOrReplaceFragment(reportFragment, R.id.fragment_container);



        bottomNavigationView = findViewById(R.id.bottom_nav_home);
        setBottomNavView();

        //startActivity(new Intent(this, ReportActivity.class));
    }

    private void setBottomNavView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            iconView.setPadding(0,0,0,0);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,0);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            iconView.setLayoutParams(layoutParams);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            /*
            case R.id.btn_report:{
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_my_complaints:{
                Intent intent = new Intent(MainActivity.this, MyComplaintsActivity.class);
                startActivity(intent);
                break;
            }
            */

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.menu_home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == R.id.menu_map) {
            startActivity(new Intent(this, MapActivity.class));
            finish();
        } else if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
        return false;
    }

    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog(){
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }



    @Override
    public void onDataChanged() {

    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onStartNewActivity(String stringActivity) {

    }
}
