package android.ece.bapti.marketwatcherapp;

import java.util.Date;

/**
 * Created by bapti on 31/01/2018.
 */

public class CatalogItem {
    private String name, imageUrl, date, googleId;
    // private String price;
    // private Date date;

    public CatalogItem(String name, String imageUrl, String date, String googleId)
    {
        this.name = name;
        this.imageUrl = imageUrl;
        this.date = date;
        this.googleId = googleId;
    }

    public String getName()
    {
        return name;
    }
    public String getImageUrl()
    {
        return imageUrl;
    }
    public String getDate() { return date; };
    public String getGoogleId()
    {
        return googleId;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }
    public void setDate(String date) { this.date = date; }
    public void setGoogleId(String googleId)
    {
        this.googleId = googleId;
    }

}
