package com.victorengineer.limpiamiciudad.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.victorengineer.limpiamiciudad.R;
import com.victorengineer.limpiamiciudad.models.Report;
import com.victorengineer.limpiamiciudad.models.Result;
import com.victorengineer.limpiamiciudad.util.ResultListener;

import java.io.File;


public class ReportDetailFragment extends BaseFragment{

    private TextView tvTipoResiduo;
    private TextView tvVolumenResiduo;
    private TextView tvDescripcion;
    private TextView tvFechaReportada;
    private ImageView ivReport;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private LinearLayout mReportRejected;

    private Report report;
    private ResultListener<Fragment> resultListener;
    Context context;

    public static ReportDetailFragment create(Context context, Report report, ResultListener<Fragment> listener) {

        ReportDetailFragment fragment = new ReportDetailFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        fragment.context = context;
        fragment.report = report;
        fragment.setResultListener(listener);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_report_detail,
                container,
                false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.tvTipoResiduo = view.findViewById(R.id.tipo_residuo);
        this.tvVolumenResiduo = view.findViewById(R.id.volumen_residuo);
        this.tvDescripcion = view.findViewById(R.id.descripcion);
        this.tvFechaReportada = view.findViewById(R.id.fecha_reportada);
        this.ivReport = view.findViewById(R.id.ivImage);
        this.mReportRejected = (LinearLayout) view.findViewById(R.id.ll_report_rejected);

        //storage = FirebaseStorage.getInstance();
        //String photoName = report.getImgUri() + ".jpg";
        storage = FirebaseStorage.getInstance();
        //storageReference = storage.getReference();
        mReportRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RejectReportDialog dialog = RejectReportDialog.newInstance(report.getReportId(), resultListener);
                dialog.show(getChildFragmentManager(), RejectReportDialog.class.getSimpleName());
            }
        });

        setDataFragment();

    }



    private void setDataFragment(){
        tvTipoResiduo.setText(report.getTipoResiduo());
        tvVolumenResiduo.setText(report.getVolumenResiduo());
        tvDescripcion.setText(report.getDescripcionResiduo());
        tvFechaReportada.setText(report.getTimestamp().toString());


        downloadImgReport(new ReportDetailCallback() {
            @Override
            public void onImgDownloadedCallback() {

            }
        });


    }

    private void downloadImgReport(final ReportDetailCallback reportDetailCallback){
        String pathfile =  report.getImgUri();

        storageReference = storage.getReference().child("images/").child(pathfile);
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                {
                    Glide.with(getActivity())
                            .load(task.getResult())
                            .apply(RequestOptions.fitCenterTransform())
                            .into(ivReport);

                    reportDetailCallback.onImgDownloadedCallback();


                }
                else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void setResultListener(ResultListener<Fragment> resultListener) {
        this.resultListener = resultListener;
    }


    private interface ReportDetailCallback {
        void onImgDownloadedCallback();
    }



}
