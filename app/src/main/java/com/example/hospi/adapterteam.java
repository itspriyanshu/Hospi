package com.example.hospi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.IntentRequiredException;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class adapterteam extends RecyclerView.Adapter<adapterteam.holder> {

    private ArrayList<model> teams;

    public adapterteam(ArrayList<model> team){
        this.teams = team;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardteam,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

        String no = Long.toString(position+1);
        String name = teams.get(position).getClg().getClgName();
        long len = teams.get(position).getClg().getSize();
        holder.snoofclg.setText(no);
        holder.clgname.setText(name);
        holder.size.setText(Long.toString(len));
    }


    @Override
    public int getItemCount() {
        return teams.size();
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView snoofclg,clgname, size ;
        public holder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            snoofclg = itemView.findViewById(R.id.noofclgtv);
            clgname = itemView.findViewById(R.id.nameofclgtv);
            size = itemView.findViewById(R.id.sizeofclgtv);
        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            Log.i("Selected",Integer.toString(getItemCount()));

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            //Log.i("information",teams.get(pos).getClg().getNumber());

            builder.setMessage("Team Name : "+teams.get(pos).getClg().getClgName()+"\n"+"Leader Name : "
            +teams.get(pos).getClg().getLeaderName()+"\n"+"Number : "+teams.get(pos).getClg().getnumber())
                    .setTitle("Details");

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

}