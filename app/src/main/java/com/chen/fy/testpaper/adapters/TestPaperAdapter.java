package com.chen.fy.testpaper.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chen.fy.testpaper.R;
import com.chen.fy.testpaper.beans.TestPaperInfo;

import java.util.List;

public class TestPaperAdapter extends RecyclerView.Adapter<TestPaperAdapter.ViewHolder> {

    private List<TestPaperInfo> list;
    private Context myContext;

    //构造方法,并传入数据源
    public TestPaperAdapter(List<TestPaperInfo> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (myContext == null) {
            myContext = viewGroup.getContext();
        }
        //反射每行的子布局,并把view传入viewHolder中,以便获取控件对象
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.test_paper_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        TestPaperInfo testPaperInfo = list.get(i);

        viewHolder.tvResult.setText(testPaperInfo.getResult());
        viewHolder.imageView.setImageBitmap(testPaperInfo.getImage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 内部类,获取各控件的对象
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView tvResult;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_test_paper_result);
            tvResult = itemView.findViewById(R.id.tv_result);
        }
    }
}
