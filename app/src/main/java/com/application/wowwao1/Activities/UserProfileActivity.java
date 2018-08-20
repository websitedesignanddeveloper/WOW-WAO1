package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Adapters.HomeAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.FeedsItem;
import com.application.wowwao1.Models.FeedsPojo;
import com.application.wowwao1.Models.MyProfilePojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.Utils.TimeZoneUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PROFILE_DETAILS_CODE = 100;
    private static final int FRIEND_REQ_CODE = 101;
    private static final int BLOCK_USER_CODE = 102;
    public static boolean pageRefresh = false;
    private Toolbar toolbar;
    private TextView title_text, txtUsername, txtEmail, txtLocation, txtDate, txtAge, txtGender, txtPhone, txtFollowers, txtFollowStatus;
    private String strUsername, strEmail, strLocation, strDate, strAge, strGender, strPhone, strFollowers,
            profileImg, coverImg, countryCode, is_friend, is_follow;
    private ImageView imgGender, imgCover, imgMsg, imgAddFriend;
    private LinearLayout layoutMsg, layoutAddFriend, layoutNoData;
    private CircleImageView imgProfile;

    private Intent intent;
    private Context mContext;
    private RecyclerView rvUserPost;
    private LinearLayoutManager layoutManager;
    private HomeAdapter adapter;
    private ArrayList<FeedsItem> arrayList = new ArrayList<>();
    private NestedScrollView nestedUserProfile;

    private ArrayList<String> params;
    private ArrayList<String> values;
    private String url, userId, receiver_id;

    /*pagination vars start*/
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    int page = 1;
    int total_records = 0;
    /*pagination vars end*/

    private boolean isInternetAvailable;
    private ConnectionCheck cd;

    /* timezone */
    private String timezone = "";
    private TimeZoneUtils timeZoneUtils = new TimeZoneUtils();
    /* ******** */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initViews();

        timezone = timeZoneUtils.getTimeZone();
        Log.e("UserProfile", "TimeZone : " + timezone);

        nestedUserProfile.post(new Runnable() {
            @Override
            public void run() {
                nestedUserProfile.scrollTo(0, 0);
            }
        });
        intent = getIntent();
        receiver_id = intent.getStringExtra("receiver_id");
        Log.e("UserProfile", "receiver_id :" + receiver_id);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            userDetails(receiver_id);
            userPost(true, receiver_id);
        } else {
            intent = new Intent(mContext, NoConnectionActivity.class);
            startActivityForResult(intent, PROFILE_DETAILS_CODE);
        }

        if (userId.equals(receiver_id)) {
            layoutAddFriend.setVisibility(View.GONE);
            layoutMsg.setVisibility(View.GONE);
        } else {
            layoutAddFriend.setVisibility(View.VISIBLE);
            layoutMsg.setVisibility(View.VISIBLE);
        }

        rvUserPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            if (arrayList.size() < total_records) {
                                page++;
                                userPost(false, receiver_id);
                            }
                        }
                    }
                }
            }
        });

        layoutAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    if (is_friend.equalsIgnoreCase("y")) {
                        friendRequest(receiver_id, "Unfriend");
                    } else if (is_friend.equalsIgnoreCase("n")) {
                        friendRequest(receiver_id, "Add");
                    }
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, FRIEND_REQ_CODE);
                }
            }
        });

        layoutMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationActivity.newMsg = false;
                Intent intent = new Intent(mContext, ConversationActivity.class);
                intent.putExtra("username", strUsername);
                intent.putExtra("receiverImg", profileImg);
                intent.putExtra("receiver_id", receiver_id);
                startActivity(intent);
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewProfilePicActivity.isProfile = true;
                intent = new Intent(mContext, ViewProfilePicActivity.class);
                intent.putExtra("img_path", profileImg);
                startActivity(intent);
            }
        });

        imgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewProfilePicActivity.isProfile = false;
                intent = new Intent(mContext, ViewProfilePicActivity.class);
                intent.putExtra("img_path", coverImg);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        pageRefresh = false;
        mContext = UserProfileActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");
        setupToolbar();
        layoutMsg = (LinearLayout) findViewById(R.id.layoutMsg);
        layoutAddFriend = (LinearLayout) findViewById(R.id.layoutAddFriend);
        layoutMsg = (LinearLayout) findViewById(R.id.layoutMsg);
        layoutNoData = (LinearLayout) findViewById(R.id.layoutNoData);

        imgGender = (ImageView) findViewById(R.id.imgGender);
        imgCover = (ImageView) findViewById(R.id.imgCover);
        imgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        imgMsg = (ImageView) findViewById(R.id.imgMsg);
        imgAddFriend = (ImageView) findViewById(R.id.imgAddFriend);

        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtAge = (TextView) findViewById(R.id.txtAge);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtFollowers = (TextView) findViewById(R.id.txtFollowers);
        txtFollowStatus = (TextView) findViewById(R.id.txtFollowStatus);


        nestedUserProfile = (NestedScrollView) findViewById(R.id.nestedUserProfile);
        rvUserPost = (RecyclerView) findViewById(R.id.rvUserPost);
        layoutManager = new LinearLayoutManager(mContext);
        rvUserPost.setLayoutManager(layoutManager);
        rvUserPost.setHasFixedSize(false);
        rvUserPost.setNestedScrollingEnabled(false);
        adapter = new HomeAdapter(UserProfileActivity.this, arrayList);
        rvUserPost.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title_text = (TextView) findViewById(R.id.title_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
    }

    private void userDetails(String receiver_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userProfile");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        new ParseJSON(mContext, url, params, values, MyProfilePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        MyProfilePojo resultObj = (MyProfilePojo) obj;
                        strUsername = resultObj.getData().getFirstName() + " " + resultObj.getData().getLastName();
                        strEmail = resultObj.getData().getEmail();
                        strEmail = resultObj.getData().getEmail();
                        strGender = resultObj.getData().getGender();
                        strDate = resultObj.getData().getDob();
                        strPhone = resultObj.getData().getPhoneNo();
                        profileImg = resultObj.getData().getProfileImg();
                        coverImg = resultObj.getData().getCoverImg();
                        countryCode = resultObj.getData().getPhone_code();
                        strAge = resultObj.getData().getAge();
                        is_friend = resultObj.getData().getIsFriend();
                        is_follow = resultObj.getData().getIsFollow();
                        strFollowers = resultObj.getData().getTotal_follower();

                        title_text.setText(strUsername);

                        Picasso.with(mContext)
                                .load(profileImg)
                                .error(R.drawable.no_user)
                                .placeholder(R.drawable.no_user)
                                .into(imgProfile);

                        Picasso.with(mContext)
                                .load(coverImg)
                                .into(imgCover);

                        if (!strEmail.equals("")) {
                            txtEmail.setText(strEmail);
                        } else {
                            txtEmail.setText(getResources().getString(R.string.not_available));
                        }

                        txtUsername.setText(strUsername);

                        if (strGender.equalsIgnoreCase(getString(R.string.edit_male))) {
                            imgGender.setImageResource(R.drawable.male);
                            txtGender.setText(strGender);
                        } else if (strGender.equalsIgnoreCase(getString(R.string.edit_female))) {
                            imgGender.setImageResource(R.drawable.female);
                            txtGender.setText(strGender);
                        } else {
                            txtGender.setText("Gender");
                        }

                        if (resultObj.getData().getCity().equals("") && resultObj.getData().getState().equals("") && resultObj.getData().getCountry().equals("")) {
                            txtLocation.setText(R.string.location);
                        } else {
                            txtLocation.setText(resultObj.getData().getCity() + ",  " + resultObj.getData().getState() + ", " + resultObj.getData().getCountry());
                        }

                        if (!strPhone.equalsIgnoreCase("")) {
                            txtPhone.setText(countryCode + "" + strPhone);
                        } else {
                            txtPhone.setText(R.string.contact_no);
                        }

                        if (!strDate.equalsIgnoreCase("")) {
                            txtDate.setText(strDate);
                        } else {
                            txtDate.setText(R.string.dob);
                        }

                        if (!strAge.equalsIgnoreCase("")) {
                            txtAge.setText(strAge + " " + getString(R.string.age));
                        } else {
                            txtAge.setText("Age");
                        }

                        if (!strFollowers.equalsIgnoreCase("")) {
                            txtFollowers.setText(strFollowers);
                        }

                        if (is_friend.equalsIgnoreCase("y")) {
                            imgAddFriend.setImageResource(R.drawable.remove_friend);
                            layoutAddFriend.setBackground(getResources().getDrawable(R.drawable.red_circle_with_white_border));
                        } else if (is_friend.equalsIgnoreCase("s")) {
                            imgAddFriend.setImageResource(R.drawable.request_sent);
                            layoutAddFriend.setBackground(getResources().getDrawable(R.drawable.blue_circle_with_white_border));
                        } else if (is_friend.equalsIgnoreCase("r")) {
                            imgAddFriend.setImageResource(R.drawable.request_received);
                            layoutAddFriend.setBackground(getResources().getDrawable(R.drawable.blue_circle_with_white_border));
                        } else if (is_friend.equalsIgnoreCase("n")) {
                            imgAddFriend.setImageResource(R.drawable.add_friend);
                            layoutAddFriend.setBackground(getResources().getDrawable(R.drawable.blue_circle_with_white_border));
                        }

                        if (is_follow.equalsIgnoreCase("y")) {
                            txtFollowStatus.setVisibility(View.VISIBLE);
                            txtFollowStatus.setText(getString(R.string.user_profile_following_status) + " " + strUsername);
                        } else {
                            txtFollowStatus.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void userPost(boolean refreshFlag, String receiver_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userPost");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        params.add("timezone");
        values.add(timezone);

        params.add("page");
        values.add(String.valueOf(page));

        if (refreshFlag) {
            arrayList.clear();
            adapter.notifyDataSetChanged();
            page = 1;
        }

        new ParseJSON(mContext, url, params, values, FeedsPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        FeedsPojo resultObj = (FeedsPojo) obj;
                        total_records = resultObj.getTotalRecords();
                        if (total_records > 0) {
                            rvUserPost.setVisibility(View.VISIBLE);
                            layoutNoData.setVisibility(View.GONE);
                            arrayList.addAll(resultObj.getData());
                            adapter.setFromFragment("UserProfileActivity");
                            adapter.notifyDataSetChanged();
                            loading = true;
                        } else {
                            rvUserPost.setVisibility(View.GONE);
                            layoutNoData.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    rvUserPost.setVisibility(View.GONE);
                    layoutNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void blockUser(String userId, String receiver_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userBlock");

        params.add("block_action");
        values.add("Block");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void friendRequest(String receiver_id, final String request_action) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userRequest");

        params.add("request_action");
        values.add(request_action);

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        if (request_action.equalsIgnoreCase("Add")) {
                            is_friend = "s";
                            is_follow = "y";
                            txtFollowStatus.setText(getString(R.string.you_are_following) + strUsername);
                            imgAddFriend.setImageResource(R.drawable.request_sent);
                            layoutAddFriend.setBackground(getResources().getDrawable(R.drawable.blue_circle_with_white_border));
                        } else if (request_action.equalsIgnoreCase("Unfriend")) {
                            is_friend = "n";
                            imgAddFriend.setImageResource(R.drawable.add_friend);
                            layoutAddFriend.setBackground(getResources().getDrawable(R.drawable.blue_circle_with_white_border));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!userId.equals(receiver_id)) {
            getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_block:
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    blockUser(userId, receiver_id);
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, BLOCK_USER_CODE);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PROFILE_DETAILS_CODE) {
                userDetails(receiver_id);
                userPost(true, receiver_id);
            } else if (requestCode == FRIEND_REQ_CODE) {
                if (is_friend.equals("y")) {
                    friendRequest(receiver_id, "Unfriend");
                } else {
                    friendRequest(receiver_id, "Add");
                }
            } else if (requestCode == BLOCK_USER_CODE) {
                blockUser(userId, receiver_id);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pageRefresh) {
            cd = new ConnectionCheck();
            isInternetAvailable = cd.isNetworkConnected(mContext);
            if (isInternetAvailable) {
                userPost(true, receiver_id);
            } else {
                intent = new Intent(mContext, NoConnectionActivity.class);
                startActivityForResult(intent, PROFILE_DETAILS_CODE);
            }
        }
    }
}
