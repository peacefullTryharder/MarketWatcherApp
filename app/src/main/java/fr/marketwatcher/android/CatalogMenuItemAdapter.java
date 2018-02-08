package fr.marketwatcher.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bapti on 08/02/2018.
 */

public class CatalogMenuItemAdapter extends RecyclerView.Adapter<CatalogMenuItemAdapter.ViewHolder> {


    private List<String> items;
    private int itemLayout;


    public CatalogMenuItemAdapter(List<String> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(CatalogMenuItemAdapter.ViewHolder holder, int position) {
        String item = items.get(position);
        holder.checkbox.setText(item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkboxCategory);
        }
    }
}