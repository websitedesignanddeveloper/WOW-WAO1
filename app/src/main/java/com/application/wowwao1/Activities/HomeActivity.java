package com.application.wowwao1.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Fragments.AccountSettingsFragment;
import com.application.wowwao1.Fragments.BlockedUsersFragment;
import com.application.wowwao1.Fragments.ContactUsFragment;
import com.application.wowwao1.Fragments.FriendRequestFragment;
import com.application.wowwao1.Fragments.FriendsFragment;
import com.application.wowwao1.Fragments.HomeFragment;
import com.application.wowwao1.Fragments.InviteFragment;
import com.application.wowwao1.Fragments.MessagesFragment;
import com.application.wowwao1.Fragments.NotificationsFragment;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.CounterPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView title_text, txtNotifications, txtFriendReq, txtMessages, txtUsername;
    private CircleImageView imgProfile;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Context mContext;
    private LinearLayout layoutNotifications, layoutFriendReq, layoutMsg;
    private String profile_img, first_name, last_name, url, userId;

    private ArrayList<String> params;
    private ArrayList<String> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        setupDrawerToggle();
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        Bundle b = getIntent().getExtras();

        if(b!=null && b.containsKey("languageChanged"))
        {
            title_text.setText(R.string.nav_account_settings);
            updateDisplay(new AccountSettingsFragment(), R.string.nav_account_settings);
        }
        else {

            title_text.setText(R.string.nav_home);
            updateDisplay(new HomeFragment(), R.string.nav_home);

        }

        navigationView.setNavigationItemSelectedListener(this);
        initializeCountDrawer();
    }

    private void initViews() {
        setupToolbar();
        mContext = HomeActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");
        profile_img = PrefsUtil.with(mContext).readString("profile_img");
        first_name = PrefsUtil.with(mContext).readString("first_name");
        last_name = PrefsUtil.with(mContext).readString("last_name");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        imgProfile = (CircleImageView) header.findViewById(R.id.imgProfile);
        txtUsername = (TextView) header.findViewById(R.id.txtUsername);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title_text = (TextView) findViewById(R.id.title_text);
        setSupportActionBar(toolbar);
    }

    private void initializeCountDrawer() {
        layoutNotifications = (LinearLayout) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_notifications));
        txtNotifications = (TextView) layoutNotifications.findViewById(R.id.txtBadgeText);

        layoutFriendReq = (LinearLayout) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_friend_req));
        txtFriendReq = (TextView) layoutFriendReq.findViewById(R.id.txtBadgeText);

        layoutMsg = (LinearLayout) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_messages));
        txtMessages = (TextView) layoutMsg.findViewById(R.id.txtBadgeText);

        updateCounter();
    }

    private void updateCounter() {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userUpdatesCount");

        params.add("user_id");
        values.add(userId);

        new ParseJSON(mContext, url, params, values, CounterPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CounterPojo resultObj = (CounterPojo) obj;
                        PrefsUtil.with(mContext).write("count_request", resultObj.getData().getCountRequest());
                        PrefsUtil.with(mContext).write("count_notification", resultObj.getData().getCountNotification());
                        PrefsUtil.with(mContext).write("count_message", resultObj.getData().getCountMessage());

                        txtFriendReq.setText(resultObj.getData().getCountRequest());
                        txtNotifications.setText(resultObj.getData().getCountNotification());
                        txtMessages.setText(resultObj.getData().getCountMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    void setupDrawerToggle() {
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerView.requestFocus();
                profile_img = PrefsUtil.with(mContext).readString("profile_img");
                Picasso.with(HomeActivity.this)
                        .load(profile_img)
                        .placeholder(R.drawable.no_user)
                        .error(R.drawable.no_user)
                        .into(imgProfile);
                txtUsername.setText(PrefsUtil.with(mContext).readString("first_name") + " " + PrefsUtil.with(mContext).readString("last_name"));
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                txtFriendReq.setText(PrefsUtil.with(mContext).readString("count_request"));
                txtNotifications.setText(PrefsUtil.with(mContext).readString("count_notification"));
                txtMessages.setText(PrefsUtil.with(mContext).readString("count_message"));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                drawerView.requestFocus();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                drawerView.requestFocus();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((getSupportFragmentManager().getBackStackEntryCount() - 1) >= 0) {
                getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        try {
                            FriendRequestFragment.fromNotification = false;
                            Fragment fragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1);
                            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
                            String tag = backEntry.getName();
                            title_text.setText(tag);
                            setDrawerSelection(tag);
                            fragment.onResume();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
            if (getSupportFragmentManager().getBackStackEntryCount() - 1 == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(R.string.exit_msg);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                getSupportFragmentManager().popBackStack();

            }
        }
    }

    private void setDrawerSelection(String name) {
        if (name.equalsIgnoreCase(getResources().getString(R.string.nav_home))) {
            navigationView.getMenu().getItem(0).setChecked(true);
        } else if (name.equalsIgnoreCase(getResources().getString(R.string.nav_friends))) {
            navigationView.getMenu().getItem(1).setChecked(true);
        } else if (name.equalsIgnoreCase(getResources().getString(R.string.nav_friend_requests))) {
            navigationView.getMenu().getItem(2).setChecked(true);
        } else if (name.equalsIgnoreCase(getResources().getString(R.string.nav_messages))) {
            navigationView.getMenu().getItem(3).setChecked(true);
        } else if (name.equalsIgnoreCase(getResources().getString(R.string.nav_notifications))) {
            navigationView.getMenu().getItem(4).setChecked(true);
        } else if (name.equalsIgnoreCase(getResources().getString(R.string.nav_blocked_users))) {
            navigationView.getMenu().getItem(5).setChecked(true);
        } else if (name.equalsIgnoreCase(getResources().getString(R.string.nav_account_settings))) {
            navigationView.getMenu().getItem(6).setChecked(true);
        } else if (name.equalsIgnoreCase(getResources().getString(R.string.nav_info))) {
            navigationView.getMenu().getItem(7).setChecked(true);
        } else if (name.equalsIgnoreCase(getResources().getString(R.string.nav_contact_us))) {
            navigationView.getMenu().getItem(8).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            title_text.setText(R.string.nav_home);
            updateDisplay(new HomeFragment(), R.string.nav_home);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_friends) {
            title_text.setText(R.string.nav_friends);
            updateDisplay(new FriendsFragment(), R.string.nav_friends);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_friend_req) {
            title_text.setText(R.string.nav_friend_requests);
            FriendRequestFragment.fromNotification = false;
            updateDisplay(new FriendRequestFragment(), R.string.nav_friend_requests);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_messages) {
            MessagesFragment.refreshInResume = false;
            title_text.setText(R.string.nav_messages);
            updateDisplay(new MessagesFragment(), R.string.nav_messages);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_notifications) {
            title_text.setText(R.string.nav_notifications);
            updateDisplay(new NotificationsFragment(), R.string.nav_notifications);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_blocked_user) {
            title_text.setText(R.string.nav_blocked_users);
            updateDisplay(new BlockedUsersFragment(), R.string.nav_blocked_users);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_acc_settings) {
            title_text.setText(R.string.nav_account_settings);
            updateDisplay(new AccountSettingsFragment(), R.string.nav_account_settings);
            drawer.closeDrawer(GravityCompat.START);
        } /*else if (id == R.id.nav_info) {
            title_text.setText(R.string.nav_info);
            updateDisplay(new InfoFragment(), R.string.nav_info);
            drawer.closeDrawer(GravityCompat.START);*/
         else if (id == R.id.nav_contact_us) {
            title_text.setText(R.string.nav_contact_us);
            updateDisplay(new ContactUsFragment(), R.string.nav_contact_us);
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(id == R.id.nave_invite)
        {
            title_text.setText(R.string.nave_invite);
            updateDisplay(new InviteFragment(), R.string.nave_invite);
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_logout) {
            drawer.closeDrawer(GravityCompat.START);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(R.string.logout_msg);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    logoutCall();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        Picasso.with(HomeActivity.this)
                .load(profile_img)
                .placeholder(R.drawable.no_user)
                .error(R.drawable.no_user)
                .into(imgProfile);
        txtUsername.setText(first_name + " " + last_name);
        return true;
    }

    private void updateDisplay(Fragment fragment, int res) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerView, fragment).addToBackStack(getResources().getString(res)).commit();
    }

    private void logoutCall() {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userLogout");

        params.add("user_id");
        values.add(userId);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        PrefsUtil.with(mContext).clearPrefs();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.containerView);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    public void setToolbarTitle(String title) {
        title_text.setText(title);
    }
}
