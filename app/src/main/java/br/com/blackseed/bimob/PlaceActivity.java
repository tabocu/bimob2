package br.com.blackseed.bimob;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import br.com.blackseed.bimob.adapter.AutoCompleteAdapter;
import br.com.blackseed.bimob.entity.AutoCompletePlace;


public class PlaceActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, ResultCallback<PlaceBuffer> {

    private static final String TAG = "ResultCallback";

    private GoogleApiClient mGoogleApiClient;

    private AutoCompleteAdapter mAdapter;

    private ListView mLocationListView;
    private EditText mAdressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mAdressEditText = (EditText) findViewById(R.id.adressEditText);
        mLocationListView = (ListView) findViewById(R.id.locationListVew);
        mAdapter = new AutoCompleteAdapter(this);

        mLocationListView.setAdapter(mAdapter);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {

            mAdressEditText.setText(bundle.getString("text"));
            mAdressEditText.setSelection(mAdressEditText.getText().length());

        }

        mLocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompletePlace autoCompletePlace = (AutoCompletePlace) parent.getItemAtPosition(position);

                Places.GeoDataApi.getPlaceById(mGoogleApiClient, autoCompletePlace.getPlaceId())
                        .setResultCallback(PlaceActivity.this);

            }
        });


        mAdressEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && ( keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_BACK)) {
                    onCanceled();
                }
                return true;
            }
        });

        mAdressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"voltar",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    public void  onCanceled() {
        Intent intent = new Intent();
        intent.putExtra("text", mAdressEditText.getText().toString());
        setResult(RESULT_CANCELED,intent);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mAdapter.setGoogleApiClient(null);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mAdapter != null)
            mAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onResult(@NonNull PlaceBuffer places) {
        if (places.getStatus().isSuccess() && places.getCount() > 0){
            final Place place = places.get(0);
            Log.i(TAG, "Place found: " + place.getName());

            Intent intent = new Intent();
            intent.putExtra("id", place.getId());                   //String
            intent.putExtra("text", mAdressEditText.getText().toString());
            intent.putExtra("description", place.getAddress());     //CharSequence
            intent.putExtra("latitude", place.getLatLng().latitude);     //Double
            intent.putExtra("longitude", place.getLatLng().longitude);    //Double
            setResult(RESULT_OK,intent);
            finish();

        } else {
            Log.e(TAG, "Place not found");
        }
        places.release();

    }

}