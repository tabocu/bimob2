package br.com.blackseed.bimob;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import br.com.blackseed.bimob.data.DbContract;

public class PessoaPickerActivity extends AppCompatActivity implements
        PessoasFragment.OnPessoaClickListener{

    public static final String PESSOA_URI = "pessoa_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pessoa_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new PessoasFragment())
                    .commit();
        }
    }

    @Override
    public void onPessoaClicked(long id) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PESSOA_URI, DbContract.PessoaEntry.buildPessoaUri(id));
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }
}
