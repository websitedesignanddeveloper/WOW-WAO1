package com.application.wowwao1.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Adapters.ComposeMsgAdapter;
import com.application.wowwao1.Adapters.ConversationAdapter;
import com.application.wowwao1.AsyncTask.ImageUploadParseJSON;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Fragments.MessagesFragment;
import com.application.wowwao1.Models.ComposeMsgPojo;
import com.application.wowwao1.Models.ConversationItem;
import com.application.wowwao1.Models.ConversationPojo;
import com.application.wowwao1.Models.SendMessagePojo;
import com.application.wowwao1.Models.UserData;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.Utils.TimeZoneUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConversationActivity extends AppCompatActivity {


    private static final int SEND_MSG_CODE = 100;
    private static final int CONTACT_LIST_CODE = 101;
    private static final int MSG_LIST_CODE = 102;
    public static boolean newMsg = false;
    private Context mContext;
    private Intent intent;
    private String strUsername, strContact;
    private LinearLayout layoutAttachment, layoutSearch;
    private ImageView imgToolBack, imgSend;
    private CircleImageView imgToolProfile;
    private EditText edtMessage, edtContact;
    private TextView txtToolTitle;

    private RecyclerView rvConversation, rvSuggestion;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager layoutManager;
    private ArrayList<ConversationItem> arrayList = new ArrayList<>();
    private ArrayList<UserData> arrayListSuggestion = new ArrayList<>();
    private ConversationAdapter adapter;
    private ComposeMsgAdapter adapterSuggestion;

    private String url, userId, receiverImg, senderImg, receiver_id, strMessage;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private ArrayList<String> fileParams;
    private ArrayList<File> fileValues;

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

    private BottomSheetDialog attachmentDialog;
    private ImageView imgGallary, imgDocuments;
    private TextView txtPhotos, txtDocuments;

    /*Image picker variable*/
    private Uri mCropImageUri;
    private int aspectRationX = 1;
    private int aspectRationY = 1;

    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        strUsername = intent.getStringExtra("username");
        receiverImg = intent.getStringExtra("receiverImg");
        receiver_id = intent.getStringExtra("receiver_id");

        typeface = Typeface.createFromAsset(getAssets(),"fonts/frieght_sans.otf");

        Log.e("Conversation", "username : " + strUsername);
        Log.e("Conversation", "receiverImg : " + receiverImg);
        Log.e("Conversation", "receiver_id : " + receiver_id);

        setContentView(R.layout.activity_conversation);
        initViews();

        timezone = timeZoneUtils.getTimeZone();
        Log.e("ConversationActivity", "TimeZone : " + timezone);

        cd = new ConnectionCheck();

        if (newMsg) {
            layoutSearch.setVisibility(View.VISIBLE);
            rvSuggestion.setVisibility(View.VISIBLE);
            rvConversation.setVisibility(View.GONE);
            imgToolProfile.setVisibility(View.GONE);
            txtToolTitle.setText("New Message");

            edtContact.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    strContact = s.toString().trim();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // service call for contact suggestion
                            isInternetAvailable = cd.isNetworkConnected(mContext);
                            if (isInternetAvailable) {
                                searchFriend(strContact);
                            } else {
                                intent = new Intent(mContext, NoConnectionActivity.class);
                                startActivityForResult(intent, CONTACT_LIST_CODE);
                            }
                        }
                    }, 2000);
                }
            });
        } else {
            layoutSearch.setVisibility(View.GONE);
            rvSuggestion.setVisibility(View.GONE);
            rvConversation.setVisibility(View.VISIBLE);
            imgToolProfile.setVisibility(View.VISIBLE);

            Picasso.with(mContext)
                    .load(receiverImg)
                    .error(R.drawable.no_user)
                    .placeholder(R.drawable.no_user)
                    .into(imgToolProfile);

            txtToolTitle.setText(strUsername);

            isInternetAvailable = cd.isNetworkConnected(mContext);
            if (isInternetAvailable) {
                getMessageList(true, receiver_id);
            } else {
                intent = new Intent(mContext, NoConnectionActivity.class);
                startActivityForResult(intent, MSG_LIST_CODE);
            }
        }

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strMessage = edtMessage.getText().toString().trim();
                if (strMessage.length() != 0) {
                    isInternetAvailable = cd.isNetworkConnected(mContext);
                    if (isInternetAvailable) {
                        sendMessage(receiver_id, strMessage);
                    } else {
                        intent = new Intent(mContext, NoConnectionActivity.class);
                        startActivityForResult(intent, SEND_MSG_CODE);
                    }
                }
            }
        });

        layoutAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentDialog();
            }
        });

       /* rvConversation.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                getMessageList(false, receiver_id);
                            }
                        }
                    }
                }
            }
        });*/
    }

    //for compose msg
    private void searchFriend(String strContact) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userComposeSearch");

        params.add("user_id");
        values.add(userId);

        params.add("keyword");
        values.add(strContact);

        params.add("page");
        values.add("1");


        new ParseJSON(mContext, url, params, values, ComposeMsgPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        ComposeMsgPojo resultObj = (ComposeMsgPojo) obj;
                        total_records = resultObj.getTotalRecords();
                        if (total_records > 0) {
                            arrayListSuggestion.clear();
                            arrayListSuggestion.addAll(resultObj.getData());
                            adapterSuggestion.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    arrayList.clear();
                }
            }
        });
    }

    private void initViews() {
        mContext = ConversationActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");
        senderImg = PrefsUtil.with(mContext).readString("profile_img");
        setupToolbar();

        layoutAttachment = (LinearLayout) findViewById(R.id.layoutAttachment);
        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);

        imgSend = (ImageView) findViewById(R.id.imgSend);

        edtMessage = (EditText) findViewById(R.id.edtMessage);

        edtContact = (EditText) findViewById(R.id.edtContact);
        edtMessage.setTypeface(typeface);
        edtContact.setTypeface(typeface);

        rvSuggestion = (RecyclerView) findViewById(R.id.rvSuggestion);
        gridLayoutManager = new GridLayoutManager(mContext, 2);
        rvSuggestion.setLayoutManager(gridLayoutManager);
        adapterSuggestion = new ComposeMsgAdapter(mContext, arrayListSuggestion);
        rvSuggestion.setAdapter(adapterSuggestion);
        adapterSuggestion.notifyDataSetChanged();

        rvConversation = (RecyclerView) findViewById(R.id.rvConversation);
        layoutManager = new LinearLayoutManager(mContext);
        rvConversation.setLayoutManager(layoutManager);
        adapter = new ConversationAdapter(mContext, arrayList);
        rvConversation.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        /* attachment dialog varialble */
        attachmentDialog = new BottomSheetDialog(mContext);
        try {
            attachmentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        attachmentDialog.setContentView(R.layout.dialog_msg_attachment);

        imgGallary = (ImageView) attachmentDialog.findViewById(R.id.imgGallary);
        imgDocuments = (ImageView) attachmentDialog.findViewById(R.id.imgDocuments);

        txtPhotos = (TextView) attachmentDialog.findViewById(R.id.txtPhotos);
        txtDocuments = (TextView) attachmentDialog.findViewById(R.id.txtDocuments);
        /* *************************** */
    }

    private void setupToolbar() {
        imgToolBack = (ImageView) findViewById(R.id.imgToolBack);
        txtToolTitle = (TextView) findViewById(R.id.txtToolTitle);
        txtToolTitle.setTypeface(typeface);
        imgToolProfile = (CircleImageView) findViewById(R.id.imgToolProfile);

        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagesFragment.refreshInResume = true;
                finish();
            }
        });

        imgToolProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("receiver_id", receiver_id);
                startActivity(intent);
            }
        });
    }

    private void getMessageList(boolean refreshFlag, String receiver_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userMessageList");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        params.add("timezone");
        values.add(timezone);

        params.add("page");
        values.add(String.valueOf(page));

        /*if (refreshFlag) {
            arrayList.clear();
            adapter.notifyDataSetChanged();
            page = 1;
        }*/

        new ParseJSON(mContext, url, params, values, ConversationPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        ConversationPojo resultObj = (ConversationPojo) obj;
                        total_records = resultObj.getTotalRecords();
                        if (total_records > 0) {
                            arrayList.addAll(resultObj.getData());
                            adapter.setImages(senderImg, receiverImg);
                            if (adapter.getItemCount() > 0) {
                                rvConversation.smoothScrollToPosition(adapter.getItemCount() - 1);
                            }
                            adapter.notifyDataSetChanged();
                            loading = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    private void sendMessage(String receiver_id, String message) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userSendMessage");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        params.add("timezone");
        values.add(timezone);

        params.add("message");
        values.add(message);

        new ParseJSON(mContext, url, params, values, SendMessagePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        edtMessage.setText("");
                        SendMessagePojo resultObj = (SendMessagePojo) obj;
                        arrayList.add(resultObj.getData());
                        adapter.setImages(senderImg, receiverImg);
                        if (adapter.getItemCount() > 0) {
                            rvConversation.smoothScrollToPosition(adapter.getItemCount() - 1);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendAttachment(String receiver_id, File file) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        fileParams = new ArrayList<>();
        fileValues = new ArrayList<>();

        params.add("action");
        values.add("userSendMessage");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        /*params.add("message");
        values.add("");*/

        fileParams.add("msg_attachment");
        fileValues.add(file);

        params.add("timezone");
        values.add(timezone);

        new ImageUploadParseJSON(mContext, url, params, values, fileParams, fileValues, SendMessagePojo.class, new ImageUploadParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        edtMessage.setText("");
                        SendMessagePojo resultObj = (SendMessagePojo) obj;
                        arrayList.add(resultObj.getData());
                        adapter.setImages(senderImg, receiverImg);
                        if (adapter.getItemCount() > 0) {
                            rvConversation.smoothScrollToPosition(adapter.getItemCount() - 1);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MessagesFragment.refreshInResume = true;
    }

    public void setUserDataInToolbar(String receiver_id, String username, String profile_img) {

        if (receiver_id.length() != 0 && username.length() != 0 && profile_img.length() != 0) {
            this.receiver_id = receiver_id;
            strUsername = username;
            receiverImg = profile_img;

            layoutSearch.setVisibility(View.GONE);
            rvSuggestion.setVisibility(View.GONE);
            rvConversation.setVisibility(View.VISIBLE);
            imgToolProfile.setVisibility(View.VISIBLE);

            Picasso.with(mContext)
                    .load(receiverImg)
                    .error(R.drawable.no_user)
                    .placeholder(R.drawable.no_user)
                    .into(imgToolProfile);

            txtToolTitle.setText(strUsername);
        }
    }

    private void attachmentDialog() {
        imgGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentDialog.dismiss();
                if (CropImage.isExplicitCameraPermissionRequired(mContext)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    CropImage.startPickImageActivity(ConversationActivity.this);
                }
            }
        });

        imgDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentDialog.dismiss();
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = new String[]{"jpg", "png", "pdf", "doc", "txt", "mp3", "mp4","3gp"};
                FilePickerDialog dialog = new FilePickerDialog(mContext, properties);
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        try {
                            String name = files[0].substring(files[0].lastIndexOf("/") + 1);
                            //txtFileName.setText(name);
                            File file = new File(files[0]);
                            Log.e("WorkroomActivity", "AttachmentPath :" + file);

                            sendAttachment(receiver_id, file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
            }
        });

        attachmentDialog.show();
        attachmentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, R.string.cancel_permissions, Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*mCurrentFragment.setImageUri(mCropImageUri);*/
                CropImage.activity(mCropImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(aspectRationX, aspectRationY)
                        .start(this);
            } else {
                Toast.makeText(this, R.string.cancel_permissions, Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                /*mCurrentFragment.setImageUri(imageUri);*/
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(aspectRationX, aspectRationY)
                        .start(this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.e("RESULT URI IS : ", result.getUri().toString());
                String img_path = result.getUri().toString();
                File file = new File(img_path.replace("file://", ""));
                Log.e("Conversation", "AttachmentPath :" + file);

                long file_size = Long.parseLong(String.valueOf(file.length() / 1024));
                Log.e("Conversation", "File size :" + file_size);

                if (file_size <= 25600) {
                    sendAttachment(receiver_id, file);
                } else {
                    Toast.makeText(mContext, R.string.max_file_size, Toast.LENGTH_LONG).show();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == SEND_MSG_CODE) {
                sendMessage(receiver_id, strMessage);
            } else if (requestCode == CONTACT_LIST_CODE) {
                searchFriend(strContact);
            } else if (requestCode == MSG_LIST_CODE) {
                getMessageList(true, receiver_id);
            }
        }
    }
}
