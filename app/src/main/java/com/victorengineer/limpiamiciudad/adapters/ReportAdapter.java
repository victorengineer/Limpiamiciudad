package com.victorengineer.limpiamiciudad.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.victorengineer.limpiamiciudad.R;
import com.victorengineer.limpiamiciudad.models.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Report> mComplaints;
    private Context mContext;

    public ReportAdapter(List<Report> complaintsList, Context context) {
        this.mComplaints = complaintsList;
        this.mContext = context;
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReportViewHolder) {
            ReportViewHolder vh = (ReportViewHolder) holder;
            Report report = mComplaints.get(position);

            vh.tvResiduo.setText(report.getTipoResiduo());
            vh.tvVolumen.setText(report.getVolumenResiduo());
            vh.tvDescripcion.setText(report.getDescripcionResiduo());
        }
    }

    public void updateData(List<Report> reportList) {
        this.mComplaints = reportList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mComplaints.size();
    }

    private static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvResiduo;
        TextView tvVolumen;
        TextView tvDescripcion;


        ReportViewHolder(View itemView) {
            super(itemView);
            tvResiduo = itemView.findViewById(R.id.tipo_residuo_answer);
            tvVolumen = itemView.findViewById(R.id.volumen_residuo_answer);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion);
        }

    }

}
