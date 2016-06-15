package br.com.blackseed.bimob;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.blackseed.bimob.data.DbContract;
import br.com.blackseed.bimob.utils.CurrencyTextWatcher;
import br.com.blackseed.bimob.utils.LocacaoPdf;

public class AddLocacaoActivity extends AppCompatActivity
        implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd 'de' MMM 'de' yyyy");
    public static final SimpleDateFormat DATE_FORMATTER_PDF = new SimpleDateFormat("dd'/'MM'/'yyyy");

    public static final String LOCACAO_URI = "locacao_uri";

    static final int LOCACAO_LOADER   = 0;

    Uri mLocadorPessoaUri;
    Uri mLocatarioPessoaUri;
    Uri mFiadorPessoaUri;
    Uri mImovelUri;

    private static final String[] LOCACAO_COLUMNS = {
            DbContract.LocacaoEntry.COLUMN_DATA_INICIO,
            DbContract.LocacaoEntry.COLUMN_DATA_FIM,
            DbContract.LocacaoEntry.COLUMN_VALOR_ALUGUEL,
            DbContract.LocacaoEntry.COLUMN_IMOVEL_ID,
            DbContract.LocacaoEntry.COLUMN_LOCADOR_ID,
            DbContract.LocacaoEntry.COLUMN_LOCATARIO_ID,
            DbContract.LocacaoEntry.COLUMN_FIADOR_ID
    };

    public static final int COL_DATA_INICIO         = 0;
    public static final int COL_DATA_FIM            = 1;
    public static final int COL_VALOR_ALUGUEL       = 2;
    public static final int COL_IMOVEL_ID           = 3;
    public static final int COL_LOCADOR_ID          = 4;
    public static final int COL_LOCATARIO_ID        = 5;
    public static final int COL_FIADOR_ID           = 6;

    private static final int REQUEST_CODE_LOCATARIO = 0;
    private static final int REQUEST_CODE_LOCADOR   = 1;
    private static final int REQUEST_CODE_FIADOR    = 2;
    private static final int REQUEST_CODE_IMOVEL    = 3;


    TextView mAddImovelBtn;
    TextView mAddLocatarioBtn;
    TextView mAddLocadorBtn;
    TextView mAddFiadorBtn;
    TextView mAddInicioDataBtn;
    TextView mAddFimDataBtn;
    TextView mPrazoTextView;
    EditText mValorEditText;

    Date mInicioDate;
    Date mFimDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOCACAO_LOADER,getIntent().getExtras(),this);
        setContentView(R.layout.activity_add_locacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAddImovelBtn = (TextView) findViewById(R.id.addImovelBtn);
        mAddLocatarioBtn = (TextView) findViewById(R.id.addLocatarioBtn);
        mAddLocadorBtn = (TextView) findViewById(R.id.addLocadorBtn);
        mAddFiadorBtn = (TextView) findViewById(R.id.addFiadorBtn);
        mAddInicioDataBtn = (TextView) findViewById(R.id.addInicioDatadBtn);
        mAddFimDataBtn = (TextView) findViewById(R.id.addFimDatadBtn);
        mPrazoTextView = (TextView) findViewById(R.id.prazoTextView);
        mValorEditText = (EditText) findViewById(R.id.valorEditText);

        mAddImovelBtn.setOnClickListener(this);
        mAddLocadorBtn.setOnClickListener(this);
        mAddLocatarioBtn.setOnClickListener(this);
        mAddFiadorBtn.setOnClickListener(this);
        mAddInicioDataBtn.setOnClickListener(this);
        mAddFimDataBtn.setOnClickListener(this);

        mInicioDate = new Date();
        mFimDate = new Date();


        mAddInicioDataBtn.setText(DATE_FORMATTER.format(mInicioDate.getTime()));
        mAddFimDataBtn.setText(DATE_FORMATTER.format(mFimDate.getTime()));
        mPrazoTextView.setText(getDiffDate());

        mValorEditText.addTextChangedListener(new CurrencyTextWatcher());

        mValorEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_locacao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_apply) {
            if(mLocadorPessoaUri == null
                    || mLocatarioPessoaUri == null
                    || mImovelUri == null
                    || mFiadorPessoaUri == null) {
                incompletoAlerta();
                return false;
            }
            saveData();
            finish();
            return true;
        } else if (id == R.id.action_pdf) {

            generatePdf();
            Toast.makeText(this,"Contrato gerado!",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getDiffDate(){
        long diff = mFimDate.getTime() - mInicioDate.getTime();
        long dia = diff/86400000+1;
        long mes = (long) (dia/30.4375);
        long mes_rest = mes%12;
        long ano = (mes-mes_rest)/12;

        long year = ano;
        long month = mes_rest;
        //1 ano e 2 meses


        String yearStr = "";
        String monthStr = "";

        if(year > 1)
            yearStr = year + " anos";
        if(month > 1)
            monthStr = month + " meses";

        if (year == 1)
            yearStr = "1 ano";
        if (month == 1)
            monthStr = "1 mes";

        String diffStr = yearStr + " e " + monthStr;

        if (year == 0 && month > 0)
            diffStr = monthStr;
        if (year > 0 && month == 0)
            diffStr = yearStr;

        if(year == 0 && month == 0)
            diffStr = " 0 meses";

        return diffStr;
    }

    @Override
    public void onClick(View v) {
        if(v == mAddImovelBtn) {
            Intent intent = new Intent(getBaseContext(), ImovelPickerActivity.class);
            startActivityForResult(intent,REQUEST_CODE_IMOVEL);
        } else if (v == mAddLocadorBtn) {
            Intent intent = new Intent(getBaseContext(), PessoaPickerActivity.class);
            startActivityForResult(intent,REQUEST_CODE_LOCADOR);
        } else if(v == mAddLocatarioBtn) {
            Intent intent = new Intent(getBaseContext(), PessoaPickerActivity.class);
            startActivityForResult(intent,REQUEST_CODE_LOCATARIO);
        } else if (v == mAddFiadorBtn) {
            Intent intent = new Intent(getBaseContext(), PessoaPickerActivity.class);
            startActivityForResult(intent,REQUEST_CODE_FIADOR);
        } else if (v == mAddInicioDataBtn) {

            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTime(mInicioDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);

                    if(newDate.getTime().getTime() > mFimDate.getTime()) {
                        mInicioDate.setTime(mFimDate.getTime());
                        mAddInicioDataBtn.setText(DATE_FORMATTER.format(mFimDate.getTime()));
                    } else {
                        mInicioDate.setTime(newDate.getTime().getTime());
                        mAddInicioDataBtn.setText(DATE_FORMATTER.format(newDate.getTime()));
                    }
                    mPrazoTextView.setText(getDiffDate());
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        } else if (v == mAddFimDataBtn) {

            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTime(mFimDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);

                    if(newDate.getTime().getTime() < mInicioDate.getTime()) {
                        mFimDate.setTime(mInicioDate.getTime());
                        mAddFimDataBtn.setText(DATE_FORMATTER.format(mInicioDate.getTime()));
                    } else {
                        mFimDate.setTime(newDate.getTime().getTime());
                        mAddFimDataBtn.setText(DATE_FORMATTER.format(newDate.getTime()));
                    }
                    mPrazoTextView.setText(getDiffDate());
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    }

    private void saveData() {



        ContentValues locacaoValues = new ContentValues();

        String valor = mValorEditText.getText().toString().replaceAll("\\D", "");
        if(!valor.isEmpty())
            locacaoValues.put(DbContract.LocacaoEntry.COLUMN_VALOR_ALUGUEL,Float.valueOf(valor)/100.0f);
        if(mLocadorPessoaUri != null)
            locacaoValues.put(DbContract.LocacaoEntry.COLUMN_LOCADOR_ID,ContentUris.parseId(mLocadorPessoaUri));
        if(mLocatarioPessoaUri != null)
            locacaoValues.put(DbContract.LocacaoEntry.COLUMN_LOCATARIO_ID,ContentUris.parseId(mLocatarioPessoaUri));
        if(mFiadorPessoaUri != null)
            locacaoValues.put(DbContract.LocacaoEntry.COLUMN_FIADOR_ID,ContentUris.parseId(mFiadorPessoaUri));
        if(mImovelUri != null)
            locacaoValues.put(DbContract.LocacaoEntry.COLUMN_IMOVEL_ID,ContentUris.parseId(mImovelUri));

        locacaoValues.put(DbContract.LocacaoEntry.COLUMN_DATA_INICIO, mInicioDate.getTime());
        locacaoValues.put(DbContract.LocacaoEntry.COLUMN_DATA_FIM, mFimDate.getTime());

        Uri locacaoUri;
        if(getIntent().getExtras() == null) { //Cria
            locacaoUri = getContentResolver().insert(DbContract.LocacaoEntry.CONTENT_URI,locacaoValues);
        } else {
            locacaoUri = getIntent().getExtras().getParcelable(LOCACAO_URI);
            getContentResolver().update(locacaoUri,locacaoValues,null,null);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_LOCADOR) {
                Bundle bundle = data.getExtras();
                mLocadorPessoaUri = bundle.getParcelable(PessoaPickerActivity.PESSOA_URI);
                Cursor c = getContentResolver().query(mLocadorPessoaUri,null,null,null,null);
                if(c.moveToFirst()) {
                    String nome = c.getString(c.getColumnIndex(DbContract.PessoaEntry.COLUMN_NOME));
                    mAddLocadorBtn.setText(nome);
                }
            } else if(requestCode == REQUEST_CODE_LOCATARIO) {
                Bundle bundle = data.getExtras();
                mLocatarioPessoaUri = bundle.getParcelable(PessoaPickerActivity.PESSOA_URI);
                Cursor c = getContentResolver().query(mLocatarioPessoaUri, null, null, null, null);
                if (c.moveToFirst()) {
                    String nome = c.getString(c.getColumnIndex(DbContract.PessoaEntry.COLUMN_NOME));
                    mAddLocatarioBtn.setText(nome);
                }
            } else if(requestCode == REQUEST_CODE_FIADOR) {
                Bundle bundle = data.getExtras();
                mFiadorPessoaUri = bundle.getParcelable(PessoaPickerActivity.PESSOA_URI);
                Cursor c = getContentResolver().query(mFiadorPessoaUri, null, null, null, null);
                if (c.moveToFirst()) {
                    String nome = c.getString(c.getColumnIndex(DbContract.PessoaEntry.COLUMN_NOME));
                    mAddFiadorBtn.setText(nome);
                }
            } else if(requestCode == REQUEST_CODE_IMOVEL) {
                Bundle bundle = data.getExtras();
                mImovelUri = bundle.getParcelable(ImovelPickerActivity.IMOVEL_URI);
                Cursor c = getContentResolver().query(mImovelUri, null, null, null, null);
                if (c.moveToFirst()) {
                    String nome = c.getString(c.getColumnIndex(DbContract.ImovelEntry.COLUMN_NOME));
                    mAddImovelBtn.setText(nome);
                }
            }

        }
    }

    private void incompletoAlerta() {
        // Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Define os parâmetros
        builder.setTitle("Contrato incompleto!")
                .setMessage("O seu contrato não está completo, não é possível salvar sem que todos os campos estejam preenchidos.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {}
                }).create().show(); // Exibe
    }

    private void generatePdf (){


        LocacaoPdf contrato = new LocacaoPdf();

        if(mImovelUri != null) {
            Cursor c = getContentResolver().query(mImovelUri,null,null,null,null);
            c.moveToFirst();

            contrato.setImovelArea(String.valueOf(c.getInt(c.getColumnIndex(DbContract.ImovelEntry.COLUMN_AREA))));

            int tipo = c.getInt(c.getColumnIndex(DbContract.ImovelEntry.COLUMN_TIPO));
            contrato.setImovelTipo(getResources().getStringArray(R.array.imoveis_tipos_array)[tipo]);

            c.close();
        }

        if(mFiadorPessoaUri != null) {
            Cursor c = getContentResolver().query(mFiadorPessoaUri,null,null,null,null);
            c.moveToFirst();

            contrato.setFiadorNome(c.getString(c.getColumnIndex(DbContract.PessoaEntry.COLUMN_NOME)));
            contrato.setFiadorCpf(c.getString(c.getColumnIndex(DbContract.PessoaEntry.COLUMN_CP)));

            c.close();
        }

        if(mLocadorPessoaUri != null) {
            Cursor c = getContentResolver().query(mLocadorPessoaUri,null,null,null,null);
            c.moveToFirst();

            contrato.setLocadorNome(c.getString(c.getColumnIndex(DbContract.PessoaEntry.COLUMN_NOME)));
            contrato.setLocadorCpf(c.getString(c.getColumnIndex(DbContract.PessoaEntry.COLUMN_CP)));

            c.close();
        }

        if(mLocatarioPessoaUri != null) {
            Cursor c = getContentResolver().query(mLocatarioPessoaUri,null,null,null,null);
            c.moveToFirst();

            contrato.setLocatarioNome(c.getString(c.getColumnIndex(DbContract.PessoaEntry.COLUMN_NOME)));
            contrato.setLocatarioCpf(c.getString(c.getColumnIndex(DbContract.PessoaEntry.COLUMN_CP)));

            c.close();
        }

        contrato.setDataInicio(DATE_FORMATTER_PDF.format(mInicioDate));
        contrato.setDataFim(DATE_FORMATTER_PDF.format(mFimDate));
        contrato.setPrazo(getDiffDate());
        String valor = mValorEditText.getText().toString().replaceAll("\\D", "");
        if(!valor.isEmpty())
            contrato.setValor(valor);

        contrato.generatePDF();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOCACAO_LOADER && args != null) {
            Uri uri = args.getParcelable(LOCACAO_URI);
            return new CursorLoader(
                    this,
                    uri,
                    LOCACAO_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mInicioDate.setTime(data.getLong(COL_DATA_INICIO));
            mFimDate.setTime(data.getLong(COL_DATA_FIM));
            mAddInicioDataBtn.setText(DATE_FORMATTER.format(mInicioDate.getTime()));
            mAddFimDataBtn.setText(DATE_FORMATTER.format(mFimDate.getTime()));

            float valor = data.getFloat(COL_VALOR_ALUGUEL);
            mValorEditText.setText(String.valueOf(valor));

            long imovelId = data.getLong(COL_IMOVEL_ID);
            mImovelUri = DbContract.ImovelEntry.buildImovelUri(imovelId);
            Cursor imovelCursor = getContentResolver().query(mImovelUri,null,null,null,null);

            if(imovelCursor.moveToFirst())
                mAddImovelBtn.setText(imovelCursor.getString(imovelCursor.getColumnIndex(DbContract.ImovelEntry.COLUMN_NOME)));

            long fiadorId = data.getLong(COL_FIADOR_ID);
            mFiadorPessoaUri = DbContract.PessoaEntry.buildPessoaUri(fiadorId);
            Cursor fiadorCursor = getContentResolver().query(mFiadorPessoaUri,null,null,null,null);

            if(fiadorCursor.moveToFirst())
                mAddFiadorBtn.setText(fiadorCursor.getString(fiadorCursor.getColumnIndex(DbContract.PessoaEntry.COLUMN_NOME)));

            long locadorId = data.getLong(COL_LOCADOR_ID);
            mLocadorPessoaUri = DbContract.PessoaEntry.buildPessoaUri(locadorId);
            Cursor locadorCursor = getContentResolver().query(mLocadorPessoaUri,null,null,null,null);

            if(locadorCursor.moveToFirst())
                mAddLocadorBtn.setText(locadorCursor.getString(locadorCursor.getColumnIndex(DbContract.PessoaEntry.COLUMN_NOME)));

            long locatarioId = data.getLong(COL_LOCATARIO_ID);
            mLocatarioPessoaUri = DbContract.PessoaEntry.buildPessoaUri(locatarioId);
            Cursor locatarioCursor = getContentResolver().query(mLocatarioPessoaUri,null,null,null,null);

            if(locatarioCursor.moveToFirst())
                mAddLocatarioBtn.setText(locatarioCursor.getString(locatarioCursor.getColumnIndex(DbContract.PessoaEntry.COLUMN_NOME)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
