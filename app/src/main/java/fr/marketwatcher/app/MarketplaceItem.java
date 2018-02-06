package fr.marketwatcher.app;

/**
 * Created by bapti on 06/02/2018.
 */

public class MarketplaceItem {
    private String name, price, imageUrl;

    MarketplaceItem(String name, String price, String imageUrl)
    {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }


    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}
