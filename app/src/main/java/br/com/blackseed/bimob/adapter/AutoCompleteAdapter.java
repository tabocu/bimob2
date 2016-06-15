package br.com.blackseed.bimob.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.blackseed.bimob.R;
import br.com.blackseed.bimob.entity.AutoCompletePlace;

public class AutoCompleteAdapter extends ArrayAdapter<AutoCompletePlace> {

    private GoogleApiClient mGoogleApiClient;
    private List<AutoCompletePlace> placeList = new ArrayList<>();


    public AutoCompleteAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater.from(getContext());
        view = layoutInflater.inflate(R.layout.temporario_location_list_item, null);

        //Pega o texto já separando entre "rua e número" e "bairro, cidade, estado, país)
        CharSequence primario = getItem(position).getPrimary();
        CharSequence secundario = getItem(position).getSecundary();
        String id = getItem(position).getPlaceId();

        if(primario != null) {
            TextView locationTextView = (TextView) view.findViewById(R.id.locationTextView);
            locationTextView.setText(primario.toString());
        }
        if(secundario != null) {
            TextView locationSecundaryTextView = (TextView) view.findViewById(R.id.locationSecundaryTextView);
            locationSecundaryTextView.setText(secundario.toString());
        }

        return view;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.mGoogleApiClient = googleApiClient;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                    Toast.makeText(getContext(), "Not connected", Toast.LENGTH_SHORT).show();
                    return null;
                }
                // Clear nao pode ficar aqui
                displayPredictiveResults(constraint.toString());
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll(placeList);
                notifyDataSetChanged();
            }


        };
    }

    public void displayPredictiveResults(String query) {
        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query, null, null)
                .setResultCallback(
                        new ResultCallback<AutocompletePredictionBuffer>() {
                            @Override
                            public void onResult(AutocompletePredictionBuffer buffer) {

                                if (buffer == null)
                                    return;

                                placeList.clear();
                                if (buffer.getStatus().isSuccess()) {
                                    for (AutocompletePrediction prediction : buffer) {
                                        placeList.add(new AutoCompletePlace(
                                                prediction.getPlaceId(),
                                                prediction.getPrimaryText(null),
                                                prediction.getSecondaryText(null),
                                                prediction.getFullText(null)));
                                    }
                                }

                                buffer.release();
                            }
                        }, 60, TimeUnit.SECONDS);
    }


}