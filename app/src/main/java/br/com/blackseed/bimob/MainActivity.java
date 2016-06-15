package br.com.blackseed.bimob;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import br.com.blackseed.bimob.data.DbContract;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener,
        PessoasFragment.OnPessoaClickListener,
        ImoveisFragment.OnImovelClickListener,
        LocacaoFragment.OnLocacaoClickListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private FrameLayout mFrameLayout;

    private FloatingActionsMenu mFabMenu;
    private View mPessoaFisicaFab;
    private View mPessoaJuridicaFab;
    private View mImovelFab;
    private View mLocacaoFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        mFabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        mPessoaFisicaFab = findViewById(R.id.fab_pessoa_fisica);
        mPessoaJuridicaFab = findViewById(R.id.fab_pessoa_juridica);
        mImovelFab = findViewById(R.id.fab_imoveis);
        mLocacaoFab = findViewById(R.id.fab_locacoes);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mFrameLayout.getBackground().setAlpha(0);
        mFabMenu.setOnFloatingActionsMenuUpdateListener(this);
        mPessoaFisicaFab.setOnClickListener(this);
        mPessoaJuridicaFab.setOnClickListener(this);
        mImovelFab.setOnClickListener(this);
        mLocacaoFab.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        mFabMenu.collapse();
        if(v == mPessoaFisicaFab) {
            Intent intent = new Intent(getBaseContext(), AddPessoaFisicaActivity.class);
            startActivity(intent);
        } else if(v == mPessoaJuridicaFab) {
            Intent intent = new Intent(getBaseContext(), AddPessoaJuridicaActivity.class);
            startActivity(intent);
        } else if(v == mImovelFab) {
            Intent intent = new Intent(getBaseContext(), AddImovelActivity.class);
            startActivity(intent);
        } else if(v == mLocacaoFab) {
            Intent intent = new Intent(getBaseContext(), AddLocacaoActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onMenuExpanded() {
        mFrameLayout.getBackground().setAlpha(240);
        mFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFabMenu.collapse();
                return true;
            }
        });
    }

    @Override
    public void onMenuCollapsed() {
        mFrameLayout.getBackground().setAlpha(0);
        mFrameLayout.setOnTouchListener(null);
    }

    @Override
    public void onPessoaClicked(ListView l, View v, int position, long id) {
        Bundle args = new Bundle();
        Intent intent;
        Uri pessoaUri = DbContract.PessoaEntry.buildPessoaUri(id);
        Cursor c = getContentResolver().query(
                pessoaUri,
                new String[]{DbContract.PessoaEntry.COLUMN_IS_PESSOA_FISICA},
                null,
                null,
                null);
        c.moveToFirst();
        if(c.getInt(0) == 1) {
            args.putParcelable(AddPessoaFisicaActivity.PESSOA_FISICA_URI, DbContract.PessoaEntry.buildPessoaUri(id));
            args.putParcelable(AddPessoaFisicaActivity.TELEFONES_URI, DbContract.TelefoneEntry.buildTelefoneOfPessoaUri(id));
            args.putParcelable(AddPessoaFisicaActivity.EMAILS_URI, DbContract.EmailEntry.buildEmailOfPessoaUri(id));
            intent = new Intent(this, AddPessoaFisicaActivity.class);
        } else {
            args.putParcelable(AddPessoaJuridicaActivity.PESSOA_JURIDICA_URI, DbContract.PessoaEntry.buildPessoaUri(id));
            args.putParcelable(AddPessoaJuridicaActivity.TELEFONES_URI, DbContract.TelefoneEntry.buildTelefoneOfPessoaUri(id));
            args.putParcelable(AddPessoaJuridicaActivity.EMAILS_URI, DbContract.EmailEntry.buildEmailOfPessoaUri(id));
            intent = new Intent(this, AddPessoaJuridicaActivity.class);
        }

        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onImovelClicked(ListView l, View v, int position, long id) {
        Bundle args = new Bundle();
        Intent intent = new Intent(this, AddImovelActivity.class);
        args.putParcelable(AddImovelActivity.IMOVEL_URI, DbContract.ImovelEntry.buildImovelUri(id));
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onLocacaoClicked(ListView l, View v, int position, long id) {
        Bundle args = new Bundle();
        Intent intent = new Intent(this, AddLocacaoActivity.class);
        args.putParcelable(AddLocacaoActivity.LOCACAO_URI, DbContract.LocacaoEntry.buildLocacaoUri(id));
        intent.putExtras(args);
        startActivity(intent);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PessoasFragment();
                case 1:
                    return new ImoveisFragment();
                case 2:
                    return new LocacaoFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.action_tab_pessoa);
                case 1:
                    return getString(R.string.action_tab_imovel);
                case 2:
                    return getString(R.string.action_tab_locação);
            }
            return null;
        }
    }
}
