package com.hvtechnologies.expensesapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class ExpListAdapter extends RecyclerView.Adapter<ExpListAdapter.ExpViewHolder> {

    private List<ExpClass> mList ;
    private Context mContext ;

    private OnNoteListener mOnNoteListener ;

    public ExpListAdapter(List<ExpClass> mList, Context mContext, OnNoteListener mOnNoteListener) {
        this.mList = mList;
        this.mContext = mContext;
        this.mOnNoteListener = mOnNoteListener;
    }

    @NonNull
    @Override
    public ExpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_view_history , parent , false);
        return new ExpViewHolder(view , mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpViewHolder holder, int position) {

        ExpClass expClass = mList.get(position);
        holder.Date.setText("Date : " + expClass.getDate());

        if(expClass.isCredit()){

            holder.txtInfo.setText("+" + expClass.getAmount() + "\n" + expClass.getNote());

        }else {

            holder.txtInfo.setText("-" + expClass.getAmount() + "\n" + expClass.getNote());

        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ExpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtInfo , Date;

        OnNoteListener onNoteListener ;

        public ExpViewHolder(View itemview , OnNoteListener onNoteListener){
            super(itemview);

            txtInfo = (TextView)itemview.findViewById(R.id.Info);
            Date = (TextView)itemview.findViewById(R.id.Date);
            this.onNoteListener = onNoteListener;

            itemview.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onNoteListener.OnNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{
        void OnNoteClick(int position);
    }

}
