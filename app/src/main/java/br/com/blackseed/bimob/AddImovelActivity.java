package br.com.blackseed.bimob;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.blackseed.bimob.data.DbContract;
import br.com.blackseed.bimob.utils.CurrencyTextWatcher;
import br.com.blackseed.bimob.utils.MaskTextWatcher;

public class AddImovelActivity extends AppCompatActivity implements
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String IMOVEL_URI = "imovel_uri";

    static final int IMOVEL_LOADER   = 0;

    private static final String[] IMOVEL_COLUMNS = {
            DbContract.ImovelEntry.COLUMN_NOME,
            DbContract.ImovelEntry.COLUMN_VALOR_ALUGUEL,
            DbContract.ImovelEntry.COLUMN_AREA,
            DbContract.ImovelEntry.COLUMN_TIPO
    };

    public static final int COL_IMOVEL_NOME          = 0;
    public static final int COL_IMOVEL_VALOR_ALUGUEL = 1;
    public static final int COL_COLUMN_AREA          = 2;
    public static final int COL_COLUMN_TIPO          = 3;

    private FloatingActionButton mFab;

    private EditText mNomeEditText;
    private EditText mAluguelEditText;
    private EditText mAreaEditText;
    private Spinner mTipoSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(IMOVEL_LOADER,getIntent().getExtras(),this);
        setContentView(R.layout.activity_add_imovel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        // Obtem referencias dos componentes do layout
        mNomeEditText = (EditText) findViewById(R.id.nomeEditText);
        mAluguelEditText = (EditText) findViewById(R.id.valorEditText);
        mAreaEditText = (EditText) findViewById(R.id.areaEditText);
        mTipoSpinner = (Spinner) findViewById(R.id.tipoEditText);

        mAluguelEditText.addTextChangedListener(new CurrencyTextWatcher());

        mAluguelEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        mAreaEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.imoveis_tipos_array));

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
            imovelValues.put(DbContract.ImovelEntry.COLUMN_VALOR_ALUGUEL,Float.valueOf(valor)/100.0f);

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
    }

    @Override
    public void onClick(View v) {
        if(v == mFab) {
            Toast.makeText(this,"Camera",Toast.LENGTH_SHORT).show();
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
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String nome = data.getString(COL_IMOVEL_NOME);
            mNomeEditText.setText(nome);

            float valor = data.getFloat(COL_IMOVEL_VALOR_ALUGUEL);
            mAluguelEditText.setText(String.valueOf(valor));

            int area = data.getInt(COL_COLUMN_AREA);
            mAreaEditText.setText(String.valueOf(area));

            int tipo = data.getInt(COL_COLUMN_TIPO);
            Log.v("SPINNER SAVE", String.valueOf(tipo));
            mTipoSpinner.setSelection(tipo);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
