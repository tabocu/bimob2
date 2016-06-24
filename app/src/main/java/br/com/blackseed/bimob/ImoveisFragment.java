package br.com.blackseed.bimob;

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
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import br.com.blackseed.bimob.adapter.ImovelAdapter;
import br.com.blackseed.bimob.adapter.ImovelAdapterX;
import br.com.blackseed.bimob.data.DbContract;
import br.com.blackseed.bimob.data.DbContract.*;

public class ImoveisFragment extends Fragment implements
        ImovelAdapterX.OnImovelClickListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] IMOVEL_COLUMNS = {
            ImovelEntry.TABLE_NAME + "." + ImovelEntry._ID,
            ImovelEntry.TABLE_NAME + "." + ImovelEntry.COLUMN_NOME,
            ImovelEntry.TABLE_NAME + "." + ImovelEntry.COLUMN_AREA,
            ImovelEntry.TABLE_NAME + "." + ImovelEntry.COLUMN_TIPO,
            FotoEntry.TABLE_NAME   + "." + FotoEntry.COLUMN_THUMB
    };

    RecyclerView imovelRecyclerView;

    RecyclerView.LayoutManager layoutManager;
    ImovelAdapterX imovelAdapter;

    OnImovelClickListener mImovelClickListener;

    SearchView mSearchView;
    String mCurFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_imovel, container, false);

        imovelRecyclerView = (RecyclerView) v.findViewById(R.id.imovelRecyclerView);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imovelRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        imovelRecyclerView.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);

        imovelAdapter = new ImovelAdapterX(getContext(),null,this);
        imovelRecyclerView.setAdapter(imovelAdapter);


        getLoaderManager().initLoader(0, null, this);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mImovelClickListener = (OnImovelClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnImovelClickListener");
        }
    }

    @Override
    public void imovelClick(long id) {
        mImovelClickListener.onImovelClicked(id);
    }

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
        Log.v("IMOVEIS: ","onCreateLoader()");

        Uri baseUri;
        if (mCurFilter != null) {
           baseUri = Uri.withAppendedPath(ImovelEntry.CONTENT_FILTER_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = ImovelEntry.CONTENT_URI;
        }
        return new CursorLoader(
                getActivity(),
                baseUri,
                IMOVEL_COLUMNS,
                null,
                null,
                null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        imovelAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        imovelAdapter.swapCursor(null);
    }

    public interface OnImovelClickListener {
        void onImovelClicked(long id);
    }
}
