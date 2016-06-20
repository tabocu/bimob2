package br.com.blackseed.bimob.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;

public class UnitTextWatcher implements TextWatcher {

    boolean mEditing;
    String unidade;
    EditText editText;

    public UnitTextWatcher(String unidade, EditText editText) {

        this.unidade = unidade;
        this.editText = editText;
        mEditing = false;
    }

    public synchronized void afterTextChanged(Editable s) {
        if (!mEditing) {
            mEditing = true;

            String digits = s.toString().replaceAll("\\D", "");

                String formatted = digits + unidade;
                s.replace(0, s.length(), formatted);
            editText.setSelection(editText.getText().length() - unidade.length());
            mEditing = false;
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

}
