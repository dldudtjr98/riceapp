package com.dldud.riceapp;

import android.content.ClipData;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * implements AbsListView.OnScrollListener
 */
public class FeedFragment extends Fragment{

    private ArrayList<Integer> feedIdxs = new ArrayList<>();
    private FeedAdapter oAdapter;
    private ArrayList<ItemData> oData;

    private String imgUrl = "http://52.78.18.156/data/riceapp/";
    private String myString;
    private String userString;
    private String likeString;
    private String replyString;

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

        WebView wv = null;
        ImageView imgView;
        RecyclerView recyclerView;
        LinearLayoutManager layoutManager;


        recyclerView = (RecyclerView) v.findViewById(R.id.dynamicLayout);
        wv = (WebView) v.findViewById(R.id.seeDetailView);
        imgView = (ImageView) v.findViewById( R.id.detailImage);

        oData = new ArrayList<>();

        try {
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
            String[] idx = task.idx.toArray(new String[task.idx.size()]);
            String[] title = task.title.toArray(new String[task.title.size()]);
            String[] thumbnail = task.thumbnail.toArray(new String[task.thumbnail.size()]);
            String[] writer_id = task.writer_id.toArray(new String[task.writer_id.size()]);
            String[] banner = task.banner.toArray(new String[task.banner.size()]);
            String[] content = task.content.toArray(new String[task.content.size()]);
            String[] locationLat = task.locationlat.toArray(new String[task.locationlat.size()]);
            String[] locationLong = task.locationlong.toArray(new String[task.locationlong.size()]);

            String[] link_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);
            String[] nickname = taskUser.nickname.toArray(new String[taskUser.nickname.size()]);
            String[] profile = taskUser.profile.toArray(new String[taskUser.profile.size()]);
            String[] userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);

            String[] likeIdx = taskLike.idx.toArray(new String[taskLike.idx.size()]);
            String[] likeCnt = taskLike.ping_idx.toArray(new String[taskLike.ping_idx.size()]);

            String[] replyIdx = taskReply.idx.toArray(new String[taskReply.idx.size()]);
            String[] replyCnt = taskReply.ping_idx.toArray(new String[taskReply.ping_idx.size()]);

            int linearNum = idx.length;
            int userLinearNum = userIdx.length;
            int likeLinearNum = likeIdx.length;
            int replyLinearNum = replyIdx.length;


            for(int i = 0 ; i<linearNum ; i++) {
                if (feedIdxs.contains(Integer.parseInt(idx[linearNum - (i + 1)])) || feedIdxs.size() == 0) {
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
                    for(int k =0; k < likeLinearNum; k++){
                        if(likeCnt[k].equals(idx[linearNum - (i + 1)])){
                            like++;
                        }
                    }

                    for(int l =0; l < replyLinearNum; l++){
                        if(replyCnt[l].equals(idx[linearNum - (i + 1)])){
                            reply++;
                        }
                    }
                    String strLike;
                    String strReply;

                    strLike = String.valueOf(like);
                    strReply = String.valueOf(reply);

                    oItem.strPingReply = strReply;
                    oItem.strPingLike = strLike;

                    if(feedIdxs.size() > 0)
                        oItem.bActiveMap = false;

                        oData.add(oItem);

                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
           e.printStackTrace();
        }

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        oAdapter = new FeedAdapter(getActivity(), oData);

        recyclerView.setAdapter(oAdapter);

        return v;
    }



}