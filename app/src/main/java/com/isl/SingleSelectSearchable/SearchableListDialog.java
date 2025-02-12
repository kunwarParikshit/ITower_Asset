package com.isl.SingleSelectSearchable;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.isl.util.Utils;

import java.io.Serializable;
import java.util.List;

import infozech.itower.R;

public class SearchableListDialog extends DialogFragment implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    Dialog filterDialog;
    private static final String ITEMS = "items";
    private ArrayAdapter listAdapter;
    private ListView _listViewItems;
    private SearchableItem _searchableItem;
    private OnSearchTextChanged _onSearchTextChanged;
    private SearchView _searchView;
    private String _strTitle;
    private String _strPositiveButtonText;
    private DialogInterface.OnClickListener _onClickListener;
    public SearchableListDialog() {

    }

    public static SearchableListDialog newInstance(List items) {
        SearchableListDialog multiSelectExpandableFragment = new SearchableListDialog();

        Bundle args = new Bundle();
        args.putSerializable(ITEMS, (Serializable) items);

        multiSelectExpandableFragment.setArguments(args);

        return multiSelectExpandableFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            _searchableItem = (SearchableItem) savedInstanceState.getSerializable("item");
        }
        filterDialog = new Dialog(getActivity());
        filterDialog.setCanceledOnTouchOutside(false);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View rootView = inflater.inflate( R.layout.searchable_list_dialog, null);
        setData(rootView);
        filterDialog.setContentView(rootView);
        final Window window_SignIn = filterDialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.setGravity( Gravity.CENTER);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Drawable d = new ColorDrawable( Color.parseColor("#CC000000"));
        filterDialog.getWindow().setBackgroundDrawable(d);
        /*filterDialog.getWindow().setBackgroundDrawableResource( R.color.nevermind_bg_color);
        filterDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        filterDialog.getWindow().setGravity( Gravity.CENTER);
        filterDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);*/
        filterDialog.show();
        return filterDialog;
    }

    // Crash on orientation change #7
    // Change Start
    // Description: Saving the instance of searchable item instance.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("item", _searchableItem);
        super.onSaveInstanceState(outState);
    }
    // Change End

    public void setTitle(String strTitle) {
        _strTitle = strTitle;
    }

    public void setPositiveButton(String strPositiveButtonText) {
        _strPositiveButtonText = strPositiveButtonText;
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        _strPositiveButtonText = strPositiveButtonText;
        _onClickListener = onClickListener;
    }

    public void setOnSearchableItemClickListener(SearchableItem searchableItem) {
        this._searchableItem = searchableItem;
    }

    public void setOnSearchTextChangedListener(OnSearchTextChanged onSearchTextChanged) {
        this._onSearchTextChanged = onSearchTextChanged;
    }

    private void setData(View rootView) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        _searchView = (SearchView) rootView.findViewById( R.id.search);
        _searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        _searchView.setOnQueryTextListener(this);
        _searchView.setOnCloseListener(this);
        _searchView.setIconified(false);
        _searchView.setFocusable(true);
        _searchView.setIconifiedByDefault(false);
        _searchView.clearAnimation();
        _searchView.clearFocus();

        TextView tv_main_header = (TextView) rootView.findViewById( R.id.tv_main_header);
        tv_main_header.setTypeface(Utils.typeFace(getActivity()));
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(_searchView.getWindowToken(), 0);
        //View footer = LayoutInflater.from(getActivity()).inflate(R.layout.list_footer, null);
        List items = (List) getArguments().getSerializable(ITEMS);
        Button _close = (Button) rootView.findViewById( R.id.btn_close);
        _listViewItems = (ListView) rootView.findViewById( R.id.listItems);
        //_listViewItems.addFooterView(footer);

        //create the adapter by passing your ArrayList data
        listAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_search_text,items){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(Utils.typeFace(getActivity()));
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(Utils.typeFace(getActivity()));
                v.setPadding(10,15,10,15);
                return v;
            }
        };
        listAdapter.setDropDownViewResource( R.layout.spinner_dropdown);
        _listViewItems.setAdapter(listAdapter);
        _listViewItems.setTextFilterEnabled(true);
        _listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _searchableItem.onSearchableItemClicked(listAdapter.getItem(position), position);
                getDialog().dismiss();
            }
        });

        _close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        _searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        if (TextUtils.isEmpty(s)) {
            ((ArrayAdapter) _listViewItems.getAdapter()).getFilter().filter(null);
        } else {
            ((ArrayAdapter)  _listViewItems.getAdapter()).getFilter().filter(s);
            //((ArrayAdapter)((HeaderViewListAdapter)_listViewItems.getAdapter()).getFilter().filter(s);
            //((ArrayAdapter)((HeaderViewListAdapter)_listViewItems.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
        }
        if (null != _onSearchTextChanged) {
            _onSearchTextChanged.onSearchTextChanged(s);
        }
        return true;
    }

    public interface SearchableItem<T> extends Serializable {
        void onSearchableItemClicked(T item, int position);
    }

    public interface OnSearchTextChanged {
        void onSearchTextChanged(String strText);
    }
}
