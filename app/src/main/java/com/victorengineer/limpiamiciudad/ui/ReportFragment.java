package com.victorengineer.limpiamiciudad.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.victorengineer.limpiamiciudad.R;
import com.victorengineer.limpiamiciudad.UserClient;
import com.victorengineer.limpiamiciudad.models.ChatMessage;
import com.victorengineer.limpiamiciudad.models.Report;
import com.victorengineer.limpiamiciudad.models.TipoResiduo;
import com.victorengineer.limpiamiciudad.models.User;
import com.victorengineer.limpiamiciudad.models.UserLocation;
import com.victorengineer.limpiamiciudad.models.VolumenResiduo;
import com.victorengineer.limpiamiciudad.util.Helpers;
import com.victorengineer.limpiamiciudad.util.LocationService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ReportFragment extends BaseFragment implements View.OnClickListener, TextWatcher {

    public static final String TAG = ReportFragment.class.getSimpleName();
    public static final int RESULT_OK = 1;
    private Spinner mTipoResiduo;
    private Spinner mVolumenResiduo;
    private Button btnDenunciar;
    private TextInputEditText mDescripcion;
    private Button btnSelect;
    private ImageView ivImage;

    private Uri filePath = null;
    private FirebaseFirestore mDb;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private UserLocation mUserLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    private int result = 0;
    List<TipoResiduo> tipoResiduoList;
    List<VolumenResiduo> volumenResiduoList;
    String[] arrayTiposResiduo;
    String[] arrayVolumenResiduo;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri imgUri;

    private String userChoosenTask;

    private boolean validate = false;


    private Context context;
    private FrameLayout dateContainer;

    public static ReportFragment newInstance(Context context) {
        ReportFragment dialog = new ReportFragment();
        dialog.setContext(context);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        setViews(view);


        validate = true;
    }

    private void setViews(View view){
        mTipoResiduo = view.findViewById(R.id.tipo_residuo);
        mTipoResiduo.requestFocus();

        mVolumenResiduo = view.findViewById(R.id.volumen_residuo);

        mDescripcion = view.findViewById(R.id.descripcion);
        mDescripcion.setFilters(getFilters(50));


        btnDenunciar = view.findViewById(R.id.btn_beneficiario_accept);
        btnDenunciar.setOnClickListener(this);

        mDescripcion.addTextChangedListener(this);

        mDescripcion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Helpers.hideKeyboard(mDescripcion);
                    handled = true;
                }
                return handled;
            }
        });

        initSpinners();

        btnSelect = (Button) view.findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) view.findViewById(R.id.ivImage);



        mTipoResiduo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (validate){
                    if (validarFormulario()){
                        btnDenunciar.setBackground(context.getResources().getDrawable(R.drawable.bg_button_enabled));
                        btnDenunciar.setEnabled(true);
                    } else {
                        btnDenunciar.setBackground(context.getResources().getDrawable(R.drawable.bg_button_disabled));
                        btnDenunciar.setEnabled(false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //empty
            }

        });

        mVolumenResiduo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (validate){
                    if (validarFormulario()){
                        btnDenunciar.setBackground(context.getResources().getDrawable(R.drawable.bg_button_enabled));
                        btnDenunciar.setEnabled(true);
                    } else {
                        btnDenunciar.setBackground(context.getResources().getDrawable(R.drawable.bg_button_disabled));
                        btnDenunciar.setEnabled(false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //empty
            }

        });


        validate = true;
    }

    private void checkUserLocation(){
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
            DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        User user = task.getResult().toObject(User.class);
                        mUserLocation.setUser(user);
                        UserClient userClient = new UserClient();
                        userClient.setUser(user);
                        getLastKnownLocation();
                    }
                }
            });
        }
        else{
            getLastKnownLocation();
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mUserLocation.setGeo_point(geoPoint);
                    mUserLocation.setTimestamp(null);
                    saveUserLocation();
                    //startLocationService();
                }
            }
        });

    }

    private void saveUserLocation(){

        if(mUserLocation != null){
            DocumentReference locationRef = mDb
                    .collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getUid());

            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
                                "\n latitude: " + mUserLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + mUserLocation.getGeo_point().getLongitude());
                    }
                }
            });
        }
    }

    private InputFilter[] getFilters(int maxLength){
        InputFilter[] fArray = new InputFilter[2];
        fArray[0] = new InputFilter.AllCaps();
        fArray[1] = new InputFilter.LengthFilter(maxLength);
        return fArray;
    }

    private void initSpinners(){


        readDataTiposResiduo(new TiposResiduoCallback() {
            @Override
            public void onTiposResiduoCallback(List<TipoResiduo> tipoResiduoList) {
                Log.d("ToDoList", "read tipos residuo completed");
                fillTiposResiduo(mTipoResiduo, tipoResiduoList);

                readDataVolumenResiduo(new VolumenResiduoCallback() {
                    @Override
                    public void onVolumenResiduoCallback(List<VolumenResiduo> volumenResiduoList) {
                        Log.d("ToDoList", "read volumen residuo completed");
                        fillVolumenResiduo(mVolumenResiduo, volumenResiduoList);

                    }
                });
            }

        });



    }

    private void readDataTiposResiduo(final TiposResiduoCallback tiposResiduoCallback){
        CollectionReference tiposResiduosRef = mDb.collection(getString(R.string.collection_tipos_residuo));
        tiposResiduosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("ToDoList", "outside");

                if (task.isSuccessful()) {
                    Log.d("ToDoList", "inside successfull");

                    tipoResiduoList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String tipoResiduoNombre = document.getString("nombre");
                        String tipoResiduoId = document.getId();
                        TipoResiduo tipoResiduo = new TipoResiduo(tipoResiduoNombre, tipoResiduoId);
                        tipoResiduoList.add(tipoResiduo);
                    }

                    tiposResiduoCallback.onTiposResiduoCallback(tipoResiduoList);
                } else {
                    Log.d("ToDoList", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void readDataVolumenResiduo(final VolumenResiduoCallback volumenResiduoCallback){
        CollectionReference volumenResiduoRef = mDb.collection(getString(R.string.collection_volumen_residuo));
        volumenResiduoRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    volumenResiduoList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String volumenResiduoNombre = document.getString("nombre");
                        String volumenResiduoId = document.getId();
                        VolumenResiduo volumenResiduo = new VolumenResiduo(volumenResiduoNombre, volumenResiduoId);
                        volumenResiduoList.add(volumenResiduo);
                    }
                    volumenResiduoCallback.onVolumenResiduoCallback(volumenResiduoList);

                } else {
                    Log.d("ToDoList", "Error getting documents: ", task.getException());
                }
            }
        });

    }


    private void fillTiposResiduo(Spinner mTipoResiduo, List<TipoResiduo> tiposResiduos) {
        arrayTiposResiduo = getTiposResiduoArray(tipoResiduoList);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item, arrayTiposResiduo);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mTipoResiduo.setAdapter(adapterSpinner);
    }

    private void fillVolumenResiduo(Spinner mVolumenResiduo, List<VolumenResiduo> volumenResiduos) {
        arrayVolumenResiduo = getVolumenResiduoArray(volumenResiduos);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item, arrayVolumenResiduo);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mVolumenResiduo.setAdapter(adapterSpinner);
    }

    private String[] getTiposResiduoArray(List<TipoResiduo> tipoResiduos){
        String[] tiposResiduoArr = new String[tipoResiduos.size() + 1];
        tiposResiduoArr[0] = getString(R.string.tipo_residuo_hint);
        for(int counter = 0; counter < tipoResiduos.size(); counter++){
            tiposResiduoArr[counter+1] = tipoResiduos.get(counter).getNombre();
        }
        return tiposResiduoArr;
    }

    private String[] getVolumenResiduoArray(List<VolumenResiduo> volumenResiduos){
        String[] volumenResiduoArr = new String[volumenResiduos.size() + 1];
        volumenResiduoArr[0] = getString(R.string.volumen_residuo_hint);
        for(int counter = 0; counter < volumenResiduos.size(); counter++){
            volumenResiduoArr[counter+1] = volumenResiduos.get(counter).getNombre();
        }
        return volumenResiduoArr;
    }


    @Override
    public void onClick(View v) {

        saveReport(new ReportUploadCallback() {
            @Override
            public void onReportUploadCallback() {
                Log.d("upload", "denuncia completada");
                resetActivity();

            }

        });

    }


    private void saveReport(final ReportUploadCallback reportUploadCallback){

        checkUserLocation();

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Enviando...");
        progressDialog.show();

        Log.d("upload", "upload image outside");

        final String namePhoto = UUID.randomUUID().toString();

        StorageReference ref= storageReference.child("images/"+ namePhoto);
        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.d("upload", "upload image successful");

                DocumentReference newReportRef = mDb
                        .collection(getString(R.string.collection_reports)).document();

                String tipoResiduo = mTipoResiduo.getSelectedItem().toString().trim();
                String volumenResiduo = mVolumenResiduo.getSelectedItem().toString().trim();
                String description = mDescripcion.getText().toString().trim();

                filePath = taskSnapshot.getUploadSessionUri();

                final Report newReport = new Report();
                newReport.setTipoResiduo(tipoResiduo);
                newReport.setVolumenResiduo(volumenResiduo);
                newReport.setDescripcionResiduo(description);
                newReport.setGeo_point(mUserLocation.getGeo_point());
                newReport.setImgUri(namePhoto);
                newReport.setReporteAceptado(false);
                newReport.setReportId(newReportRef.getId());


                newReportRef.set(newReport).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), getString(R.string.report_success), Toast.LENGTH_SHORT).show();
                            reportUploadCallback.onReportUploadCallback();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), getString(R.string.report_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("upload", "upload image failed");

                progressDialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.report_failed), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Enviado: "+(int)progress+"%");

                Log.d("upload", "uploading image");

            }
        });

    }

    private void resetActivity(){
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }


    private boolean validarFormulario(){

        if(mDescripcion.getText().length() < 2){
            return false;
        }

        boolean seleccionoTipoResiduo = validarTipoResiduo(mTipoResiduo.getSelectedItem().toString());

        boolean seleccionoVolumenResiduo = validarVolumenResiduo(mVolumenResiduo.getSelectedItem().toString());

        if(filePath == null){
            return false;
        }

        return  seleccionoTipoResiduo && seleccionoVolumenResiduo;
    }


    private boolean validarTipoResiduo(String txtTipoResiduo){
        if(!txtTipoResiduo.equals(getString(R.string.tipo_residuo_hint))) {
            String toEvalute = txtTipoResiduo.toUpperCase();

            if (tipoResiduoList != null && !tipoResiduoList.isEmpty()) {
                for (TipoResiduo tipoResiduo : tipoResiduoList) {
                    if (toEvalute.trim().equalsIgnoreCase(tipoResiduo.getNombre().trim())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean validarVolumenResiduo(String txtVolumenResiduo){
        if(!txtVolumenResiduo.equals(getString(R.string.volumen_residuo_hint))) {
            String toEvalute = txtVolumenResiduo.toUpperCase();

            if (volumenResiduoList != null && !volumenResiduoList.isEmpty()) {
                for (VolumenResiduo volumenResiduo : volumenResiduoList) {
                    if (toEvalute.trim().equalsIgnoreCase(volumenResiduo.getNombre().trim())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }



    public void setContext(Context context) {
        this.context = context;
    }



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //nothing
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //nothing
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (validate){
            if (validarFormulario()){
                btnDenunciar.setBackground(context.getResources().getDrawable(R.drawable.bg_button_enabled));
                btnDenunciar.setEnabled(true);
            } else {
                btnDenunciar.setBackground(context.getResources().getDrawable(R.drawable.bg_button_disabled));
                btnDenunciar.setEnabled(false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Helpers.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Tomar una foto"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Elegir una foto de galeria"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Tomar una foto", "Elegir una foto de galeria",
                "Cancelar" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Helpers.checkPermission(getContext());

                if (items[item].equals("Tomar una foto")) {
                    userChoosenTask ="Tomar una foto";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Elegir una foto de galeria")) {
                    userChoosenTask ="Elegir una foto de galeria";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

        if (validate){
            if (validarFormulario()){
                btnDenunciar.setBackground(context.getResources().getDrawable(R.drawable.bg_button_enabled));
                btnDenunciar.setEnabled(true);
            } else {
                btnDenunciar.setBackground(context.getResources().getDrawable(R.drawable.bg_button_disabled));
                btnDenunciar.setEnabled(false);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        filePath = data.getData();

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                filePath = data.getData();

                bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }

    private interface TiposResiduoCallback {
        void onTiposResiduoCallback(List<TipoResiduo> tipoResiduoList);
    }

    private interface VolumenResiduoCallback {
        void onVolumenResiduoCallback(List<VolumenResiduo> volumenResiduoList);
    }

    private interface  ReportUploadCallback {
        void onReportUploadCallback();
    }

}
