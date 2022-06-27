package lk.bitapp.garbfree.Helper;

public class Transaction {
    private String itemName;
    private float itemPrice;
    private int itemQty;
    private float total;

    public Transaction(String itemName, float itemPrice, int itemQty, float total) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQty = itemQty;
        this.total = total;
    }

    public String getItemName() {
        return itemName;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public int getItemQty() {
        return itemQty;
    }

    public float getTotal(){return total;}

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }

    public void setTotal(float total){ this.total = total; }
}
