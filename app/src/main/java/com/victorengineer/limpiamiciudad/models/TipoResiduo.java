package com.victorengineer.limpiamiciudad.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TipoResiduo implements Parcelable {

    private String nombre;
    private String tipo_residuo_id;


    public TipoResiduo(String nombre, String tipo_residuo_id) {
        this.nombre = nombre;
        this.tipo_residuo_id = tipo_residuo_id;
    }

    public TipoResiduo() {

    }

    protected TipoResiduo(Parcel in) {
        nombre = in.readString();
        tipo_residuo_id = in.readString();
    }

    public static final Creator<TipoResiduo> CREATOR = new Creator<TipoResiduo>() {
        @Override
        public TipoResiduo createFromParcel(Parcel in) {
            return new TipoResiduo(in);
        }

        @Override
        public TipoResiduo[] newArray(int size) {
            return new TipoResiduo[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String title) {
        this.nombre = nombre;
    }

    public String getTipo_residuo_id() {
        return tipo_residuo_id;
    }

    public void setTipo_residuo_id(String tipo_residuo_id) {
        this.tipo_residuo_id = tipo_residuo_id;
    }

    @Override
    public String toString() {
        return "TipoResiduo{" +
                "nombre='" + nombre + '\'' +
                ", tipo_residuo_id='" + tipo_residuo_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(tipo_residuo_id);
    }
}

