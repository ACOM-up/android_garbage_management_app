package lk.bitapp.garbfree.Helper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.Serializable;

public class Reward_items implements Serializable{

    private String title;
    private String description;
    private boolean hasClaimed;
    private String createdDate;
    private String expireDate;

    public Reward_items(String title, String description, boolean hasClaimed, String createdDate, String expireDate) {
        this.title = title;
        this.description = description;
        this.hasClaimed = hasClaimed;
        this.createdDate = createdDate;
        this.expireDate = expireDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasClaimed() {
        return hasClaimed;
    }

    public void setHasClaimed(boolean hasClaimed) {
        this.hasClaimed = hasClaimed;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
