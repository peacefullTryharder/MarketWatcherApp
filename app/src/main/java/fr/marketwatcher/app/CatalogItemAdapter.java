package fr.marketwatcher.app;

/**
 * Created by bapti on 31/01/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

public class CatalogItemAdapter extends ArrayAdapter<CatalogItem> {

    public CatalogItemAdapter(Context context, List<CatalogItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_catalog,parent, false);
        }

        ItemViewHolder viewHolder = (ItemViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new ItemViewHolder();
            viewHolder.txtNameArticle = (TextView) convertView.findViewById(R.id.txtNameArticle);
            viewHolder.txtAboutArticle = (TextView) convertView.findViewById(R.id.txtAboutArticle);
            viewHolder.imgArticle = (ImageView) convertView.findViewById(R.id.imgArticle);
            viewHolder.txtMinPriceArticle = (TextView) convertView.findViewById(R.id.txtMinPriceArticle);
            viewHolder.txtMaxPriceArticle = (TextView) convertView.findViewById(R.id.txtMaxPriceArticle);
            convertView.setTag(viewHolder);
        }

        final CatalogItem catalogItem = getItem(position);

        viewHolder.txtNameArticle.setText(catalogItem.getName());

        viewHolder.txtAboutArticle.setText(
                catalogItem.getBrand() + " " +
                catalogItem.getModel());

        viewHolder.txtMinPriceArticle.setText((catalogItem.getPriceMin() != 0) ?
                (
                        ((Integer.parseInt(("" + catalogItem.getPriceMin()).split("\\.")[1]) == 0) ?
                                ("" + catalogItem.getPriceMin()).split("\\.")[0] :
                                (("" + catalogItem.getPriceMin()).split("\\.")[0]
                                    + "," + ("" + catalogItem.getPriceMin()).split("\\.")[1])) + "€"
                        ) : "");

        viewHolder.txtMaxPriceArticle.setText((catalogItem.getPriceMax() != 0) ?
                (
                        ((Integer.parseInt(("" + catalogItem.getPriceMax()).split("\\.")[1]) == 0) ?
                                ("" + catalogItem.getPriceMax()).split("\\.")[0] :
                                (("" + catalogItem.getPriceMax()).split("\\.")[0]
                                        + "," + ("" + catalogItem.getPriceMax()).split("\\.")[1])) + "€"
                ) : "");

        new DownloadImageFromInternet(viewHolder.imgArticle)
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
        public TextView txtNameArticle;
        public TextView txtAboutArticle;
        public ImageView imgArticle;
        public TextView txtMinPriceArticle;
        public TextView txtMaxPriceArticle;
    }
}