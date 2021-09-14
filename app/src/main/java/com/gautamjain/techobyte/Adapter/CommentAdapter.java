package com.gautamjain.techobyte.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gautamjain.techobyte.Modal.Comment;
import com.gautamjain.techobyte.Modal.User;
import com.gautamjain.techobyte.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.ViewHolder>
{
    private Context mContext;
    private List<Comment> mComments;
    private FirebaseUser firebaseUser;

    String postId;

    public CommentAdapter(Context mContext, List<Comment> mComments,  String postId) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Comment commentDescription = mComments.get(position);

        holder.commentDescription.setText(commentDescription.getComment());

        FirebaseDatabase.getInstance().getReference().child("Users").child(commentDescription.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       User user = snapshot.getValue(User.class);

                       holder.username.setText(user.getUsername());

                       if(user.getImageUrl().equals("default"))
                       {
                           holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                       }
                       else
                       {
                           Picasso.get().load(user.getImageUrl()).into(holder.imageProfile);
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView imageProfile;
        public TextView username;
        public TextView commentDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile_comment);
            username = itemView.findViewById(R.id.id_username_comment);
            commentDescription = itemView.findViewById(R.id.id_comment_description);
        }
    }


}
