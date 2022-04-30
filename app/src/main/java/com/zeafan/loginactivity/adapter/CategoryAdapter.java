package com.zeafan.loginactivity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.activity.GroupsListActivity;
import com.zeafan.loginactivity.data.Category;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {
    Context context;
	LayoutInflater inflater;
	ArrayList<Category> categories;
	GroupsListActivity.CategorySelected onSelectedItem;
	public CategoryAdapter(Context context, ArrayList<Category> map, GroupsListActivity.CategorySelected onSelectedItem) {
		this.categories = map;
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onSelectedItem = onSelectedItem;
	}
	@NonNull
	@Override
	public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new Holder(inflater.inflate(R.layout.category_list_row,parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull Holder holder, int position) {
		holder.category = categories.get(position);
		holder.Index = position;
		holder.tvItemTitle.setText(holder.category.CategoryName);
		holder.tvItemLatinName.setText(holder.category.LatinCategoryName);
		if(!holder.category.ImagePath.isEmpty()) {
					Picasso.with(context).load(holder.category.ImagePath)
							.resize(context.getResources().getDimensionPixelSize(R.dimen.d_60_70_90), context.getResources().getDimensionPixelSize(R.dimen.d_60_70_90))
							.placeholder(R.drawable.image)
							.error(R.drawable.image)
							.into(holder.ivItemIcon);
		}else {
			holder.ivItemIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.image));
		}

		holder.layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onSelectedItem.onCategorySelected(holder.Index);
			}
		});
	}

	@Override
	public int getItemCount() {
		return categories.size();
	}
	public class  Holder extends RecyclerView.ViewHolder{
		LinearLayout layout;
		TextView tvItemTitle;
		TextView tvItemLatinName;
		CircleImageView ivItemIcon;
		Category category;
		int Index;
		public Holder(@NonNull View itemView) {
			super(itemView);
			 layout = itemView.findViewById(R.id.layout);
			 tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
			 tvItemLatinName = itemView.findViewById(R.id.tvItemLatinName);
			 ivItemIcon = itemView.findViewById(R.id.ivItemIcon);
		}
	}
}
