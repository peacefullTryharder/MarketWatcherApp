package fr.marketwatcher.android;

/**
 * Created by bapti on 31/01/2018.
 */

public class CatalogItem {
    private String name, imageUrl, date, googleId, brand, model, category;
    private Double price_min, price_max;

    public CatalogItem(String name, String imageUrl, String date, String googleId, String brand,
                       String model, String category, Double price_min, Double price_max)
    {
        this.name = name;
        this.imageUrl = imageUrl;
        this.date = date;
        this.googleId = googleId;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.price_min = price_min;
        this.price_max = price_max;
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
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getCategory() { return category; }
    public Double getPriceMin() { return price_min; }
    public Double getPriceMax() { return price_max; }


    public void setName(String name)
    {
        this.name = name;
    }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDate(String date) { this.date = date; }
    public void setGoogleId(String googleId)
    {
        this.googleId = googleId;
    }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setCategory(String category) { this.category = category; }
    public void setPriceMin(Double price_min) { this.price_min = price_min; }
    public void setPriceMax(Double price_max) { this.price_max = price_max; }

}
