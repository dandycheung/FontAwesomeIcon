package com.sumit.fontawesomeicon.adapter;

/**
 * Created by Sumit on 7/27/2017.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumit.fontawesomeicon.R;
import com.sumit.fontawesomeicon.model.FontAwesomeIcon;
import com.sumit.fontawesomeicon.util.FontManager;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private final static int FADE_DURATION_IN_MILLI = 500;

    private ArrayList<FontAwesomeIcon> fontAwesomeIcons;
    private Context context;


    public DataAdapter(Context context,ArrayList<FontAwesomeIcon> fontAwesomeIcons) {
        setHasStableIds(true);
        this.fontAwesomeIcons = fontAwesomeIcons;
        this.context = context;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        viewHolder.textViewIconUnicode.setText(fontAwesomeIcons.get(i).getIconUnicode());
        viewHolder.textViewIconUnicode.setTextColor(fontAwesomeIcons.get(i).getIconColor());
        viewHolder.textViewIconName.setText(fontAwesomeIcons.get(i).getIconClassName());

        // Dynamically change the stroke color
        GradientDrawable gradientDrawable = (GradientDrawable) viewHolder.linearLayoutRoot.getBackground();
        gradientDrawable.setStroke(5, fontAwesomeIcons.get(i).getIconColor());

        //setFadeAnimation(viewHolder.textViewIconUnicode);
        //setFadeAnimation(viewHolder.textViewIconName);
    }

    @Override
    public int getItemCount() {
        return fontAwesomeIcons.size();
    }

    @Override
    public long getItemId(int position) {
        return fontAwesomeIcons.get(position).getId();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        // Prevent animation on fast scrolling
        /*if(((ViewHolder) holder).textViewIconUnicode != null && ((ViewHolder) holder).textViewIconName != null) {
            ((ViewHolder) holder).textViewIconUnicode.clearAnimation();
            ((ViewHolder) holder).textViewIconName.clearAnimation();
        }*/

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayoutRoot;
        private TextView textViewIconUnicode;
        private TextView textViewIconName;

        ViewHolder(View view) {
            super(view);

            linearLayoutRoot = view.findViewById(R.id.layout_root);
            textViewIconUnicode = view.findViewById(R.id.text_icon_unicode);
            textViewIconName = view.findViewById(R.id.text_icon_name);

            Typeface iconFont = FontManager.getTypeface(context.getApplicationContext(), FontManager.FONT_AWESOME);
            FontManager.markAsIconContainer(textViewIconUnicode, iconFont);

        }
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION_IN_MILLI);
        view.startAnimation(anim);
    }

}