package com.info.kuryem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.CardViewObject> {

    private Context context;
    private List<String> data;

    public Adapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    public class CardViewObject extends RecyclerView.ViewHolder {

        public TextView name, location;
        private CardView cardView;

        public CardViewObject(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.courierName);
            location = itemView.findViewById(R.id.courirerLocation);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    @NonNull
    @Override
    public CardViewObject onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new CardViewObject(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewObject holder, int position) {
        String name = data.get(position);
        holder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
