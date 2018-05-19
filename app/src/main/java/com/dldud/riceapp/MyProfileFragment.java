package com.dldud.riceapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

import static com.dldud.riceapp.UserProfileSettingActivity.userId;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {

    String imgUrl = "http://52.78.18.156/data/riceapp/";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView profileImage;
    private TextView profileUserName;
    private ImageView detailImage = null;
    //private GridView feedGrid;
    private TextView profileMyPing;
    private TextView profileLikePing;
    TaskUser taskUser = new TaskUser();
    TaskLike taskLike = new TaskLike();
    Task taskPing = new Task();
    private int targetUser = 0;

    ArrayList<Integer> myLike = new ArrayList<>();
    ArrayList<Integer> myPing = new ArrayList<>();

    String userString;
    String userImg;
    String userName;
    static String UserThumbnailPath;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    public static MyProfileFragment newInstance() {
        Bundle args = new Bundle();
        MyProfileFragment fragment = new MyProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onStart() {
        super.onStart();

        PageAdapter pageAdapter
                = new PageAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount(), getContext());

        pageAdapter.myLike = myLike;
        pageAdapter.myPing = myPing;

        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);

        viewPager = (ViewPager)v.findViewById(R.id.profileViewPager);
        tabLayout = (TabLayout)v.findViewById(R.id.tabLayout);
        profileImage = (ImageView)v.findViewById(R.id.bannerImage);
        profileUserName = (TextView)v.findViewById(R.id.myUserName);
        detailImage = (ImageView)v.findViewById(R.id.profileDetail);
        //feedGrid = (GridView)v.findViewById(R.id.imageGrid);

        profileMyPing = (TextView)v.findViewById(R.id.myPingNum);
        profileLikePing = (TextView)v.findViewById(R.id.sharedPingNum);

        try {
            userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
            taskUser.jsonParser(userString);

            String pingString = taskPing.execute("http://52.78.18.156/public/ping_db.php").get();
            taskPing.jsonParser(pingString);

            String[] ping_idx = taskPing.idx.toArray(new String[taskPing.idx.size()]);
            String[] ping_writer_idx = taskPing.writer_id.toArray(new String[taskPing.writer_id.size()]);

            String[] link_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);
            String[] idx = taskUser.idx.toArray(new String[taskUser.idx.size()]);
            String[] profile = taskUser.profile.toArray(new String[taskUser.profile.size()]);
            String[] nickname = taskUser.nickname.toArray(new String[taskUser.profile.size()]);

            int userLinearNum = idx.length;

            for (int i = 0; i < userLinearNum; i++) {
                String val = link_id[i];
                if (val.contains(userId)) {
                    userImg = imgUrl + profile[i];
                    userName = nickname[i];
                }
            }

            String like = taskLike.execute("http://52.78.18.156/public/ping_like_db.php").get();

            taskLike.jsonParser(like);
            String[] likePingIdx = taskLike.ping_idx.toArray(new String[taskLike.ping_idx.size()]);
            String[] likeUserIdx = taskLike.user_idx.toArray(new String[taskLike.user_idx.size()]);

            for(int i = 0 ; i < likeUserIdx.length; i++)
            {
                if(likeUserIdx[i].equals(userId))
                    myLike.add(Integer.parseInt(likePingIdx[i]));
            }

            for(int i=0; i < ping_writer_idx.length; i++)
            {
                if(ping_writer_idx[i].equals(userId))
                {
                    myPing.add(Integer.parseInt(ping_idx[i]));
                }
            }

            profileLikePing.setText(Integer.toString(myLike.size()));
            profileMyPing.setText(Integer.toString(myPing.size()));
        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }
/*
        Picasso.with(getContext())
                .load(userImg)
                .fit()
                .centerCrop()
                .into(profileImage);
*/

        Glide.with(getContext())
                .load(userImg)
                .apply(new RequestOptions().circleCropTransform())
                .into(profileImage);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //imgClose.setVisibility(View.VISIBLE);
                detailImage.setVisibility(View.VISIBLE);
                Picasso.with(getContext())
                        .load(userImg)
                        .into(detailImage);
            }
        });

        detailImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                detailImage.setVisibility(View.GONE);
            }
        });

        profileUserName.setText(userName);


        /*
        ProfileGridAdapter pga = new ProfileGridAdapter(getActivity(), "");

        feedGrid.setAdapter(pga);
*/

        return v;
    }

    public void setTargetUser(int t)
    {
        targetUser = t;
    }

    public int getTargetUser()
    {
        return targetUser;
    }
}
