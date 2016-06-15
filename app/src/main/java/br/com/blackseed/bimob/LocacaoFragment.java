package br.com.blackseed.bimob;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import br.com.blackseed.bimob.adapter.LocacaoAdapter;
import br.com.blackseed.bimob.data.DbContract.LocacaoEntry;

public class LocacaoFragment extends ListFragment
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    OnLocacaoClickListener mLocacaoClickListener;

    LocacaoAdapter mAdapter;
    SearchView mSearchView;
    String mCurFilter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("Não há locações cadastradas");

        setHasOptionsMenu(true);

//        mAdapter = new SimpleCursorAdapter(getActivity(),
//                android.R.layout.simple_list_item_1, null,
//                new String[] { LocacaoEntry.COLUMN_DATA_INICIO},
//                new int[] { android.R.id.text1 }, 0);

        mAdapter = new LocacaoAdapter(getContext(),null);
        setListAdapter(mAdapter);

        setListShown(false);

        getLoaderManager().initLoader(0, null, this);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLocacaoClickListener = (OnLocacaoClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLocacaoClickListener");
        }
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mLocacaoClickListener.onLocacaoClicked(l,v,position,id);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
//        if (mCurFilter != null) {
//            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
//                    Uri.encode(mCurFilter));
//        } else {
        baseUri = LocacaoEntry.CONTENT_URI;
//        }
//
        return new CursorLoader(getActivity(), baseUri,
                null,null,null,null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public interface OnLocacaoClickListener {
        void onLocacaoClicked(ListView l, View v, int position, long id);
    }
}
