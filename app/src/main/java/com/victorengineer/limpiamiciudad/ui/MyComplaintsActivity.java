package com.victorengineer.limpiamiciudad.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.victorengineer.limpiamiciudad.R;

import java.util.ArrayList;

public class MyComplaintsActivity extends AppCompatActivity {

    //ArrayList<Cliente> listaClientes;

    RecyclerView recyclerViewClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints_list);


        //listaClientes=new ArrayList<>();

        recyclerViewClientes= (RecyclerView) findViewById(R.id.recyclerPersonas);
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(this));

        //consultarListaPersonas();

        /*
        ListaPersonasAdapter adapter=new ListaPersonasAdapter(listaClientes);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Seleccion: " +
//                listaClientes.get(recyclerViewClientes.getChildAdapterPosition(view)).getNombre(),Toast.LENGTH_SHORT).show();
                Integer clienteSeleccionado;
                clienteSeleccionado= listaClientes.get(recyclerViewClientes.getChildAdapterPosition(view)).getClave();
                Intent intent=new Intent(MyComplaintsActivity.this,ConsultarClientesActivity.class);

                Bundle bundle=new Bundle();
                bundle.putSerializable("cliente_id",clienteSeleccionado);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        recyclerViewClientes.setAdapter(adapter);

         */
    }

    /*
    private void consultarListaPersonas() {
        SQLiteDatabase db=conn.getReadableDatabase();

        Cliente cliente=null;
        Cursor cursor=db.rawQuery("SELECT * FROM "+ Utils.TABLA_CLIENTE,null);

        while (cursor.moveToNext()){
            cliente=new Cliente();
            cliente.setClave(cursor.getInt(0));
            cliente.setNombre(cursor.getString(1));
            cliente.setApellido_pat(cursor.getString(2));
            cliente.setApellido_mat(cursor.getString(3));
            cliente.setRfc(cursor.getString(4));

            listaClientes.add(cliente);
        }
    }
    */

}
