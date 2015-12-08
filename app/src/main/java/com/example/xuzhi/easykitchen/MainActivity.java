package com.example.xuzhi.easykitchen;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity implements MineFragment.OnFragmentInteractionListener{

    private RadioGroup bottomRg;
    private RadioButton rbOne, rbTwo, rbThree,rbFour;
    private final String LOG_TAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Restore preferences
       /* SharedPreferences settings = getPreferences(0);
        boolean dbExist = settings.getBoolean("dbExist", false);
        Log.v(LOG_TAG, "dbExist =" + dbExist);
        if(!dbExist) {
            try {
                Utility.copyDataBase(getBaseContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("dbExist", true);
                // Commit the edits!
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }*/
        //set button click listener
        setFragmentIndicator();
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                Log.v(LOG_TAG,"savedInstanceState != null");
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MainActivityFragment firstFragment = new MainActivityFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }


    }

    private void setFragmentIndicator() {

        this.bottomRg = (RadioGroup) findViewById(R.id.bottomRg);
        this.rbOne = (RadioButton) findViewById(R.id.rbOne);
        this.rbTwo = (RadioButton) findViewById(R.id.rbTwo);
        this.rbThree = (RadioButton) findViewById(R.id.rbThree);
        this.rbFour = (RadioButton) findViewById(R.id.rbFour);

        bottomRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.v(LOG_TAG,"click");

                switch (checkedId) {
                    case R.id.rbOne: {

                        Log.v(LOG_TAG,"click rbone");

                        // Create fragment and give it an argument specifying the article it should show
                        MainActivityFragment newFragment = new MainActivityFragment();
                        Bundle args = new Bundle();
                        //args.putInt(ArrangeActivityFragment.ARG_POSITION, position);
                        newFragment.setArguments(args);

                        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.fragment_container, newFragment);
                        //transaction.addToBackStack(null);

                        // Commit the transaction
                        transaction.commit();
                        break;
                    }
                    case R.id.rbTwo: {
                        Log.v(LOG_TAG,"click rbtwo");
                        // Create fragment and give it an argument specifying the article it should show
                        MenuActivityFragment newFragment = new MenuActivityFragment();
                        Bundle args = new Bundle();
                        //args.putInt(ArrangeActivityFragment.ARG_POSITION, position);
                        newFragment.setArguments(args);

                        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.fragment_container, newFragment);
                        //transaction.addToBackStack(null);

                        // Commit the transaction
                        transaction.commit();
                        break;
                    }
                    case R.id.rbThree: {
                        Log.v(LOG_TAG,"click rbthree");
                        // Create fragment and give it an argument specifying the article it should show
                        ArrangeActivityFragment newFragment = new ArrangeActivityFragment();
                        Bundle args = new Bundle();
                        //args.putInt(ArrangeActivityFragment.ARG_POSITION, position);
                        newFragment.setArguments(args);

                        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.fragment_container, newFragment);
                        //transaction.addToBackStack(null);

                        // Commit the transaction
                        transaction.commit();
                        break;
                    }
                    case R.id.rbFour: {
                        Log.v(LOG_TAG,"click rbfour");
                        // Create fragment and give it an argument specifying the article it should show
                        MineFragment newFragment = new MineFragment();
                        Bundle args = new Bundle();
                        //args.putInt(ArrangeActivityFragment.ARG_POSITION, position);
                        newFragment.setArguments(args);

                        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.fragment_container, newFragment);
                        //transaction.addToBackStack(null);

                        // Commit the transaction
                        transaction.commit();
                        break;
                    }
                    default:
                        Log.v(LOG_TAG,"click default");
                        break;
                }

            }
        });
    }
    @Override
    public void onFragmentInteraction(Uri uri)
    {}
}