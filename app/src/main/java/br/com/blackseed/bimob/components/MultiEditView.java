package br.com.blackseed.bimob.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.blackseed.bimob.R;

public class MultiEditView extends LinearLayout {

    private int numberOfViews = 0;
    private String mHint;
    private String mAddText;
    private int mInputType;

    private List<EditText> editTextList;


    public MultiEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MultiEditView,
                0, 0);

        try {
            mHint = a.getString(R.styleable.MultiEditView_hint);
            mAddText = a.getString(R.styleable.MultiEditView_addText);
            mInputType = a.getInt(R.styleable.MultiEditView_inputType, InputType.TYPE_CLASS_TEXT);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        editTextList = new ArrayList<>();

        View addBtn = inflate(getContext(), R.layout.multieditview_add_field_btn, null);
        addBtn.findViewById(R.id.addFieldBtn).setOnClickListener(new AddClickListener());
        ((TextView) addBtn.findViewById(R.id.addFieldBtn)).setText(mAddText);
        addView(addBtn);
    }

    public List<String> getTextList() {
        List<String> textList = new ArrayList<>();
        for (EditText editText : editTextList)
            textList.add(editText.getText().toString());
        return textList;
    }

    public void setTextList(List<String> textList) {
        editTextList.clear();
        numberOfViews = 0;
        removeAllViews();
        View addBtn = inflate(getContext(), R.layout.multieditview_add_field_btn, null);
        addBtn.findViewById(R.id.addFieldBtn).setOnClickListener(new AddClickListener());
        ((TextView) addBtn.findViewById(R.id.addFieldBtn)).setText(mAddText);
        addView(addBtn);
        for (String text : textList)
            createField(text);
    }

    private void createField(String text) {
        View view = inflate(getContext(), R.layout.multieditview_single_item, null);
        view.findViewById(R.id.deleteBtn).setOnClickListener(new RemoveClickListener(view));
        EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.setHint(mHint);
        editText.setInputType(mInputType);
        editText.setText(text);
        editText.requestFocus();
        editTextList.add(editText);

        addView(view, numberOfViews);
        numberOfViews++;
    }

    public class RemoveClickListener implements OnClickListener {

        private View parent;

        public RemoveClickListener(View parent) {
            this.parent = parent;

        }

        @Override
        public void onClick(View view) {
            EditText editText = (EditText) parent.findViewById(R.id.editText);
            editTextList.remove(editText);
            removeView(parent);
            numberOfViews--;
        }
    }

    public class AddClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            createField("");
        }
    }


}
