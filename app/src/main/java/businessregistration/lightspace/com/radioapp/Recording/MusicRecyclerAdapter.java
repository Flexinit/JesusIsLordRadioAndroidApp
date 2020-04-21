package businessregistration.lightspace.com.radioapp.Recording;

/**
 * Created by user on 8/27/2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import businessregistration.lightspace.com.radioapp.R;

public class MusicRecyclerAdapter extends
        RecyclerView.Adapter<MusicRecyclerAdapter.ViewHolder> {

    private List<AudioModel> songsList;
    private ArrayList<RecordingFiles> list_members = new ArrayList<>();   // to store the list of files
    //inflator is used to create to new objects of custom view we made above. Here we assign it to the context
    // received from activity class.

    View view;
    RecordingsLibrary activity;

    //folderHistory stack is used to keep track of all the folders clicked so that
    //we can go back to the previous folder and refresh recycler view again.
    Stack<String> folderHistory;
    FolderHistory fh;

    Context context;

    public MusicRecyclerAdapter(Context context, RecordingsLibrary activity) {

        this.context = context;
        this.songsList = songsList;
        this.activity = activity;
        fh = new FolderHistory();
        folderHistory = fh.getHistory();
    }


    @NonNull
    @Override
    public MusicRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        View v = LayoutInflater.from(context).inflate(R.layout.music_card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicRecyclerAdapter.ViewHolder viewHolder, int position) {
        RecordingFiles list_items = list_members.get(position);
        //  viewHolder.user_name.setText(list_items.getFileName());
        viewHolder.itemImage.setImageResource(getImageId(list_items.getFileImage()));
        viewHolder.itemTitle.setText(list_items.getFileName());
        viewHolder.itemDetail.setText(list_items.getDetail());
        // viewHolder.time.setText(list_items.);
        // viewHolder.itemDetail.setText(details[position]);
        // viewHolder.itemImage.setImageResource(R.drawable.jesusislordradio2);

    }

    public int getImageId(String s) {

        return context.getResources().getIdentifier("drawable/" + s, null, context.getPackageName());
    }

    public void setListContent(ArrayList<RecordingFiles> list_members) {
        this.list_members = list_members;
        Log.i("Steps", "setListContent");
        notifyItemRangeChanged(0, list_members.size());

    }

    public boolean goBack() {

        if (folderHistory.isEmpty())

            return true;

        folderHistory.pop();

        if (!folderHistory.isEmpty())
            activity.populateRecyclerViewValues(folderHistory.peek());

        return false;
    }

    @Override
    public int getItemCount() {

        return list_members.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView itemImage;
        public TextView itemTitle;
        public TextView itemDetail;
        TextView user_name, content, time;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            //itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDetail = itemView.findViewById(R.id.item_detail);
           // time =  itemView.findViewById(R.id.timeModified);
            itemImage =  itemView.findViewById(R.id.item_image);
            Log.i("Steps","MyViewHolderConstructor");


        }

        @Override
        public void onClick(View view) {
            TextView a =  view.findViewById(R.id.item_title);
            String clicked = a.getText().toString();

            if (folderHistory.isEmpty())
                folderHistory = fh.getHistory();
            String history = folderHistory.peek();
            File clickedFile = new File(history, clicked);

            if (clickedFile.isDirectory()) {
                folderHistory.push(clickedFile.toString());
                activity.populateRecyclerViewValues(clickedFile.toString());
            } else {
                String ext = getExtension(clickedFile.toString());
                activity.playFile(clickedFile.toString(),ext);
            }
        }

        public String getExtension(String fileName)
        {
            int l = fileName.length();

            return fileName.substring(l-3,l);
        }
    }


}