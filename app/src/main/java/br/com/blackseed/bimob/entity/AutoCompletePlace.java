package br.com.blackseed.bimob.entity;

import com.google.android.gms.maps.model.LatLng;

public class AutoCompletePlace {

    private String placeId;
    private CharSequence primary;
    private CharSequence secundary;
    private CharSequence fullText;
    private LatLng latLng;

    public AutoCompletePlace(String placeId, CharSequence primary, CharSequence secundary, CharSequence fullText) {
        this.placeId = placeId;
        this.primary = primary;
        this.secundary = secundary;
        this.fullText = fullText;
    }

    public AutoCompletePlace(String placeId, CharSequence fullText) {
        this.placeId = placeId;
        this.fullText = fullText;
    }

    public LatLng getLatLng() {return latLng;}

    public void setLatLng(LatLng latLng) {this.latLng = latLng;}

    public CharSequence getPrimary() {return primary;}

    public void setPrimary(CharSequence primary) {this.primary = primary;}

    public CharSequence getSecundary() {return secundary;}

    public void setSecundary(CharSequence secundary) {this.secundary = secundary;}

    public CharSequence getFullText() {return fullText;}

    public void setFullText(CharSequence fullText) {this.fullText = fullText;}

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String id) {
        this.placeId = id;
    }
}
