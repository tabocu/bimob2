package br.com.blackseed.bimob;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
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

import br.com.blackseed.bimob.components.MultiEditView;
import br.com.blackseed.bimob.data.DbContract;
import br.com.blackseed.bimob.utils.MaskTextWatcher;

public class AddPessoaFisicaActivity extends AppCompatActivity implements
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String PESSOA_FISICA_URI = "pessoa_fisica_uri";
    public static final String TELEFONES_URI = "telefones_uri";
    public static final String EMAILS_URI = "emails_uri";

    static final int PESSOA_LOADER   = 0;
    static final int TELEFONE_LOADER = 1;
    static final int EMAIL_LOADER    = 2;

    private static final String[] PESSOA_COLUMNS = {
            DbContract.PessoaEntry.COLUMN_NOME ,
            DbContract.PessoaEntry.COLUMN_CP,
            DbContract.PessoaEntry.COLUMN_RG,
            DbContract.PessoaEntry.COLUMN_ESTADO_CIVIL
    };

    private static final String[] TELEFONE_COLUMNS = {
            DbContract.TelefoneEntry.COLUMN_NUMERO
    };

    private static final String[] EMAIL_COLUMNS = {
            DbContract.EmailEntry.COLUMN_ENDERECO
    };

    public static final int COL_PESSOA_NOME     = 0;
    public static final int COL_PESSOA_CP       = 1;
    public static final int COL_PESSOA_RG       = 2;
    public static final int COL_PESSOA_ESTADO_CIVIL = 3;
    public static final int COL_TELEFONE_NUMERO = 0;
    public static final int COL_EMAIL_ENDERECO  = 0;

    private FloatingActionButton mFab;

    private EditText mNomeEditText;
    private EditText mCpfEditText;
    private EditText mRgEditText;
    private Spinner mEstadoCivilSpinner;
    private MultiEditView mTelefoneMultiEditView;
    private MultiEditView mEmailMultiEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(PESSOA_LOADER,getIntent().getExtras(),this);
        getLoaderManager().initLoader(TELEFONE_LOADER,getIntent().getExtras(),this);
        getLoaderManager().initLoader(EMAIL_LOADER,getIntent().getExtras(),this);
        setContentView(R.layout.activity_add_pessoa_fisica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        // Obtem referencias dos componentes do layout
        mNomeEditText = (EditText) findViewById(R.id.nomeEditText);
        mCpfEditText = (EditText) findViewById(R.id.cpfEditText);
        mRgEditText = (EditText) findViewById(R.id.rgEditText);
        mEstadoCivilSpinner = (Spinner) findViewById(R.id.estadoCivilSpinner);
        mTelefoneMultiEditView = (MultiEditView) findViewById(R.id.telefoneMultiEditView);
        mEmailMultiEditView = (MultiEditView) findViewById(R.id.emailMultiEditView);

        // Configura o campo de Cpf com mascara e tipo de entrada
        mCpfEditText.addTextChangedListener(new MaskTextWatcher(MaskTextWatcher.Mask.CPF));
        mCpfEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                if (hasFocus && editText.getText().toString().isEmpty()) {
                    editText.setText("0");
                } else if (!hasFocus && editText.getText().toString().equals("000.000.000-00")) {
                    editText.setText("");
                }
            }
        });
        mCpfEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.item_spinner,R.id.itemTextView,
                getResources().getStringArray(R.array.estados_civis_array));

        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        mEstadoCivilSpinner.setAdapter(dataAdapter);
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
        ContentValues pessoaValues = new ContentValues();
        pessoaValues.put(DbContract.PessoaEntry.COLUMN_NOME,mNomeEditText.getText().toString());
        pessoaValues.put(DbContract.PessoaEntry.COLUMN_CP,mCpfEditText.getText().toString().replaceAll("\\D", ""));
        pessoaValues.put(DbContract.PessoaEntry.COLUMN_RG,mRgEditText.getText().toString());
        pessoaValues.put(DbContract.PessoaEntry.COLUMN_ESTADO_CIVIL,mEstadoCivilSpinner.getSelectedItemPosition());
        pessoaValues.put(DbContract.PessoaEntry.COLUMN_IS_PESSOA_FISICA,true);
        pessoaValues.put(DbContract.PessoaEntry.COLUMN_IS_FAVORITO,false);

        Uri pessoaUri;
        if(getIntent().getExtras() == null) { //Cria
            pessoaUri = getContentResolver().insert(DbContract.PessoaEntry.CONTENT_URI,pessoaValues);
        } else {
            pessoaUri = getIntent().getExtras().getParcelable(PESSOA_FISICA_URI);
            getContentResolver().update(pessoaUri,pessoaValues,null,null);
            getContentResolver().delete((Uri)getIntent().getExtras().getParcelable(TELEFONES_URI),null,null);
            getContentResolver().delete((Uri)getIntent().getExtras().getParcelable(EMAILS_URI),null,null);
        }

        long pessoaId = ContentUris.parseId(pessoaUri);

        ContentValues telefoneValues = new ContentValues();
        List<String> telefoneList = mTelefoneMultiEditView.getTextList();
        for (String telefone : telefoneList) {
            telefoneValues.put(DbContract.TelefoneEntry.COLUMN_PESSOA_ID, pessoaId);
            telefoneValues.put(DbContract.TelefoneEntry.COLUMN_NUMERO, telefone);
            getContentResolver().insert(DbContract.TelefoneEntry.CONTENT_URI,telefoneValues);
        }

        ContentValues emailValues = new ContentValues();
        List<String> emailList = mEmailMultiEditView.getTextList();
        for (String email : emailList) {
            emailValues.put(DbContract.EmailEntry.COLUMN_PESSOA_ID, pessoaId);
            emailValues.put(DbContract.EmailEntry.COLUMN_ENDERECO, email);
            getContentResolver().insert(DbContract.EmailEntry.CONTENT_URI,emailValues);
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

        if(id == PESSOA_LOADER && args != null) {
            Uri uri = args.getParcelable(PESSOA_FISICA_URI);
            return new CursorLoader(
                    this,
                    uri,
                    PESSOA_COLUMNS,
                    null,
                    null,
                    null
            );
        } else if(id == TELEFONE_LOADER && args != null) {
            Uri uri = args.getParcelable(TELEFONES_URI);
            return new CursorLoader(
                    this,
                    uri,
                    TELEFONE_COLUMNS,
                    null,
                    null,
                    null
            );

        } else if (id == EMAIL_LOADER && args != null) {
            Uri uri = args.getParcelable(EMAILS_URI);
            return new CursorLoader(
                    this,
                    uri,
                    EMAIL_COLUMNS,
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
            case 0:
                if (data != null && data.moveToFirst()) {
                    String nome = data.getString(COL_PESSOA_NOME);
                    mNomeEditText.setText(nome);

                    String cpf = data.getString(COL_PESSOA_CP);
                    mCpfEditText.setText(cpf);

                    String rg = data.getString(COL_PESSOA_RG);
                    mRgEditText.setText(rg);

                    int estadoCivil = data.getInt(COL_PESSOA_ESTADO_CIVIL);
                    mEstadoCivilSpinner.setSelection(estadoCivil);
                }
                break;
            case 1:
                List<String> numerosList = new ArrayList<>();
                while (data != null && data.moveToNext()) {
                    numerosList.add(data.getString(COL_TELEFONE_NUMERO));
                }
                mTelefoneMultiEditView.setTextList(numerosList);
                break;
            case 2:
                List<String> enderecosList = new ArrayList<>();
                while (data != null && data.moveToNext()) {
                    enderecosList.add(data.getString(COL_EMAIL_ENDERECO));
                }
                mEmailMultiEditView.setTextList(enderecosList);
                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
