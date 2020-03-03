package com.victorengineer.limpiamiciudad.ui;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.victorengineer.limpiamiciudad.R;
import com.victorengineer.limpiamiciudad.adapters.ReportAdapter;
import com.victorengineer.limpiamiciudad.adapters.UserRecyclerAdapter;
import com.victorengineer.limpiamiciudad.models.ClusterMarker;
import com.victorengineer.limpiamiciudad.models.PolylineData;
import com.victorengineer.limpiamiciudad.models.Report;
import com.victorengineer.limpiamiciudad.models.UserLocation;
import com.victorengineer.limpiamiciudad.util.MyClusterManagerRenderer;
import com.victorengineer.limpiamiciudad.util.SessionHandler;
import com.victorengineer.limpiamiciudad.util.ViewWeightAnimationWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener{

    private static final String TAG = "MapFragment";

    private static final int LOCATION_UPDATE_INTERVAL = 3000;

    //widgets
    private RelativeLayout mMapContainer;
    private ImageButton btnResetMap;

    //vars
    private ArrayList<Report> mReportList = new ArrayList<>();
    private ArrayList<GeoPoint> mGeoPointList = new ArrayList<>();
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private UserRecyclerAdapter mUserRecyclerAdapter;
    private GoogleMap mGoogleMap;
    private UserLocation mUserPosition;
    private LatLngBounds mMapBoundary;
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private int mMapLayoutState = 0;
    private GeoApiContext mGeoApiContext;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();
    private Marker mSelectedMarker = null;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private FirebaseFirestore mDb;
    private UserLocation mUserLocation;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = FirebaseFirestore.getInstance();

        initGoogleMap();


        readDataReports(new MapFragment.ReportListCallback() {
            @Override
            public void onReportListCallback(List<Report> reportList) {
                for(Report report : reportList){
                    GeoPoint geoPoint = new GeoPoint(
                            report.getGeo_point().getLatitude(),
                            report.getGeo_point().getLongitude()
                    );

                    mGeoPointList.add(geoPoint);

                }

                getUserLocation(new MapFragment.UserLocationCallback() {
                    @Override
                    public void onUserLocationCallback() {
                        setReportsPositions();

                        addMapMarkers();

                    }

                });

            }

        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        //mMapContainer = view.findViewById(R.id.map_container);mMapContainer = view.findViewById(R.id.map_container);
        //btnResetMap = view.findViewById(R.id.btn_reset_map);
        //btnResetMap.setOnClickListener(this);


        initGoogleMap();

        return view;
    }




    private void getUserLocation(final MapFragment.UserLocationCallback userLocationCallback){

        DocumentReference userRef = mDb.collection(getString(R.string.collection_user_locations))
                .document(FirebaseAuth.getInstance().getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    UserLocation userLocation = task.getResult().toObject(UserLocation.class);
                    mUserLocation = userLocation;

                    userLocationCallback.onUserLocationCallback();

                }
            }
        });

    }

    private void readDataReports(final MapFragment.ReportListCallback reportListCallback){



        CollectionReference complaintsListRef = mDb.collection(getString(R.string.collection_reports));
        Query query = complaintsListRef.whereEqualTo("reporteAceptado", true);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    Log.d("complaints", "complaints successfull");

                    mReportList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String reportId = document.getString("reportId");
                        String tipoResiduo = document.getString("tipoResiduo");
                        String volumenResiduo = document.getString("volumenResiduo");
                        String descripcionResiduo = document.getString("descripcionResiduo");
                        String imgUri = document.getString("imgUri");
                        Date timestamp = document.getDate("timestamp");
                        GeoPoint geoPoint = document.getGeoPoint("geo_point");

                        Report report = new Report();
                        report.setReportId(reportId);
                        report.setTipoResiduo(tipoResiduo);
                        report.setVolumenResiduo(volumenResiduo);
                        report.setDescripcionResiduo(descripcionResiduo);
                        report.setImgUri(imgUri);
                        report.setGeo_point(geoPoint);
                        report.setTimestamp(timestamp);

                        mReportList.add(report);
                    }

                    reportListCallback.onReportListCallback(mReportList);
                } else {
                    Log.d("ToDoList", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void initGoogleMap() {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);

        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }


        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        map.setMyLocationEnabled(true);
//        mGoogleMap = map;
//        setCameraView();

        mGoogleMap = map;
        mGoogleMap.setOnPolylineClickListener(this);
    }

    private void addMapMarkers(){

        if(mGoogleMap != null){

            resetMap();

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity(), mGoogleMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            mGoogleMap.setOnInfoWindowClickListener(this);

            String snippet = "";

            String idUser = SessionHandler.getIdUser(getActivity());
            if(mUserLocation.getUser().getUser_id().equals(idUser)){
                snippet = "This is you";
            }

            for(GeoPoint geoPoint: mGeoPointList){

                Log.d(TAG, "addMapMarkers: location: " + geoPoint.toString());
                try{

                    snippet = "Determine route to " + geoPoint.toString() + " ?";

                    int avatar = R.drawable.cartman_cop; // set the default avatar
                    try{
                        //avatar = Integer.parseInt(userLocation.getUser().getAvatar());
                    }catch (NumberFormatException e){
                        Log.d(TAG, "addMapMarkers: no avatar for " + geoPoint.toString() + ", setting default.");
                    }
                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()),
                            geoPoint.toString(),
                            snippet,
                            avatar,
                            mUserLocation.getUser()
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);

                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                }

            }
            mClusterManager.cluster();

            setCameraView();
        }
    }

    private void resetMap(){
        if(mGoogleMap != null) {
            mGoogleMap.clear();

            if(mClusterManager != null){
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

            if(mPolyLinesData.size() > 0){
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

    private void setCameraView() {

        // Set a boundary to start
        double bottomBoundary = mUserPosition.getGeo_point().getLatitude() - .1;
        double leftBoundary = mUserPosition.getGeo_point().getLongitude() - .1;
        double topBoundary = mUserPosition.getGeo_point().getLatitude() + .1;
        double rightBoundary = mUserPosition.getGeo_point().getLongitude() + .1;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 30));

        /*
        moveCamara(new MapActivity.MoveCamaraCallback() {
            @Override
            public void onMoveCamaraCallback() {

            }

        });

         */

    }

    private void moveCamara(final MapFragment.MoveCamaraCallback moveCamaraCallback){

        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 30));
                moveCamaraCallback.onMoveCamaraCallback();
            }
        });
    }

    private void setReportsPositions() {
        for (GeoPoint geoPoint : mGeoPointList) {
            if (mUserLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())) {
                mUserPosition = mUserLocation;
            }
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        if(marker.getSnippet().equals("This is you")){
            marker.hideInfoWindow();
        }
        else{

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(marker.getSnippet())
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            resetSelectedMarker();
                            mSelectedMarker = marker;
                            calculateDirections(marker);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mUserPosition.getGeo_point().getLatitude(),
                        mUserPosition.getGeo_point().getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
//                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
//                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
//                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
//                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                Log.d(TAG, "onResult: successfully retrieved directions.");
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    private void resetSelectedMarker(){
        if(mSelectedMarker != null){
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    private void removeTripMarkers(){
        for(Marker marker: mTripMarkers){
            marker.remove();
        }
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(mPolyLinesData.size() > 0){
                    for(PolylineData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }

                    mSelectedMarker.setVisible(false);
                }
            }
        });
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        int index = 0;
        for(PolylineData polylineData: mPolyLinesData){
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trip #" + index)
                        .snippet("Duration: " + polylineData.getLeg().duration
                        ));

                mTripMarkers.add(marker);

                marker.showInfoWindow();
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*
            case R.id.btn_reset_map:{
                addMapMarkers();
                break;
            }
            */
        }
    }


    private interface ReportListCallback {
        void onReportListCallback(List<Report> reportList);
    }

    private interface UserLocationCallback {
        void onUserLocationCallback();
    }

    private interface MoveCamaraCallback {
        void onMoveCamaraCallback();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
