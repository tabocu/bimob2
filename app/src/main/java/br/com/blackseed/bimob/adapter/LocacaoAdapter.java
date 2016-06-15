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
import br.com.blackseed.bimob.data.DbContract.*;

public class LocacaoAdapter extends CursorAdapter {


    public LocacaoAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.temporario_locacao_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int columnId = cursor.getColumnIndexOrThrow(LocacaoEntry._ID);
        int columnNomePessoa = cursor.getColumnIndexOrThrow(ImovelEntry.COLUMN_NOME);
        int columnNomeImovel = cursor.getColumnIndexOrThrow(PessoaEntry.COLUMN_NOME);

        ImageView iconImageView = (ImageView) view.findViewById(R.id.iconImageView);
        TextView idTextView = (TextView) view.findViewById(R.id.idTextView);
        TextView nomePessoaTextView = (TextView) view.findViewById(R.id.nomePessoaTextView);
        TextView nomeImovelTextView = (TextView) view.findViewById(R.id.nomeImovelTextView);

        idTextView.setText(String.valueOf(columnId));
        nomePessoaTextView.setText(cursor.getString(columnNomePessoa));
        nomeImovelTextView.setText(cursor.getString(columnNomeImovel));
    }
}
