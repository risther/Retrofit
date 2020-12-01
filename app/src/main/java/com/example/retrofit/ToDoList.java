package com.example.retrofit;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ToDoList extends BaseAdapter{
    private Activity context;
    List<ToDo> toDos;

    public ToDoList(Activity context, List<ToDo> toDos) {
        this.context = context;
        this.toDos = toDos;
    }

    public class ViewHolder {
        TextView tvTitle;
        TextView tvUserId;
        TextView tvCompleted;
    }

    @Override
    public int getCount() {
        if (toDos.size() <= 0){
            return 1;
        }
        return toDos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            row = inflater.inflate(R.layout.list_item,null,true);
            viewHolder.tvTitle = (TextView)row.findViewById(R.id.tvTitle);
            viewHolder.tvUserId = (TextView)row.findViewById(R.id.tvUserId);
            viewHolder.tvCompleted = (TextView)row.findViewById(R.id.tvCompleted);
            row.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvTitle.setText(toDos.get(position).getTitle());
        viewHolder.tvUserId.setText(context.getString(R.string.userId)+Integer.toString(toDos.get(position).getUserId()));
        if(toDos.get(position).getCompleted()){
            viewHolder.tvCompleted.setTextColor(context.getResources().getColor(R.color.comnpleted));
            viewHolder.tvCompleted.setText(context.getString(R.string.completed));
        }else{
            viewHolder.tvCompleted.setTextColor(context.getResources().getColor(R.color.uncompleted));
            viewHolder.tvCompleted.setText(context.getString(R.string.uncompleted));
        }
        return row;
    }
}
