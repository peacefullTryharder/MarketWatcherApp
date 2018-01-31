package android.ece.bapti.marketwatcherapp;

/**
 * Created by bapti on 31/01/2018.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        CatalogItem catalogItem = getItem(position);
        viewHolder.txtNameArticle.setText(tempName(catalogItem.getName()));
        viewHolder.txtAboutArticle.setText(extractDescription(catalogItem.getName()));

        new DownloadImageFromInternet(viewHolder.imgArticle)
                .execute(catalogItem.getImageUrl());
        // viewHolder.avatar.setImageDrawable(new ColorDrawable(#FFFFFF));

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

    public String tempName(String fullName)
    {
        String finaleName = fullName.split(" - ")[0];

        if (finaleName == null) finaleName = fullName.split 
        return fullName.split(" - ")[0];
    }

    public String extractDescription(String fullName)
    {
        return fullName.split(" - ", 2)[1];
    }

    public String dateTransform(String date)
    {
        String year = date.split("-")[0];
        String month;

        switch(Integer.parseInt(date.split("-")[1]))
        {
            case 1:
                month = "Janvier";
                break;
            case 2:
                month = "Février";
                break;
            case 3:
                month = "Mars";
                break;
            case 4:
                month = "Avril";
                break;
            case 5:
                month = "Mai";
                break;
            case 6:
                month = "Juin";
            default:
                month = "mdr";
                break;
        }

        return month + " " + year;
    }
}