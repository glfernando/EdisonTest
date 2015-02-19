package me.no_ip.glfernando.edisontest;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private DrawerRowAdapter mDrawerRowAdapter;
    private ListView listView;
    private String [] options;
    private View layout;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        options = getActivity().getResources().getStringArray(R.array.drawer_options);
        listView = (ListView) layout.findViewById(R.id.drawer_list);
        listView.setAdapter(new DrawerRowAdapter(getActivity()));
        listView.setOnItemClickListener(this);

        return layout;

    }


    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar) {
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this, "option " + options[position], Toast.LENGTH_SHORT).show();
        Log.d("TAG", "option selected " + position);
        DrawerFragmentComm comm = (DrawerFragmentComm) getActivity();
        listView.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        comm.updateFragment(position);

    }

    public interface DrawerFragmentComm {
        public void updateFragment(int position);
    }
}
