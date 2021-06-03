package com.example.comarcalendarioapp.gui.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comarcalendarioapp.R;
import com.example.comarcalendarioapp.model.TaskFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TaskAdapter extends FirestoreRecyclerAdapter<TaskFirestore, TaskAdapter.ViewHolder> {

    //Clase con el adaptador del RecyclerView que recoge los datos de Firestore
    public TaskAdapter(@NonNull FirestoreRecyclerOptions<TaskFirestore> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull TaskFirestore model) {
        viewHolder.tvName.setText(model.getDescription());
        viewHolder.tvDate.setText(model.getDate());
        viewHolder.tvPlace.setText(model.getPlace());
        viewHolder.tvAuthor.setText(model.getAuthor());
        viewHolder.ibDelete.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            //método mediante el cual se borra el elemento del recycler y se actualizan las posiciones
                            getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).getReference().delete();
                            int newPosition =viewHolder.getAdapterPosition();
                            notifyItemRemoved(newPosition);
                            notifyItemRangeChanged(newPosition,getSnapshots().size());
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("¿Estás seguro de eliminar esta cita?").setPositiveButton("Sí", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_element, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvAuthor;
        TextView tvDate;
        TextView tvPlace;
        ImageButton ibDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPlace = itemView.findViewById(R.id.tvPlace);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            ibDelete =itemView.findViewById((R.id.ibDelete));
        }
    }

}
