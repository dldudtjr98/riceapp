package com.dldud.riceapp;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    TaskUser taskUser = new TaskUser();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);

        viewPager = (ViewPager)v.findViewById(R.id.profileViewPager);
        tabLayout = (TabLayout)v.findViewById(R.id.tabLayout);
        profileImage = (ImageView)v.findViewById(R.id.bannerImage);
        profileUserName = (TextView)v.findViewById(R.id.myUserName);
        detailImage = (ImageView)v.findViewById(R.id.profileDetail);

        try {
            userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
            taskUser.jsonParser(userString);
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
        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }

        Picasso.with(getContext())
                .load(userImg)
                .fit()
                .centerCrop()
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

        PageAdapter pageAdapter
                = new PageAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return v;
    }


}
