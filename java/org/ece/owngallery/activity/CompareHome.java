package org.ece.owngallery.activity;

import org.ece.owngallery.R;
import org.ece.owngallery.adapter.SlideMenuAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class CompareHome extends ActionBarActivity{

    private Context mContext;
    private Toolbar toolbar;
    private DrawerLayout Drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment currentFragment = null;

    private ListView slidingList;
    private SlideMenuAdapter mSlideMenuAdapter;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mContext = CompareHome.this;
        initializeActionBar();
        initialCalling();

    }

    @Override
    public void onBackPressed() {
        if (Drawer.isDrawerOpen(Gravity.LEFT)) {
            Drawer.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.cancel: {
                Intent mIntent=new Intent(mContext,ActivityHome.class);
                mContext.startActivity(mIntent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void initializeActionBar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void closeDrware() {
        if (Drawer.isDrawerOpen(Gravity.LEFT)) {
            Drawer.closeDrawer(Gravity.LEFT);
        }
    }

    private void initialCalling() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        getFragment(0);
        attachedFragment();
    }


    private void attachedFragment() {
        try {
            if (currentFragment != null) {
                if (fragmentTransaction.isEmpty()) {
                    fragmentTransaction.add(R.id.fragment_container, currentFragment, "" + currentFragment.toString());
                    fragmentTransaction.commit();
                    toolbar.setTitle(title[currentPosition]);
                } else {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, currentFragment, "" + currentFragment.toString());
                    fragmentTransaction.commit();
                    toolbar.setTitle(title[currentPosition]);
                }

            }
            closeDrware();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getFragment(int postion) {
        switch (postion) {
            case 0:
                currentFragment = new CompareGalleryFragment();
                break;
            default:
                break;
        }
    }


    /**
     * Slide Menu List Array.
     */
    private String[] title = {"사진을 선택해주세요."};


}
