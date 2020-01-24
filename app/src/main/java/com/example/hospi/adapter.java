package com.example.hospi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.IntentRequiredException;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class adapter extends RecyclerView.Adapter<adapter.holder> {

    private ArrayList<cfdata> results;
    private String hostelName;

    public adapter(ArrayList<cfdata> values,String Hostel){
        this.results = values;
        this.hostelName = Hostel;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

        String name = results.get(position).getName();
        long cap = results.get(position).getCapacity();
        long fil = results.get(position).getFilled();
        holder.card_name.setText(name);
        holder.card_capacity.append(Long.toString(cap));
        holder.card_filled.append(Long.toString(fil));
    }


    @Override
    public int getItemCount() {
        return results.size();
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView card_name,card_capacity,card_filled ;
        public holder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            card_name = itemView.findViewById(R.id.name_on_card);
            card_capacity = itemView.findViewById(R.id.card_capacity);
            card_filled = itemView.findViewById(R.id.card_filled);
        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            Toast.makeText(view.getContext(),Integer.toString(pos)+" "+hostelName,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(view.getContext(),updateScreen.class);
            intent.putExtra("hostelname",hostelName);
            intent.putExtra("roomno",results.get(pos).getName());
            view.getContext().startActivity(intent);
        }
    }

}