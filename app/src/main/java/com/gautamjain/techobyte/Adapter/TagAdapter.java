package com.gautamjain.techobyte.Adapter;

import android.content.Context;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gautamjain.techobyte.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

    private Context mContext;
    private List<String> mTags;
    private List<String> mtagCount;

    public TagAdapter(Context mContext, List<String> mTags, List<String> mtagCount) {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mtagCount = mtagCount;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item, parent,false);
        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, int position) {

         holder.tag.setText("# " + mTags.get(position));
         holder.noOfPosts.setText(mtagCount.get(position) + " Posts");

    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tag;
        private TextView noOfPosts;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);

            tag = itemView.findViewById(R.id.hash_tag);
            noOfPosts = itemView.findViewById(R.id.no_of_posts);
        }
    }

    public void filter(List<String> Filtertags, List<String> filterTagsCount)
    {
        this.mTags = Filtertags;
        this.mtagCount = filterTagsCount;

        notifyDataSetChanged();
    }

}
