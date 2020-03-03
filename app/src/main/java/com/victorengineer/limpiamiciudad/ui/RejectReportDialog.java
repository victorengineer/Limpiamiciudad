package com.victorengineer.limpiamiciudad.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.victorengineer.limpiamiciudad.R;
import com.victorengineer.limpiamiciudad.models.Report;
import com.victorengineer.limpiamiciudad.models.Result;
import com.victorengineer.limpiamiciudad.util.CustomAlertDialog;
import com.victorengineer.limpiamiciudad.util.ResultListener;

public class RejectReportDialog extends CustomAlertDialog{

    public static final String REPORT_ID = "reportId";

    private FirebaseFirestore mDb;

    private String reportId;

    public static RejectReportDialog newInstance(String reportId, ResultListener<Fragment> resultListener) {

        RejectReportDialog dialog = new RejectReportDialog();
        Bundle bundle = new Bundle();
        bundle.putString(REPORT_ID, reportId);
        dialog.setArguments(bundle);
        dialog.setResultListener(resultListener);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reportId = getArguments().getString(REPORT_ID, null);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb = FirebaseFirestore.getInstance();
        showConfirm();
    }

    private void showConfirm() {
        Builder builder = new Builder()
                .setDescription(getString(R.string.report_rejected))
                .setCancelableAtOutside(false)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.aceptar_uppercase), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoader(getString(R.string.report_rejecting));

                        rejectReport();
                    }
                })
                .setNegativeButton(getString(R.string.cancelar_uppercase), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                    }
                });

        setBuilder(builder);
        build();
    }

    private void rejectReport(){

        mDb.collection(getString(R.string.collection_reports))
                .document(reportId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideLoader();
                        showResult(Result.OK);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideLoader();
                showResult(Result.ERROR);
            }
        });
    }

    private void showResult(final Result result) {
        String resultDescription;
        int resultImage;
        if (result == Result.OK) {
            resultDescription = getString(R.string.report_rejected_success);
            resultImage = R.drawable.ic_alert_success;
        } else {
            resultDescription = getString(R.string.report_aprove_failed);
            resultImage = R.drawable.ic_alert_error;
        }

        Builder builder = new Builder()
                .setDescription(resultDescription, Gravity.CENTER)
                .setImage(resultImage)
                .setCancelableAtOutside(false)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.entendido), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getResultListener() != null) {
                            getResultListener().onResult(result, null);
                        }
                        dismiss();

                    }
                });
        setBuilder(builder);
        build();
    }
}
