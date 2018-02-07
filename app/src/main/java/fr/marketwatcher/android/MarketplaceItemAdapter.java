package fr.marketwatcher.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by bapti on 06/02/2018.
 */

public class MarketplaceItemAdapter extends ArrayAdapter<MarketplaceItem> {

    public MarketplaceItemAdapter(Context context, List<MarketplaceItem> items)
    {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_marketplace,parent, false);
        }

        MarketplaceItemViewHolder viewHolder = (MarketplaceItemViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new MarketplaceItemViewHolder();
            viewHolder.txtNameMarketplace = (TextView) convertView.findViewById(R.id.txtNameMarketplace);
            viewHolder.txtPriceMarketplace = (TextView) convertView.findViewById(R.id.txtPriceMarketplace);
            viewHolder.imgMarketplace = (ImageView) convertView.findViewById(R.id.imgMarketplace);
            viewHolder.checkboxSeries = (CheckBox) convertView.findViewById(R.id.checkboxSeries);
            convertView.setTag(viewHolder);
        }

        final MarketplaceItem marketplaceItem = getItem(position);

        viewHolder.txtNameMarketplace.setText(marketplaceItem.getName());
        viewHolder.txtPriceMarketplace.setText(marketplaceItem.getPrice());
        viewHolder.checkboxSeries.setChecked(marketplaceItem.getCheckedValue());

        new DownloadImageFromInternet(viewHolder.imgMarketplace)
                .execute(marketplaceItem.getImageUrl());

        /* viewHolder.checkboxSeries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                marketplaceItem.setCheckedValue(!marketplaceItem.getCheckedValue());
            }
        }); */

        return convertView;
    }

    private class MarketplaceItemViewHolder {
        public TextView txtNameMarketplace;
        public TextView txtPriceMarketplace;
        public ImageView imgMarketplace;
        public CheckBox checkboxSeries;
    }

}
