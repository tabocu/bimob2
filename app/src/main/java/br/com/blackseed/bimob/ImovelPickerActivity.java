package br.com.blackseed.bimob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import br.com.blackseed.bimob.data.DbContract;

public class ImovelPickerActivity extends AppCompatActivity implements
        ImoveisFragment.OnImovelClickListener{

    public static final String IMOVEL_URI = "imovel_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imovel_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new ImoveisFragment())
                    .commit();
        }
    }

    @Override
    public void onImovelClicked(long id) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IMOVEL_URI, DbContract.ImovelEntry.buildImovelUri(id));
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }
}
