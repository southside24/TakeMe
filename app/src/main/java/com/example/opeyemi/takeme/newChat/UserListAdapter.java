package com.example.opeyemi.takeme.newChat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

import com.example.opeyemi.takeme.FindUserActivity;
import com.example.opeyemi.takeme.NewChatActivity;
import com.example.opeyemi.takeme.R;
import com.example.opeyemi.takeme.common.Common;
import com.example.opeyemi.takeme.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private ArrayList<User> mUserList;
    private ArrayList<ChatObject> mChatList;

    public UserListAdapter(ArrayList<User> userList, ArrayList<ChatObject> chatList) {
        mUserList = userList;
        mChatList = chatList;
    }


    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_user_list, parent, false);

        return new UserListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, final int position) {
        final String key = mUserList.get(position).getPhoneNumber();
        holder.name.setText(mUserList.get(position).getName());
        holder.phone.setText(key);
        String imageUri = mUserList.get(position).getImage();
        if( imageUri != null){
            if (!imageUri.isEmpty()){
                Picasso.with(holder.picture.getContext()).load(mUserList.get(position).getImage()).into(holder.picture);
            }
        }

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //Open the chat activity passing the phone number of the user current user wishes to
                //chat with
                Intent intent = new Intent(view.getContext(), NewChatActivity.class);
                intent.putExtra("chatObject", mChatList.get(position));
                intent.putExtra("userObject", mUserList.get(position));
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class UserListViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phone;
        CircleImageView picture;
        public LinearLayout mLayout;

        public UserListViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_list_name);
            phone = itemView.findViewById(R.id.user_list_phone);
            picture = itemView.findViewById(R.id.user_list_picture);
            mLayout = itemView.findViewById(R.id.layout);

        }
    }
}
