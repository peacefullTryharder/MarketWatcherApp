package fr.marketwatcher.android;

/**
 * Created by bapti on 06/02/2018.
 */

public class MarketplaceItem {
    private String name, price, imageUrl;
    private boolean checkedValue;

    MarketplaceItem(String name, String price, String imageUrl, boolean checkedValue)
    {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.checkedValue = checkedValue;
    }

    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCheckedValue(boolean checkedValue) { this.checkedValue = checkedValue; }


    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public boolean getCheckedValue() { return checkedValue; }
}
