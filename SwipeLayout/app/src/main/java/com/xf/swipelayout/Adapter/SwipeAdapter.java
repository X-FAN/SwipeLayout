package com.xf.swipelayout.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xf.swipelayout.R;
import com.xf.swipelayout.widget.SwipeLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by X-FAN on 2016/12/2.
 */

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.ViewHolder> {

    private final String TAG = "SwipeAdapter";
    private int mOpenPosition = -1;

    private List<String> mStrings;

    private SwipeLayout mOpenSwipeLayout;


    public SwipeAdapter(List<String> strings) {
        this.mStrings = strings;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_swipe, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "删除" + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenPosition = -1;
                viewHolder.swipeLayout.smoothClose();
            }
        });
        viewHolder.swipeLayout.setSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onOpenListener(SwipeLayout swipeLayout) {
                if (mOpenSwipeLayout != null && mOpenSwipeLayout != swipeLayout) {
                    mOpenSwipeLayout.smoothClose();
                }
                mOpenSwipeLayout = swipeLayout;
                mOpenPosition = viewHolder.getAdapterPosition();
                Log.d(TAG, "mOpenPosition onOpenListener" + mOpenPosition);
            }

            @Override
            public void onCloseListener(SwipeLayout swipeLayout) {
                mOpenPosition = -1;
                Log.d(TAG, "mOpenPosition onCloseListener" + mOpenPosition);
            }

            @Override
            public void onClickListener() {
                Toast.makeText(context, "item click" + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SwipeLayout swipeLayout = holder.swipeLayout;
        if (mOpenPosition == position && swipeLayout.getState() == SwipeLayout.CLOSE) {
            swipeLayout.setState(SwipeLayout.OPEN);
            mOpenSwipeLayout = swipeLayout;
        } else if (swipeLayout.getState() == SwipeLayout.OPEN) {
            swipeLayout.setState(SwipeLayout.CLOSE);
        }
        String str = mStrings.get(position);
        holder.test.setText(str);
    }

    @Override
    public int getItemCount() {
        return mStrings == null ? 0 : mStrings.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.delete)
        View delete;
        @Bind(R.id.edit)
        View edit;
        @Bind(R.id.test)
        TextView test;
        @Bind(R.id.swipe_layout)
        SwipeLayout swipeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
