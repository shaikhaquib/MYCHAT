package com.ishook.inc.ychat.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ishook.inc.ychat.R;

import java.util.ArrayList;
import java.util.List;

import com.ishook.inc.ychat.adapters.SettingViewPagerAdapter;
import com.ishook.inc.ychat.fragments.Change_Password;
import com.ishook.inc.ychat.fragments.Update_ProfilePic;

/**
 * Activity having TABS with TEXT
 *
 * Also, this Activity contains codes for TABS with both TEXT + ICONS.
 */
public class Setting extends AppCompatActivity {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    private ViewPager viewPager;
    private SettingViewPagerAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initialise();
        setTitle("Settings");

        prepareDataResource();

        adapter = new SettingViewPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);

        // Bind Adapter to ViewPager.
        viewPager.setAdapter(adapter);

        // Link ViewPager and TabLayout
        tabLayout.setupWithViewPager(viewPager);

        // Uncomment the LINE BELOW to see TABS with both TEXT+ICONS
        //setTabIcons();
    }

    // Initialise Activity Data.
    private void initialise() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");

        viewPager = (ViewPager) findViewById(R.id.container1);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    // Let's prepare Data for our Tabs - Fragments and Title List
    private void prepareDataResource() {

        addData(new Update_ProfilePic(), "Account");
        addData(new Change_Password(), "Change Password");
    }

    private void addData(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }
}
