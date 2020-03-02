package com.victorengineer.limpiamiciudad.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.firestore.DocumentSnapshot;
import com.victorengineer.limpiamiciudad.BaseActivity;
import com.victorengineer.limpiamiciudad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.victorengineer.limpiamiciudad.models.Report;
import com.victorengineer.limpiamiciudad.models.User;
import com.victorengineer.limpiamiciudad.util.SessionHandler;


public class MainActivity extends BaseActivity implements BaseFragment.OnChangeListener,
        View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener,
        ComplaintsListFragment.ReportListener
{

    public static final String TAG = "MainActivity";

    //widgets
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    //vars
    private FirebaseFirestore mDb;
    private ReportFragment reportFragment;
    private ComplaintsListFragment complaintsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDb = FirebaseFirestore.getInstance();
        init();


    }

    private void init(){
        bottomNavigationView = findViewById(R.id.bottom_nav_home);
        setBottomNavView();

        String idUser = SessionHandler.getIdUser(getApplicationContext());
        DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                .document(idUser);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    if(user.getUser_type() == 1) {
                        setToolbarTitle(getString(R.string.report));
                        setBoldActionBartitle();
                        reportFragment = ReportFragment.newInstance(getApplicationContext());
                        addOrReplaceFragment(reportFragment, R.id.fragment_container);
                    }else {
                        setToolbarTitle(getString(R.string.report_list));
                        setBoldActionBartitle();
                        inflateFragmentComplaints();
                    }

                }
            }
        });
    }

    private void setToolbarTitle(String title) {
        try {
            setTitle(title);
            toolbar.setTitle(title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void inflateFragmentComplaints(){
        complaintsListFragment = ComplaintsListFragment.newInstance(this);
        addOrReplaceFragment(complaintsListFragment, R.id.fragment_container);
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

    @Override
    public void onReportSelected(Report report) {
        Intent myIntent = new Intent(getApplicationContext(), ReportDetailActivity.class);
        myIntent.putExtra("reportId", report.getReportId());
        myIntent.putExtra("tipoResiduo", report.getTipoResiduo());
        myIntent.putExtra("volumenResiduo", report.getVolumenResiduo());
        myIntent.putExtra("descripcionResiduo", report.getDescripcionResiduo());
        myIntent.putExtra("fechaReportada", report.getTimestamp());
        myIntent.putExtra("imgUri", report.getImgUri());
        startActivity(myIntent);
    }
}
