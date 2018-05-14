package com.dldud.riceapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kakao.usermgmt.response.model.UserProfile;
import com.squareup.picasso.Picasso;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.dldud.riceapp.UserProfileSettingActivity.userId;

/**
 * Created by dldud on 2018-05-03.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    Context context;
    private ArrayList<ItemData> items = new ArrayList<>();
    private ArrayList<ReplyItemData> rData;
    private LinearLayoutManager layoutManager;
    private ReplyAdapter rAdapter;
    private String getLikeUserString;
    private String replyString;
    private String userString;
    private String likeString;
    private String imgUrl = "http://52.78.18.156/data/riceapp/";
    final String insertUrlPath = "http://52.78.18.156/public/Ping_like_insert.php";


    public FeedAdapter(Context context, ArrayList<ItemData> items){
        this.context = context;
        this.items = items;
    }

    public void add(ItemData data){
        items.add(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card,parent,false);
        return new ViewHolder(convertView);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        NetworkUtil.setNetworkPolicy();
        final RecyclerView recyclerView;
        final RecyclerView replyView;
        final ItemData item = items.get(position);
        rData = new ArrayList<>();

        final WebView wv;
        final ImageView detailImage;
        final Button viewClose,imgClose;
        final Animation animScaleAlpha = AnimationUtils.loadAnimation(context,R.anim.anim_scale_alpha);
        final LinearLayout reply;

        recyclerView = (RecyclerView) ((Activity)context).findViewById(R.id.dynamicLayout);
        detailImage = (ImageView) ((Activity)context).findViewById(R.id.detailImage);
        wv = (WebView) ((Activity)context).findViewById(R.id.seeDetailView);
        viewClose = (Button)((Activity)context).findViewById(R.id.wvCloseBtn);
        imgClose = (Button)((Activity)context).findViewById(R.id.imgCloseBtn);
        reply = (LinearLayout)((Activity)context).findViewById(R.id.replyLayout);
        replyView = (RecyclerView)((Activity)context).findViewById(R.id.replyCard);

        holder.oTextLike.setText(item.getStrLike());
        holder.oTextShare.setText(item.getStrShare());
        holder.oTextReply.setText(item.getStrReply());
        holder.oTextUserId.setText(item.getStrUserName());
        holder.oTextContent.setText(item.getStrContent());
        holder.oTextLikeCnt.setText(item.getStrPingLike());


        holder.oTextLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try{
                String idx;
                idx = item.getStrIdx();

                TaskLike taskLike = new TaskLike();
                TaskUser taskUser = new TaskUser();

                PHPRequestLike request = new PHPRequestLike(insertUrlPath);

                userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
                likeString = taskLike.execute("http://52.78.18.156/public/ping_like_db.php").get();

                taskLike.jsonParser(likeString);
                taskUser.jsonParser(userString);

                String[] userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);
                String[] userLink_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);

                String[] pingIdx = taskLike.idx.toArray(new String[taskLike.idx.size()]);
                int userLinearNum = userIdx.length;

                for(int i = 0 ; i < userLinearNum ; i++){
                    String val = userLink_id[i];
                    if(val.contains(userId)){
                        getLikeUserString = val;
                    }
                }

                request.PhPtest(getLikeUserString,idx);

            }catch(InterruptedException e){
                e.printStackTrace();
            }catch(ExecutionException e){
                e.printStackTrace();
            }catch(MalformedURLException e){
                e.printStackTrace();
            }
            }
        });

        holder.oTextReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                TaskReply taskReply = new TaskReply();
                TaskUser taskUser = new TaskUser();
                replyString = taskReply.execute("http://52.78.18.156/public/comment_db.php").get();
                userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
                taskUser.jsonParser(userString);
                taskReply.jsonParser(replyString);

                String idx;
                idx = item.getStrIdx();

                String[] replyIdx = taskReply.idx.toArray(new String[taskReply.idx.size()]);
                String[] replyUser = taskReply.user_idx.toArray(new String[taskReply.user_idx.size()]);
                String[] replyPing = taskReply.ping_idx.toArray(new String[taskReply.ping_idx.size()]);
                String[] replyContent = taskReply.content.toArray(new String[taskReply.content.size()]);

                String[] nickname = taskUser.nickname.toArray(new String[taskUser.nickname.size()]);
                String[] profile = taskUser.profile.toArray(new String[taskUser.profile.size()]);
                String[] userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);

                int replyNum = replyIdx.length;
                int userLinearNum = userIdx.length;


                for(int i = 0; i < replyNum; i++) {
                    String val = replyPing[i];
                    if(val.contains(idx)) {
                        ReplyItemData rItem = new ReplyItemData();

                        String strUserId;
                        strUserId = replyUser[replyNum - (i + 1)];
                        rItem.strReplyContent = replyContent[replyNum - (i + 1)];

                        for (int j = 0; j < userLinearNum; j++) {
                            String val1 = userIdx[j];
                            if (val1.contains(strUserId)) {
                                rItem.strReplyUserImage = imgUrl + profile[j];
                                rItem.strReplyUserName = nickname[j];
                            }
                        }
                        rData.add(rItem);
                    }
                }

                layoutManager = new LinearLayoutManager(context);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                replyView.setLayoutManager(layoutManager);
                rAdapter = new ReplyAdapter(context,rData);
                replyView.setAdapter(rAdapter);

                reply.setVisibility(View.VISIBLE);
                replyView.setVisibility(View.VISIBLE);


            }catch(InterruptedException e){
                e.printStackTrace();
            }catch(ExecutionException e){
                e.printStackTrace();
            }
            }
        });

        holder.oImageBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getStrVideo().substring(item.getStrVideo().length()-3,item.getStrVideo().length()).equals("mp4")) {
                    String name = item.getStrVideo();
                    String type = "video/mp4";
                    wv.setVisibility(View.VISIBLE);
                    detailImage.setVisibility(View.GONE);
                    wv.loadUrl("http://52.78.18.156/public/playVideo.php?video=" + name + "&type=" + type);
                } else {
                    imgClose.setVisibility(View.VISIBLE);
                    wv.setVisibility(View.GONE);
                    detailImage.setVisibility(View.VISIBLE);
                    Picasso.with(detailImage.getContext())
                            .load(item.getStrVideo())
                            .into(detailImage);
                }
            }
        });

        viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv.setVisibility(View.GONE);
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailImage.setVisibility(View.GONE);
                imgClose.setVisibility(View.GONE);
            }
        });

        holder.oButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.oFeedMap.setVisibility(View.VISIBLE);
                holder.oMap = new MapView(context);
                holder.oMap.setVisibility(View.VISIBLE);
                holder.oMapContainer.setVisibility(View.VISIBLE);
                holder.oMapContainer.addView(holder.oMap);
                holder.oMap.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(item.getDouLatitude(),item.getDouLongitude()),1,true);
                holder.oMap.removeAllPOIItems();
                MapPOIItem customMarker = new MapPOIItem();
                customMarker.setItemName("here");
                customMarker.setTag(1);
                customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(item.getDouLatitude(),item.getDouLongitude()));
                customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                customMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                customMarker.setCustomImageResourceId(R.drawable.marker_red);
                customMarker.setShowCalloutBalloonOnTouch(false);
                holder.oMap.addPOIItem(customMarker);
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && holder.oFeedMap.getVisibility() == View.VISIBLE) {
                    // Scrolling up
                    holder.oMap.startAnimation(animScaleAlpha);
                    holder.oFeedMap.setVisibility(View.GONE);
                    holder.oMapContainer.setVisibility(View.GONE);
                    holder.oMap.setVisibility(View.GONE);

                } else if (dy <0 && holder.oFeedMap.getVisibility() == View.VISIBLE){
                    // Scrolling down
                    holder.oMap.startAnimation(animScaleAlpha);
                    holder.oFeedMap.setVisibility(View.GONE);
                    holder.oMapContainer.setVisibility(View.GONE);
                    holder.oMap.setVisibility(View.GONE);

                }
            }
        });

        Picasso.with(context)
                .load(item.getStrThumbnailImage())
                .fit()
                .centerCrop()
                .into(holder.oImageBanner);

        Picasso.with(context)
                .load(item.getStrUserImage())
                .fit()
                .centerCrop()
                .into(holder.oImageUser);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView oTextLike;
        private TextView oTextShare;
        private TextView oTextReply;
        private TextView oTextUserId;
        private TextView oTextContent;
        private TextView oTextLikeCnt;
        private Button oButtonMap;
        private ImageView oImageBanner;
        private ImageView oImageUser;
        private FrameLayout oFeedMap;
        private ViewGroup oMapContainer;
        private MapView oMap;

        ViewHolder(View v) {
            super(v);
            oImageBanner = (ImageView)v.findViewById(R.id.bannerImage);
            oImageUser = (ImageView)v.findViewById(R.id.userImage);
            oButtonMap = (Button)v.findViewById(R.id.mapButton);
            oTextLike = (TextView)v.findViewById(R.id.likeText);
            oTextShare = (TextView)v.findViewById(R.id.shareText);
            oTextReply = (TextView)v.findViewById(R.id.replyContent);
            oTextUserId = (TextView)v.findViewById(R.id.userIdText);
            oTextContent = (TextView)v.findViewById(R.id.contentText);
            oTextLikeCnt = (TextView)v.findViewById(R.id.likeCnt);
            oFeedMap = (FrameLayout)v.findViewById(R.id.mapFrame);
            oMapContainer = (ViewGroup)v.findViewById(R.id.mapFeed);


        }
    }
}
