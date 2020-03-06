package com.victorengineer.limpiamiciudad.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.victorengineer.limpiamiciudad.BaseActivity;
import com.victorengineer.limpiamiciudad.R;
import com.victorengineer.limpiamiciudad.models.Report;
import com.victorengineer.limpiamiciudad.models.Result;
import com.victorengineer.limpiamiciudad.util.ResultListener;

import java.util.Date;

public class ReportDetailActivity extends BaseActivity implements BaseFragment.OnChangeListener, ResultListener<Fragment> {

    public static final String REPORT_ID = "reportId";
    public static final String TIPO_RESIDUO = "tipoResiduo";
    public static final String VOLUMEN_RESIDUO = "volumenResiduo";
    public static final String DESCRIPCION = "descripcionResiduo";
    public static final String FECHA_REPORTADA = "fechaReportada";
    public static final String IMG_URI = "imgUri";

    private ReportDetailFragment reportDetailFragment;
    private TextView btnAprove;
    private TextView toolbar;
    private ImageView btnBack;

    String reportId;
    String tipoResiduo;
    String volumenResiduo;
    String descripcionResiduo;
    Date fechaReportada;
    String imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        toolbar = findViewById(R.id.toolbar);
        btnBack = findViewById(R.id.btn_back_action);
        setToolbarTitle(getString(R.string.report_detail));
        setBoldActionBartitle();

        btnAprove = findViewById(R.id.btn_aprove);

        reportId = getIntent().getExtras().getString(REPORT_ID, null);
        tipoResiduo = getIntent().getExtras().getString(TIPO_RESIDUO, null);
        volumenResiduo = getIntent().getExtras().getString(VOLUMEN_RESIDUO, null);
        descripcionResiduo = getIntent().getExtras().getString(DESCRIPCION, null);
        fechaReportada = (Date)getIntent().getSerializableExtra(FECHA_REPORTADA);
        imgUri = getIntent().getExtras().getString(IMG_URI, null);

        final Report report = new Report();
        report.setReportId(reportId);
        report.setTipoResiduo(tipoResiduo);
        report.setVolumenResiduo(volumenResiduo);
        report.setDescripcionResiduo(descripcionResiduo);
        report.setTimestamp(fechaReportada);
        report.setImgUri(imgUri);

        reportDetailFragment = ReportDetailFragment.create(this, report, this);
        addOrReplaceFragment(reportDetailFragment, R.id.fragment_container_detail);

        btnAprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAproveDialog(report);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setToolbarTitle(String title) {
        try {
            toolbar.setText(title);;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void openAproveDialog(Report report){
        AproveReportDialog dialog = AproveReportDialog.newInstance(report.getReportId(), this);
        dialog.show(getSupportFragmentManager(), AproveReportDialog.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
    public void onResult(Result result, Fragment instance) {
        switch (result){
            case OK:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

}
