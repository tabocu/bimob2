package br.com.blackseed.bimob.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.util.Log;

import br.com.blackseed.bimob.data.DbContract.*;
import java.io.FileNotFoundException;

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

    static final int FOTO                   = 600;
    static final int FOTO_ID                = 601;
    static final int FOTO_OF_IMOVEL         = 602;
    static final int FOTO_OF_PESSOA         = 603;

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
        matcher.addURI(authority, DbContract.PATH_TELEFONE + "/" +
                        DbContract.PessoaEntry.TABLE_NAME + "/#",   TELEFONE_OF_PESSOA);

        matcher.addURI(authority, DbContract.PATH_EMAIL,             EMAIL);
        matcher.addURI(authority, DbContract.PATH_EMAIL + "/" +
                        DbContract.PessoaEntry.TABLE_NAME + "/#",      EMAIL_OF_PESSOA);

        matcher.addURI(authority, DbContract.PATH_LOCACAO,           LOCACAO);
        matcher.addURI(authority, DbContract.PATH_LOCACAO + "/#",    LOCACAO_ID);

        matcher.addURI(authority, DbContract.PATH_FOTO,              FOTO);
        matcher.addURI(authority, DbContract.PATH_FOTO + "/#",       FOTO_ID);
        matcher.addURI(authority, DbContract.PATH_FOTO + "/" +
                        DbContract.ImovelEntry.TABLE_NAME + "/#",       FOTO_OF_IMOVEL);
        matcher.addURI(authority, DbContract.PATH_FOTO + "/" +
                        DbContract.PessoaEntry.TABLE_NAME + "/#",       FOTO_OF_PESSOA);

        return matcher;
    }

    private static final String IMOVEL_TABLE =
                                ImovelEntry.TABLE_NAME + " LEFT JOIN " + FotoEntry.TABLE_NAME +
                                " ON " +
                                ImovelEntry.TABLE_NAME + "." + ImovelEntry._ID +
                                " = " +
                                FotoEntry.TABLE_NAME + "." + FotoEntry.COLUMN_IMOVEL_ID;

    private static final String IMOVEL_WHERE = " ( " +
                            FotoEntry.TABLE_NAME + "." + FotoEntry.COLUMN_IMOVEL_ID + " IS null OR " +
                            FotoEntry.TABLE_NAME + "." + FotoEntry.COLUMN_PRIMARY + " IS 1 ) ";

    private static final String PESSOA_TABLE =
            PessoaEntry.TABLE_NAME + " LEFT JOIN " + FotoEntry.TABLE_NAME +
                    " ON " +
                    PessoaEntry.TABLE_NAME + "." + PessoaEntry._ID +
                    " = " +
                    FotoEntry.TABLE_NAME + "." + FotoEntry.COLUMN_PESSOA_ID;

    private static final String PESSOA_WHERE = " ( " +
            FotoEntry.TABLE_NAME + "." + FotoEntry.COLUMN_PESSOA_ID + " IS null OR " +
            FotoEntry.TABLE_NAME + "." + FotoEntry.COLUMN_PRIMARY + " IS 1 ) ";


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

            case FOTO:
                return DbContract.FotoEntry.CONTENT_TYPE;
            case FOTO_ID:
                return DbContract.FotoEntry.CONTENT_ITEM_TYPE;
            case FOTO_OF_IMOVEL:
                return DbContract.FotoEntry.CONTENT_TYPE;
            case FOTO_OF_PESSOA:
                return DbContract.FotoEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.v("DB_PROVIDER","query");

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {

            case PESSOA: {
                SQLiteDatabase db = mOpenHelper.getReadableDatabase();
                Cursor cursor = db.query(
                        PESSOA_TABLE,
                        projection,
                        PESSOA_WHERE,
                        null,
                        null,
                        null,
                        null,
                        null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
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
                        PESSOA_WHERE + " AND " + DbContract.PessoaEntry.COLUMN_NOME + " LIKE ?",
                        new String[] {"%"+ uri.getLastPathSegment()+ "%" },
                        null,
                        null,
                        null,
                        null);
            }

            case IMOVEL: {
                SQLiteDatabase db = mOpenHelper.getReadableDatabase();
                Cursor cursor = db.query(
                        IMOVEL_TABLE,
                        projection,
                        IMOVEL_WHERE,
                        null,
                        null,
                        null,
                        null,
                        null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
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
                        IMOVEL_WHERE + " AND " + DbContract.ImovelEntry.COLUMN_NOME + " LIKE ?",
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

                                LocacaoEntry.TABLE_NAME  + " LEFT JOIN " +
                                ImovelEntry.TABLE_NAME + " ON "  +
                                LocacaoEntry.TABLE_NAME + "." +
                                LocacaoEntry.COLUMN_IMOVEL_ID + " = " +
                                ImovelEntry.TABLE_NAME + "." +
                                ImovelEntry._ID + " LEFT JOIN " +
                                PessoaEntry.TABLE_NAME + " ON " +
                                LocacaoEntry.TABLE_NAME + "." +
                                LocacaoEntry.COLUMN_LOCADOR_ID + " = " +
                                PessoaEntry.TABLE_NAME + "." +
                                PessoaEntry._ID);

                break;
            }
            case LOCACAO_ID: {
                queryBuilder.setTables( DbContract.LocacaoEntry.TABLE_NAME);
                queryBuilder.appendWhere(DbContract.LocacaoEntry._ID + " = " + uri.getLastPathSegment());
                break;
            }

            case FOTO: {
                queryBuilder.setTables(DbContract.FotoEntry.TABLE_NAME);
                break;
            }

            case FOTO_ID: {
                queryBuilder.setTables( DbContract.FotoEntry.TABLE_NAME);
                queryBuilder.appendWhere(DbContract.FotoEntry._ID + " = " + uri.getLastPathSegment());
                break;
            }

            case FOTO_OF_IMOVEL: {
                queryBuilder.setTables(DbContract.FotoEntry.TABLE_NAME);
                queryBuilder.appendWhere(DbContract.FotoEntry.COLUMN_IMOVEL_ID +
                        " = " + uri.getLastPathSegment());
                break;
            }

            case FOTO_OF_PESSOA: {
                queryBuilder.setTables(DbContract.FotoEntry.TABLE_NAME);
                queryBuilder.appendWhere(DbContract.FotoEntry.COLUMN_PESSOA_ID +
                        " = " + uri.getLastPathSegment());
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor = queryBuilder.query(mOpenHelper.getReadableDatabase(),
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
            case FOTO: {
                long _id = db.insert(DbContract.FotoEntry.TABLE_NAME, null, values);
                returnUri = DbContract.FotoEntry.buildFotoUri(_id);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.v("DB_PROVIDER","insert");
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
