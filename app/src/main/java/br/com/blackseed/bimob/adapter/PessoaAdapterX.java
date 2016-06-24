package br.com.blackseed.bimob.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.blackseed.bimob.R;
import br.com.blackseed.bimob.data.DbContract;
import br.com.blackseed.bimob.data.DbContract.PessoaEntry;
import br.com.blackseed.bimob.utils.Utils;

public class PessoaAdapterX extends RecyclerViewCursorAdapter<PessoaAdapterX.PessoaViewHolder> {

    private static final String TAG = ImovelAdapterX.class.getSimpleName();
    private final Context context;

    OnPessoaClickListener pessoaClickListener;

    public PessoaAdapterX(Context context, Cursor cursor, OnPessoaClickListener l) {
        super(cursor);
        this.context = context;
        pessoaClickListener = l;
    }

    @Override
    public PessoaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.temporario_pessoa_list_item, parent, false);
        return new PessoaViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(PessoaViewHolder holder, Cursor cursor) {
        int columnId = cursor.getColumnIndexOrThrow(DbContract.PessoaEntry._ID);
        int columnNome = cursor.getColumnIndexOrThrow(DbContract.PessoaEntry.COLUMN_NOME);
        int columnRazaoSocial = cursor.getColumnIndexOrThrow(PessoaEntry.COLUMN_RAZAO_SOCIAL);
        int columnThumb = cursor.getColumnIndexOrThrow(DbContract.FotoEntry.COLUMN_THUMB);

        holder._id = cursor.getLong(columnId);
        holder.idTextView.setText(String.valueOf(holder._id));
        holder.nomeTextView.setText(cursor.getString(columnNome));
        holder.razaoSocialTextView.setText(cursor.getString(columnRazaoSocial));

        byte[] blob = cursor.getBlob(columnThumb);
        if(blob != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(blob , 0, blob.length);
            bitmap = Utils.getResizedBitmap(bitmap,500);
            bitmap = Utils.getCropedBitmap(bitmap);
            Resources res = context.getResources();
            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
            dr.setCircular(true);


            holder.iconImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.iconImageView.setImageDrawable(dr);
        } else {
            Resources res = context.getResources();
            Bitmap src = BitmapFactory.decodeResource(res, R.drawable.bm_person_dark_24dp);
            RoundedBitmapDrawable dr =
                    RoundedBitmapDrawableFactory.create(res, src);

            dr.setCircular(true);


            holder.iconImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.iconImageView.setImageDrawable(dr);
        }
    }

    public class PessoaViewHolder extends RecyclerView.ViewHolder {

        long _id;

        ImageView iconImageView;
        TextView idTextView;
        TextView nomeTextView;
        TextView razaoSocialTextView;

        public PessoaViewHolder(View v) {
            super(v);
            iconImageView = (ImageView) v.findViewById(R.id.iconImageView);
            idTextView = (TextView) v.findViewById(R.id.idTextView);
            nomeTextView = (TextView) v.findViewById(R.id.nomeTextView);
            razaoSocialTextView = (TextView) v.findViewById(R.id.razaoSocialTextView);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pessoaClickListener.pessoaClick(_id);
                }
            });
        }
    }

    public interface OnPessoaClickListener {
        void pessoaClick(long id);
    }
}