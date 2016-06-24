package br.com.blackseed.bimob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import br.com.blackseed.bimob.adapter.ImovelAdapterX;
import br.com.blackseed.bimob.adapter.PessoaAdapter;
import br.com.blackseed.bimob.adapter.PessoaAdapterX;
import br.com.blackseed.bimob.data.DbContract.*;

import br.com.blackseed.bimob.data.DbContract;
import br.com.blackseed.bimob.data.DbProvider;


public class PessoasFragment extends Fragment implements
        PessoaAdapterX.OnPessoaClickListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static String[] PESSOA_COLUMNS = {
            PessoaEntry.TABLE_NAME + "." + PessoaEntry._ID,
            PessoaEntry.TABLE_NAME + "." + PessoaEntry.COLUMN_NOME,
            PessoaEntry.TABLE_NAME + "." + PessoaEntry.COLUMN_RAZAO_SOCIAL,
            FotoEntry.TABLE_NAME   + "." + FotoEntry.COLUMN_THUMB
    };

    RecyclerView pessoaRecyclerView;

    RecyclerView.LayoutManager layoutManager;
    PessoaAdapterX pessoaAdapter;

    OnPessoaClickListener mPessoaClickListener;

    SearchView mSearchView;
    String mCurFilter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pessoa, container, false);

        pessoaRecyclerView = (RecyclerView) v.findViewById(R.id.pessoaRecyclerView);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pessoaRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        pessoaRecyclerView.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);

        pessoaAdapter = new PessoaAdapterX(getContext(),null,this);
        pessoaRecyclerView.setAdapter(pessoaAdapter);


        getLoaderManager().initLoader(0, null, this);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mPessoaClickListener = (OnPessoaClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnPessoaClickListener");
        }
    }

    @Override
    public void pessoaClick(long id) { mPessoaClickListener.onPessoaClicked(id); }

    public static class MySearchView extends SearchView {

        public MySearchView(Context context) {
            super(context);
        }

        @Override
        public void onActionViewCollapsed() {
            setQuery("", false);
            super.onActionViewCollapsed();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuItem item = menu.add("Search");
        item.setIcon(R.drawable.bm_search_light_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView = new MySearchView(getActivity());
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconifiedByDefault(true);
        item.setActionView(mSearchView);
    }

    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onClose() {
        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            mSearchView.setQuery(null, true);
        }
        return true;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(PessoaEntry.CONTENT_FILTER_URI,
                    Uri.encode(mCurFilter));
        } else {
        baseUri = DbContract.PessoaEntry.CONTENT_URI;
        }

        return new CursorLoader(
                getActivity(),
                baseUri,
                PESSOA_COLUMNS,
                null,
                null,
                PessoaEntry.COLUMN_NOME + " ASC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pessoaAdapter.swapCursor(data);

    }

    public void onLoaderReset(Loader<Cursor> loader) {
        pessoaAdapter.swapCursor(null);
    }

    public interface OnPessoaClickListener {
        void onPessoaClicked(long id);
    }

}
