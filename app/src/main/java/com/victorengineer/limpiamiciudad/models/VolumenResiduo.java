package com.victorengineer.limpiamiciudad.models;

import android.os.Parcel;
import android.os.Parcelable;

public class VolumenResiduo implements Parcelable {

    private String nombre;
    private String volumen_residuo_id;


    public VolumenResiduo(String nombre, String volumen_residuo_id) {
        this.nombre = nombre;
        this.volumen_residuo_id = volumen_residuo_id;
    }

    public VolumenResiduo() {

    }

    protected VolumenResiduo(Parcel in) {
        nombre = in.readString();
        volumen_residuo_id = in.readString();
    }

    public static final Creator<VolumenResiduo> CREATOR = new Creator<VolumenResiduo>() {
        @Override
        public VolumenResiduo createFromParcel(Parcel in) {
            return new VolumenResiduo(in);
        }

        @Override
        public VolumenResiduo[] newArray(int size) {
            return new VolumenResiduo[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String title) {
        this.nombre = nombre;
    }

    public String getVolumen_residuo_id() {
        return volumen_residuo_id;
    }

    public void setVolumen_residuo_id(String volumen_residuo_id) {
        this.volumen_residuo_id = volumen_residuo_id;
    }

    @Override
    public String toString() {
        return "VolumenResiduo{" +
                "nombre='" + nombre + '\'' +
                ", volumen_residuo_id='" + volumen_residuo_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(volumen_residuo_id);
    }
}


