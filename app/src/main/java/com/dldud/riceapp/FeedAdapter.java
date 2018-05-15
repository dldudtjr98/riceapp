package com.dldud.riceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.usermgmt.response.model.UserProfile;
import com.squareup.picasso.Picasso;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static com.dldud.riceapp.UserProfileSettingActivity.userId;

/**
 * Created by dldud on 2018-05-03.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    Context context;

    private ArrayList<ReplyItemData> rData;

    int distance =0;
    int scrollDirection;

    private ReplyAdapter rAdapter;
    private String getLikeUserString;
    private String replyString;
    private String userString;
    private String likeString;
    private String imgUrl = "http://52.78.18.156/data/riceapp/";
    private String likeInsertUrlPath = "http://52.78.18.156/public/Ping_like_insert.php";
    private String replyInsertUrlPath = "http://52.78.18.156/public/Comment_insert.php";

    private ArrayList<ItemData> items = new ArrayList<>();

    private LinearLayoutManager layoutManager;


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
        final ItemData item = items.get(position);
        rData = new ArrayList<>();

        final WebView wv;
        final ImageView detailImage;
        final Button viewClose,imgClose;
        final Animation animScaleAlpha = AnimationUtils.loadAnimation(context,R.anim.anim_scale_alpha);

        recyclerView = (RecyclerView) ((Activity)context).findViewById(R.id.dynamicLayout);
        detailImage = (ImageView) ((Activity)context).findViewById(R.id.detailImage);
        wv = (WebView) ((Activity)context).findViewById(R.id.seeDetailView);
        viewClose = (Button)((Activity)context).findViewById(R.id.wvCloseBtn);
        imgClose = (Button)((Activity)context).findViewById(R.id.imgCloseBtn);

        final int mTotalScrolled  = recyclerView.computeVerticalScrollOffset();
        holder.oTextLike.setText(item.getStrLike());
        holder.oTextShare.setText(item.getStrShare());
        holder.oTextReply.setText(item.getStrReply());
        holder.oTextUserId.setText(item.getStrUserName());
        holder.oTextContent.setText(item.getStrContent());
        holder.oTextLikeCnt.setText(item.getStrPingLike());
        holder.oTextReplyCnt.setText(item.getStrPingReply());

        holder.replyView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action){
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


        holder.oTextLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try{
                String idx;
                boolean isExistLike = false;
                TaskLike taskLike = new TaskLike();
                TaskUser taskUser = new TaskUser();
                PHPRequestLike request = new PHPRequestLike(likeInsertUrlPath);

                idx = item.getStrIdx();

                userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
                likeString = taskLike.execute("http://52.78.18.156/public/ping_like_db.php").get();

                taskLike.jsonParser(likeString);
                taskUser.jsonParser(userString);

                String[] userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);
                String[] userLink_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);

                String[] pingIdx = taskLike.idx.toArray(new String[taskLike.idx.size()]);
                String[] pingUserIdx = taskLike.user_idx.toArray(new String[taskLike.user_idx.size()]);
                String[] pingPingIdx = taskLike.ping_idx.toArray(new String[taskLike.ping_idx.size()]);

                int userLinearNum = userIdx.length;
                int pingLinearNum = pingIdx.length;

                for(int i = 0 ; i < userLinearNum ; i++){
                    String val = userLink_id[i];
                    if(val.contains(userId)){
                        getLikeUserString = userIdx[i];
                    }
                }

                for(int j = 0; j< pingLinearNum ; j++){
                    String s1 = pingUserIdx[j];
                    String s2 = pingPingIdx[j];
                    if(s2.contains(idx) && s1.contains(getLikeUserString)){
                        Toast.makeText(context,"이미 좋아요를 누른 게시물입니다",Toast.LENGTH_LONG).show();
                        isExistLike = true;
                        break;
                    }
                }
                if(!isExistLike) {
                    request.PhPtest(getLikeUserString, idx);
                    holder.oTextLike.setTypeface(null, Typeface.BOLD);
                    holder.oTextLike.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);
                    int getCnt = Integer.parseInt(item.getStrPingLike());
                    getCnt++;
                    holder.oTextLikeCnt.setText(String.valueOf(getCnt));
                }

                holder.reply.setVisibility(View.GONE);
                holder.replyView.setVisibility(View.GONE);

            }catch(InterruptedException e){
                e.printStackTrace();
            }catch(ExecutionException e){
                e.printStackTrace();
            }catch(MalformedURLException e){
                e.printStackTrace();
            }
            }
        });

        holder.oTextShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"공유기능은 아직 지원하지 않습니다",Toast.LENGTH_LONG).show();
            }
        });

        holder.oTextReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                if (holder.reply.getVisibility() == View.VISIBLE) {
                    holder.reply.setVisibility(View.GONE);
                } else {
                    holder.oTextReply.setTypeface(null, Typeface.BOLD);
                    holder.oTextReply.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
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


                    for (int i = 0; i < replyNum; i++) {
                        String val = replyPing[replyNum - (i + 1)];
                        if (val.contains(idx)) {
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
                            notifyDataSetChanged();
                        }
                    }

                    layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    holder.replyView.setLayoutManager(layoutManager);
                    rAdapter = new ReplyAdapter(context, rData);
                    holder.replyView.setAdapter(rAdapter);

                    holder.reply.setVisibility(View.VISIBLE);
                    holder.replyView.setVisibility(View.VISIBLE);
                }
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

                    holder.reply.setVisibility(View.GONE);
                    holder.replyView.setVisibility(View.GONE);
                    holder.oTextReply.setTypeface(null, Typeface.NORMAL);
                } else {
                    imgClose.setVisibility(View.VISIBLE);
                    wv.setVisibility(View.GONE);
                    detailImage.setVisibility(View.VISIBLE);
                    Picasso.with(detailImage.getContext())
                            .load(item.getStrVideo())
                            .into(detailImage);

                    holder.reply.setVisibility(View.GONE);
                    holder.replyView.setVisibility(View.GONE);
                    holder.oTextReply.setTypeface(null, Typeface.NORMAL);
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

        holder.replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                EditText insertReply;
                insertReply = (EditText) ((Activity)context).findViewById(R.id.comment);
                TaskUser taskUser = new TaskUser();
                String idx;
                String replyContent;
                PHPRequestReply request = new PHPRequestReply(replyInsertUrlPath);

                replyContent = insertReply.getText().toString();

                userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
                taskUser.jsonParser(userString);

                String[] userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);
                String[] userLink_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);

                int userLinearNum = userIdx.length;

                for(int i = 0 ; i < userLinearNum ; i++){
                    String val = userLink_id[i];
                    if(val.contains(userId)){
                        getLikeUserString = userIdx[i];
                    }
                }

                if(replyContent.equals("")){
                    Toast.makeText(context, "댓글을 입력 후 확인버튼을 눌러주세요", Toast.LENGTH_LONG).show();
                }else {
                    idx = item.getStrIdx();

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formatDate = sdfNow.format(date);

                    request.PhPtest(getLikeUserString, idx, replyContent, formatDate);
                    Toast.makeText(context, "댓글을 남겼습니다", Toast.LENGTH_LONG).show();
                    holder.reply.setVisibility(View.GONE);
                    holder.replyView.setVisibility(View.GONE);

                    //keyboard gone
                    InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(insertReply.getWindowToken(), 0);
                    notifyDataSetChanged();

                    int getCnt = Integer.parseInt(item.getStrPingReply());
                    getCnt++;
                    holder.oTextReplyCnt.setText(String.valueOf(getCnt));

                    //Edittext initialize
                    insertReply.setText("");
                    insertReply.setHint("댓글을 입력해주세요");

                    holder.oTextReply.setTypeface(null, Typeface.NORMAL);
                    holder.oTextReply.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                }

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }catch (ExecutionException e){
                e.printStackTrace();
            }
            }
        });

        holder.oButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(holder.oFeedMap.getVisibility() == View.VISIBLE){
                holder.oMap.setVisibility(View.GONE);
                holder.oMapContainer.setVisibility(View.GONE);
                holder.oFeedMap.setVisibility(View.GONE);
            } else {
                holder.oFeedMap.setVisibility(View.VISIBLE);
                holder.oMap = new MapView(context);
                holder.oMap.setVisibility(View.VISIBLE);
                holder.oMapContainer.setVisibility(View.VISIBLE);
                holder.oMapContainer.addView(holder.oMap);
                holder.oMap.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(item.getDouLatitude(), item.getDouLongitude()), 1, true);
                holder.oMap.removeAllPOIItems();
                MapPOIItem customMarker = new MapPOIItem();
                customMarker.setItemName("here");
                customMarker.setTag(1);
                customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(item.getDouLatitude(), item.getDouLongitude()));
                customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                customMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                customMarker.setCustomImageResourceId(R.drawable.marker_red);
                customMarker.setShowCalloutBalloonOnTouch(false);
                holder.oMap.addPOIItem(customMarker);

                holder.reply.setVisibility(View.GONE);
                holder.replyView.setVisibility(View.GONE);
            }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
                scrollDirection = dy;
                distance+=dy;
                Log.d("TAG", "onScrolled: " + dy);

                if (dy > 0 && holder.oFeedMap.getVisibility() == View.VISIBLE) {
                    // Scrolling up
                    holder.oMap.startAnimation(animScaleAlpha);
                    holder.oFeedMap.setVisibility(View.GONE);
                    holder.oMapContainer.setVisibility(View.GONE);
                    holder.oMap.setVisibility(View.GONE);
                    holder.oTextReply.setTypeface(null, Typeface.NORMAL);
                    holder.oTextReply.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);

                } else if (dy <0 && holder.oFeedMap.getVisibility() == View.VISIBLE){
                    // Scrolling down
                    holder.oMap.startAnimation(animScaleAlpha);
                    holder.oFeedMap.setVisibility(View.GONE);
                    holder.oMapContainer.setVisibility(View.GONE);
                    holder.oMap.setVisibility(View.GONE);

                    holder.oTextReply.setTypeface(null, Typeface.NORMAL);
                    holder.oTextReply.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                } else if(dy >0){
                    holder.oTextReply.setTypeface(null, Typeface.NORMAL);
                    holder.oTextReply.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                } else if(dy <0){
                    holder.oTextReply.setTypeface(null, Typeface.NORMAL);
                    holder.oTextReply.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                }
                if(distance > 25000){
                    holder.reply.setVisibility(View.GONE);
                    holder.replyView.setVisibility(View.GONE);
                    //keyboard gone
                    EditText insertReply;
                    insertReply = (EditText) ((Activity)context).findViewById(R.id.comment);
                    InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(insertReply.getWindowToken(), 0);
                } else if(distance < -25000){
                    holder.reply.setVisibility(View.GONE);
                    holder.replyView.setVisibility(View.GONE);
                    //keyboard gone
                    EditText insertReply;
                    insertReply = (EditText) ((Activity)context).findViewById(R.id.comment);
                    InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(insertReply.getWindowToken(), 0);
                }
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (scrollDirection > 0) {
                        Log.d("TAG", "onScrollStateChanged: " + " STOPPED " + distance);
                        distance = 0;
                    } else {
                        Log.d("TAG", "onScrollStateChanged: " + " STOPPED " + distance);
                        distance = 0;
                    }
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
        private ImageView oFilterImage;
        private TextView oTextReplyCnt;
        private FrameLayout oFeedMap;
        private ViewGroup oMapContainer;
        private MapView oMap;
        private RelativeLayout reply;
        private Button replyBtn;
        private RecyclerView replyView;

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
            oTextReplyCnt = (TextView)v.findViewById(R.id.replyCnt);
            oFeedMap = (FrameLayout)v.findViewById(R.id.mapFrame);
            oMapContainer = (ViewGroup)v.findViewById(R.id.mapFeed);
            oFilterImage = (ImageView)v.findViewById(R.id.filterImage);
            reply = (RelativeLayout) v.findViewById(R.id.replyLayout);
            replyView = (RecyclerView)v.findViewById(R.id.replyCard);
            replyBtn = (Button) v.findViewById(R.id.replyBtn);

        }
    }
}
