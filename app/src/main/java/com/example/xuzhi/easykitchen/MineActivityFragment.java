package com.example.xuzhi.easykitchen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MineActivityFragment extends Fragment {
    private ArrayAdapter<String> mUsersListAdapter;
    public MineActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        final ListView listView = (ListView)rootView.findViewById(R.id.users_menu_list);
        final String[] usersMenu = {"我的信息","我的食材","心爱菜谱","自定义菜谱","更新"};
        mUsersListAdapter =
                new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, usersMenu);
        listView.setAdapter(mUsersListAdapter);
        /*calculate the height of all the items and adjust the listview*/
        Utility.setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                String listItem = (String) listView.getItemAtPosition(position);
                switch (listItem) {
                    case "我的信息":
                        /*Intent intent = new Intent(getActivity(), FavoriteRecipesActivity.class);
                        startActivity(intent);*/
                        break;
                    case "心爱菜谱":
                        intent = new Intent(getActivity(), FavoriteRecipesActivity.class);
                        startActivity(intent);
                        break;
                    case "我的食材":
                        intent = new Intent(getActivity(), MaterialActivity.class);
                        startActivity(intent);
                        break;
                    case "自定义菜谱":
                        intent = new Intent(getActivity(), CustomRecipesActivity.class);
                        startActivity(intent);
                        break;
                    case "更新":
                        FetchDbTask dbTask = new FetchDbTask(getActivity());
                        dbTask.execute();
                        break;
                    default:
                        break;
                }
            }
        });
        return rootView;
    }

}
