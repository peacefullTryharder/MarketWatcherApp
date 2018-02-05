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

        TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TweetViewHolder();
            viewHolder.txtNameArticle = (TextView) convertView.findViewById(R.id.txtNameArticle);
            viewHolder.txtAboutArticle = (TextView) convertView.findViewById(R.id.txtAboutArticle);
            viewHolder.imgArticle = (ImageView) convertView.findViewById(R.id.imgArticle);
            convertView.setTag(viewHolder);
        }

        final CatalogItem catalogItem = getItem(position);

        viewHolder.txtNameArticle.setText(
                ((catalogItem.getCategory() != "null") ?
                        catalogItem.getCategory() + " " : "") +
                        catalogItem.getBrand() + " " +
                        catalogItem.getModel());

        viewHolder.txtAboutArticle.setText(catalogItem.getName());

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

    private class TweetViewHolder{
        public TextView txtNameArticle;
        public TextView txtAboutArticle;
        public ImageView imgArticle;

    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}