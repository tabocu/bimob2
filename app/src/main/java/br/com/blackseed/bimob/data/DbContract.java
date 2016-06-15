package br.com.blackseed.bimob.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DbContract {

    public static final String CONTENT_AUTHORITY = "br.com.blackseed.bimob";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PESSOA= "pessoa";
    public static final String PATH_FILTER_PESSOA= "pessoa_filter";
    public static final String PATH_IMOVEL= "imovel";
    public static final String PATH_FILTER_IMOVEL= "imovel_filter";
    public static final String PATH_TELEFONE = "telefone";
    public static final String PATH_EMAIL = "email";
    public static final String PATH_LOCACAO = "locacao";
    public static final String PATH_ENDERECO = "endereco";

    public static final class PessoaEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PESSOA).build();

        public static final Uri CONTENT_FILTER_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILTER_PESSOA).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PESSOA;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PESSOA;

        public static final String TABLE_NAME = "pessoa";

        public static final String COLUMN_NOME = "pessoa_nome";
        public static final String COLUMN_RAZAO_SOCIAL = "pessoa_razao_social";
        public static final String COLUMN_CP = "pessoa_cp";
        public static final String COLUMN_IS_PESSOA_FISICA = "pessoa_tipo";
        public static final String COLUMN_IS_FAVORITO = "pessoa_favorito";
        public static final String COLUMN_ENDERECO_ID = "pessoa_endereco_id";

        public static Uri buildPessoaUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ImovelEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMOVEL).build();

        public static final Uri CONTENT_FILTER_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILTER_IMOVEL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMOVEL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMOVEL;

        public static final String TABLE_NAME = "imovel";

        public static final String COLUMN_NOME = "imovel_nome";
        public static final String COLUMN_VALOR_ALUGUEL = "imovel_valor_aluguel";
        public static final String COLUMN_AREA = "imovel_area";
        public static final String COLUMN_TIPO = "imovel_tipo";

        public static final String COLUMN_IS_FAVORITO = "imovel_favorito";
        public static final String COLUMN_ENDERECO_ID = "imovel_endereco_id";

        public static Uri buildImovelUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TelefoneEntry implements BaseColumns{

        public static final  Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TELEFONE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TELEFONE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TELEFONE;

        //Table name
        public static final String TABLE_NAME = "telefone";
        //Columns
        public static final String COLUMN_NUMERO = "telefone_numero";
        public static final String COLUMN_PESSOA_ID = "telefone_pessoa_id";   //Foreign key

        public static Uri buildTelefoneUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildTelefoneOfPessoaUri(long pessoaId){
            return ContentUris.withAppendedId(CONTENT_URI.buildUpon().appendPath(PessoaEntry.TABLE_NAME).build(),pessoaId);
        }
    }

    public static final class EmailEntry implements BaseColumns{

        public static final  Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EMAIL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EMAIL;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EMAIL;

        //Table name
        public static final String TABLE_NAME = "email";
        //Columns
        public static final String COLUMN_ENDERECO = "email_endereco";
        public static final String COLUMN_PESSOA_ID = "email_pessoa_id";   //Foreign key

        public static Uri buildEmailUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildEmailOfPessoaUri(long pessoaId){
            return ContentUris.withAppendedId(CONTENT_URI.buildUpon().appendPath(PessoaEntry.TABLE_NAME).build(),pessoaId);
        }
    }

    public static final class LocacaoEntry implements BaseColumns{

        public static final  Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCACAO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCACAO;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCACAO;

        //Table name
        public static final String TABLE_NAME = "locacao";

        public static final String COLUMN_LOCATARIO_ID  = "locatario_id";   //Foreign key
        public static final String COLUMN_LOCADOR_ID    = "locador_id";   //Foreign key
        public static final String COLUMN_FIADOR_ID     = "fiador_id";   //Foreign key
        public static final String COLUMN_IMOVEL_ID     = "locacao_id";   //Foreign key
        public static final String COLUMN_DATA_ATUAL    = "data_atual";
        public static final String COLUMN_DATA_INICIO   = "data_incio";
        public static final String COLUMN_DATA_FIM      = "data_fim";
        public static final String COLUMN_VALOR_ALUGUEL = "valor_aluguel";

        public static Uri buildLocacaoUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }

    public static final class EnderecoEntry implements BaseColumns{

        public static final  Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENDERECO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENDERECO;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ENDERECO;

        //Table name
        public static final String TABLE_NAME = "endereco";

        public static final String COLUMN_PLACE_ID = "endereco_place_id";
        public static final String COLUMN_LOCAL = "endereco_local";
        public static final String COLUMN_COMPLEMENTO = "endereco_complemento";
        public static final String COLUMN_LATITUDE = "endereco_latitude";
        public static final String COLUMN_LONGITUDE = "endereco_longitude";

        public static Uri buildEnderecoUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }



}
