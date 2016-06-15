package br.com.blackseed.bimob.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.blackseed.bimob.R;
import br.com.blackseed.bimob.data.DbContract.ImovelEntry;

public class ImovelAdapter extends CursorAdapter {


    public ImovelAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.temporario_imovel_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int columnId = cursor.getColumnIndexOrThrow(ImovelEntry._ID);
        int columnNome = cursor.getColumnIndexOrThrow(ImovelEntry.COLUMN_NOME);
        int columnArea = cursor.getColumnIndexOrThrow(ImovelEntry.COLUMN_AREA);
        int columnTipo = cursor.getColumnIndexOrThrow(ImovelEntry.COLUMN_TIPO);


        ImageView iconImageView = (ImageView) view.findViewById(R.id.iconImageView);
        TextView idTextView = (TextView) view.findViewById(R.id.idTextView);
        TextView nomeTextView = (TextView) view.findViewById(R.id.nomeTextView);
        TextView tipoTextView = (TextView) view.findViewById(R.id.tipoTextView);
        TextView areaTextView = (TextView) view.findViewById(R.id.areaTextView);

        idTextView.setText(String.valueOf(cursor.getLong(columnId)));
        nomeTextView.setText(cursor.getString(columnNome));
        tipoTextView.setText(context.getResources().getStringArray(R.array.imoveis_tipos_array)[cursor.getInt(columnTipo)]);
        areaTextView.setText(cursor.getInt(columnArea) + "m2");
    }
}
