package com.example.mine.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 多种条目样例
 */
/*public class MultiTypeAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<String> data = new ArrayList<>();
    public static final int TYPE_PRODUCT = 0;
    public static final int TYPE_STORES = 1;
    public static final int TYPE_SUPPORT = 2;

    public SubResultAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if ("".equals("0")) {
            return TYPE_STORES;
        } else if ("".equals("1")) {
            return TYPE_SUPPORT;
        } else {
            return TYPE_PRODUCT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_STORES) {
            view = View.inflate(context, R.layout.item_store_result_list, null);
            return new StoreItemViewHolder(view);
        } else if (viewType == TYPE_SUPPORT) {
            view = View.inflate(context, R.layout.item_support_result_list, null);
            return new SupportItemViewHolder(view);
        } else {
            view = View.inflate(context, R.layout.item_product_result_list, null);
            return new ProductItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ProductItemViewHolder) {
            ProductItemViewHolder productItemViewHolder = (ProductItemViewHolder) holder;

        } else if (holder instanceof StoreItemViewHolder) {
            StoreItemViewHolder storeItemViewHolder = (StoreItemViewHolder) holder;

        } else if (holder instanceof SupportItemViewHolder) {
            SupportItemViewHolder supportItemViewHolder = (SupportItemViewHolder) holder;

        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    static class ProductItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.sdv_product_item_img)
        ImageView productImg;
        @BindView(R2.id.tv_item_title)
        CustomTextView titleTv;
        @BindView(R2.id.tv_item_num)
        CustomTextView numTv;
        @BindView(R2.id.tv_item_price)
        CustomTextView priceTv;

        ProductItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class StoreItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_item_title)
        CustomTextView titleTv;
        @BindView(R2.id.tv_item_location)
        CustomTextView locationTv;
        @BindView(R2.id.tv_item_time)
        CustomTextView timeTv;
        @BindView(R2.id.tv_item_phone)
        CustomTextView phoneTv;
        @BindView(R2.id.tv_item_details)
        CustomTextView detailsTv;

        StoreItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class SupportItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_item_title)
        CustomTextView titleTv;
        @BindView(R2.id.tv_item_tip)
        CustomTextView tipTv;
        @BindView(R2.id.tv_item_describe)
        CustomTextView describeTv;
        @BindView(R2.id.tv_item_net)
        CustomTextView netTv;

        SupportItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}*/
