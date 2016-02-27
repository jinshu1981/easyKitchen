package com.example.xuzhi.easykitchen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyMenuListsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static public MenuListAdapter mMenuListAdapter;
    static final int MY_MENU_LISTS_LOADER_MENU = -1;
    //static final int MY_RECIPE_LISTS_OF_MENU_LOADER = 1;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private View mRootView;
    TextView mHintText;
    static Context mContext;
    static Cursor mCursor;
    static ExpandableListView mEListview;
    static MyMenuListsActivityFragment mThis;
    public MyMenuListsActivityFragment() {
        mThis = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_menu_lists, container, false);
        mMenuListAdapter = new MenuListAdapter(null,getActivity(),false,this);
        mContext = getActivity();
        mHintText = (TextView) mRootView.findViewById(R.id.hintText);
        ExpandableListView eListView = (ExpandableListView) mRootView.findViewById(R.id.my_menu_lists);
        mEListview = eListView;
        eListView.setAdapter(mMenuListAdapter);
        Utility.setListViewHeightBasedOnChildren(eListView);

        /*e ListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Cursor cursor = (Cursor) parent.getExpandableListAdapter().getGroup(groupPosition);
                if (cursor != null) {
                    mCursor = cursor;
                    new LongClickDialogFragment().show(getFragmentManager(), "longclick");
                }
                return true;
            }
        });*/
        eListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Cursor cursor = (Cursor) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                if (cursor != null) {
                    //int idIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_ID);
                    //int id = cursor.getInt(idIndex);
                    Log.v(LOG_TAG, "recipe id = " + id);
                    Intent intent = new Intent(getActivity(), RecipeActivity.class).setData(EasyKitchenContract.Recipe.buildRecipeUriById((int) (id)));
                    startActivity(intent);
                }
                return false;
            }
        });
        eListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                long packedPosition = mEListview.getExpandableListPosition(position);

                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);


                /*  if group item clicked */
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP)

                {
                    //  ...
                    Cursor cursor = (Cursor) (mEListview.getExpandableListAdapter()).getGroup(groupPosition);
                    if (cursor != null) {
                        mCursor = cursor;
                        new LongClickDialogFragment().show(getFragmentManager(), "grouplongclick");
                    }
                    return true;
                }

                /*  if child item clicked */
                else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD)

                {
                    //  ...
                    //onChildLongClick(groupPosition, childPosition);
            }

            return false;
        }
        });

        return mRootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MY_MENU_LISTS_LOADER_MENU, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        //String sortOrder = EasyKitchenContract.UsersMenuList.COLUMN_MENU_NAME + " ASC";
        //Uri uri = EasyKitchenContract.UsersMenuList.buildMenuUriByUserName("user");
        Log.d(LOG_TAG, "onCreateLoader for loader_id " + i);
        CursorLoader cursor = null;
        HashMap<Integer, Integer> groupMap = mMenuListAdapter.getGroupMap();

        if (i == MY_MENU_LISTS_LOADER_MENU) {
            String sortOrder = EasyKitchenContract.UsersMenuList.COLUMN_MENU_NAME + " ASC";
            Uri uri = EasyKitchenContract.UsersMenuList.buildMenuUriByUserName("user");
            cursor = new CursorLoader(getActivity(),
                                        uri,
                                        null,
                                        null,
                                        null,
                                        sortOrder);

            } else{ // child group
                //int groupPos = groupMap.get(i);

                String sortOrder = EasyKitchenContract.UsersRecipeList.COLUMN_ID + " ASC";
                Uri uri = EasyKitchenContract.UsersRecipeList.buildRecipeUriByMenuId(i);//use menu_id as loader id
                cursor = new CursorLoader(getActivity(),
                        uri,
                        null,
                        null,
                        null,
                        sortOrder);
            }

        Log.v(LOG_TAG,"onCreateLoader id = " + i );
        return cursor;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor==null)
        {
            Log.v(LOG_TAG, " return cursorLoader.getId()" + cursorLoader.getId());
            return;
        }
        int loadId = cursorLoader.getId();
        if (loadId == MY_MENU_LISTS_LOADER_MENU) {
            if (cursor.getCount() == 0) {
                mHintText.setVisibility(View.VISIBLE);
            } else {
                mHintText.setVisibility(View.GONE);

            }
            Log.v(LOG_TAG,"cursor.getCount() = " + cursor.getCount());
            mMenuListAdapter.setGroupCursor(cursor);
        }
        else{
            HashMap<Integer, Integer> groupMap = mMenuListAdapter.getGroupMap();
            int groupPos = groupMap.get(loadId);
            mMenuListAdapter.setChildrenCursor(groupPos,cursor);
        }

        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        int loadId = cursorLoader.getId();
        if (loadId == MY_MENU_LISTS_LOADER_MENU){
            Log.v(LOG_TAG,"RESET GROUP CURSOR");
            mMenuListAdapter.setGroupCursor(null);}
        else{
            HashMap<Integer, Integer> groupMap = mMenuListAdapter.getGroupMap();
            int groupPos = groupMap.get(loadId);
            Log.v(LOG_TAG,"RESET CHILD CURSOR");
            mMenuListAdapter.setChildrenCursor(groupPos, null);
        }
    }

    public static class LongClickDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_edittext, null));
            builder.setTitle(R.string.dialog_long_click_title);
            builder.setNegativeButton(R.string.dialog_long_click_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int index = mCursor.getColumnIndex(EasyKitchenContract.UsersMenuList.COLUMN_ID);
                    int id = mCursor.getInt(index);
                    mContext.getContentResolver().delete(EasyKitchenContract.UsersMenuList.buildUserMenuUriById(id), null, null);
                    //refresh the display
                    mCursor.requery();

                }
            });
            builder.setPositiveButton(R.string.dialog_long_click_rename, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*get dialog name*/
                    EditText editText = (EditText) getDialog().findViewById(R.id.add_menu_name);
                    String name = editText.getText().toString().trim();
                    if (!name.equals("")) {
                        int index = mCursor.getColumnIndex(EasyKitchenContract.UsersMenuList.COLUMN_ID);
                        int id = mCursor.getInt(index);
                        ContentValues menuValue = new ContentValues();
                        menuValue.put(EasyKitchenContract.UsersMenuList.COLUMN_MENU_NAME, name);
                        mContext.getContentResolver().update(EasyKitchenContract.UsersMenuList.buildUserMenuUriById(id), menuValue, null, null);
                        //refresh the display
                        mCursor.requery();

                    }
                }
            });
            return builder.create();
        }
    }
}
