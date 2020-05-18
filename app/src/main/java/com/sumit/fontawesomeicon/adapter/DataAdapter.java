package com.sumit.fontawesomeicon.adapter;

/**
 * Created by Sumit on 7/27/2017.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sumit.fontawesomeicon.R;
import com.sumit.fontawesomeicon.model.fa.FAIcon;
import com.sumit.fontawesomeicon.util.FontManager;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private final static int FADE_DURATION_IN_MILLI = 500;

    private ArrayList<FAIcon> fontAwesomeIcons;
    private Context context;


    public DataAdapter(Context context, ArrayList<FAIcon> fontAwesomeIcons) {
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

        viewHolder.textViewIconUnicode.setText(fontAwesomeIcons.get(i).getAttributes().getUnicode());
        viewHolder.textViewIconUnicode.setTextColor(fontAwesomeIcons.get(i).getAttributes().getIconColor());

        viewHolder.textViewIconId.setText("#" + (i + 1));
        viewHolder.textViewIconName.setText(fontAwesomeIcons.get(i).getId());
        viewHolder.textViewIconName.setSelected(true);

        // Dynamically change the stroke color
        GradientDrawable gradientDrawable = (GradientDrawable) viewHolder.linearLayoutRoot.getBackground();
        gradientDrawable.setStroke(5, fontAwesomeIcons.get(i).getAttributes().getIconColor());

        //setFadeAnimation(viewHolder.textViewIconUnicode);
        //setFadeAnimation(viewHolder.textViewIconName);
    }

    @Override
    public int getItemCount() {
        return fontAwesomeIcons.size();
    }

    @Override
    public long getItemId(int position) {
        return fontAwesomeIcons.get(position).getSequence();
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayoutRoot;
        private TextView textViewIconUnicode;
        private TextView textViewIconId;
        private TextView textViewIconName;

        ViewHolder(View view) {
            super(view);

            linearLayoutRoot = view.findViewById(R.id.layout_root);
            textViewIconUnicode = view.findViewById(R.id.text_icon_unicode);
            textViewIconId = view.findViewById(R.id.text_icon_id);
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