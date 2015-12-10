package com.example.xuzhi.easykitchen;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class StartActivityFragment extends Fragment {

    public StartActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        TextView breakfast = (TextView)rootView.findViewById(R.id.id_breakfast);
        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuActivity.class).putExtra("mealType","B");
                startActivity(intent);
            }
        });

        TextView lunch = (TextView)rootView.findViewById(R.id.id_lunch);
        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuActivity.class).putExtra("mealType","M");
                startActivity(intent);
            }
        });
        TextView supper = (TextView)rootView.findViewById(R.id.id_supper);
        supper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuActivity.class).putExtra("mealType","M");
                startActivity(intent);
            }
        });
        TextView myKitchen = (TextView)rootView.findViewById(R.id.id_my_kitchen);
        myKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MineActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
