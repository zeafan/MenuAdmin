package com.zeafan.loginactivity.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.activity.ItemsListActivity;
import com.zeafan.loginactivity.data.ProductItem;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
public class ProductItemsAdapter extends RecyclerView.Adapter<ProductItemsAdapter.Holder> {
    Context context;
	LayoutInflater inflater;
	ArrayList<ProductItem> orderItemsList;
	ItemsListActivity.ProductSelect onSelectedItem;
	public ProductItemsAdapter(Context context, ArrayList<ProductItem> map, ItemsListActivity.ProductSelect onSelectedItem) {
		this.orderItemsList = map;
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onSelectedItem = onSelectedItem;
	}
	@NonNull
	@Override
	public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new Holder(inflater.inflate(R.layout.item_list_row,parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull Holder holder, int position) {
		holder.ProductItem = orderItemsList.get(position);
		holder.Index = position;
		holder.tvItemTitle.setText(holder.ProductItem.itemName);
		holder.tvItemDetails.setText(holder.ProductItem.describeItem);
		holder.tvItemPrice.setText(String.valueOf(holder.ProductItem.price));
		holder.tvItemUnit.setText(String.valueOf(holder.ProductItem.UnitName));
		if(!holder.ProductItem.imagePaths.isEmpty()) {
					Picasso.with(context).load(holder.ProductItem.imagePaths)
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
				onSelectedItem.onProductSelected(holder.Index);
			}
		});
	}

	@Override
	public int getItemCount() {
		return orderItemsList.size();
	}
	public class  Holder extends RecyclerView.ViewHolder{
		LinearLayout layout;
		TextView tvItemTitle;
		TextView tvItemPrice;
		TextView tvItemDetails;
		CircleImageView ivItemIcon;
		TextView tvItemUnit;
		ProductItem ProductItem;
		
		int Index;
		public Holder(@NonNull View itemView) {
			super(itemView);
			 layout = itemView.findViewById(R.id.layout);
			 tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
			 tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
			 tvItemDetails = itemView.findViewById(R.id.tvItemDetails);
			 ivItemIcon = itemView.findViewById(R.id.ivItemIcon);
			tvItemUnit = itemView.findViewById(R.id.tvItemUnitName);
		}
	}
}
