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
import br.com.blackseed.bimob.data.DbContract.PessoaEntry;

public class PessoaAdapter extends CursorAdapter {


    public PessoaAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.temporario_pessoa_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int columnId = cursor.getColumnIndexOrThrow(PessoaEntry._ID);
        int columnNome = cursor.getColumnIndexOrThrow(PessoaEntry.COLUMN_NOME);
        int columnRazaoSocial = cursor.getColumnIndexOrThrow(PessoaEntry.COLUMN_RAZAO_SOCIAL);
        int columnCp = cursor.getColumnIndexOrThrow(PessoaEntry.COLUMN_CP);
        int columnIsPessoaFisica = cursor.getColumnIndexOrThrow(PessoaEntry.COLUMN_IS_PESSOA_FISICA);

        ImageView iconImageView = (ImageView) view.findViewById(R.id.iconImageView);
        TextView idTextView = (TextView) view.findViewById(R.id.idTextView);
        TextView nomeTextView = (TextView) view.findViewById(R.id.nomeTextView);
        TextView razaoSocialTextView = (TextView) view.findViewById(R.id.razaoSocialTextView);
        TextView cpfCnpjTextView = (TextView) view.findViewById(R.id.cpfCnpjTextView);

        idTextView.setText(String.valueOf(cursor.getLong(columnId)));
        nomeTextView.setText(cursor.getString(columnNome));

        if (cursor.getInt(columnIsPessoaFisica) == 1) {
            razaoSocialTextView.setVisibility(View.GONE);
            iconImageView.setImageResource(R.drawable.bm_person_dark_24dp);
            cpfCnpjTextView.setText(cursor.getString(columnCp));
        } else {
            razaoSocialTextView.setVisibility(View.VISIBLE);
            iconImageView.setImageResource(R.drawable.bm_domain_dark_24dp);
            razaoSocialTextView.setText(cursor.getString(columnRazaoSocial));
            cpfCnpjTextView.setText(cursor.getString(columnCp));
        }
    }
}
