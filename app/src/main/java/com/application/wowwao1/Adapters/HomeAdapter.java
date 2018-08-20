package com.application.wowwao1.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Activities.AddNewPostActivity;
import com.application.wowwao1.Activities.CommentsDialogActivity;
import com.application.wowwao1.Activities.HomeActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Activities.SharePostActivity;
import com.application.wowwao1.Activities.UserProfileActivity;
import com.application.wowwao1.Application.ApplicationController;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.FeedsImageItem;
import com.application.wowwao1.Models.FeedsItem;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.application.wowwao1.viewpagerindicator.MyPagerAdapter;
import com.application.wowwao1.viewpagerindicator.ViewPagerIndicator;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.View_Holder> {

    private static final int ADAPTER_HOME_CODE = 1000;
    private ArrayList<FeedsItem> arrayList;
    private Activity context;

    private Intent intent;
    private String userId, url;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private ArrayList<FeedsImageItem> arrayListImages = new ArrayList<>();

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private String screenName = "";
    private int imgHeight;

    public HomeAdapter(Activity context, ArrayList<FeedsItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

        userId = PrefsUtil.with(context).readString("userId");
        cd = new ConnectionCheck();
        imgHeight = (((int) (getDispWidth() - convertDpToPixel(32f, context))) / 4) * 3;
        Log.e("adapter_home", "imgHeight -------------------> " + imgHeight);
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        final FeedsItem items = arrayList.get(position);

        Log.e("HomeAdapter", "Emoji : " + StringEscapeUtils.unescapeJava(items.getSharePostText()));

        /*holder.imgPost.getLayoutParams().height = imgHeight;
        holder.img1.getLayoutParams().height = imgHeight;
        holder.img3.getLayoutParams().height = imgHeight;
        holder.layoutMultiImage.getLayoutParams().height = imgHeight;*/

        if (!items.getSharePostId().equals("")) {
            holder.layoutMainPost.setVisibility(View.GONE);
            holder.layoutMainSharedPost.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(items.getUserData().getProfileImg())
                    .error(R.drawable.no_user)
                    .placeholder(R.drawable.no_user)
                    .into(holder.imgSharedProfile);

            Picasso.with(context)
                    .load(items.getShareUserData().getProfileImg())
                    .error(R.drawable.no_user)
                    .placeholder(R.drawable.no_user)
                    .into(holder.imgSharedProfile1);

            holder.txtSharedUsername.setText(items.getUserData().getFirstName() + " " + items.getUserData().getLastName() + " Shared " +
                    items.getShareUserData().getFirstName() + " " + items.getShareUserData().getLastName() + "\'s Post");
            holder.txtSharedUsername1.setText(items.getShareUserData().getFirstName() + " " + items.getShareUserData().getLastName());


            holder.txtSharedDate.setText(items.getPostDate());
            holder.txtSharedDate1.setText(items.getShare_post_date());

            holder.txtShared_diff.setText(items.getDiff());

            String postType = items.getPostType();
            if (postType.equalsIgnoreCase("Public")) {
                holder.imgSharedPostType.setImageResource(R.drawable.public_2);
            } else if (postType.equalsIgnoreCase("Private")) {
                holder.imgSharedPostType.setImageResource(R.drawable.private_2);
            }

            String isLiked = items.getIsLiked();
            if (isLiked.equalsIgnoreCase("y")) {
                holder.imgSharedLike.setImageResource(R.drawable.ic_favorite);
            } else {
                holder.imgSharedLike.setImageResource(R.drawable.ic_unfavorite);
            }

            if (!items.getSharePostText().equalsIgnoreCase("")) {
                holder.txtSharedDescription.setVisibility(View.VISIBLE);
                holder.txtSharedDescription.setText(StringEscapeUtils.unescapeJava(items.getSharePostText()));
            } else {
                holder.txtSharedDescription.setVisibility(View.GONE);
            }

            if (!items.getPostText().equalsIgnoreCase("")) {
                holder.txtSharedDescription1.setVisibility(View.VISIBLE);
                //holder.txtSharedDescription1.setText(items.getPostText());
                holder.txtSharedDescription1.setText(StringEscapeUtils.unescapeJava(items.getPostText()));
            } else {
                holder.txtSharedDescription1.setVisibility(View.GONE);
            }

            holder.txtSharedTotalLikes.setText(items.getTotalLike());
            //holder.txtSharedTotalComments.setText(items.getTotalComment());
            holder.txtSharedTotalComments.setText(StringEscapeUtils.unescapeJava(items.getTotalComment()));


            //displaySharedImage(items, holder, position);
            sharedImageViewPager(items, holder);

            holder.layoutSharedLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isInternetAvailable = cd.isNetworkConnected(context);
                    if (isInternetAvailable) {
                        if (arrayList.get(position).getIsLiked().equals("y")) {
                            unLikePost(userId, arrayList.get(position).getId(), "Unlike", holder, position, arrayList.get(position).getTotalLike());
                        } else {
                            likePost(userId, arrayList.get(position).getId(), "Like", holder, position, arrayList.get(position).getTotalLike());
                        }
                    } else {
                        intent = new Intent(context, NoConnectionActivity.class);
                        ((HomeActivity) context).startActivityForResult(intent, ADAPTER_HOME_CODE);
                    }

                }
            });

            holder.layoutSharedComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(context, CommentsDialogActivity.class);
                    intent.putExtra("post_id", arrayList.get(position).getId());
                    intent.putExtra("post_user_id", arrayList.get(position).getUserData().getId());
                    intent.putExtra("screenName", screenName);
                    context.startActivity(intent);
                }
            });

            /*holder.layoutSharedViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayListImages.clear();
                    arrayListImages.addAll(arrayList.get(position).getImage());
                    ((ApplicationController) context.getApplicationContext()).arrayList = arrayListImages;
                    intent = new Intent(context, DisplayPhotosActivity.class);
                    context.startActivity(intent);
                }
            });*/

            holder.imgSharedShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String receiver_id = items.getUserData().getId();
                    isInternetAvailable = cd.isNetworkConnected(context);
                    if (isInternetAvailable) {
                        showMoreOptionDialog(position, holder, receiver_id, items.getId(), arrayList, arrayList.get(position).getIs_reported(), holder.imgSharedShowMore);
                    } else {
                        intent = new Intent(context, NoConnectionActivity.class);
                        ((HomeActivity) context).startActivityForResult(intent, ADAPTER_HOME_CODE);
                    }

                }
            });

            holder.imgSharedProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("receiver_id", arrayList.get(position).getUserData().getId());
                    context.startActivity(intent);
                }
            });

            holder.imgSharedProfile1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("receiver_id", arrayList.get(position).getShareUserData().getId());
                    context.startActivity(intent);
                }
            });

            holder.imgSharePostShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharePostActivity.editSharedPost = false;
                    arrayListImages.clear();
                    arrayListImages.addAll(arrayList.get(position).getImage());
                    ((ApplicationController) context.getApplicationContext()).arrayList = arrayListImages;
                    intent = new Intent(context, SharePostActivity.class);
                    intent.putExtra("post_id", items.getId());
                    intent.putExtra("profileImg", items.getUserData().getProfileImg());
                    intent.putExtra("username", items.getUserData().getFirstName() + " " + items.getUserData().getLastName());
                    intent.putExtra("post_date", items.getPostDate());
                    intent.putExtra("post_time", items.getPostTime());
                    intent.putExtra("post_text", items.getPostText());
                    intent.putExtra("shared_post_text", items.getSharePostText());
                    intent.putExtra("post_type", items.getPostType());
                    context.startActivity(intent);
                }
            });

        } else {

            holder.layoutMainPost.setVisibility(View.VISIBLE);
            holder.layoutMainSharedPost.setVisibility(View.GONE);

           /* if (userId.equals(items.getUserData().getId())) {
                holder.imgShare.setVisibility(View.GONE);
            } else {
                holder.imgShare.setVisibility(View.VISIBLE);
            }*/

            Picasso.with(context)
                    .load(items.getUserData().getProfileImg())
                    .error(R.drawable.no_user)
                    .placeholder(R.drawable.no_user)
                    .into(holder.imgProfile);

            holder.txtUsername.setText(items.getUserData().getFirstName() + " " + items.getUserData().getLastName());
            holder.txtDate.setText(items.getPostDate());
            String postType = items.getPostType();
            Log.e("HomeAdapter", "postType----->" + postType);
            if (postType.equalsIgnoreCase("Public")) {
                Log.e("HomeAdapter", "Public : " + postType);
                holder.imgPostType.setImageResource(R.drawable.public_2);
                //holder.txtDiff.setText(items.getDiff());
               // holder.txt_diff2.setText(items.getDiff());


                holder.txtDiff.setText(items.getDiff());



            } else if (postType.equalsIgnoreCase("Private")) {
                Log.e("HomeAdapter", "Private : " + postType);
                holder.imgPostType.setImageResource(R.drawable.private_2);
                holder.txtDiff.setText("");
                //holder.txt_diff2.setText("");
            }

            String isLiked = items.getIsLiked();
            if (isLiked.equalsIgnoreCase("y")) {
                holder.imgLike.setImageResource(R.drawable.ic_favorite);
            } else {
                holder.imgLike.setImageResource(R.drawable.ic_unfavorite);
            }

            if (!items.getPostText().equalsIgnoreCase("")) {
                holder.txtDescription.setVisibility(View.VISIBLE);
                holder.txtDescription.setText(StringEscapeUtils.unescapeJava(items.getPostText()));
            } else {
                holder.txtDescription.setVisibility(View.GONE);
            }

            holder.txtTotalLikes.setText(items.getTotalLike());
            holder.txtTotalComments.setText(items.getTotalComment());



            //displayImage(items, holder, position);
            imageViewPager(items, holder);

            holder.layoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isInternetAvailable = cd.isNetworkConnected(context);
                    if (isInternetAvailable) {
                        if (arrayList.get(position).getIsLiked().equals("y")) {
                            unLikePost(userId, arrayList.get(position).getId(), "Unlike", holder, position, arrayList.get(position).getTotalLike());
                        } else {
                            likePost(userId, arrayList.get(position).getId(), "Like", holder, position, arrayList.get(position).getTotalLike());
                        }
                    } else {
                        intent = new Intent(context, NoConnectionActivity.class);
                        ((HomeActivity) context).startActivityForResult(intent, ADAPTER_HOME_CODE);
                    }
                }
            });

            holder.imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (userId.equals(arrayList.get(position).getUserData().getId())) {
                        ((HomeActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.containerView, new HomeFragment())
                                .commit();
                    } else {*/
                    intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("receiver_id", arrayList.get(position).getUserData().getId());
                    context.startActivity(intent);
                    //}
                }
            });

            holder.layoutComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(context, CommentsDialogActivity.class);
                    intent.putExtra("post_id", arrayList.get(position).getId());
                    intent.putExtra("post_user_id", arrayList.get(position).getUserData().getId());
                    intent.putExtra("screenName", screenName);
                    context.startActivity(intent);
                }
            });

            /*if (userId.equals(items.getUserData().getId())) {
                holder.imgShare.setVisibility(View.GONE);
            } else {
                holder.imgShare.setVisibility(View.VISIBLE);
            }*/

            holder.imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharePostActivity.editSharedPost = false;
                    arrayListImages.clear();
                    arrayListImages.addAll(arrayList.get(position).getImage());
                    ((ApplicationController) context.getApplicationContext()).arrayList = arrayListImages;
                    intent = new Intent(context, SharePostActivity.class);
                    intent.putExtra("post_id", items.getId());
                    intent.putExtra("profileImg", items.getUserData().getProfileImg());
                    intent.putExtra("username", items.getUserData().getFirstName() + " " + items.getUserData().getLastName());
                    intent.putExtra("post_date", items.getPostDate());
                    intent.putExtra("post_time", items.getPostTime());
                    intent.putExtra("post_text", items.getPostText());
                    intent.putExtra("shared_post_text", items.getSharePostText());
                    intent.putExtra("post_type", items.getPostType());
                    context.startActivity(intent);
                }
            });

            /*holder.layoutViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayListImages.clear();
                    arrayListImages.addAll(arrayList.get(position).getImage());
                    ((ApplicationController) context.getApplicationContext()).arrayList = arrayListImages;
                    intent = new Intent(context, DisplayPhotosActivity.class);
                    context.startActivity(intent);
                }
            });*/

            holder.imgShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String receiver_id = items.getUserData().getId();
                    showMoreOptionDialog(position, holder, receiver_id, items.getId(), arrayList, arrayList.get(position).getIs_reported(), holder.imgShowMore);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setFromFragment(String screenName) {
        this.screenName = screenName;
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private LinearLayout layoutLike, layoutComments, layoutMultiImage, layoutViewMore, layoutSharedLike, layoutSharedComments, layoutSharedMultiImage, layoutSharedViewMore,
                layoutMainPost, layoutMainSharedPost;
        private RelativeLayout layoutImg3, layoutSharedImg3;
        private CircleImageView imgProfile, imgSharedProfile, imgSharedProfile1;
        private ImageView imgPostType, imgShowMore, imgPost, imgLike, imgShare, img1, img3, imgSharedPostType, imgSharedShowMore, imgSharedPost, imgSharedLike, imgShared1, imgShared3, imgSharePostShare;
        private TextView txtUsername, txtDate, txtDescription, txtTotalLikes, txtTotalComments, txtSharedUsername, txtSharedUsername1, txtSharedDate, txtSharedDate1,
                txtSharedDescription, txtSharedDescription1, txtSharedTotalLikes, txtSharedTotalComments, txtDiff, txtShared_diff;

        private FrameLayout framePostImage, frameShareImage;
        private ViewPager viewPagerPost, viewPagerShare;
        private ViewPagerIndicator viewPagerIndicatorPost, viewPagerIndicatorShare;

        public View_Holder(View itemView) {
            super(itemView);
            layoutMainPost = (LinearLayout) itemView.findViewById(R.id.layoutMainPost);
            layoutMainSharedPost = (LinearLayout) itemView.findViewById(R.id.layoutMainSharedPost);
            layoutLike = (LinearLayout) itemView.findViewById(R.id.layoutLike);
            layoutComments = (LinearLayout) itemView.findViewById(R.id.layoutComments);
            /*layoutMultiImage = (LinearLayout) itemView.findViewById(R.id.layoutMultiImage);
            layoutViewMore = (LinearLayout) itemView.findViewById(R.id.layoutViewMore);
            layoutImg3 = (RelativeLayout) itemView.findViewById(R.id.layoutImg3);*/

            layoutSharedLike = (LinearLayout) itemView.findViewById(R.id.layoutSharedLike);
            layoutSharedComments = (LinearLayout) itemView.findViewById(R.id.layoutSharedComments);
            //layoutSharedMultiImage = (LinearLayout) itemView.findViewById(R.id.layoutSharedMultiImage);
            //layoutSharedViewMore = (LinearLayout) itemView.findViewById(R.id.layoutSharedViewMore);
            //layoutSharedImg3 = (RelativeLayout) itemView.findViewById(R.id.layoutSharedImg3);

            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);
            imgSharedProfile = (CircleImageView) itemView.findViewById(R.id.imgSharedProfile);
            imgSharedProfile1 = (CircleImageView) itemView.findViewById(R.id.imgSharedProfile1);

            imgPostType = (ImageView) itemView.findViewById(R.id.imgPostType);
            imgShowMore = (ImageView) itemView.findViewById(R.id.imgShowMore);
            //imgPost = (ImageView) itemView.findViewById(R.id.imgPost);
            imgLike = (ImageView) itemView.findViewById(R.id.imgLike);
            imgShare = (ImageView) itemView.findViewById(R.id.imgShare);
            //img1 = (ImageView) itemView.findViewById(R.id.img1);
            //img3 = (ImageView) itemView.findViewById(R.id.img3);

            imgSharedPostType = (ImageView) itemView.findViewById(R.id.imgSharedPostType);
            imgSharedShowMore = (ImageView) itemView.findViewById(R.id.imgSharedShowMore);
            //imgSharedPost = (ImageView) itemView.findViewById(R.id.imgSharedPost);
            imgSharedLike = (ImageView) itemView.findViewById(R.id.imgSharedLike);
            //imgShared1 = (ImageView) itemView.findViewById(R.id.imgShared1);
            //imgShared3 = (ImageView) itemView.findViewById(R.id.imgShared3);
            imgSharePostShare = (ImageView) itemView.findViewById(R.id.imgSharePostShare);
            txtShared_diff = (TextView) itemView.findViewById(R.id.txtShared_diff);

            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtTotalLikes = (TextView) itemView.findViewById(R.id.txtTotalLikes);
            txtTotalComments = (TextView) itemView.findViewById(R.id.txtTotalComments);

            txtSharedUsername = (TextView) itemView.findViewById(R.id.txtSharedUsername);
            txtSharedUsername1 = (TextView) itemView.findViewById(R.id.txtSharedUsername1);
            txtSharedDate = (TextView) itemView.findViewById(R.id.txtSharedDate);
            txtSharedDate1 = (TextView) itemView.findViewById(R.id.txtSharedDate1);
            txtSharedDescription = (TextView) itemView.findViewById(R.id.txtSharedDescription);
            txtSharedDescription1 = (TextView) itemView.findViewById(R.id.txtSharedDescription1);
            txtSharedTotalLikes = (TextView) itemView.findViewById(R.id.txtSharedTotalLikes);
            txtSharedTotalComments = (TextView) itemView.findViewById(R.id.txtSharedTotalComments);

            txtDiff = (TextView) itemView.findViewById(R.id.txt_diff);
            //txt_diff2 = (TextView) itemView.findViewById(R.id.txt_diff2);

            framePostImage = (FrameLayout) itemView.findViewById(R.id.framePostImage);
            viewPagerPost = (ViewPager) itemView.findViewById(R.id.viewPagerPost);
            viewPagerIndicatorPost = (ViewPagerIndicator) itemView.findViewById(R.id.viewPagerIndicatorPost);

            frameShareImage = (FrameLayout) itemView.findViewById(R.id.frameShareImage);
            viewPagerShare = (ViewPager) itemView.findViewById(R.id.viewPagerShare);
            viewPagerIndicatorShare = (ViewPagerIndicator) itemView.findViewById(R.id.viewPagerIndicatorShare);
        }
    }

    private void imageViewPager(FeedsItem items, final View_Holder holder) {
        if (items.getImage().size() > 0) {
            holder.framePostImage.setVisibility(View.VISIBLE);
            holder.framePostImage.getLayoutParams().height = imgHeight;
            ArrayList<FeedsImageItem> arrayListPager = new ArrayList<>();
            arrayListPager.addAll(items.getImage());



            MyPagerAdapter myPagerAdapter = new MyPagerAdapter(context, arrayListPager);
            holder.viewPagerPost.setAdapter(myPagerAdapter);
            myPagerAdapter.notifyDataSetChanged();
            holder.viewPagerIndicatorPost.setupWithViewPager(holder.viewPagerPost);
            holder.viewPagerIndicatorPost.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            if(arrayListPager.size() == 1)
            {
                holder.viewPagerIndicatorPost.setVisibility(View.GONE);
            }

        } else {
            holder.framePostImage.setVisibility(View.GONE);

        }
    }

    private void sharedImageViewPager(FeedsItem items, View_Holder holder) {
        if (items.getImage().size() > 0) {
            holder.frameShareImage.setVisibility(View.VISIBLE);
            holder.frameShareImage.getLayoutParams().height = imgHeight;
            ArrayList<FeedsImageItem> arrayListPager = new ArrayList<>();
            arrayListPager.addAll(items.getImage());
            MyPagerAdapter myPagerAdapter = new MyPagerAdapter(context, arrayListPager);
            holder.viewPagerShare.setAdapter(myPagerAdapter);
            myPagerAdapter.notifyDataSetChanged();
            holder.viewPagerIndicatorShare.setupWithViewPager(holder.viewPagerShare);
            holder.viewPagerIndicatorShare.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            holder.frameShareImage.setVisibility(View.GONE);
        }
    }

    // display multiple post images
    /*private void displayImage(FeedsItem items, View_Holder holder, int position) {
        if (items.getImage().size() > 0) {
            if (items.getImage().size() == 1) {
                holder.imgPost.setVisibility(View.VISIBLE);
                holder.layoutMultiImage.setVisibility(View.GONE);
                holder.layoutViewMore.setVisibility(View.GONE);
                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(0).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.imgPost);
            } else if (items.getImage().size() == 2) {
                holder.imgPost.setVisibility(View.GONE);
                holder.layoutMultiImage.setVisibility(View.VISIBLE);
                holder.layoutViewMore.setVisibility(View.GONE);
                holder.img1.setVisibility(View.VISIBLE);
                holder.layoutImg3.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(0).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.img1);

                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(1).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.img3);
            } else if (items.getImage().size() > 2) {
                holder.imgPost.setVisibility(View.GONE);
                holder.layoutMultiImage.setVisibility(View.VISIBLE);
                holder.layoutViewMore.setVisibility(View.VISIBLE);
                holder.img1.setVisibility(View.VISIBLE);
                holder.layoutImg3.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(0).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.img1);

                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(1).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.img3);
            }
        } else {
            holder.imgPost.setVisibility(View.GONE);
            holder.layoutMultiImage.setVisibility(View.GONE);
            holder.layoutViewMore.setVisibility(View.GONE);
            holder.img1.setVisibility(View.GONE);
            holder.layoutImg3.setVisibility(View.GONE);
        }
    }*/

    // display multiple post images for shared post
    /*private void displaySharedImage(FeedsItem items, View_Holder holder, int position) {
        if (items.getImage().size() > 0) {
            if (items.getImage().size() == 1) {
                holder.imgSharedPost.setVisibility(View.VISIBLE);
                holder.layoutSharedMultiImage.setVisibility(View.GONE);
                holder.layoutSharedViewMore.setVisibility(View.GONE);
                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(0).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.imgSharedPost);
            } else if (items.getImage().size() == 2) {
                holder.imgSharedPost.setVisibility(View.GONE);
                holder.layoutSharedMultiImage.setVisibility(View.VISIBLE);
                holder.layoutSharedViewMore.setVisibility(View.GONE);
                holder.imgShared1.setVisibility(View.VISIBLE);
                holder.layoutSharedImg3.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(0).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.imgShared1);

                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(1).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.imgShared3);
            } else if (items.getImage().size() > 2) {
                holder.imgSharedPost.setVisibility(View.GONE);
                holder.layoutSharedMultiImage.setVisibility(View.VISIBLE);
                holder.layoutSharedViewMore.setVisibility(View.VISIBLE);
                holder.imgShared1.setVisibility(View.VISIBLE);
                holder.layoutSharedImg3.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(0).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.imgShared1);

                Picasso.with(context)
                        .load(arrayList.get(position).getImage().get(1).getPath())
                        .fit()
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.imgShared3);
            }
        } else {
            holder.imgSharedPost.setVisibility(View.GONE);
            holder.layoutSharedMultiImage.setVisibility(View.GONE);
            holder.layoutSharedViewMore.setVisibility(View.GONE);
            holder.imgShared1.setVisibility(View.GONE);
            holder.layoutSharedImg3.setVisibility(View.GONE);
        }
    }*/

    private void unLikePost(String userId, String post_id, String like_action, final View_Holder holder, final int position, final String totalLike) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userLikePost");

        params.add("user_id");
        values.add(userId);

        params.add("post_id");
        values.add(post_id);

        params.add("like_action");
        values.add(like_action);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        holder.imgLike.setImageResource(R.drawable.ic_unfavorite);
                        int result = Integer.parseInt(totalLike);
                        if (result > 0) {
                            result -= 1;
                            arrayList.get(position).setTotalLike(String.valueOf(result));
                        }
                        arrayList.get(position).setIsLiked("n");
                        notifyDataSetChanged();
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void likePost(String userId, String post_id, String like_action, final View_Holder holder, final int position, final String totalLike) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userLikePost");

        params.add("user_id");
        values.add(userId);

        params.add("post_id");
        values.add(post_id);

        params.add("like_action");
        values.add(like_action);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        holder.imgLike.setImageResource(R.drawable.ic_favorite);
                        int result = Integer.parseInt(totalLike);
                        if (result >= 0) {
                            result += 1;
                            arrayList.get(position).setTotalLike(String.valueOf(result));
                        }
                        arrayList.get(position).setIsLiked("y");
                        notifyDataSetChanged();
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // popup dialog for show more options like edit post, report post.
    private void showMoreOptionDialog(final int position, View_Holder holder, final String receiver_id, final String post_id, final ArrayList<FeedsItem> arrayList, final String is_reported, ImageView imageView) {
        final PopupMenu popup = new PopupMenu(context, imageView);
        if (userId.equals(receiver_id)) {
            popup.getMenuInflater().inflate(R.menu.my_home_option_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.option_delete:
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(false);
                            builder.setMessage(R.string.delete_post_alert_msg);
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user pressed "yes", then he is allowed to exit from application
                                    isInternetAvailable = cd.isNetworkConnected(context);
                                    if (isInternetAvailable) {
                                        deletePost(position, post_id);
                                    } else {
                                        intent = new Intent(context, NoConnectionActivity.class);
                                        ((HomeActivity) context).startActivityForResult(intent, ADAPTER_HOME_CODE);
                                    }
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
                            break;
                        case R.id.option_edit:
                            arrayListImages.clear();
                            arrayListImages.addAll(arrayList.get(position).getImage());
                            ((ApplicationController) context.getApplicationContext()).arrayList = arrayListImages;
                            if (arrayList.get(position).getSharePostId().equals("")) {
                                AddNewPostActivity.isEditPost = true;
                                intent = new Intent(context, AddNewPostActivity.class);
                                intent.putExtra("post_id", arrayList.get(position).getId());
                                intent.putExtra("postType", arrayList.get(position).getPostType());
                                intent.putExtra("post_text", arrayList.get(position).getPostText());
                                context.startActivity(intent);
                            } else {
                                SharePostActivity.editSharedPost = true;
                                intent = new Intent(context, SharePostActivity.class);
                                intent.putExtra("post_id", arrayList.get(position).getId());
                                intent.putExtra("profileImg", arrayList.get(position).getShareUserData().getProfileImg());
                                intent.putExtra("username", arrayList.get(position).getShareUserData().getFirstName() + " " + arrayList.get(position).getShareUserData().getLastName());
                                intent.putExtra("post_date", arrayList.get(position).getPostDate());
                                intent.putExtra("post_time", arrayList.get(position).getPostTime());
                                intent.putExtra("post_text", arrayList.get(position).getSharePostText());
                                intent.putExtra("shared_post_text", arrayList.get(position).getPostText());
                                intent.putExtra("post_type", arrayList.get(position).getPostType());
                                context.startActivity(intent);
                            }
                            break;
                    }
                    return true;
                }
            });
        } else {
            if (is_reported.equals("y")) {
                popup.getMenuInflater().inflate(R.menu.reported_menu, popup.getMenu());
            } else {
                popup.getMenuInflater().inflate(R.menu.user_home_option_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.option_report:
                                if (item.getTitle().toString().equals("Report")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setCancelable(false);
                                    builder.setMessage(R.string.report_post_alert_msg);
                                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //if user pressed "yes", then he is allowed to exit from application
                                            isInternetAvailable = cd.isNetworkConnected(context);
                                            if (isInternetAvailable) {
                                                reportPost(post_id, receiver_id, position);
                                            } else {
                                                intent = new Intent(context, NoConnectionActivity.class);
                                                ((HomeActivity) context).startActivityForResult(intent, ADAPTER_HOME_CODE);
                                            }
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
                                break;
                        }
                        return true;
                    }
                });
            }

        }
        popup.setGravity(Gravity.RIGHT);
        popup.show();
    }

    private void reportPost(final String post_id, String receiver_id, final int position) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userReportPost");

        params.add("user_id");
        values.add(userId);

        params.add("post_id");
        values.add(post_id);

        params.add("post_user_id");
        values.add(receiver_id);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        arrayList.get(position).setIs_reported("y");
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void deletePost(final int position, String post_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("deletePost");

        params.add("post_id");
        values.add(post_id);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        arrayList.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public int getDispWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
