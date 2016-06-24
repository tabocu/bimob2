package br.com.blackseed.bimob;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.blackseed.bimob.data.DbContract;
import br.com.blackseed.bimob.utils.CurrencyTextWatcher;
import br.com.blackseed.bimob.utils.MaskTextWatcher;
import br.com.blackseed.bimob.utils.UnitTextWatcher;
import br.com.blackseed.bimob.utils.Utils;

public class AddImovelActivity extends AppCompatActivity implements
        View.OnClickListener,
        DialogInterface.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String IMOVEL_URI = "imovel_uri";
    public static final String FOTO_URI   = "foto_uri";

    static final int IMOVEL_LOADER   = 0;
    static final int FOTO_LOADER     = 1;

    private static final String[] IMOVEL_COLUMNS = {
            DbContract.ImovelEntry.COLUMN_NOME,
            DbContract.ImovelEntry.COLUMN_VALOR_ALUGUEL,
            DbContract.ImovelEntry.COLUMN_VALOR_IMOVEL,
            DbContract.ImovelEntry.COLUMN_AREA,
            DbContract.ImovelEntry.COLUMN_TIPO
    };

    private static final String[] FOTO_COLUMNS = {
            DbContract.FotoEntry.COLUMN_THUMB
    };

    public static final int COL_IMOVEL_NOME          = 0;
    public static final int COL_IMOVEL_VALOR_ALUGUEL = 1;
    public static final int COL_IMOVEL_VALOR_IMOVEL  = 2;
    public static final int COL_IMOVEL_AREA          = 3;
    public static final int COL_IMOVEL_TIPO          = 4;

    public static final int COL_FOTO_THUMB           = 0;

    static final int REQUEST_CODE_CAMERA  = 0;
    static final int REQUEST_CODE_GALLERY = 1;
    static final int REQUEST_CODE_PLACE   = 2;

    private FloatingActionButton mFab;

    private ImageView mPhotoImageView;
    private Bitmap mBitmap;

    private EditText mNomeEditText;
    private EditText mAluguelEditText;
    private EditText mValorImovelEditText;
    private EditText mAreaEditText;
    private Spinner mTipoSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(IMOVEL_LOADER,getIntent().getExtras(),this);
        getLoaderManager().initLoader(FOTO_LOADER,getIntent().getExtras(),this);
        setContentView(R.layout.activity_add_imovel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        // Obtem referencias dos componentes do layout
        mPhotoImageView = (ImageView) findViewById(R.id.photoImageView);
        mNomeEditText = (EditText) findViewById(R.id.nomeEditText);
        mAluguelEditText = (EditText) findViewById(R.id.valorEditText);
        mValorImovelEditText = (EditText) findViewById(R.id.valorImovelEditText);
        mAreaEditText = (EditText) findViewById(R.id.areaEditText);
        mTipoSpinner = (Spinner) findViewById(R.id.tipoSpinner);

        mAreaEditText.addTextChangedListener(new UnitTextWatcher(" m²", mAreaEditText));
        mAluguelEditText.addTextChangedListener(new CurrencyTextWatcher());
        mValorImovelEditText.addTextChangedListener(new CurrencyTextWatcher());

        mAluguelEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mValorImovelEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mAreaEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.item_spinner,R.id.itemTextView,
                getResources().getStringArray(R.array.imoveis_tipos_array));

        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        mTipoSpinner.setAdapter(dataAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_apply) {
            saveData();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        ContentValues imovelValues = new ContentValues();
        imovelValues.put(DbContract.ImovelEntry.COLUMN_NOME,mNomeEditText.getText().toString());

        String valor = mAluguelEditText.getText().toString().replaceAll("\\D", "");
        if(!valor.isEmpty())
            imovelValues.put(DbContract.ImovelEntry.COLUMN_VALOR_ALUGUEL,Integer.valueOf(valor));

        String valorImovel = mValorImovelEditText.getText().toString().replaceAll("\\D", "");
        if(!valorImovel.isEmpty())
            imovelValues.put(DbContract.ImovelEntry.COLUMN_VALOR_IMOVEL,Integer.valueOf(valorImovel));

        String area = mAreaEditText.getText().toString().replaceAll("\\D", "");
        if(!area.isEmpty())
            imovelValues.put(DbContract.ImovelEntry.COLUMN_AREA,Integer.valueOf(area));

        imovelValues.put(DbContract.ImovelEntry.COLUMN_TIPO,mTipoSpinner.getSelectedItemPosition());
        Log.v("SPINNER SAVE", String.valueOf(mTipoSpinner.getSelectedItemPosition()));

        Uri imovelUri;
        if(getIntent().getExtras() == null) { //Cria
            imovelUri = getContentResolver().insert(DbContract.ImovelEntry.CONTENT_URI,imovelValues);
        } else {
            imovelUri = getIntent().getExtras().getParcelable(IMOVEL_URI);
            getContentResolver().update(imovelUri,imovelValues,null,null);
        }

        long imovelId = ContentUris.parseId(imovelUri);


        if(mBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Utils.getResizedBitmap(mBitmap, 500).compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] byteArray = stream.toByteArray();

            ContentValues fotoValues = new ContentValues();
            fotoValues.put(DbContract.FotoEntry.COLUMN_IMOVEL_ID, imovelId);
            fotoValues.put(DbContract.FotoEntry.COLUMN_THUMB, byteArray);
            fotoValues.put(DbContract.FotoEntry.COLUMN_PRIMARY, true);

            Uri fotoUri;
            if (getIntent().getExtras() == null) {
                fotoUri = getContentResolver().insert(DbContract.FotoEntry.CONTENT_URI, fotoValues);
            }
        }


//
//        if(mBitmap != null) {
//
//            File bitmapTree = new File(
//                    Environment.getExternalStorageDirectory() +
//                            File.separator +
//                            "br.com.blackseed.bimob" +
//                            File.separator +
//                            "imoveis" +
//                            File.separator +
//                            ".nomedia");
//            bitmapTree.getParentFile().getParentFile().mkdir();
//            bitmapTree.getParentFile().mkdir();
//            try {
//                bitmapTree.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            File bitmapFile = new File(
//                    Environment.getExternalStorageDirectory() +
//                    File.separator +
//                    "br.com.blackseed.bimob" +
//                    File.separator +
//                    "imoveis",
//                    imovelUri.getLastPathSegment() + ".jpg");
//
//            Utils.saveBitmap(mBitmap,bitmapFile);
//        }
    }

    @Override
    public void onClick(View v) {
        if(v == mFab) {
            selectImage();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(id == IMOVEL_LOADER && args != null) {
            Uri uri = args.getParcelable(IMOVEL_URI);
            return new CursorLoader(
                    this,
                    uri,
                    IMOVEL_COLUMNS,
                    null,
                    null,
                    null
            );

        } else if(id == FOTO_LOADER && args != null) {
            Uri uri = args.getParcelable(FOTO_URI);
            return new CursorLoader(
                    this,
                    uri,
                    FOTO_COLUMNS,
                    null,
                    null,
                    null
            );
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case IMOVEL_LOADER: {
                if (data != null && data.moveToFirst()) {
                    String nome = data.getString(COL_IMOVEL_NOME);
                    mNomeEditText.setText(nome);

                    int valor = data.getInt(COL_IMOVEL_VALOR_ALUGUEL);
                    mAluguelEditText.setText(String.valueOf(valor));

                    int valorImovel = data.getInt(COL_IMOVEL_VALOR_IMOVEL);
                    mValorImovelEditText.setText(String.valueOf(valorImovel));

                    int area = data.getInt(COL_IMOVEL_AREA);
                    mAreaEditText.setText(String.valueOf(area));

                    int tipo = data.getInt(COL_IMOVEL_TIPO);
                    mTipoSpinner.setSelection(tipo);
                }
                break;
            }
            case FOTO_LOADER: {
                if (data != null && data.moveToFirst()) {
                    byte[] blob = data.getBlob(COL_FOTO_THUMB);
                    setBitmap(BitmapFactory.decodeByteArray(blob , 0, blob.length));
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    public void selectImage() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        CharSequence[] options = getResources().getStringArray(R.array.origem_imagem_array);
        builder.setItems(options,this);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        if(item == 0) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        } else if(item == 1) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        }
    }

    private void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            mBitmap = bitmap;
            mPhotoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mPhotoImageView.setImageBitmap(Utils.getResizedBitmap(mBitmap,500));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                try {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    setBitmap(BitmapFactory.decodeFile(f.getAbsolutePath(),bitmapOptions));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

            }
        } else if(requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();

                Cursor c = getContentResolver().query(
                        selectedImage,
                        new String[]{MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null);

                c.moveToFirst();
                String picturePath = c.getString(0);
                c.close();
                setBitmap(BitmapFactory.decodeFile(picturePath));
            } else {

            }
        } else if (requestCode == REQUEST_CODE_PLACE) {
            if (resultCode == RESULT_OK) {

            } else {

            }
        }
    }
}
