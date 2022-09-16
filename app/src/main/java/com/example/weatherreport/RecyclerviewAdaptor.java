package com.example.weatherreport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerviewAdaptor  extends RecyclerView.Adapter<RecyclerviewAdaptor.ViewHolder> {

    private Context context;
    private ArrayList<Recyclerviewmodel> RecyclerviewmodelArraylist;

    public RecyclerviewAdaptor(Context context, ArrayList<Recyclerviewmodel> recyclerviewmodelArraylist) {
        this.context = context;
        RecyclerviewmodelArraylist = recyclerviewmodelArraylist;
    }

    @NonNull
    @Override
    public RecyclerviewAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewAdaptor.ViewHolder holder, int position) {
        Recyclerviewmodel modal= RecyclerviewmodelArraylist.get(position);
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.condition);
        holder.tvtemperature.setText(modal.getTemperature()+"°C");
        holder.windspeed.setText(modal.getWindspeed()+"km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");

        try{
            Date d= input.parse(modal.getTime());
            holder.time.setText(output.format(d));
        }catch (ParseException e){
               e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return RecyclerviewmodelArraylist.size();
    }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView condition;
            private TextView time,tvtemperature,windspeed;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            condition=itemView.findViewById(R.id.weathercondition);
            time=itemView.findViewById(R.id.time);
            tvtemperature=itemView.findViewById(R.id.tvtemperature);
            windspeed=itemView.findViewById(R.id.windspeed);
        }
    }
}
