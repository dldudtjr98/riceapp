package com.dldud.riceapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.dldud.riceapp.MainActivity.navigation;

/**
 * A simple {@link Fragment} subclass.
 * implements AbsListView.OnScrollListener
 */
public class FeedFragment extends Fragment implements FeedAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{

    private ArrayList<Integer> feedIdxs = new ArrayList<>();
    private FeedAdapter oAdapter;
    private ArrayList<ItemData> oData;

    private String imgUrl = "http://52.78.18.156/data/riceapp/";

    private String dateDiff;
    private Date date;
    String[] idx, title, thumbnail, writer_id, banner, content,
            locationLat, locationLong,create_date, link_id, nickname,
            profile, userIdx, likeIdx, likeCnt, replyIdx, replyCnt;
    int linearNum, userLinearNum, likeLinearNum, replyLinearNum;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerView recyclerView;

    public FeedFragment() {
        // Required empty public constructor
    }

    public void setIdxs(ArrayList<Integer> arr)
    {
        feedIdxs = arr;
    }

    /*
    public static FeedFragment newInstance{
        return new FeedFragment();
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.dynamicLayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        oData = new ArrayList<>();

        long now = System.currentTimeMillis();
        date = new Date(now);

        String myString;
        String userString;
        String likeString;
        String replyString;


        try{
            Task task = new Task();
            TaskUser taskUser = new TaskUser();
            TaskLike taskLike = new TaskLike();
            TaskReply taskReply = new TaskReply();

            myString = task.execute("http://52.78.18.156/public/ping_db.php").get();
            userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
            likeString = taskLike.execute("http://52.78.18.156/public/ping_like_db.php").get();
            replyString = taskReply.execute("http://52.78.18.156/public/comment_db.php").get();

            task.jsonParser(myString);
            taskUser.jsonParser(userString);
            taskLike.jsonParser(likeString);
            taskReply.jsonParser(replyString);

            //list to array
            idx = task.idx.toArray(new String[task.idx.size()]);
            title = task.title.toArray(new String[task.title.size()]);
            thumbnail = task.thumbnail.toArray(new String[task.thumbnail.size()]);
            writer_id = task.writer_id.toArray(new String[task.writer_id.size()]);
            banner = task.banner.toArray(new String[task.banner.size()]);
            content = task.content.toArray(new String[task.content.size()]);
            locationLat = task.locationlat.toArray(new String[task.locationlat.size()]);
            locationLong = task.locationlong.toArray(new String[task.locationlong.size()]);
            create_date = task.create_date.toArray(new String[task.create_date.size()]);

            link_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);
            nickname = taskUser.nickname.toArray(new String[taskUser.nickname.size()]);
            profile = taskUser.profile.toArray(new String[taskUser.profile.size()]);
            userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);

            likeIdx = taskLike.idx.toArray(new String[taskLike.idx.size()]);
            likeCnt = taskLike.ping_idx.toArray(new String[taskLike.ping_idx.size()]);

            replyIdx = taskReply.idx.toArray(new String[taskReply.idx.size()]);
            replyCnt = taskReply.ping_idx.toArray(new String[taskReply.ping_idx.size()]);

            linearNum = idx.length;
            userLinearNum = userIdx.length;
            likeLinearNum = likeIdx.length;
            replyLinearNum = replyIdx.length;

        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        oAdapter = new FeedAdapter(getActivity(),this);
        oAdapter.setRecyclerView(recyclerView);
        oAdapter.setLinearLayoutManager(layoutManager);
        recyclerView.setAdapter(oAdapter);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        loadData();
    }

    // load initial data
    private void loadData() {
        oData.clear();
        for (int i = 0; i <= 5; i++) {
            if (feedIdxs.contains(Integer.parseInt(idx[linearNum - (i + 1)])) || feedIdxs.size() == 0) {
                try {
                    ItemData oItem = new ItemData();
                    int like = 0;
                    int reply = 0;

                    oItem.strIdx = idx[linearNum - (i + 1)];
                    oItem.strLike = "좋아요";
                    oItem.strShare = "공유";
                    oItem.strUserId = writer_id[linearNum - (i + 1)];
                    oItem.strReply = "댓글";
                    oItem.strContent = content[linearNum - (i + 1)];
                    oItem.strTitile = title[linearNum - (i + 1)];

                    oItem.strThumbnailImage = imgUrl + thumbnail[linearNum - (i + 1)];
                    oItem.douLatitude = Double.parseDouble(locationLat[linearNum - (i + 1)]);
                    oItem.douLongitude = Double.parseDouble(locationLong[linearNum - (i + 1)]);
                    oItem.strVideo = imgUrl + banner[linearNum - (i + 1)];

                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                    Date d2 = f.parse(create_date[linearNum - (i + 1)]);
                    long diff = date.getTime() - d2.getTime();
                    long sec = diff / 1000;
                    if(sec < 60) {
                        dateDiff = Long.toString(sec) + "초 전";
                    } else if (sec < 3600 && sec >= 60){
                        dateDiff = Long.toString(sec/60) + "분 전";
                    } else if (sec < 86400 && sec >=3600){
                        dateDiff = Long.toString(sec/3600) + "시간 전";
                    } else if (sec < 604800 && sec >= 86400){
                        dateDiff = Long.toString(sec/86400) + "일 전";
                    } else if (sec < 2419200 && sec >= 604800){
                        dateDiff = Long.toString(sec/604800) + "주 전";
                    } else if (sec < 29030400 && sec >= 2419200){
                        dateDiff = Long.toString(sec/36288000) + "달 전";
                    } else{
                        dateDiff = "오래 전";
                    }
                    oItem.strTime = dateDiff;

                    if (!oItem.strUserId.equals("0")) {
                        for (int j = 0; j < userLinearNum; j++) {
                            String val = link_id[j];
                            if (val.contains(oItem.strUserId)) {
                                oItem.strUserImage = imgUrl + profile[j];
                                oItem.strUserName = nickname[j];
                            }
                        }
                    } else {
                        oItem.strUserImage = imgUrl + "null.jpg";
                        oItem.strUserName = "익명";
                    }
                    for (int k = 0; k < likeLinearNum; k++) {
                        if (likeCnt[k].equals(idx[linearNum - (i + 1)])) {
                            like++;
                        }
                    }

                    for (int l = 0; l < replyLinearNum; l++) {
                        if (replyCnt[l].equals(idx[linearNum - (i + 1)])) {
                            reply++;
                        }
                    }
                    String strLike;
                    String strReply;

                    strLike = String.valueOf(like);
                    strReply = String.valueOf(reply);

                    oItem.strPingReply = strReply;
                    oItem.strPingLike = strLike;

                    oData.add(oItem);
                }catch(ParseException e){
                    e.printStackTrace();
                }
            }
        }
        oAdapter.addAll(oData);
    }

    @Override
    public void onLoadMore() {
        Log.d("MainActivity_", "onLoadMore");
        oAdapter.setProgressMore(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            oData.clear();
            oAdapter.setProgressMore(false);

            int start = oAdapter.getItemCount();
            int end = start + 5;

            //bound end
            if(end > linearNum){
                end = linearNum - 1;
            }

            for (int i = start + 1; i <= end; i++) {
                if (feedIdxs.contains(Integer.parseInt(idx[linearNum - (i + 1)])) || feedIdxs.size() == 0) {
                    try {
                        ItemData oItem = new ItemData();
                        int like = 0;
                        int reply = 0;

                        oItem.strIdx = idx[linearNum - (i + 1)];
                        oItem.strLike = "좋아요";
                        oItem.strShare = "공유";
                        oItem.strUserId = writer_id[linearNum - (i + 1)];
                        oItem.strReply = "댓글";
                        oItem.strContent = content[linearNum - (i + 1)];
                        oItem.strTitile = title[linearNum - (i + 1)];

                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                        Date d2 = f.parse(create_date[linearNum - (i + 1)]);
                        long diff = date.getTime() - d2.getTime();
                        long sec = diff / 1000;
                        if(sec < 60) {
                            dateDiff = Long.toString(sec) + "초 전";
                        } else if (sec < 3600 && sec >= 60){
                            dateDiff = Long.toString(sec/60) + "분 전";
                        } else if (sec < 86400 && sec >=3600){
                            dateDiff = Long.toString(sec/3600) + "시간 전";
                        } else if (sec < 604800 && sec >= 86400){
                            dateDiff = Long.toString(sec/86400) + "일 전";
                        } else if (sec < 2419200 && sec >= 604800){
                            dateDiff = Long.toString(sec/604800) + "주 전";
                        } else if (sec < 29030400 && sec >= 2419200){
                            dateDiff = Long.toString(sec/36288000) + "달 전";
                        } else{
                            dateDiff = "오래 전";
                        }

                        oItem.strTime = dateDiff;
                        oItem.strThumbnailImage = imgUrl + thumbnail[linearNum - (i + 1)];
                        oItem.douLatitude = Double.parseDouble(locationLat[linearNum - (i + 1)]);
                        oItem.douLongitude = Double.parseDouble(locationLong[linearNum - (i + 1)]);
                        oItem.strVideo = imgUrl + banner[linearNum - (i + 1)];

                        if (!oItem.strUserId.equals("0")) {
                            for (int j = 0; j < userLinearNum; j++) {
                                String val = link_id[j];
                                if (val.contains(oItem.strUserId)) {
                                    oItem.strUserImage = imgUrl + profile[j];
                                    oItem.strUserName = nickname[j];
                                }
                            }
                        } else {
                            oItem.strUserImage = imgUrl + "null.jpg";
                            oItem.strUserName = "익명";
                        }
                        for (int k = 0; k < likeLinearNum; k++) {
                            if (likeCnt[k].equals(idx[linearNum - (i + 1)])) {
                                like++;
                            }
                        }

                        for (int l = 0; l < replyLinearNum; l++) {
                            if (replyCnt[l].equals(idx[linearNum - (i + 1)])) {
                                reply++;
                            }
                        }
                        String strLike;
                        String strReply;

                        strLike = String.valueOf(like);
                        strReply = String.valueOf(reply);

                        oItem.strPingReply = strReply;
                        oItem.strPingLike = strLike;

                        oData.add(oItem);
                    } catch(ParseException e){
                        e.printStackTrace();
                    }
                }
            }
            //////////////////////////////////////////////////
            if(feedIdxs.size() > 0)
                oAdapter.setShowMap(false);
            oAdapter.addItemMore(oData);
            oAdapter.setMoreLoading(false);
            }
        }, 1000);
    }

    @Override
    public void onRefresh() {
        navigation.setSelectedItemId(R.id.navigation_feed);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}