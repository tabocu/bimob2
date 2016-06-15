package br.com.blackseed.bimob.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class DbProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int PESSOA                 = 100;
    static final int PESSOA_ID              = 101;
    static final int PESSOA_WITH_ADRESS     = 102;
    static final int PESSOA_ID_WITH_ADRESS  = 103;
    static final int PESSOA_FILTER          = 110;


    static final int IMOVEL                 = 200;
    static final int IMOVEL_ID              = 201;
    static final int IMOVEL_WITH_ADRESS     = 202;
    static final int IMOVEL_ID_WITH_ADRESS  = 203;
    static final int IMOVEL_FILTER          = 210;

    static final int TELEFONE               = 300;
    static final int TELEFONE_OF_PESSOA     = 301;

    static final int EMAIL                  = 400;
    static final int EMAIL_OF_PESSOA        = 401;

    static final int LOCACAO                = 500;
    static final int LOCACAO_ID             = 501;




    private static final SQLiteQueryBuilder sLocacaoQueryBuilder;

    static{
        sLocacaoQueryBuilder = new SQLiteQueryBuilder();

        sLocacaoQueryBuilder.setTables(
                        DbContract.PessoaEntry.TABLE_NAME + " , " +
                        DbContract.LocacaoEntry.TABLE_NAME + " , " +
                        DbContract.ImovelEntry.TABLE_NAME +
                        " ON " + DbContract.LocacaoEntry.TABLE_NAME +
                        "." + DbContract.LocacaoEntry.COLUMN_IMOVEL_ID +
                        " = " + DbContract.ImovelEntry.TABLE_NAME +
                        "." + DbContract.ImovelEntry._ID +
                        " AND " + DbContract.LocacaoEntry.TABLE_NAME +
                                "." + DbContract.LocacaoEntry.COLUMN_IMOVEL_ID +
                                " = " + DbContract.ImovelEntry.TABLE_NAME +
                                "." + DbContract.ImovelEntry._ID);
    }



    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DbContract.PATH_PESSOA,            PESSOA);
        matcher.addURI(authority, DbContract.PATH_PESSOA + "/#",     PESSOA_ID);
        matcher.addURI(authority, DbContract.PATH_PESSOA + "/*",     PESSOA_WITH_ADRESS);
        matcher.addURI(authority, DbContract.PATH_PESSOA + "/*/#",   PESSOA_ID_WITH_ADRESS);

        matcher.addURI(authority, DbContract.PATH_FILTER_PESSOA + "/*",     PESSOA_FILTER);

        matcher.addURI(authority, DbContract.PATH_IMOVEL,            IMOVEL);
        matcher.addURI(authority, DbContract.PATH_IMOVEL + "/#",     IMOVEL_ID);
        matcher.addURI(authority, DbContract.PATH_IMOVEL + "/*",     IMOVEL_WITH_ADRESS);
        matcher.addURI(authority, DbContract.PATH_IMOVEL + "/*/#",   IMOVEL_ID_WITH_ADRESS);

        matcher.addURI(authority, DbContract.PATH_FILTER_IMOVEL + "/*",     IMOVEL_FILTER);

        matcher.addURI(authority, DbContract.PATH_TELEFONE,          TELEFONE);
        matcher.addURI(authority, DbContract.PATH_TELEFONE + "/*/#", TELEFONE_OF_PESSOA);

        matcher.addURI(authority, DbContract.PATH_EMAIL,             EMAIL);
        matcher.addURI(authority, DbContract.PATH_EMAIL + "/*/#",    EMAIL_OF_PESSOA);

        matcher.addURI(authority, DbContract.PATH_LOCACAO,           LOCACAO);
        matcher.addURI(authority, DbContract.PATH_LOCACAO + "/#",  LOCACAO_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        Log.v("DB_PROVIDER getType(): ",String.valueOf(match));

        switch (match) {
            case PESSOA:
                return DbContract.PessoaEntry.CONTENT_TYPE;
            case PESSOA_ID:
                return DbContract.PessoaEntry.CONTENT_ITEM_TYPE;
            case PESSOA_WITH_ADRESS:
                return DbContract.PessoaEntry.CONTENT_TYPE;
            case PESSOA_ID_WITH_ADRESS:
                return DbContract.PessoaEntry.CONTENT_ITEM_TYPE;

            case PESSOA_FILTER:
                return DbContract.PessoaEntry.CONTENT_TYPE;

            case IMOVEL:
                return DbContract.ImovelEntry.CONTENT_TYPE;
            case IMOVEL_ID:
                return DbContract.ImovelEntry.CONTENT_ITEM_TYPE;
            case IMOVEL_WITH_ADRESS:
                return DbContract.ImovelEntry.CONTENT_TYPE;
            case IMOVEL_ID_WITH_ADRESS:
                return DbContract.ImovelEntry.CONTENT_ITEM_TYPE;

            case TELEFONE:
                return DbContract.TelefoneEntry.CONTENT_TYPE;
            case TELEFONE_OF_PESSOA:
                return DbContract.TelefoneEntry.CONTENT_TYPE;

            case EMAIL:
                return DbContract.EmailEntry.CONTENT_TYPE;
            case EMAIL_OF_PESSOA:
                return DbContract.EmailEntry.CONTENT_TYPE;

            case LOCACAO:
                return DbContract.LocacaoEntry.CONTENT_TYPE;
            case LOCACAO_ID:
                return DbContract.LocacaoEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.v("DB_PROVIDER","query");

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {

            case PESSOA: {
                queryBuilder.setTables(DbContract.PessoaEntry.TABLE_NAME);
                break;
            }
            case PESSOA_ID: {
                queryBuilder.setTables(DbContract.PessoaEntry.TABLE_NAME);
                queryBuilder.appendWhere(DbContract.PessoaEntry._ID + " = " + uri.getLastPathSegment());
                break;
            }
            case PESSOA_FILTER: {
                SQLiteDatabase db = mOpenHelper.getReadableDatabase();
                return db.query(true,
                        DbContract.PessoaEntry.TABLE_NAME,
                        null,
                        DbContract.PessoaEntry.COLUMN_NOME + " LIKE ? OR " +
                        DbContract.PessoaEntry.COLUMN_RAZAO_SOCIAL + " LIKE ? ",
                        new String[] {
                                "%"+ uri.getLastPathSegment()+ "%" ,
                                "%"+ uri.getLastPathSegment()+ "%" },
                        null,
                        null,
                        null,
                        null);
            }

            case IMOVEL: {
                queryBuilder.setTables(DbContract.ImovelEntry.TABLE_NAME);
                break;
            }
            case IMOVEL_ID: {
                queryBuilder.setTables(DbContract.ImovelEntry.TABLE_NAME);
                queryBuilder.appendWhere(DbContract.ImovelEntry._ID + " = " + uri.getLastPathSegment());
                break;
            }

            case IMOVEL_FILTER: {
                SQLiteDatabase db = mOpenHelper.getReadableDatabase();
                return db.query(true,
                        DbContract.ImovelEntry.TABLE_NAME,
                        null,
                        DbContract.ImovelEntry.COLUMN_NOME + " LIKE ?",
                        new String[] {"%"+ uri.getLastPathSegment()+ "%" },
                        null,
                        null,
                        null,
                        null);
            }

            case TELEFONE: {
                queryBuilder.setTables(DbContract.TelefoneEntry.TABLE_NAME);
                break;
            }
            case TELEFONE_OF_PESSOA: {
                queryBuilder.setTables(DbContract.TelefoneEntry.TABLE_NAME);
                queryBuilder.appendWhere(DbContract.TelefoneEntry.COLUMN_PESSOA_ID +
                        " = " + uri.getLastPathSegment());
                break;
            }

            case EMAIL: {
                queryBuilder.setTables(DbContract.EmailEntry.TABLE_NAME);
                break;
            }
            case EMAIL_OF_PESSOA: {
                queryBuilder.setTables(DbContract.EmailEntry.TABLE_NAME);
                queryBuilder.appendWhere(DbContract.EmailEntry.COLUMN_PESSOA_ID +
                        " = " + uri.getLastPathSegment());
                break;
            }

            case LOCACAO: {

                queryBuilder.setTables(

                        DbContract.LocacaoEntry.TABLE_NAME  + " LEFT JOIN " +
                                DbContract.ImovelEntry.TABLE_NAME + " ON "  +
                                DbContract.LocacaoEntry.TABLE_NAME + "." +
                                DbContract.LocacaoEntry.COLUMN_IMOVEL_ID + " = " +
                                DbContract.ImovelEntry.TABLE_NAME + "." +
                                DbContract.ImovelEntry._ID + " LEFT JOIN " +
                                DbContract.PessoaEntry.TABLE_NAME + " ON " +
                                DbContract.LocacaoEntry.TABLE_NAME + "." +
                                DbContract.LocacaoEntry.COLUMN_LOCADOR_ID + " = " +
                                DbContract.PessoaEntry.TABLE_NAME + "." +
                                DbContract.PessoaEntry._ID);

                break;
            }
            case LOCACAO_ID: {
                queryBuilder.setTables( DbContract.LocacaoEntry.TABLE_NAME);
                queryBuilder.appendWhere(DbContract.LocacaoEntry._ID + " = " + uri.getLastPathSegment());
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Cursor retCursor = queryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v("DB_PROVIDER","insert");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PESSOA: {
                long _id = db.insert(DbContract.PessoaEntry.TABLE_NAME, null, values);
                returnUri = DbContract.PessoaEntry.buildPessoaUri(_id);
                break;
            }
            case IMOVEL: {
                long _id = db.insert(DbContract.ImovelEntry.TABLE_NAME, null, values);
                returnUri = DbContract.PessoaEntry.buildPessoaUri(_id);
                break;
            }
            case TELEFONE: {
                long _id = db.insert(DbContract.TelefoneEntry.TABLE_NAME, null, values);
                returnUri = DbContract.TelefoneEntry.buildTelefoneUri(_id);
                break;
            }
            case EMAIL: {
                long _id = db.insert(DbContract.EmailEntry.TABLE_NAME, null, values);
                returnUri = DbContract.EmailEntry.buildEmailUri(_id);
                break;
            }
            case LOCACAO: {
                long _id = db.insert(DbContract.LocacaoEntry.TABLE_NAME, null, values);
                returnUri = DbContract.LocacaoEntry.buildLocacaoUri(_id);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v("DB_PROVIDER","delete");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case PESSOA:
                rowsDeleted = db.delete(
                        DbContract.PessoaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case IMOVEL:
                rowsDeleted = db.delete(
                        DbContract.ImovelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TELEFONE:
                rowsDeleted = db.delete(
                        DbContract.TelefoneEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TELEFONE_OF_PESSOA:
                rowsDeleted = db.delete(
                        DbContract.TelefoneEntry.TABLE_NAME,
                        DbContract.TelefoneEntry.COLUMN_PESSOA_ID +
                                " = " + uri.getLastPathSegment(),
                        null);
                break;
            case EMAIL:
                rowsDeleted = db.delete(
                        DbContract.EmailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EMAIL_OF_PESSOA:
                rowsDeleted = db.delete(
                        DbContract.EmailEntry.TABLE_NAME,
                        DbContract.EmailEntry.COLUMN_PESSOA_ID +
                                " = " + uri.getLastPathSegment(),
                        null);
                break;
            case LOCACAO:
                rowsDeleted = db.delete(
                        DbContract.LocacaoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v("DB_PROVIDER","update");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PESSOA:
                rowsUpdated = db.update(DbContract.PessoaEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PESSOA_ID:
                rowsUpdated = db.update(DbContract.PessoaEntry.TABLE_NAME,
                        values,
                        DbContract.PessoaEntry._ID + " = " + uri.getLastPathSegment(),
                        null);
                break;
            case IMOVEL:
                rowsUpdated = db.update(DbContract.ImovelEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case IMOVEL_ID:
                rowsUpdated = db.update(DbContract.ImovelEntry.TABLE_NAME,
                        values,
                        DbContract.ImovelEntry._ID + " = " + uri.getLastPathSegment(),
                        null);
                break;
            case TELEFONE:
                rowsUpdated = db.update(DbContract.TelefoneEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case EMAIL:
                rowsUpdated = db.update(DbContract.EmailEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case LOCACAO:
                rowsUpdated = db.update(DbContract.LocacaoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCACAO_ID:
                rowsUpdated = db.update(DbContract.LocacaoEntry.TABLE_NAME,
                        values,
                        DbContract.LocacaoEntry._ID + " = " + uri.getLastPathSegment(),
                        null);
                break;
            default:

                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
