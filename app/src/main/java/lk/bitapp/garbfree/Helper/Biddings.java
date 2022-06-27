package lk.bitapp.garbfree.Helper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Biddings{

    private String itemName;
    private String biddingId;
    private String userName;
    private int bidQuantity;
    private float bidPrice;

    public Biddings( String itemName, String biddingId, String userName, int bidQuantity, float bidPrice) {
        this.itemName = itemName;
        this.biddingId = biddingId;
        this.userName = userName;
        this.bidQuantity = bidQuantity;
        this.bidPrice = bidPrice;
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBiddingId() { return biddingId; }

    public void setBiddingId(String biddingId) { this.biddingId = biddingId; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getBidQuantity() {
        return bidQuantity;
    }

    public void setBidQuantity(int bidQuantity) {
        this.bidQuantity = bidQuantity;
    }

    public float getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(float bidPrice) {
        this.bidPrice = bidPrice;
    }

}