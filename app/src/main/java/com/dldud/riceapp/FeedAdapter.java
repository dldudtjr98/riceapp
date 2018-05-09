package com.dldud.riceapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

/**
 * Created by dldud on 2018-05-03.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    Context context;
    private ArrayList<ItemData> items = new ArrayList<>();
    private RecyclerView recyclerView;

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

        final ItemData item = items.get(position);
        final WebView wv;
        final ImageView detailImage;
        final Button viewClose,imgClose;
        final Animation animScaleAlpha = AnimationUtils.loadAnimation(context,R.anim.anim_scale_alpha);

        recyclerView = (RecyclerView) ((Activity)context).findViewById(R.id.dynamicLayout);
        detailImage = (ImageView) ((Activity)context).findViewById(R.id.detailImage);
        wv = (WebView) ((Activity)context).findViewById(R.id.seeDetailView);
        viewClose = (Button)((Activity)context).findViewById(R.id.wvCloseBtn);
        imgClose = (Button)((Activity)context).findViewById(R.id.imgCloseBtn);


        holder.oTextLike.setText(item.getStrLike());
        holder.oTextShare.setText(item.getStrShare());
        holder.oTextReply.setText(item.getStrReply());
        holder.oTextUserId.setText(item.getStrUserName());
        holder.oTextContent.setText(item.getStrContent());

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
            oTextReply = (TextView)v.findViewById(R.id.replyText);
            oTextUserId = (TextView)v.findViewById(R.id.userIdText);
            oTextContent = (TextView)v.findViewById(R.id.contentText);
            oFeedMap = (FrameLayout)v.findViewById(R.id.mapFrame);
            oMapContainer = (ViewGroup)v.findViewById(R.id.mapFeed);

        }
    }
}
