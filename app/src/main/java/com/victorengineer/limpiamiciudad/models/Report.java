package com.victorengineer.limpiamiciudad.models;

import android.net.Uri;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Report {

    private String reportId;

    private String tipoResiduo;

    private String volumenResiduo;

    private String descripcionResiduo;

    private GeoPoint geo_point;

    private String imgUri;

    private boolean reporteAceptado;

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Report(String reportId, String tipoResiduo, String volumenResiduo, String descripcionResiduo, GeoPoint geo_point, String imgUri, Date timestamp) {
        this.reportId = reportId;
        this.tipoResiduo = tipoResiduo;
        this.volumenResiduo = volumenResiduo;
        this.descripcionResiduo = descripcionResiduo;
        this.geo_point = geo_point;
        this.imgUri = imgUri;
        this.timestamp = timestamp;
    }

    public Report(String reportId, String tipoResiduo, String volumenResiduo, String descripcionResiduo, GeoPoint geo_point, Date timestamp) {
        this.reportId = reportId;
        this.tipoResiduo = tipoResiduo;
        this.volumenResiduo = volumenResiduo;
        this.descripcionResiduo = descripcionResiduo;
        this.geo_point = geo_point;
        this.timestamp = timestamp;
    }

    public Report() {
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    private @ServerTimestamp Date timestamp;

    public String getTipoResiduo() {
        return tipoResiduo;
    }

    public void setTipoResiduo(String tipoResiduo) {
        this.tipoResiduo = tipoResiduo;
    }

    public String getVolumenResiduo() {
        return volumenResiduo;
    }

    public void setVolumenResiduo(String volumenResiduo) {
        this.volumenResiduo = volumenResiduo;
    }

    public String getDescripcionResiduo() {
        return descripcionResiduo;
    }

    public void setDescripcionResiduo(String descripcionResiduo) {
        this.descripcionResiduo = descripcionResiduo;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public boolean isReporteAceptado() {
        return reporteAceptado;
    }

    public void setReporteAceptado(boolean reporteAceptado) {
        this.reporteAceptado = reporteAceptado;
    }
}
