package lk.bitapp.garbfree.Helper;

import android.widget.ImageView;

import java.io.Serializable;

public class Items implements Serializable {

    private String itemId;
    private String iName;
    private float iPrice;
    private int quantity;
    private String location;
    private String img;
    private String uid;
    private String email;

    public Items(String id, String iName, float iPrice, int quantity, String location, String img,String uid, String email) {
        this.itemId = id;
        this.iName = iName;
        this.iPrice = iPrice;
        this.quantity = quantity;
        this.location = location;
        this.img = img;
        this.uid = uid;
        this.email = email;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String id) {
        this.itemId = id;
    }

    public String getiName() {
        return iName;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }

    public float getiPrice() {
        return iPrice;
    }

    public void setiPrice(float iPrice) {
        this.iPrice = iPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}