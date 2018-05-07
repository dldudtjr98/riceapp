package com.dldud.riceapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static android.view.View.VISIBLE;

/**
 * Created by dldud on 2018-04-05.
 */

public class ListAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    private ArrayList<ItemData> m_oData;
    private int nListCnt = 0;
    private MapView mv;

    public ListAdapter(Context context, ArrayList<ItemData> _oData){
        this.inflater = LayoutInflater.from(context);
        this.m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount() {
        Log.i("TAG","getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        final Context context = parent.getContext();
        final ItemData myItemData = m_oData.get(position);
        TaskLike taskLike = new TaskLike();

        final WebView wv;
        final ImageView detailImage;
        final ViewGroup mvContainer;
        final Button viewClose,imgClose,mapClose;


        detailImage = (ImageView) ((Activity)context).findViewById(R.id.detailImage);
        wv = (WebView) ((Activity)context).findViewById(R.id.seeDetailView);
        mvContainer = (ViewGroup)((Activity)context).findViewById(R.id.feedMap);
        viewClose = (Button)((Activity)context).findViewById(R.id.wvCloseBtn);
        imgClose = (Button)((Activity)context).findViewById(R.id.imgCloseBtn);
        mapClose = (Button)((Activity)context).findViewById(R.id.mapCloseBtn);

        if(convertView == null){
            if(inflater == null){
                inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.card,parent,false);
            holder = new ViewHolder();
            holder.oImageBanner = (ImageView)convertView.findViewById(R.id.bannerImage);
            holder.oImageUser = (ImageView)convertView.findViewById(R.id.userImage);
            holder.oButtonMap = (Button)convertView.findViewById(R.id.mapButton);
            holder.oTextLike = (TextView)convertView.findViewById(R.id.likeText);
            holder.oTextShare = (TextView)convertView.findViewById(R.id.shareText);
            holder.oTextReply = (TextView)convertView.findViewById(R.id.replyText);
            holder.oTextUserId = (TextView)convertView.findViewById(R.id.userIdText);
            holder.oTextLikeCnt = (TextView)convertView.findViewById(R.id.likeCnt);

            convertView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
        }

        Picasso.with(convertView.getContext())
                    .load(myItemData.strThumbnailImage)
                    .fit()
                    .centerCrop()
                    .into(holder.oImageBanner);

        Picasso.with(convertView.getContext())
                .load(myItemData.strUserImage)
                .fit()
                .centerCrop()
                .into(holder.oImageUser);

        holder.oTextLike.setText(myItemData.strLike);
        holder.oTextShare.setText(myItemData.strShare);
        holder.oTextReply.setText(myItemData.strReply);
        holder.oTextUserId.setText(myItemData.strUserId);


        holder.oTextLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        holder.oImageBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myItemData.strVideo.substring(myItemData.strVideo.length()-3,myItemData.strVideo.length()).equals("mp4")) {
                    String name = myItemData.strVideo;
                    String type = "video/mp4";
                    wv.setVisibility(VISIBLE);
                    detailImage.setVisibility(View.GONE);
                    wv.loadUrl("http://52.78.18.156/public/playVideo.php?video=" + name + "&type=" + type);
                } else {
                    imgClose.setVisibility(VISIBLE);
                    wv.setVisibility(View.GONE);
                    detailImage.setVisibility(VISIBLE);
                    Picasso.with(context)
                            .load(myItemData.strVideo)
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

        mapClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mv.setVisibility(View.GONE);
                mvContainer.setVisibility(View.GONE);
                mapClose.setVisibility(View.GONE);

            }
        });

        holder.oButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mv = new MapView(context);
                mv.setVisibility(View.VISIBLE);
                mvContainer.setVisibility(View.VISIBLE);
                mapClose.setVisibility(View.VISIBLE);
                mvContainer.addView(mv);
                mv.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(myItemData.douLatitude, myItemData.douLongitude),1, true);
                mv.removeAllPOIItems();
                MapPOIItem customMarker = new MapPOIItem();
                customMarker.setItemName("여기!");
                customMarker.setTag(1);
                customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(myItemData.douLatitude, myItemData.douLongitude));
                customMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                customMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                customMarker.setShowCalloutBalloonOnTouch(false);
                mv.addPOIItem(customMarker);
            }
        });

        return convertView;
    }

    public static class ViewHolder{
        private TextView oTextLike;
        private TextView oTextShare;
        private TextView oTextReply;
        private TextView oTextUserId;
        private TextView oTextLikeCnt;
        private Button oButtonMap;
        private ImageView oImageBanner;
        private ImageView oImageUser;
    }
}
