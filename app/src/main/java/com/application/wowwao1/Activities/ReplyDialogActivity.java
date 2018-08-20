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

import com.application.wowwao1.Adapters.RepliesAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.AddReplyPojo;
import com.application.wowwao1.Models.ReplyCommentListItem;
import com.application.wowwao1.Models.ReplyCommentListPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.Utils.TimeZoneUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReplyDialogActivity extends AppCompatActivity {

    private static final int REPLY_COMMENT_LIST_CODE = 100;
    private static final int POST_REPLY_CODE = 101;
    private Dialog dialog;

    private ArrayList<ReplyCommentListItem> arrayList = new ArrayList<>();
    private RecyclerView rvReplies;
    private LinearLayoutManager layoutManager;
    private RepliesAdapter adapter;

    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;

    /*pagination vars start*/
    boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    int page = 1;
    int total_records = 0;
    private String post_id, post_user_id, cmnt_id, cmnt_user_id;
    /*pagination vars end*/

    /*Dialog variable*/
    private ImageView imgBack;
    private TextView txtReplies;
    private EditText edtMessage;
    private ImageView imgSend;
    private String strReply;
    private Context mContext;
    private Intent intent;

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
        cmnt_id = intent.getStringExtra("cmnt_id");
        cmnt_user_id = intent.getStringExtra("cmnt_user_id");

        Log.e("Reply", "post_id : " + post_id);
        Log.e("Reply", "post_user_id : " + post_user_id);
        Log.e("Reply", "cmnt_id : " + cmnt_id);
        Log.e("Reply", "cmnt_user_id : " + cmnt_user_id);

        timezone = timeZoneUtils.getTimeZone();
        Log.e("Replies", "TimeZone : " + timezone);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            replyCommentsList(true, post_id, cmnt_id);
        } else {
            intent = new Intent(mContext, NoConnectionActivity.class);
            startActivityForResult(intent, REPLY_COMMENT_LIST_CODE);
        }


        rvReplies.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                replyCommentsList(false, post_id, cmnt_id);
                            }
                        }
                    }
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                CommentsDialogActivity.isCommentRefresh = true;
                ReplyDialogActivity.this.finish();
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strReply = edtMessage.getText().toString().trim();
                if (!strReply.equalsIgnoreCase("")) {
                    isInternetAvailable = cd.isNetworkConnected(mContext);
                    if (isInternetAvailable) {
                        replyComment(post_id, strReply, post_user_id, cmnt_user_id, cmnt_id);
                    } else {
                        intent = new Intent(mContext, NoConnectionActivity.class);
                        startActivityForResult(intent, POST_REPLY_CODE);
                    }
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ReplyDialogActivity.this.finish();
                CommentsDialogActivity.isCommentRefresh = true;
            }
        });
    }

    private void initViews() {
        mContext = ReplyDialogActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");

        dialog = new Dialog(mContext);
        try {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setContentView(R.layout.dialog_replies);
        imgBack = (ImageView) dialog.findViewById(R.id.imgBack);
        txtReplies = (TextView) dialog.findViewById(R.id.txtReplies);
        edtMessage = (EditText) dialog.findViewById(R.id.edtMessage);
        imgSend = (ImageView) dialog.findViewById(R.id.imgSend);

        rvReplies = (RecyclerView) dialog.findViewById(R.id.rvReplies);
        layoutManager = new LinearLayoutManager(mContext);
        rvReplies.setLayoutManager(layoutManager);
        adapter = new RepliesAdapter(mContext, arrayList);
        rvReplies.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void replyCommentsList(boolean refreshFlag, String post_id, String cmnt_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userReplyList");

        params.add("user_id");
        values.add(userId);

        params.add("post_id");
        values.add(post_id);

        params.add("cmnt_id");
        values.add(cmnt_id);

        params.add("page");
        values.add(String.valueOf(page));

        params.add("timezone");
        values.add(timezone);

        if (refreshFlag) {
            arrayList.clear();
            adapter.notifyDataSetChanged();
            page = 1;
        }

        new ParseJSON(mContext, url, params, values, ReplyCommentListPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        ReplyCommentListPojo resultObj = (ReplyCommentListPojo) obj;
                        total_records = resultObj.getTotalRecords();
                        adapter.totalRecordCounter(total_records);
                        txtReplies.setText(getString(R.string.Replies_title) + " (" + total_records + ")");
                        if (total_records > 0) {
                            arrayList.addAll(resultObj.getData());
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

    private void replyComment(String post_id, String strReply, String post_user_id, String cmnt_user_id, String cmnt_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("addReply");

        params.add("user_id");
        values.add(userId);

        params.add("post_id");
        values.add(post_id);

        params.add("cmnt_id");
        values.add(cmnt_id);

        params.add("post_user_id");
        values.add(post_user_id);

        params.add("cmnt_user_id");
        values.add(cmnt_user_id);

        params.add("reply_text");
        values.add(strReply);

        params.add("timezone");
        values.add(timezone);

        new ParseJSON(mContext, url, params, values, AddReplyPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        AddReplyPojo resultObj = (AddReplyPojo) obj;
                        arrayList.add(resultObj.getData());
                        rvReplies.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        edtMessage.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        CommentsDialogActivity.isCommentRefresh = true;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REPLY_COMMENT_LIST_CODE) {
                replyCommentsList(true, post_id, cmnt_id);
            } else if (requestCode == POST_REPLY_CODE) {
                replyComment(post_id, strReply, post_user_id, cmnt_user_id, cmnt_id);
            }
        }
    }

    public void totalRecordsCounter(int total_records) {
        txtReplies.setText(getString(R.string.Replies_title) + " (" + total_records + ")");
    }
}
