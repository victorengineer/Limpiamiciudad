package com.victorengineer.limpiamiciudad.ui;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.victorengineer.limpiamiciudad.R;


public class ReportActivity extends AppCompatActivity {

    EditText edt_nombre,edt_apellido_pat, edt_apellido_mat, edt_rfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        edt_nombre= (EditText) findViewById(R.id.edt_nombre);
        edt_apellido_pat= (EditText) findViewById(R.id.edt_apellido_pat);
        edt_apellido_mat= (EditText) findViewById(R.id.edt_apellido_mat);
        edt_rfc= (EditText) findViewById(R.id.edt_rfc);

    }

    public void onClick(View view) {
        //registrarUsuarios();
    }

    /*

    private void registrarUsuariosSql() {
        ConexionSQLiteHelper conn=new ConexionSQLiteHelper(this,"bd_la_vendimia",null,1);

        SQLiteDatabase db=conn.getWritableDatabase();


        String insert="INSERT INTO "+ Utils.TABLA_CLIENTE
                +" ( " +Utils.nombre_cliente+","+Utils.apellido_pat_cliente+","+Utils.apellido_mat_cliente+","+Utils.rfc_cliente+")" +
                " VALUES ("+", '"+edt_nombre.getText().toString()+"','"+edt_apellido_pat.getText().toString()+"','"
                +edt_apellido_mat.getText().toString()+"','"+edt_rfc.getText().toString()+"')";

        db.execSQL(insert);


        db.close();
    }


    private void registrarUsuarios() {
        ConexionSQLiteHelper conn=new ConexionSQLiteHelper(this,"bd_la_vendimia",null,1);

        SQLiteDatabase db=conn.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(Utils.nombre_cliente,edt_nombre.getText().toString());
        values.put(Utils.apellido_pat_cliente,edt_apellido_pat.getText().toString());
        values.put(Utils.apellido_mat_cliente,edt_apellido_mat.getText().toString());
        values.put(Utils.rfc_cliente,edt_rfc.getText().toString());

        Long idResultante=db.insert(Utils.TABLA_CLIENTE,Utils.clave_cliente,values);

        Toast.makeText(getApplicationContext(),"Se registro correctamente!" + "\n" + "Clave Registro: "+idResultante, Toast.LENGTH_SHORT).show();
        db.close();
    }


     */
}