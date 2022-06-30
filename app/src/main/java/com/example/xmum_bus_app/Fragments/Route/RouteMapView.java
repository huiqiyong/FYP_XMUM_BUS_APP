package com.example.xmum_bus_app.Fragments.Route;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xmum_bus_app.BusSchedule.BusStop;
import com.example.xmum_bus_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class RouteMapView extends Fragment implements OnMapReadyCallback, View.OnClickListener{

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int MapLayoutState = 0;

    private String route;
    private TextView btn_back;
    private RecyclerView stoplist_recyclerview;
    private MapView mapView;
    private LatLng location1, location2, location3, location4, location5, location6, location7, location8, location9;
    private RelativeLayout mapContainer;
    private LinearLayout bottom_layout;
    private DatabaseReference dbRef;
    GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_route_map_view, container, false);

        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.toolbar_background);
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.toolbar_content_background));

        Bundle bundle = this.getArguments();

        if(bundle != null){
            route = bundle.getString("route");
        }

        dbRef = FirebaseDatabase.getInstance().getReference().child("BusStop").child(route);
        bottom_layout = (LinearLayout) rootView.findViewById(R.id.route_bottom_layout);

        stoplist_recyclerview = (RecyclerView) rootView.findViewById(R.id.stop_list_recyclerview);
        stoplist_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        mapView = (MapView) rootView.findViewById(R.id.route_map);
        mapContainer = rootView.findViewById(R.id.map_container);
        rootView.findViewById(R.id.btn_map_full_screen).setOnClickListener(this);

        btn_back = (TextView) rootView.findViewById(R.id.tv_back);
        btn_back.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RouteFragment()).commit();
        });

        initGoogleMap(savedInstanceState);

        getStopList(dbRef, stoplist_recyclerview);

        return rootView;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Stop1").exists()){
                    String t1 = snapshot.child("Stop1").child("Title").getValue().toString();
                    String lt1 = snapshot.child("Stop1").child("Latitude").getValue().toString();
                    String lg1 = snapshot.child("Stop1").child("Longitude").getValue().toString();
                    location1 = new LatLng(Double.parseDouble(lt1), Double.parseDouble(lg1));
                    addMarker(map, location1, t1);
                }
                if (snapshot.child("Stop2").exists()){
                    String t2 = snapshot.child("Stop2").child("Title").getValue().toString();
                    String lt2 = snapshot.child("Stop2").child("Latitude").getValue().toString();
                    String lg2 = snapshot.child("Stop2").child("Longitude").getValue().toString();
                    location2 = new LatLng(Double.parseDouble(lt2), Double.parseDouble(lg2));
                    new GetPathFromLocation(location1, location2, new DirectionPointListener() {
                        @Override
                        public void onPath(PolylineOptions polyLine) {
                            addMarker(map, location2, t2);
                            map.addPolyline(polyLine);
                        }
                    }).execute();
                }
                if (snapshot.child("Stop3").exists()){
                    String t3 = snapshot.child("Stop3").child("Title").getValue().toString();
                    String lt3 = snapshot.child("Stop3").child("Latitude").getValue().toString();
                    String lg3 = snapshot.child("Stop3").child("Longitude").getValue().toString();
                    location3 = new LatLng(Double.parseDouble(lt3), Double.parseDouble(lg3));
                    new GetPathFromLocation(location2, location3, new DirectionPointListener() {
                        @Override
                        public void onPath(PolylineOptions polyLine) {
                            addMarker(map, location3, t3);
                            map.addPolyline(polyLine);
                        }
                    }).execute();
                }
                if (snapshot.child("Stop4").exists()){
                    String t4 = snapshot.child("Stop4").child("Title").getValue().toString();
                    String lt4 = snapshot.child("Stop4").child("Latitude").getValue().toString();
                    String lg4 = snapshot.child("Stop4").child("Longitude").getValue().toString();
                    location4 = new LatLng(Double.parseDouble(lt4), Double.parseDouble(lg4));
                    new GetPathFromLocation(location3, location4, new DirectionPointListener() {
                        @Override
                        public void onPath(PolylineOptions polyLine) {
                            addMarker(map, location4, t4);
                            map.addPolyline(polyLine);
                        }
                    }).execute();
                }
                if (snapshot.child("Stop5").exists()){
                    String t5 = snapshot.child("Stop5").child("Title").getValue().toString();
                    String lt5 = snapshot.child("Stop5").child("Latitude").getValue().toString();
                    String lg5 = snapshot.child("Stop5").child("Longitude").getValue().toString();
                    location5 = new LatLng(Double.parseDouble(lt5), Double.parseDouble(lg5));
                    new GetPathFromLocation(location4, location5, new DirectionPointListener() {
                        @Override
                        public void onPath(PolylineOptions polyLine) {
                            addMarker(map, location5, t5);
                            map.addPolyline(polyLine);
                        }
                    }).execute();
                }
                if (snapshot.child("Stop6").exists()){
                    String t6 = snapshot.child("Stop6").child("Title").getValue().toString();
                    String lt6 = snapshot.child("Stop6").child("Latitude").getValue().toString();
                    String lg6 = snapshot.child("Stop6").child("Longitude").getValue().toString();
                    location6 = new LatLng(Double.parseDouble(lt6), Double.parseDouble(lg6));
                    new GetPathFromLocation(location5, location6, new DirectionPointListener() {
                        @Override
                        public void onPath(PolylineOptions polyLine) {
                            addMarker(map, location6, t6);
                            map.addPolyline(polyLine);
                        }
                    }).execute();
                }
                if (snapshot.child("Stop7").exists()){
                    String t7 = snapshot.child("Stop7").child("Title").getValue().toString();
                    String lt7 = snapshot.child("Stop7").child("Latitude").getValue().toString();
                    String lg7 = snapshot.child("Stop7").child("Longitude").getValue().toString();
                    location7 = new LatLng(Double.parseDouble(lt7), Double.parseDouble(lg7));
                    new GetPathFromLocation(location6, location7, new DirectionPointListener() {
                        @Override
                        public void onPath(PolylineOptions polyLine) {
                            addMarker(map, location7, t7);
                            map.addPolyline(polyLine);
                        }
                    }).execute();
                }
                if (snapshot.child("Stop8").exists()){
                    String t8 = snapshot.child("Stop8").child("Title").getValue().toString();
                    String lt8 = snapshot.child("Stop8").child("Latitude").getValue().toString();
                    String lg8 = snapshot.child("Stop8").child("Longitude").getValue().toString();
                    location8 = new LatLng(Double.parseDouble(lt8), Double.parseDouble(lg8));
                    new GetPathFromLocation(location7, location8, new DirectionPointListener() {
                        @Override
                        public void onPath(PolylineOptions polyLine) {
                            addMarker(map, location8, t8);
                            map.addPolyline(polyLine);
                        }
                    }).execute();
                }
                if (snapshot.child("Stop9").exists()){
                    String t9 = snapshot.child("Stop9").child("Title").getValue().toString();
                    String lt9 = snapshot.child("Stop9").child("Latitude").getValue().toString();
                    String lg9 = snapshot.child("Stop9").child("Longitude").getValue().toString();
                    location9 = new LatLng(Double.parseDouble(lt9), Double.parseDouble(lg9));
                    new GetPathFromLocation(location8, location9, new DirectionPointListener() {
                        @Override
                        public void onPath(PolylineOptions polyLine) {
                            addMarker(map, location9, t9);
                            map.addPolyline(polyLine);
                        }
                    }).execute();
                }

                setCameraView(map, location1, location2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getStopList(DatabaseReference dbRef, RecyclerView recyclerView) {

        FirebaseRecyclerOptions<BusStop> options = new FirebaseRecyclerOptions.Builder<BusStop>().setQuery(dbRef, BusStop.class).build();
        FirebaseRecyclerAdapter<BusStop, RouteViewHolder> adapter = new FirebaseRecyclerAdapter<BusStop, RouteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RouteViewHolder holder, int position, @NonNull BusStop model) {
                holder.stopName.setText(model.getTitle());
                holder.stopSequence.setText(model.getSequence());
            }

            @NonNull
            @Override
            public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_map_view_item_design, parent, false);
                return new RouteViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void setCameraView(GoogleMap googleMap, LatLng mLocation1, LatLng mLocation2){
        //LatLngBound will cover all markers on Google Maps
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mLocation1); //Taking Point A (First LatLng)
        builder.include(mLocation2); //Taking Point B (Second LatLng)
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        googleMap.moveCamera(cu);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void addMarker(GoogleMap map, LatLng location, String title){
        map.addMarker(new MarkerOptions().position(location).
                icon(BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(requireContext(),R.drawable.bus_marker)))).setTitle(title);
    }

    private static Bitmap createCustomMarker(Context context, @DrawableRes int resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_image_marker, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.circleImgView);
        markerImage.setImageResource(resource);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    //MapAnimation
    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                40,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(bottom_layout);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                60,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                40);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(bottom_layout);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                60);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_map_full_screen:{

                if(MapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    MapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(MapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    MapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }

        }
    }

}