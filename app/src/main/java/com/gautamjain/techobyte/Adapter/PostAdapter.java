package com.gautamjain.techobyte.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.gautamjain.techobyte.CommentActivity;
import com.gautamjain.techobyte.Fragments.PostDetailFragment;
import com.gautamjain.techobyte.Fragments.ProfileFragment;
import com.gautamjain.techobyte.Modal.Post;
import com.gautamjain.techobyte.Modal.User;
import com.gautamjain.techobyte.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder>{

    private Context mContext;
    private List<Post> mPost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.Viewholder holder, int position) {

        final Post post = mPost.get(position);
        Picasso.get().load(post.getImageUrl()).into(holder.imagePost);
        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(user.getImageUrl().equals("default"))
                {
                    holder.profileImage.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher);
                }

                holder.username.setText(user.getUsername());
                holder.author.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {}
        });

//       <------------------------------------ Like Button -------------------------------------------->

        isLiked(post.getPostId(), holder.like); // like Button
        noOfLikes(post.getPostId(), holder.no_of_likes); // Count likes

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like"))// tag-> like -> if User not liked
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

//      <------------------------------------ Add Comment Activity -------------------------------------->

        noOfComments(post.getPostId(), holder.no_of_comments);

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.no_of_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

//        <------------------------------------- Saved Images------------------------------------------->

        isSaved(post.getPostId(), holder.save);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostId()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostId()).removeValue();
                }
            }
        });

//        // Redirecting to profile of user
//        holder.profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
//                        .putString("profileId", post.getPublisher()).apply();
//
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
//            }
//        });
//
//        // Redirecting to profile of user
//        holder.username.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
//                        .putString("profileId", post.getPublisher()).apply();
//
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
//            }
//        });
//
//        // Redirecting to profile of user
//        holder.author.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
//                        .putString("profileId", post.getPublisher()).apply();
//
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
//            }
//        });

        // Redirecting to Post Details
        holder.imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId", post.getPostId()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

    }

    private void isSaved(String postId, ImageView save) {

        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(postId).exists())
                        {
                            save.setImageResource(R.drawable.ic_saved_black);
                            save.setTag("saved");
                        }
                        else
                        {
                            save.setImageResource(R.drawable.ic_save);
                            save.setTag("save");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

//    <------------------------------------ is Liked ------------------------------------------------>

    private void isLiked(String postId, ImageView imageView)
    {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists())//If Liked
                {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }
                else
                {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

//    <------------------------------------ Count No of Likes ---------------------------------------->

    private void noOfLikes(String postid, TextView text_Likes)
    {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text_Likes.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

//    <-------------------------------------- Count no of Comments -------------------------------------->

    private void noOfComments(String postid, TextView text_comments)
    {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text_comments.setText("View all " + snapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public ImageView profileImage;
        public ImageView imagePost;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;

        public TextView username;
        public TextView no_of_likes;
        public TextView author;
        public TextView no_of_comments;
        public SocialTextView description;

        public Viewholder(View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image_post);
            imagePost = itemView.findViewById(R.id.id_Image_post);
            like = itemView.findViewById(R.id.id_like);
            save = itemView.findViewById(R.id.id_save);
            comment = itemView.findViewById(R.id.id_comment);
            more = itemView.findViewById(R.id.id_more);

            username = itemView.findViewById(R.id.username_post);
            no_of_likes = itemView.findViewById(R.id.id_no_of_likes);
            no_of_comments = itemView.findViewById(R.id.id_no_of_Comments);
            author = itemView.findViewById(R.id.id_authorname);
            description = itemView.findViewById(R.id.description);
        }
    }
}
