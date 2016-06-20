package br.com.blackseed.bimob.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import br.com.blackseed.bimob.data.DbContract.*;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;

    static final String DATABASE_NAME = "bimob.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PESSOA_TABLE        = "CREATE TABLE " +
                PessoaEntry.TABLE_NAME              + " (" +
                PessoaEntry._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PessoaEntry.COLUMN_NOME             + " VARCHAR(80), " +
                PessoaEntry.COLUMN_RAZAO_SOCIAL     + " VARCHAR(80), " +
                PessoaEntry.COLUMN_CP               + " VARCHAR(14), " +
                PessoaEntry.COLUMN_RG               + " VARCHAR(20), " +
                PessoaEntry.COLUMN_ESTADO_CIVIL     + " INTEGER, " +
                PessoaEntry.COLUMN_IS_FAVORITO      + " BOOLEAN, " +
                PessoaEntry.COLUMN_IS_PESSOA_FISICA + " BOOLEAN " +
                ");";


        final String SQL_CREATE_IMOVEL_TABLE        = "CREATE TABLE " +
                ImovelEntry.TABLE_NAME              + " (" +
                ImovelEntry._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ImovelEntry.COLUMN_NOME             + " VARCHAR(80), " +
                ImovelEntry.COLUMN_AREA             + " INTEGER, " +
                ImovelEntry.COLUMN_TIPO             + " INTEGER, " +
                ImovelEntry.COLUMN_VALOR_ALUGUEL    + " INTEGER, " +
                ImovelEntry.COLUMN_VALOR_IMOVEL     + " INTEGER, " +
                ImovelEntry.COLUMN_IS_FAVORITO      + " BOOLEAN " +
                ");";

        final String SQL_CREATE_TELEFONE_TABLE      = "CREATE TABLE " +
                TelefoneEntry.TABLE_NAME            + " (" +
                TelefoneEntry._ID                   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TelefoneEntry.COLUMN_NUMERO         + " TEXT NOT NULL, " +
                TelefoneEntry.COLUMN_PESSOA_ID      + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + TelefoneEntry.COLUMN_PESSOA_ID + ") REFERENCES " +
                TelefoneEntry.TABLE_NAME + " (" + PessoaEntry._ID + "));";


        final String SQL_CREATE_EMAIL_TABLE     = "CREATE TABLE " +
                EmailEntry.TABLE_NAME           + " (" +
                EmailEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EmailEntry.COLUMN_ENDERECO      + " TEXT NOT NULL, " +
                EmailEntry.COLUMN_PESSOA_ID     + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + EmailEntry.COLUMN_PESSOA_ID + ") REFERENCES " +
                EmailEntry.TABLE_NAME + " (" + PessoaEntry._ID + ")" +");";

        final String SQL_CREATE_LOCACAO_TABLE   = "CREATE TABLE " +
                LocacaoEntry.TABLE_NAME           + " (" +
                LocacaoEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LocacaoEntry.COLUMN_DATA_ATUAL    + " INTEGER, " +
                LocacaoEntry.COLUMN_DATA_INICIO   + " INTEGER, " +
                LocacaoEntry.COLUMN_DATA_FIM      + " INTEGER, " +
                LocacaoEntry.COLUMN_VALOR_ALUGUEL + " INTEGER, " +
                LocacaoEntry.COLUMN_LOCADOR_ID    + " INTEGER, " +
                LocacaoEntry.COLUMN_LOCATARIO_ID  + " INTEGER, " +
                LocacaoEntry.COLUMN_FIADOR_ID     + " INTEGER, " +
                LocacaoEntry.COLUMN_IMOVEL_ID     + " INTEGER, " +

                " FOREIGN KEY (" + LocacaoEntry.COLUMN_LOCADOR_ID + ") REFERENCES " +
                LocacaoEntry.TABLE_NAME + " (" + PessoaEntry._ID + "), " +

                " FOREIGN KEY (" + LocacaoEntry.COLUMN_LOCATARIO_ID + ") REFERENCES " +
                LocacaoEntry.TABLE_NAME + " (" + PessoaEntry._ID + "), " +

                " FOREIGN KEY (" + LocacaoEntry.COLUMN_FIADOR_ID + ") REFERENCES " +
                LocacaoEntry.TABLE_NAME + " (" + PessoaEntry._ID + "), " +

                " FOREIGN KEY (" + LocacaoEntry.COLUMN_IMOVEL_ID + ") REFERENCES " +
                LocacaoEntry.TABLE_NAME + " (" + ImovelEntry._ID + ")" +

                ");";

        final String SQL_CREATE_ENDERECO_TABLE      = "CREATE TABLE " +
                EnderecoEntry.TABLE_NAME            + " (" +
                EnderecoEntry._ID                   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EnderecoEntry.COLUMN_PLACE_ID       + " TEXT, " +
                EnderecoEntry.COLUMN_LOCAL          + " TEXT, " +
                EnderecoEntry.COLUMN_COMPLEMENTO    + " TEXT, " +
                EnderecoEntry.COLUMN_LONGITUDE      + " REAL, " +
                EnderecoEntry.COLUMN_LATITUDE       + " REAL "  +
                ");";

        db.execSQL(SQL_CREATE_PESSOA_TABLE);
        db.execSQL(SQL_CREATE_IMOVEL_TABLE);
        db.execSQL(SQL_CREATE_TELEFONE_TABLE);
        db.execSQL(SQL_CREATE_EMAIL_TABLE);
        db.execSQL(SQL_CREATE_LOCACAO_TABLE);
        db.execSQL(SQL_CREATE_ENDERECO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PessoaEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ImovelEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TelefoneEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EmailEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocacaoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EnderecoEntry.TABLE_NAME);
        onCreate(db);
    }
}
