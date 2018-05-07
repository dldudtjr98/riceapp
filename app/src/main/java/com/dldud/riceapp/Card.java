package com.dldud.riceapp;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;

/**
 * Created by dldud on 2018-04-04.
 * Using in FeedFragment (Dynamic Layout)
 */


public class Card extends ConstraintLayout {
    public Card(Context context) {
        super(context);

        init(context);
    }
    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.card,this,true);
    }
}
