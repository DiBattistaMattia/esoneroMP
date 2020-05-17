package it.thecommits.bluetoothdemo_6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private ArrayList<String> mDataset;
    OnItemClickListener onItemClickListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MyViewHolder(TextView v){
            super(v);
            textView = v;
        }
    }

    public MyAdapter(ArrayList<String> data, OnItemClickListener _onClickListener){
        mDataset = data;
        onItemClickListener = _onClickListener;
    }

    @Override
    public void onClick(View v) {
        int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
        String deviceAddress = mDataset.get(position);
        // TODO: return result to activity for bluetooth connection
        onItemClickListener.OnClick(deviceAddress);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.device_name, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        v.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder h = (MyViewHolder) holder;
        h.textView.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void add(String data){
        mDataset.add(data);
    }
}
