package com.example.opeyemi.takeme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.opeyemi.takeme.ProfileFragments.JobPostFragment;
import com.example.opeyemi.takeme.bottomNavigationViewHelper.BaseActivity;
import com.example.opeyemi.takeme.ProfileFragments.ProfileTabFragmentPagerAdapter;
import com.example.opeyemi.takeme.common.Common;
import com.example.opeyemi.takeme.model.Job;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends BaseActivity implements JobPostFragment.OnListFragmentInteractionListener {

    private ViewPager mProfileViewpager;
    private ProfileTabFragmentPagerAdapter mPagerAdapter;
    private TabLayout mProfileTab;
    private TextView mProfileNameOfUserTextView;
    private ImageView mProfileImageOfUserImageView;
    private TextView mTotalJob;
    private TextView mActiveJobs;
    private TextView mRatingValueTextView;
    RatingBar rb;
    LinearLayout ratingLayout;

    final private int RATING_REQUEST_CODE = 5;

    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user");

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeStatusBarColor();

        initializeViews();

        if (getIntent().getStringExtra("profileId") != null) {
            currentUserID = getIntent().getStringExtra("profileId");
        } else {
            currentUserID = Common.currentUser.getPhoneNumber();
        }


        setUpRatingBar();

        initializeViewsDetailsForUser(currentUserID);


    }

    private void setUpRatingBar() {
        ratingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDailogBuilder = new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle(R.string.rate_user)
                        .setMessage(R.string.rate_user_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent rateIntent = new Intent(ProfileActivity.this, RatingActivity.class);
                                startActivityForResult(rateIntent, RATING_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                alertDailogBuilder.create().show();
            }
        });
    }

    private void changeStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    private void initializeViews() {
        //instantiate viewpager
        mProfileViewpager = findViewById(R.id.pager);
        mPagerAdapter = new ProfileTabFragmentPagerAdapter(getSupportFragmentManager(), this);
        mProfileViewpager.setAdapter(mPagerAdapter);
        mProfileTab = findViewById(R.id.profile_tab);
        mProfileTab.setupWithViewPager(mProfileViewpager);
        //initial user profile name text view
        mProfileNameOfUserTextView = findViewById(R.id.profileUsernameTextView);
        mProfileImageOfUserImageView = findViewById(R.id.profile_imageView);
        mActiveJobs = findViewById(R.id.profileJobActiveTextView);
        mTotalJob = findViewById(R.id.profileJobsNumbers);
        mRatingValueTextView = findViewById(R.id.rating_value);
        rb = findViewById(R.id.user_ratingBar);
        ratingLayout = findViewById(R.id.rating_layout);
    }

    private void initializeViewsDetailsForUser(String currentUserPhone) {
        DatabaseReference currentUserdb = userRef.child(currentUserPhone);
        currentUserdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("name").getValue() != null) {
                        mProfileNameOfUserTextView.setText(dataSnapshot.child("name").getValue().toString());
                    }

                    if (dataSnapshot.child("image").getValue() != null) {
                        Picasso.with(mProfileImageOfUserImageView.getContext())
                                .load(dataSnapshot.child("image").getValue().toString())
                                .into(mProfileImageOfUserImageView);
                    }

                    if (dataSnapshot.child("totalJobs").getValue() != null) {
                        mTotalJob.setText(dataSnapshot.child("totalJobs").getValue().toString());
                    }

                    if (dataSnapshot.child("activeJobs").getValue() != null) {
                        mActiveJobs.setText(dataSnapshot.child("activeJobs").getValue().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //overriding BaseActivity method
    public int getContentViewId() {
        return R.layout.activity_profile;
    }

    public int getNavigationMenuItemId() {
        return R.id.navigation_profile;
    }

    @Override
    public void onListFragmentInteraction(Job job) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.profile_logout_menu:
                OneSignal.setSubscription(false);
                Common.logout(ProfileActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == RATING_REQUEST_CODE) {
                String ratingValueReturned = data.getStringExtra("rate");

                String ratingCommentReturned = data.getStringExtra("comment");

                userRef.child(currentUserID).child("ratings");

                HashMap<String, Object> newRating = new HashMap<>();
                newRating.put("raterName", Common.currentUser.getName());
                newRating.put("raterPhone", Common.currentUser.getPhoneNumber());
                newRating.put("ratingValue", ratingValueReturned);
                newRating.put("raterComment", ratingCommentReturned);

                mRatingValueTextView.setText(ratingValueReturned);
                userRef.child("ratings").push().updateChildren(newRating);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getProfileId(){
        return currentUserID;
    }
}
