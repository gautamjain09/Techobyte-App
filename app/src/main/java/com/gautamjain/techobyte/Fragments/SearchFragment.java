package com.gautamjain.techobyte.Fragments;

import android.accessibilityservice.GestureDescription;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gautamjain.techobyte.Adapter.TagAdapter;
import com.gautamjain.techobyte.Adapter.UserAdapter;
import com.gautamjain.techobyte.Modal.User;
import com.gautamjain.techobyte.R;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    private RecyclerView recyclerViewTags;
    private List<String> mHashtags;
    private List<String> mHashtagsCount;
    private TagAdapter mtagAdapter;

    private SocialAutoCompleteTextView search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


//      <----------------------------- Listing users on Search --------------------------------->

        recyclerView = view.findViewById(R.id.Recycler_view_search_Fragments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        userAdapter  = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);


//      <----------------------------- Listing users on Search --------------------------------->

        recyclerViewTags = view.findViewById(R.id.Recycler_view_tags_search_Fragments);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

        mHashtags = new ArrayList<>();
        mHashtagsCount = new ArrayList<>();
        mtagAdapter = new TagAdapter(getContext(), mHashtags, mHashtagsCount);
        recyclerViewTags.setAdapter(mtagAdapter);

        search_bar = view.findViewById(R.id.search_bar_search_fragments);

        readUsers();
        readTags();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return view;
    }

    private void readTags()
    {
        DatabaseReference db_reff = FirebaseDatabase.getInstance().getReference().child("Hashtags");
        db_reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHashtags.clear();
                mHashtagsCount.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    mHashtags.add(dataSnapshot.getKey());
                    mHashtagsCount.add(dataSnapshot.getChildrenCount() + "");
                }
                mtagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {}
        });
    }

    private void readUsers()
    {
        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child("Users");
        db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(search_bar.getText().toString()))
                {
                    mUsers.clear();
                    for(DataSnapshot datasnapshot : snapshot.getChildren())
                    {
                        User user = datasnapshot.getValue(User.class);
                        mUsers.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void searchUser(String s)
    {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void filter(String txt)
    {
        List<String> mSearchtags = new ArrayList<>();
        List<String> mSearchtagsCount = new ArrayList<>();

        for(String s : mHashtags)
        {
            if(s.toLowerCase().contains(txt.toLowerCase()))
            {
                mSearchtags.add(s);
                mSearchtagsCount.add(mHashtagsCount.get(mHashtags.indexOf(s)));
            }
        }

        mtagAdapter.filter(mSearchtags, mSearchtagsCount);

    }

}