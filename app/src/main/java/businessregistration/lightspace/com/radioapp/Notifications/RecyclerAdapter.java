package businessregistration.lightspace.com.radioapp.Notifications;

/**
 * Created by user on 8/27/2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import businessregistration.lightspace.com.radioapp.R;

public class RecyclerAdapter extends
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> notificationsList;


    public RecyclerAdapter(Context context, ArrayList<String> notificationsList) {

        this.context = context;
        this.notificationsList = notificationsList;
    }


    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        View v = LayoutInflater.from(context).inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder viewHolder, int position) {

        viewHolder.itemDetail.setText(notificationsList.get(position));
        // viewHolder.itemTitle.setText(titles[position]);
        // viewHolder.itemDetail.setText(details[position]);
        // viewHolder.itemImage.setImageResource(R.drawable.jesusislordradio2);

    }

    @Override
    public int getItemCount() {

        return notificationsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemTitle;
        public TextView itemDetail;

        public ViewHolder(View itemView) {
            super(itemView);

            //itemImage = itemView.findViewById(R.id.item_image);
            //  itemTitle = itemView.findViewById(R.id.item_title);
            itemDetail = itemView.findViewById(R.id.item_detail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }


}