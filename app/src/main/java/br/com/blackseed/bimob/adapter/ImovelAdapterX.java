package br.com.blackseed.bimob.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.blackseed.bimob.R;
import br.com.blackseed.bimob.data.DbContract;
import br.com.blackseed.bimob.utils.Utils;


public class ImovelAdapterX extends RecyclerViewCursorAdapter<ImovelAdapterX.ImovelViewHolder> {

    private static final String TAG = ImovelAdapterX.class.getSimpleName();
    private final Context context;

    OnImovelClickListener imovelClickListener;

    public ImovelAdapterX(Context context, Cursor cursor, OnImovelClickListener l) {
        super(cursor);
        this.context = context;
        imovelClickListener = l;
    }

    @Override
    public ImovelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.temporario_imovel_list_item, parent, false);
        return new ImovelViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(ImovelViewHolder holder, Cursor cursor) {
        int columnId = cursor.getColumnIndexOrThrow(DbContract.ImovelEntry._ID);
        int columnNome = cursor.getColumnIndexOrThrow(DbContract.ImovelEntry.COLUMN_NOME);
        int columnTipo = cursor.getColumnIndexOrThrow(DbContract.ImovelEntry.COLUMN_TIPO);
        int columnArea = cursor.getColumnIndexOrThrow(DbContract.ImovelEntry.COLUMN_AREA);
        int columnThumb = cursor.getColumnIndexOrThrow(DbContract.FotoEntry.COLUMN_THUMB);

        holder._id = cursor.getLong(columnId);
        holder.idTextView.setText(String.valueOf(holder._id));
        holder.nomeTextView.setText(cursor.getString(columnNome));
        String tipo = context.getResources().getStringArray(R.array.imoveis_tipos_array)[cursor.getInt(columnTipo)];
        holder.tipoTextView.setText(tipo);
        holder.areaTextView.setText(cursor.getInt(columnArea) + " m²");
        byte[] blob = cursor.getBlob(columnThumb);
        if(blob != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(blob , 0, blob.length);
            holder.iconImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.iconImageView.setImageBitmap(Utils.getResizedBitmap(bitmap,500));
        } else {
            holder.iconImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.iconImageView.setImageResource(R.drawable.bm_person_dark_24dp);
        }
    }

    public class ImovelViewHolder extends RecyclerView.ViewHolder {

        long _id;

        ImageView iconImageView;
        TextView idTextView;
        TextView nomeTextView;
        TextView tipoTextView;
        TextView areaTextView;

        public ImovelViewHolder(View v) {
            super(v);
            iconImageView = (ImageView) v.findViewById(R.id.iconImageView);
            idTextView = (TextView) v.findViewById(R.id.idTextView);
            nomeTextView = (TextView) v.findViewById(R.id.nomeTextView);
            tipoTextView = (TextView) v.findViewById(R.id.tipoTextView);
            areaTextView = (TextView) v.findViewById(R.id.areaTextView);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imovelClickListener.imovelClick(_id);
                }
            });
        }
    }

    public interface OnImovelClickListener {
        void imovelClick(long id);
    }
}
