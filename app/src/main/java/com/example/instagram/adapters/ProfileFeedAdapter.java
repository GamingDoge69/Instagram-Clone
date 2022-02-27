package com.example.instagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.databinding.PostListItemBinding;
import com.example.instagram.databinding.ProfilePostListItemBinding;
import com.example.instagram.models.Post;

public class ProfileFeedAdapter extends PagedListAdapter<Post, ProfileFeedAdapter.UserPostViewHolder> {
    Context context;

    public ProfileFeedAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Post>() {
            @Override
            public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                return oldItem.getObjectId().equals(newItem.getObjectId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.context = context;
    }



    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.profile_post_list_item, parent, false);

        return new UserPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    public class UserPostViewHolder extends RecyclerView.ViewHolder {
        ProfilePostListItemBinding binding;

        public UserPostViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ProfilePostListItemBinding.bind(itemView);
        }

        public void bind(Post post) {
            binding.setPost(post);
        }
    }
}
