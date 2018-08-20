package com.application.wowwao1.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.wowwao1.Adapters.CommentsAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Fragments.FollowersPostFragment;
import com.application.wowwao1.Fragments.FriendsPostFragment;
import com.application.wowwao1.Fragments.MyProfileFragment;
import com.application.wowwao1.Fragments.NearByPostFragment;
import com.application.wowwao1.Models.CommentLIstItem;
import com.application.wowwao1.Models.CommentListPojo;
import com.application.wowwao1.Models.PostCommentPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.Utils.TimeZoneUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CommentsDialogActivity extends AppCompatActivity {

    private static final int COMMENT_LIST_CODE = 100;
    private static final int POST_COMMENT_CODE = 101;
    public static boolean isCommentRefresh = false;
    private Dialog dialog;
    private Context mContext;

    private TextView txtTotalComments;
    private EditText edtMessage;
    private ImageView imgSend, imgClose;

    private ArrayList<CommentLIstItem> arrayList = new ArrayList<>();
    private RecyclerView rvComments;
    private LinearLayoutManager layoutManager;
    private CommentsAdapter adapter;
    private Intent intent;


    private String userId, url, strComment, post_id, post_user_id, screenName;
    ;
    private ArrayList<String> params;
    private ArrayList<String> values;

    /*pagination vars start*/
    boolean loading = true;
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
        initViews();
        intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        post_user_id = intent.getStringExtra("post_user_id");
        screenName = intent.getStringExtra("screenName");
        Log.e("CommentLIstItem", "post_id : " + post_id);
        Log.e("CommentLIstItem", "post_user_id : " + post_user_id);
        Log.e("CommentLIstItem", "screenName : " + screenName);

        timezone = timeZoneUtils.getTimeZone();
        Log.e("Comments", "TimeZone : " + timezone);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(mContext);

        if (isInternetAvailable) {
            getCommentList(true, post_id, post_user_id);
        } else {
            intent = new Intent(mContext, NoConnectionActivity.class);
            startActivityForResult(intent, COMMENT_LIST_CODE);
        }


        rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                getCommentList(false, post_id, post_user_id);
                            }
                        }
                    }
                }
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strComment = edtMessage.getText().toString().trim();
                if (strComment.length() != 0) {
                    isInternetAvailable = cd.isNetworkConnected(mContext);
                    if (isInternetAvailable) {
                        postComment(post_id, strComment, post_user_id);
                    } else {
                        intent = new Intent(mContext, NoConnectionActivity.class);
                        startActivityForResult(intent, POST_COMMENT_CODE);
                    }
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                setFlag();
                CommentsDialogActivity.this.finish();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFlag();
                CommentsDialogActivity.this.finish();
            }
        });
    }

    private void initViews() {
        isCommentRefresh = false;
        mContext = CommentsDialogActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");

        dialog = new Dialog(mContext);
        try {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setContentView(R.layout.dialog_comments);

        txtTotalComments = (TextView) dialog.findViewById(R.id.txtTotalComments);
        edtMessage = (EditText) dialog.findViewById(R.id.edtMessage);
        imgSend = (ImageView) dialog.findViewById(R.id.imgSend);
        imgClose = (ImageView) dialog.findViewById(R.id.imgClose);

        rvComments = (RecyclerView) dialog.findViewById(R.id.rvComments);
        layoutManager = new LinearLayoutManager(mContext);
        rvComments.setLayoutManager(layoutManager);
        adapter = new CommentsAdapter(mContext, arrayList);
        rvComments.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getCommentList(boolean refreshFlag, final String post_id, final String post_user_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userCommentList");

        params.add("user_id");
        values.add(userId);

        params.add("post_id");
        values.add(post_id);

        params.add("page");
        values.add(String.valueOf(page));

        params.add("timezone");
        values.add(timezone);

        if (refreshFlag) {
            arrayList.clear();
            adapter.notifyDataSetChanged();
            page = 1;
        }

        new ParseJSON(mContext, url, params, values, CommentListPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommentListPojo resultObj = (CommentListPojo) obj;
                        total_records = resultObj.getTotalRecords();
                        adapter.totalRecordCounter(total_records);
                        txtTotalComments.setText(getString(R.string.comments_title)+ " (" + total_records + ")");
                        if (total_records > 0) {
                            arrayList.addAll(resultObj.getData());
                            adapter.setPostId(post_id, post_user_id);
                            adapter.notifyDataSetChanged();
                            loading = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void postComment(String post_id, String strComment, String post_user_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("addComment");

        params.add("user_id");
        values.add(userId);

        params.add("post_id");
        values.add(post_id);

        params.add("cmnt_text");
        values.add(strComment);

        params.add("post_user_id");
        values.add(post_user_id);

        params.add("timezone");
        values.add(timezone);

        new ParseJSON(mContext, url, params, values, PostCommentPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        PostCommentPojo resultObj = (PostCommentPojo) obj;

                        total_records += 1;
                        adapter.totalRecordCounter(total_records);
                        txtTotalComments.setText(getString(R.string.comments_title)+" (" + total_records + ")");

                        arrayList.add(resultObj.getData());
                        rvComments.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        edtMessage.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setFlag() {
        if (screenName.equalsIgnoreCase("NearByPostFragment")) {
            NearByPostFragment.pageRefresh = true;
        } else {
            NearByPostFragment.pageRefresh = false;
        }

        if (screenName.equalsIgnoreCase("FollowersPostFragment")) {
            FollowersPostFragment.pageRefresh = true;
        } else {
            FollowersPostFragment.pageRefresh = false;
        }

        if (screenName.equalsIgnoreCase("FriendsPostFragment")) {
            FriendsPostFragment.pageRefresh = true;
        } else {
            FriendsPostFragment.pageRefresh = false;
        }

        if (screenName.equalsIgnoreCase("UserProfileActivity")) {
            UserProfileActivity.pageRefresh = true;
        } else {
            UserProfileActivity.pageRefresh = false;
        }

        if (screenName.equalsIgnoreCase("MyProfileFragment")) {
            MyProfileFragment.pageRefresh = true;
        } else {
            MyProfileFragment.pageRefresh = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setFlag();
        finish();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == COMMENT_LIST_CODE) {
                getCommentList(true, post_id, post_user_id);
            } else if (requestCode == POST_COMMENT_CODE) {
                postComment(post_id, strComment, post_user_id);
            }
        }
    }

    public void totalRecordsCounter(int total_records) {
        txtTotalComments.setText("Comments (" + total_records + ")");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCommentRefresh) {
            cd = new ConnectionCheck();
            isInternetAvailable = cd.isNetworkConnected(mContext);

            if (isInternetAvailable) {
                getCommentList(true, post_id, post_user_id);
            } else {
                intent = new Intent(mContext, NoConnectionActivity.class);
                startActivityForResult(intent, COMMENT_LIST_CODE);
            }
        }
    }
}
