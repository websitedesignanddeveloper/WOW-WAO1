package com.application.wowwao1.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Adapters.HomeAdapter;
import com.application.wowwao1.Adapters.SearchUsersAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.FeedsItem;
import com.application.wowwao1.Models.SearchPojo;
import com.application.wowwao1.Models.UserData;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.KeyboardUtils;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.Utils.TimeZoneUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int SEARCH_CODE = 100;
    private static final int SEARCH_FILTER_CODE = 101;
    private static final int FOLLOW_OPTION_CODE = 102;
    private static final int ADD_OPTION_CODE = 103;
    private ImageView imgToolBack, imgSearch, imgToolFilter, imgAdd1, imgAdd2, imgAdd3;
    private CircleImageView imgProfile1, imgProfile2, imgProfile3;
    private EditText edtSearch;
    private TextView txtPeopleTittle, txtPostTittle, txtUsername1, txtUsername2, txtUsername3, txtFollowStatus1, txtFollowStatus2, txtFollowStatus3, txtNoUserFound, txtNoPostFound;
    private LinearLayout layoutSearchUser, layoutAdd1, layoutAdd2, layoutAdd3, layoutUser1, layoutUser2, layoutUser3, layoutDivider1, layoutDivider2;
    private Button btnViewAll;

    private RecyclerView rvSearchPost, rvSearchUsers;
    private LinearLayoutManager layoutManagerPost, layoutManagerUsers;
    private ArrayList<FeedsItem> arrayListPost = new ArrayList<>();
    private ArrayList<UserData> arrayListUsers = new ArrayList<>();
    private HomeAdapter adapterPost;
    private SearchUsersAdapter adapterUser;
    private Context mContext;

    private LinearLayout layoutFilterDialog, layoutRvSearchUsers;

    /* Dialog variable initialization */
    private TextView txtLocation;
    private LinearLayout layoutTypeUser, layoutTypePost;
    private TextView txtUser, txtPost;
    private ImageView imgUser, imgPost, imgArrow;

    /* Temporary variable for designing time filter */
    private String isTypePost = "n";
    private String isTypeUser = "n";

    /* ***************************************** */

    /*Auto suggestion google api variable*/
    protected GoogleApiClient mGoogleApiClient;
    PlaceAutocompleteFragment autocompleteFragment;
    /* ********************************* */

    private String url, userId, strSearch = "", searchType = "both";
    private double latitude = 0.0, longitude = 0.0;
    private ArrayList<String> params;
    private ArrayList<String> values;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;

    /* timezone */
    private String timezone = "";
    private TimeZoneUtils timeZoneUtils = new TimeZoneUtils();
    /* ******** */

    /*pagination vars start*/
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    int page = 1;
    int total_records = 0;
    /*pagination vars end*/

    private boolean isShowFilter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
        setContentView(R.layout.activity_search);
        initViews();
        new KeyboardUtils().setupUI(findViewById(R.id.activity_search), SearchActivity.this);

        timezone = timeZoneUtils.getTimeZone();
        Log.e("SearchActivity", "TimeZone : " + timezone);

        cd = new ConnectionCheck();

        layoutSearchUser.setVisibility(View.GONE);
        btnViewAll.setVisibility(View.GONE);
        rvSearchUsers.setVisibility(View.GONE);
        rvSearchPost.setVisibility(View.GONE);
        txtPeopleTittle.setVisibility(View.GONE);
        txtPostTittle.setVisibility(View.GONE);

        /*edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strSearch = s.toString().trim();
                if (strSearch.length() == 0) {
                    latitude = 0;
                    longitude = 0;
                    searchType = "both";
                    layoutSearchUser.setVisibility(View.GONE);
                    btnViewAll.setVisibility(View.GONE);
                    rvSearchUsers.setVisibility(View.GONE);
                    rvSearchPost.setVisibility(View.GONE);
                } else {
                    isInternetAvailable = cd.isNetworkConnected(mContext);
                    if (isInternetAvailable) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                searchingList(strSearch, latitude, longitude, searchType);
                            }
                        }, 3000);
                    } else {
                        intent = new Intent(mContext, NoConnectionActivity.class);
                        startActivityForResult(intent, SEARCH_CODE);
                    }

                }
            }
        });*/

        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSearchUser.setVisibility(View.GONE);
                btnViewAll.setVisibility(View.GONE);
                rvSearchUsers.setVisibility(View.VISIBLE);
                rvSearchPost.setVisibility(View.GONE);
                layoutRvSearchUsers.setVisibility(View.VISIBLE);
                txtNoUserFound.setVisibility(View.GONE);
                layoutRvSearchUsers.setVisibility(View.VISIBLE);
                txtPostTittle.setVisibility(View.GONE);
                txtPeopleTittle.setVisibility(View.VISIBLE);

                rvSearchUsers.setAdapter(adapterUser);
                adapterUser.notifyDataSetChanged();
            }
        });

        txtFollowStatus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    if (arrayListUsers.get(0).getIs_follow().equals("y")) {
                        followOperation(arrayListUsers.get(0).getId(), "Unfollow", "1");
                    } else {
                        followOperation(arrayListUsers.get(0).getId(), "Follow", "1");
                    }
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, FOLLOW_OPTION_CODE);
                }
            }
        });

        txtFollowStatus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    if (arrayListUsers.get(1).getIs_follow().equals("y")) {
                        followOperation(arrayListUsers.get(1).getId(), "Unfollow", "2");
                    } else {
                        followOperation(arrayListUsers.get(1).getId(), "Follow", "2");
                    }
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, FOLLOW_OPTION_CODE);
                }

            }
        });

        txtFollowStatus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    if (arrayListUsers.get(2).getIs_follow().equals("y")) {
                        followOperation(arrayListUsers.get(2).getId(), "Unfollow", "3");
                    } else {
                        followOperation(arrayListUsers.get(2).getId(), "Follow", "3");
                    }
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, FOLLOW_OPTION_CODE);
                }

            }
        });

        layoutAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    if (arrayListUsers.get(0).getIs_friend().equals("n")) {
                        friendReqOperation(arrayListUsers.get(0).getId(), "Add", "1");
                    } else if (arrayListUsers.get(0).getIs_friend().equals("y")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setCancelable(false);
                        builder.setMessage(R.string.unfriend_alert_msg);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", then he is allowed to exit from application
                                dialog.cancel();
                                friendReqOperation(arrayListUsers.get(0).getId(), "Unfriend", "1");
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
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, ADD_OPTION_CODE);
                }

            }
        });

        layoutAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    if (arrayListUsers.get(1).getIs_friend().equals("n")) {
                        friendReqOperation(arrayListUsers.get(1).getId(), "Add", "2");
                    } else if (arrayListUsers.get(1).getIs_friend().equals("y")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setCancelable(false);
                        builder.setMessage(R.string.unfriend_alert_msg);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", then he is allowed to exit from application
                                dialog.cancel();
                                friendReqOperation(arrayListUsers.get(1).getId(), "Unfriend", "2");
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
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, ADD_OPTION_CODE);
                }

            }
        });

        layoutAdd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    if (arrayListUsers.get(2).getIs_friend().equals("n")) {
                        friendReqOperation(arrayListUsers.get(2).getId(), "Add", "3");
                    } else if (arrayListUsers.get(2).getIs_friend().equals("y")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setCancelable(false);
                        builder.setMessage(R.string.unfriend_alert_msg);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", then he is allowed to exit from application
                                dialog.cancel();
                                friendReqOperation(arrayListUsers.get(2).getId(), "Unfriend", "3");
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
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, ADD_OPTION_CODE);
                }

            }
        });

        imgProfile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("receiver_id", arrayListUsers.get(0).getId());
                startActivity(intent);
            }
        });

        imgProfile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("receiver_id", arrayListUsers.get(1).getId());
                startActivity(intent);
            }
        });

        imgProfile3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("receiver_id", arrayListUsers.get(2).getId());
                startActivity(intent);
            }
        });

        rvSearchPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManagerPost.getChildCount();
                    totalItemCount = layoutManagerPost.getItemCount();
                    pastVisibleItems = layoutManagerPost.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            if (arrayListPost.size() < total_records) {
                                page++;
                                searchingList(strSearch, latitude, longitude, searchType, false);
                            }
                        }
                    }
                }
            }
        });
    }

    private void initViews() {
        mContext = SearchActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");
        setupToolbar();

        txtPeopleTittle = (TextView) findViewById(R.id.txtPeopleTittle);
        txtPostTittle = (TextView) findViewById(R.id.txtPostTittle);

        layoutRvSearchUsers = (LinearLayout) findViewById(R.id.layoutRvSearchUsers);
        txtNoUserFound = (TextView) findViewById(R.id.txtNoUserFound);
        txtNoPostFound = (TextView) findViewById(R.id.txtNoPostFound);

        layoutSearchUser = (LinearLayout) findViewById(R.id.layoutSearchUser);
        layoutFilterDialog = (LinearLayout) findViewById(R.id.layoutFilterDialog);

        imgProfile1 = (CircleImageView) findViewById(R.id.imgProfile1);
        imgProfile2 = (CircleImageView) findViewById(R.id.imgProfile2);
        imgProfile3 = (CircleImageView) findViewById(R.id.imgProfile3);

        imgAdd1 = (ImageView) findViewById(R.id.imgAdd1);
        imgAdd2 = (ImageView) findViewById(R.id.imgAdd2);
        imgAdd3 = (ImageView) findViewById(R.id.imgAdd3);

        txtUsername1 = (TextView) findViewById(R.id.txtUsername1);
        txtUsername2 = (TextView) findViewById(R.id.txtUsername2);
        txtUsername3 = (TextView) findViewById(R.id.txtUsername3);

        txtFollowStatus1 = (TextView) findViewById(R.id.txtFollowStatus1);
        txtFollowStatus2 = (TextView) findViewById(R.id.txtFollowStatus2);
        txtFollowStatus3 = (TextView) findViewById(R.id.txtFollowStatus3);

        layoutAdd1 = (LinearLayout) findViewById(R.id.layoutAdd1);
        layoutAdd2 = (LinearLayout) findViewById(R.id.layoutAdd2);
        layoutAdd3 = (LinearLayout) findViewById(R.id.layoutAdd3);

        layoutUser1 = (LinearLayout) findViewById(R.id.layoutUser1);
        layoutUser2 = (LinearLayout) findViewById(R.id.layoutUser2);
        layoutUser3 = (LinearLayout) findViewById(R.id.layoutUser3);
        layoutDivider1 = (LinearLayout) findViewById(R.id.layoutDivider1);
        layoutDivider2 = (LinearLayout) findViewById(R.id.layoutDivider2);

        btnViewAll = (Button) findViewById(R.id.btnViewAll);

        rvSearchPost = (RecyclerView) findViewById(R.id.rvSearchPost);
        layoutManagerPost = new LinearLayoutManager(mContext);
        layoutManagerPost = new LinearLayoutManager(mContext);
        rvSearchPost.setLayoutManager(layoutManagerPost);
        rvSearchPost.setHasFixedSize(false);
        rvSearchPost.setNestedScrollingEnabled(false);
        adapterPost = new HomeAdapter(SearchActivity.this, arrayListPost);
        rvSearchPost.setAdapter(adapterPost);
        adapterPost.notifyDataSetChanged();

        rvSearchUsers = (RecyclerView) findViewById(R.id.rvSearchUsers);
        layoutManagerUsers = new LinearLayoutManager(mContext);
        rvSearchUsers.setLayoutManager(layoutManagerUsers);
        rvSearchUsers.setHasFixedSize(false);
        rvSearchUsers.setNestedScrollingEnabled(false);
        adapterUser = new SearchUsersAdapter(mContext, arrayListUsers);
        rvSearchUsers.setAdapter(adapterUser);
        adapterUser.notifyDataSetChanged();

        /*dialog = new Dialog(mContext);*/
        /* dialog variable declaration */
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        txtLocation = (TextView) findViewById(R.id.txtLocation);

        layoutTypeUser = (LinearLayout) findViewById(R.id.layoutTypeUser);
        layoutTypePost = (LinearLayout) findViewById(R.id.layoutTypePost);

        txtUser = (TextView) findViewById(R.id.txtUser);
        txtPost = (TextView) findViewById(R.id.txtPost);

        imgUser = (ImageView) findViewById(R.id.imgUser);
        imgPost = (ImageView) findViewById(R.id.imgPost);
        imgArrow = (ImageView) findViewById(R.id.imgArrow);

        /* ***************************************** */

    }

    private void setupToolbar() {
        imgToolBack = (ImageView) findViewById(R.id.imgToolBack);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        imgToolFilter = (ImageView) findViewById(R.id.imgToolFilter);
        edtSearch = (EditText) findViewById(R.id.edtSearch);

        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strSearch = edtSearch.getText().toString().toString().trim();
                if (strSearch.length() > 0) {
                    searchingList(strSearch, latitude, longitude, searchType, true);
                } else {
                    searchType = "both";
                    latitude = Double.parseDouble(PrefsUtil.with(mContext).readString("latitude"));
                    longitude = Double.parseDouble(PrefsUtil.with(mContext).readString("longitude"));
                    searchingList(strSearch, latitude, longitude, searchType, true);
                }
            }
        });

        imgToolFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowFilter) {
                    isShowFilter = false;
                    dialogSearch();
                } else {
                    isShowFilter = true;
                    layoutFilterDialog.setVisibility(View.GONE);
                }
            }
        });
    }

    private void searchingList(String strSearch, double latitude, double longitude, final String searchType, boolean refreshFlag) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("search");

        params.add("user_id");
        values.add(userId);

        params.add("keyword");
        values.add(strSearch);

        params.add("lat");
        values.add(String.valueOf(latitude));

        params.add("long");
        values.add(String.valueOf(longitude));

        params.add("search_type");
        values.add(searchType);

        params.add("page");
        values.add(String.valueOf(page));

        params.add("timezone");
        values.add(timezone);

        if (refreshFlag) {
            arrayListPost.clear();
            adapterPost.notifyDataSetChanged();
            page = 1;
        }

        new ParseJSON(mContext, url, params, values, SearchPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        SearchPojo resultObj = (SearchPojo) obj;

                        if (searchType.equalsIgnoreCase("both")) {
                            layoutSearchUser.setVisibility(View.VISIBLE);
                            rvSearchUsers.setVisibility(View.GONE);
                            rvSearchPost.setVisibility(View.VISIBLE);
                            txtPeopleTittle.setVisibility(View.VISIBLE);
                            txtPostTittle.setVisibility(View.VISIBLE);

                            arrayListUsers.clear();
                            arrayListUsers.addAll(resultObj.getData().getUser());
                            setUserData(arrayListUsers);

                            if (resultObj.getData().getPost().size() > 0) {
                                rvSearchPost.setVisibility(View.VISIBLE);
                                txtNoPostFound.setVisibility(View.GONE);
                                arrayListPost.clear();
                                arrayListPost.addAll(resultObj.getData().getPost());
                                adapterPost.notifyDataSetChanged();
                            } else {
                                rvSearchPost.setVisibility(View.GONE);
                                txtNoPostFound.setVisibility(View.VISIBLE);
                            }
                        } else if (searchType.equalsIgnoreCase("user")) {
                            if (resultObj.getData().getUser().size() > 0) {
                                layoutSearchUser.setVisibility(View.GONE);
                                rvSearchUsers.setVisibility(View.VISIBLE);
                                rvSearchPost.setVisibility(View.GONE);
                                btnViewAll.setVisibility(View.GONE);
                                layoutRvSearchUsers.setVisibility(View.VISIBLE);
                                txtNoUserFound.setVisibility(View.GONE);
                                txtPeopleTittle.setVisibility(View.VISIBLE);
                                txtPostTittle.setVisibility(View.GONE);

                                arrayListUsers.clear();
                                arrayListUsers.addAll(resultObj.getData().getUser());
                                adapterUser.notifyDataSetChanged();
                            } else {
                                layoutSearchUser.setVisibility(View.GONE);
                                rvSearchUsers.setVisibility(View.GONE);
                                rvSearchPost.setVisibility(View.GONE);
                                btnViewAll.setVisibility(View.GONE);
                                layoutRvSearchUsers.setVisibility(View.GONE);
                                txtNoUserFound.setVisibility(View.VISIBLE);
                                txtPeopleTittle.setVisibility(View.VISIBLE);
                            }
                        } else if (searchType.equalsIgnoreCase("post")) {
                            if (resultObj.getData().getPost().size() > 0) {
                                layoutSearchUser.setVisibility(View.GONE);
                                rvSearchUsers.setVisibility(View.GONE);
                                rvSearchPost.setVisibility(View.VISIBLE);
                                layoutRvSearchUsers.setVisibility(View.GONE);
                                btnViewAll.setVisibility(View.GONE);
                                txtNoPostFound.setVisibility(View.GONE);
                                txtPeopleTittle.setVisibility(View.GONE);
                                txtPostTittle.setVisibility(View.VISIBLE);

                                arrayListPost.clear();
                                arrayListPost.addAll(resultObj.getData().getPost());
                                adapterPost.notifyDataSetChanged();
                            } else {
                                layoutSearchUser.setVisibility(View.GONE);
                                rvSearchUsers.setVisibility(View.GONE);
                                rvSearchPost.setVisibility(View.GONE);
                                layoutRvSearchUsers.setVisibility(View.GONE);
                                btnViewAll.setVisibility(View.GONE);
                                txtNoPostFound.setVisibility(View.VISIBLE);
                                txtPostTittle.setVisibility(View.VISIBLE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    layoutSearchUser.setVisibility(View.GONE);
                    rvSearchUsers.setVisibility(View.GONE);
                    rvSearchPost.setVisibility(View.GONE);
                    btnViewAll.setVisibility(View.GONE);
                    layoutRvSearchUsers.setVisibility(View.GONE);
                    txtNoUserFound.setVisibility(View.VISIBLE);
                    txtNoPostFound.setVisibility(View.VISIBLE);
                    txtPeopleTittle.setVisibility(View.VISIBLE);
                    txtPostTittle.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setUserData(ArrayList<UserData> arrayListUsers) {
        if (arrayListUsers.size() > 0) {
            layoutSearchUser.setVisibility(View.VISIBLE);
            if (arrayListUsers.size() == 1) {

                layoutUser1.setVisibility(View.VISIBLE);
                layoutDivider1.setVisibility(View.GONE);
                layoutUser2.setVisibility(View.GONE);
                layoutDivider2.setVisibility(View.GONE);
                layoutUser3.setVisibility(View.GONE);
                btnViewAll.setVisibility(View.GONE);
                layoutRvSearchUsers.setVisibility(View.GONE);
                txtNoUserFound.setVisibility(View.GONE);

                Picasso.with(mContext)
                        .load(arrayListUsers.get(0).getProfileImg())
                        .error(R.drawable.no_user)
                        .placeholder(R.drawable.no_user)
                        .into(imgProfile1);

                txtUsername1.setText(arrayListUsers.get(0).getFirstName() + " " + arrayListUsers.get(0).getLastName());
                if (arrayListUsers.get(0).getIs_follow().equalsIgnoreCase("y")) {
                    txtFollowStatus1.setText("Unfollow");
                    txtFollowStatus1.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    txtFollowStatus1.setText("Follow");
                    txtFollowStatus1.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("n")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.add_friend);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("y")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.remove_friend);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("s")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.request_sent);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("r")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.request_received);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                }
            } else if (arrayListUsers.size() == 2) {

                layoutUser1.setVisibility(View.VISIBLE);
                layoutDivider1.setVisibility(View.VISIBLE);
                layoutUser2.setVisibility(View.VISIBLE);
                layoutDivider2.setVisibility(View.GONE);
                layoutUser3.setVisibility(View.GONE);
                btnViewAll.setVisibility(View.GONE);
                layoutRvSearchUsers.setVisibility(View.GONE);
                txtNoUserFound.setVisibility(View.GONE);

                Picasso.with(mContext)
                        .load(arrayListUsers.get(0).getProfileImg())
                        .error(R.drawable.no_user)
                        .placeholder(R.drawable.no_user)
                        .into(imgProfile1);

                txtUsername1.setText(arrayListUsers.get(0).getFirstName() + " " + arrayListUsers.get(0).getLastName());
                if (arrayListUsers.get(0).getIs_follow().equalsIgnoreCase("y")) {
                    txtFollowStatus1.setText(R.string.unfollow);
                    txtFollowStatus1.setTextColor(getResources().getColor(R.color.descriptionColor));

                } else {
                    txtFollowStatus1.setText(R.string.follow);
                    txtFollowStatus1.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("n")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.add_friend);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("y")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.remove_friend);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("s")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.request_sent);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("r")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.request_received);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                }

                Picasso.with(mContext)
                        .load(arrayListUsers.get(1).getProfileImg())
                        .error(R.drawable.no_user)
                        .placeholder(R.drawable.no_user)
                        .into(imgProfile2);

                txtUsername2.setText(arrayListUsers.get(1).getFirstName() + " " + arrayListUsers.get(1).getLastName());
                if (arrayListUsers.get(1).getIs_follow().equalsIgnoreCase("y")) {
                    txtFollowStatus2.setText(R.string.unfollow);
                    txtFollowStatus2.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    txtFollowStatus2.setText(R.string.follow);
                    txtFollowStatus2.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("n")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.add_friend);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("y")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.remove_friend);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
                } else if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("s")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.request_sent);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("r")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.request_received);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                }
            } else if (arrayListUsers.size() == 3) {

                layoutUser1.setVisibility(View.VISIBLE);
                layoutDivider1.setVisibility(View.VISIBLE);
                layoutUser2.setVisibility(View.VISIBLE);
                layoutDivider2.setVisibility(View.VISIBLE);
                layoutUser3.setVisibility(View.VISIBLE);
                btnViewAll.setVisibility(View.GONE);
                layoutRvSearchUsers.setVisibility(View.GONE);
                txtNoUserFound.setVisibility(View.GONE);

                Picasso.with(mContext)
                        .load(arrayListUsers.get(0).getProfileImg())
                        .error(R.drawable.no_user)
                        .placeholder(R.drawable.no_user)
                        .into(imgProfile1);

                txtUsername1.setText(arrayListUsers.get(0).getFirstName() + " " + arrayListUsers.get(0).getLastName());
                if (arrayListUsers.get(0).getIs_follow().equalsIgnoreCase("y")) {
                    txtFollowStatus1.setText(R.string.unfollow);
                    txtFollowStatus1.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    txtFollowStatus1.setText(R.string.follow);
                    txtFollowStatus1.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("n")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.add_friend);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("y")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.remove_friend);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("s")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.request_sent);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("r")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.request_received);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                }

                Picasso.with(mContext)
                        .load(arrayListUsers.get(1).getProfileImg())
                        .error(R.drawable.no_user)
                        .placeholder(R.drawable.no_user)
                        .into(imgProfile2);

                txtUsername2.setText(arrayListUsers.get(1).getFirstName() + " " + arrayListUsers.get(1).getLastName());
                if (arrayListUsers.get(1).getIs_follow().equalsIgnoreCase("y")) {
                    txtFollowStatus2.setText(R.string.unfollow);
                    txtFollowStatus2.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    txtFollowStatus2.setText(R.string.follow);
                    txtFollowStatus2.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("n")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.add_friend);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("y")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.remove_friend);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
                } else if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("s")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.request_sent);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("r")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.request_received);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                }

                Picasso.with(mContext)
                        .load(arrayListUsers.get(2).getProfileImg())
                        .error(R.drawable.no_user)
                        .placeholder(R.drawable.no_user)
                        .into(imgProfile3);

                txtUsername3.setText(arrayListUsers.get(2).getFirstName() + " " + arrayListUsers.get(2).getLastName());
                if (arrayListUsers.get(2).getIs_follow().equalsIgnoreCase("y")) {
                    txtFollowStatus3.setText(R.string.unfollow);
                    txtFollowStatus3.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    txtFollowStatus3.setText(R.string.follow);
                    txtFollowStatus3.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (arrayListUsers.get(2).getIs_friend().equalsIgnoreCase("n")) {
                    layoutAdd3.setVisibility(View.VISIBLE);
                    imgAdd3.setImageResource(R.drawable.add_friend);
                    layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(2).getIs_friend().equalsIgnoreCase("y")) {
                    layoutAdd3.setVisibility(View.VISIBLE);
                    imgAdd3.setImageResource(R.drawable.remove_friend);
                    layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
                } else if (arrayListUsers.get(2).getIs_friend().equalsIgnoreCase("s")) {
                    layoutAdd3.setVisibility(View.VISIBLE);
                    imgAdd3.setImageResource(R.drawable.request_sent);
                    layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(2).getIs_friend().equalsIgnoreCase("r")) {
                    layoutAdd3.setVisibility(View.VISIBLE);
                    imgAdd3.setImageResource(R.drawable.request_received);
                    layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                }
            } else if (arrayListUsers.size() > 3) {
                layoutUser1.setVisibility(View.VISIBLE);
                layoutDivider1.setVisibility(View.VISIBLE);
                layoutUser2.setVisibility(View.VISIBLE);
                layoutDivider2.setVisibility(View.VISIBLE);
                layoutUser3.setVisibility(View.VISIBLE);
                btnViewAll.setVisibility(View.VISIBLE);
                layoutRvSearchUsers.setVisibility(View.GONE);
                txtNoUserFound.setVisibility(View.GONE);

                Picasso.with(mContext)
                        .load(arrayListUsers.get(0).getProfileImg())
                        .error(R.drawable.no_user)
                        .placeholder(R.drawable.no_user)
                        .into(imgProfile1);

                txtUsername1.setText(arrayListUsers.get(0).getFirstName() + " " + arrayListUsers.get(0).getLastName());
                if (arrayListUsers.get(0).getIs_follow().equalsIgnoreCase("y")) {
                    txtFollowStatus1.setText(R.string.unfollow);
                    txtFollowStatus1.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    txtFollowStatus1.setText(R.string.follow);
                    txtFollowStatus1.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("n")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.add_friend);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("y")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.remove_friend);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("s")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.request_sent);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(0).getIs_friend().equalsIgnoreCase("r")) {
                    layoutAdd1.setVisibility(View.VISIBLE);
                    imgAdd1.setImageResource(R.drawable.request_received);
                    layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                }

                Picasso.with(mContext)
                        .load(arrayListUsers.get(1).getProfileImg())
                        .error(R.drawable.no_user)
                        .placeholder(R.drawable.no_user)
                        .into(imgProfile2);

                txtUsername2.setText(arrayListUsers.get(1).getFirstName() + " " + arrayListUsers.get(1).getLastName());
                if (arrayListUsers.get(1).getIs_follow().equalsIgnoreCase("y")) {
                    txtFollowStatus2.setText(R.string.unfollow);
                    txtFollowStatus2.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    txtFollowStatus2.setText(R.string.follow);
                    txtFollowStatus2.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("n")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.add_friend);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("y")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.remove_friend);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
                } else if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("s")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.request_sent);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(1).getIs_friend().equalsIgnoreCase("r")) {
                    layoutAdd2.setVisibility(View.VISIBLE);
                    imgAdd2.setImageResource(R.drawable.request_received);
                    layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                }

                Picasso.with(mContext)
                        .load(arrayListUsers.get(2).getProfileImg())
                        .error(R.drawable.no_user)
                        .placeholder(R.drawable.no_user)
                        .into(imgProfile3);

                txtUsername3.setText(arrayListUsers.get(2).getFirstName() + " " + arrayListUsers.get(2).getLastName());
                if (arrayListUsers.get(2).getIs_follow().equalsIgnoreCase("y")) {
                    txtFollowStatus3.setText(R.string.unfollow);
                    txtFollowStatus3.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    txtFollowStatus3.setText(R.string.follow);
                    txtFollowStatus3.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                if (arrayListUsers.get(2).getIs_friend().equalsIgnoreCase("n")) {
                    layoutAdd3.setVisibility(View.VISIBLE);
                    imgAdd3.setImageResource(R.drawable.add_friend);
                    layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(2).getIs_friend().equalsIgnoreCase("y")) {
                    layoutAdd3.setVisibility(View.VISIBLE);
                    imgAdd3.setImageResource(R.drawable.remove_friend);
                    layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
                } else if (arrayListUsers.get(2).getIs_friend().equalsIgnoreCase("s")) {
                    layoutAdd3.setVisibility(View.VISIBLE);
                    imgAdd3.setImageResource(R.drawable.request_sent);
                    layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                } else if (arrayListUsers.get(2).getIs_friend().equalsIgnoreCase("r")) {
                    layoutAdd3.setVisibility(View.VISIBLE);
                    imgAdd3.setImageResource(R.drawable.request_received);
                    layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                }
            }
        } else {
            layoutSearchUser.setVisibility(View.GONE);
            layoutUser1.setVisibility(View.GONE);
            layoutDivider1.setVisibility(View.GONE);
            layoutUser2.setVisibility(View.GONE);
            layoutDivider2.setVisibility(View.GONE);
            layoutUser3.setVisibility(View.GONE);
            btnViewAll.setVisibility(View.GONE);
            layoutRvSearchUsers.setVisibility(View.GONE);
            txtNoUserFound.setVisibility(View.VISIBLE);
            txtPeopleTittle.setVisibility(View.VISIBLE);
        }
    }

    private void dialogSearch() {

        layoutFilterDialog.setVisibility(View.VISIBLE);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("BookingDetailsActivity", "Place: " + place.getName());
                LatLng latLng = place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

                Log.i("BookingDetailsActivity", "An error occurred: " + status + "  status.getStatusCode() " + status.getStatusCode());
            }
        });

        imgArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    layoutFilterDialog.setVisibility(View.GONE);
                    if (isTypeUser.equals("y") && isTypePost.equals("n")) {
                        searchType = "user";
                        Log.e("SearchActivity", "searchType : " + searchType);
                        searchingList(strSearch, latitude, longitude, searchType, true);
                    } else if (isTypePost.equals("y") && isTypeUser.equals("n")) {
                        searchType = "post";
                        Log.e("SearchActivity", "searchType : " + searchType);
                        searchingList(strSearch, latitude, longitude, searchType, true);
                    } else if (isTypeUser.equals("y") && isTypePost.equals("y")) {
                        searchType = "both";
                        Log.e("SearchActivity", "searchType : " + searchType);
                        searchingList(strSearch, latitude, longitude, searchType, true);
                    } else {
                        searchType = "both";
                        Log.e("SearchActivity", "searchType : " + searchType);
                        searchingList(strSearch, latitude, longitude, searchType, true);
                    }
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, SEARCH_FILTER_CODE);
                }
            }
        });

        layoutTypeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTypeUser.equals("y")) {
                    isTypeUser = "n";
                    txtUser.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    isTypeUser = "y";
                    txtUser.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        layoutTypePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTypePost.equals("y")) {
                    isTypePost = "n";
                    txtPost.setTextColor(getResources().getColor(R.color.descriptionColor));
                } else {
                    isTypePost = "y";
                    txtPost.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void followOperation(String receiver_id, final String follow_action, final String user) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userFollow");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        params.add("follow_action");
        values.add(follow_action);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        if (user.equals("1")) {
                            if (follow_action.equalsIgnoreCase("follow")) {
                                arrayListUsers.get(0).setIs_follow("y");
                                txtFollowStatus1.setText(R.string.unfollow);
                                txtFollowStatus1.setTextColor(getResources().getColor(R.color.descriptionColor));
                            } else {
                                arrayListUsers.get(0).setIs_follow("n");
                                txtFollowStatus1.setText(R.string.follow);
                                txtFollowStatus1.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                            adapterUser.notifyDataSetChanged();
                        }

                        if (user.equals("2")) {
                            if (follow_action.equalsIgnoreCase("follow")) {
                                arrayListUsers.get(1).setIs_follow("y");
                                txtFollowStatus2.setText(R.string.unfollow);
                                txtFollowStatus2.setTextColor(getResources().getColor(R.color.descriptionColor));
                            } else {
                                arrayListUsers.get(1).setIs_follow("n");
                                txtFollowStatus2.setText(R.string.follow);
                                txtFollowStatus2.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                            adapterUser.notifyDataSetChanged();
                        }

                        if (user.equals("3")) {
                            if (follow_action.equalsIgnoreCase("follow")) {
                                arrayListUsers.get(2).setIs_follow("y");
                                txtFollowStatus3.setText(R.string.unfollow);
                                txtFollowStatus3.setTextColor(getResources().getColor(R.color.descriptionColor));
                            } else {
                                arrayListUsers.get(2).setIs_follow("n");
                                txtFollowStatus3.setText(R.string.follow);
                                txtFollowStatus3.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                            adapterUser.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    private void friendReqOperation(String receiver_id, final String request_action, final String user) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userRequest");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        params.add("request_action");
        values.add(request_action);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        if (request_action.equalsIgnoreCase("Add")) {
                            if (user.equals("1")) {
                                arrayListUsers.get(0).setIs_friend("s");
                                arrayListUsers.get(0).setIs_follow("y");
                                imgAdd1.setImageResource(R.drawable.request_sent);
                                layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                                adapterUser.notifyDataSetChanged();
                                txtFollowStatus1.setText(R.string.unfollow);
                                txtFollowStatus1.setTextColor(R.color.descriptionColor);
                            }

                            if (user.equals("2")) {
                                arrayListUsers.get(1).setIs_friend("s");
                                arrayListUsers.get(1).setIs_follow("y");
                                imgAdd2.setImageResource(R.drawable.request_sent);
                                layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                                adapterUser.notifyDataSetChanged();
                                txtFollowStatus2.setText(R.string.unfollow);
                                txtFollowStatus2.setTextColor(R.color.descriptionColor);
                            }

                            if (user.equals("3")) {
                                arrayListUsers.get(2).setIs_friend("s");
                                arrayListUsers.get(2).setIs_follow("y");
                                imgAdd3.setImageResource(R.drawable.request_sent);
                                layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                                adapterUser.notifyDataSetChanged();
                                txtFollowStatus3.setText(R.string.unfollow);
                                txtFollowStatus3.setTextColor(R.color.descriptionColor);
                            }
                        } else if (request_action.equalsIgnoreCase("Unfriend")) {
                            if (user.equals("1")) {
                                arrayListUsers.get(0).setIs_friend("n");
                                imgAdd1.setImageResource(R.drawable.add_friend);
                                layoutAdd1.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                                adapterUser.notifyDataSetChanged();
                                txtFollowStatus1.setText(R.string.follow);
                                txtFollowStatus1.setTextColor(R.color.colorPrimary);
                            }

                            if (user.equals("2")) {
                                arrayListUsers.get(1).setIs_friend("n");
                                imgAdd2.setImageResource(R.drawable.add_friend);
                                layoutAdd2.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                                adapterUser.notifyDataSetChanged();
                                txtFollowStatus2.setText(R.string.follow);
                                txtFollowStatus2.setTextColor(R.color.colorPrimary);
                            }

                            if (user.equals("3")) {
                                arrayListUsers.get(2).setIs_friend("n");
                                imgAdd3.setImageResource(R.drawable.add_friend);
                                layoutAdd3.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_icon));
                                adapterUser.notifyDataSetChanged();
                                txtFollowStatus3.setText(R.string.follow);
                                txtFollowStatus3.setTextColor(R.color.colorPrimary);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH_CODE) {
                searchingList(strSearch, latitude, longitude, searchType, true);
            } else if (requestCode == SEARCH_FILTER_CODE) {
                layoutFilterDialog.setVisibility(View.GONE);
                if (isTypeUser.equals("y") && isTypePost.equals("n")) {
                    searchType = "user";
                    Log.e("SearchActivity", "searchType : " + searchType);
                    searchingList(strSearch, latitude, longitude, searchType, true);
                } else if (isTypePost.equals("y") && isTypeUser.equals("n")) {
                    searchType = "post";
                    Log.e("SearchActivity", "searchType : " + searchType);
                    searchingList(strSearch, latitude, longitude, searchType, true);
                } else if (isTypeUser.equals("y") && isTypePost.equals("y")) {
                    searchType = "both";
                    Log.e("SearchActivity", "searchType : " + searchType);
                    searchingList(strSearch, latitude, longitude, searchType, true);
                } else {
                    searchType = "both";
                    Log.e("SearchActivity", "searchType : " + searchType);
                    searchingList(strSearch, latitude, longitude, searchType, true);
                }
            }
        }
    }
}
