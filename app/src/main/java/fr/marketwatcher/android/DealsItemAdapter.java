package fr.marketwatcher.android;

/**
 * Created by bapti on 07/02/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DealsItemAdapter extends ArrayAdapter<CatalogItem> {

    public DealsItemAdapter(Context context, List<CatalogItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_deals,parent, false);
        }

        ItemViewHolder viewHolder = (ItemViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new ItemViewHolder();
            viewHolder.txtNameDeal = (TextView) convertView.findViewById(R.id.txtNameDeal);
            viewHolder.txtAboutDeal = (TextView) convertView.findViewById(R.id.txtAboutDeal);
            viewHolder.imgDeal = (ImageView) convertView.findViewById(R.id.imgDeal);
            viewHolder.txtPriceDeal = (TextView) convertView.findViewById(R.id.txtPriceDeal);
            convertView.setTag(viewHolder);
        }

        final CatalogItem catalogItem = getItem(position);

        viewHolder.txtNameDeal.setText(catalogItem.getName());

        viewHolder.txtAboutDeal.setText(
                catalogItem.getBrand() + " " +
                        catalogItem.getModel());

        viewHolder.txtPriceDeal.setText((catalogItem.getPriceMin() != 0) ?
                (
                        ((Integer.parseInt(("" + catalogItem.getPriceMin()).split("\\.")[1]) == 0) ?
                                ("" + catalogItem.getPriceMin()).split("\\.")[0] :
                                (("" + catalogItem.getPriceMin()).split("\\.")[0]
                                        + "," + ("" + catalogItem.getPriceMin()).split("\\.")[1])) + "€"
                ) : "");

        new DownloadImageFromInternet(viewHolder.imgDeal)
                .execute(catalogItem.getImageUrl());

        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent ArticleIntent = new Intent(getContext(), ArticleActivity.class);
                ArticleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ArticleIntent.putExtra("googleId", catalogItem.getGoogleId());
                getContext().startActivity(ArticleIntent);
            }
        });

        return convertView;
    }

    private class ItemViewHolder{
        public TextView txtNameDeal;
        public TextView txtAboutDeal;
        public ImageView imgDeal;
        public TextView txtPriceDeal;
    }
}
