package br.com.blackseed.bimob.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class MaskTextWatcher implements TextWatcher {

    private boolean mEditing;
    private Mask mMask;

    public MaskTextWatcher(Mask mask) {
        mEditing = false;
        mMask = mask;
    }

    public static String formatter(Mask pattern, String source) {

        if (source.length() > pattern.getSize())
            source = source.substring(source.length() - pattern.getSize());

        StringBuilder result = new StringBuilder();

        int j = 0;
        for (int i = 0; i < pattern.getMask().length(); i++) {
            if (pattern.getMask().charAt(i) == '#') {
                if (j < source.length())
                    result.append(source.charAt(j++));
                else
                    result.append('0');
            } else {
                result.append(pattern.getMask().charAt(i));
            }
        }

        return result.toString();
    }

    public synchronized void afterTextChanged(Editable s) {
        if (!mEditing) {
            mEditing = true;
            if (s.toString().isEmpty()) {
                mEditing = false;
                return;
            }

            String digits = s.toString().replaceAll("\\D", "");
            try {
                String formatted = formatter(mMask, digits);
                s.replace(0, s.length(), formatted);
            } catch (NumberFormatException nfe) {
                s.clear();
            }

            mEditing = false;
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public enum Mask {

        CPF("###.###.###-##", 11),
        CNPJ("##.###.###/####-##", 14),
        CEP("##.###-###", 8);

        private final String text;
        private final int size;

        Mask(final String text, final int size) {
            this.text = text;
            this.size = size;
        }

        public String getMask() {
            return text;
        }

        public int getSize() {
            return size;
        }
    }

}

