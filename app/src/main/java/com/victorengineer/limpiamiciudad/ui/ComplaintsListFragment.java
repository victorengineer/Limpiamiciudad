package com.victorengineer.limpiamiciudad.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.victorengineer.limpiamiciudad.R;
import com.victorengineer.limpiamiciudad.adapters.RecyclerItemClickListener;
import com.victorengineer.limpiamiciudad.adapters.ReportAdapter;
import com.victorengineer.limpiamiciudad.models.Report;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ComplaintsListFragment extends BaseFragment implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener {

    public static final String TAG = ComplaintsListFragment.class.getSimpleName();

    private Context context;
    private FirebaseFirestore mDb;


    private RecyclerView rvComplaints;
    private RelativeLayout loadingView;
    private ReportListener listener;

    private List<Report> reportList = new ArrayList<>();
    private ReportAdapter adapter;

    public ComplaintsListFragment() {
    }

    public static ComplaintsListFragment newInstance( ReportListener reportListener) {
        ComplaintsListFragment fragment = new ComplaintsListFragment();
        Bundle args = new Bundle();
        fragment.listener = reportListener;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = FirebaseFirestore.getInstance();

    }

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_complaints_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        setViews(view);


    }

    private void setViews(View view){

        rvComplaints = view.findViewById(R.id.rv_complaints_list);
        loadingView = view.findViewById(R.id.relative_loading);

        rvComplaints.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));

        loadingView.setVisibility(View.VISIBLE);


        readData(new ComplaintsListFragment.ReportListCallback() {
            @Override
            public void onReportListCallback(List<Report> reportList) {
                rvComplaints.setVisibility(View.VISIBLE);
                rvComplaints.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new ReportAdapter(reportList, getContext());
                rvComplaints.setAdapter(adapter);

                if (loadingView != null){
                    loadingView.setVisibility(View.GONE);
                }

            }

        });


    }


    private void readData(final ReportListCallback reportListCallback){



        CollectionReference complaintsListRef = mDb.collection(getString(R.string.collection_reports));
        Query query = complaintsListRef.whereEqualTo("reporteAceptado", false);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    Log.d("complaints", "complaints successfull");

                    reportList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String reportId = document.getString("reportId");
                        String tipoResiduo = document.getString("tipoResiduo");
                        String volumenResiduo = document.getString("volumenResiduo");
                        String descripcionResiduo = document.getString("descripcionResiduo");
                        String imgUri = document.getString("imgUri");
                        Date timestamp = document.getDate("timestamp");

                        Report report = new Report();
                        report.setReportId(reportId);
                        report.setTipoResiduo(tipoResiduo);
                        report.setVolumenResiduo(volumenResiduo);
                        report.setDescripcionResiduo(descripcionResiduo);
                        report.setImgUri(imgUri);
                        report.setTimestamp(timestamp);

                        reportList.add(report);
                    }

                    reportListCallback.onReportListCallback(reportList);
                } else {
                    Log.d("ToDoList", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Report report = reportList.get(position);
        if(listener != null) {
            listener.onReportSelected(report);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private interface ReportListCallback {
        void onReportListCallback(List<Report> reportList);
    }

    public interface ReportListener {
        void onReportSelected(Report report);
    }



}
