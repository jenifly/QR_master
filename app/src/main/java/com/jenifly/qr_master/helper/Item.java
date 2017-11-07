package com.jenifly.qr_master.helper;


public class Item {

    private int itemId;
    private String itemName;
    private String itemContent;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemContent() {
        return itemContent;
    }

    public void setItemContent(String itemContent) {
        this.itemContent = itemContent;
    }

    public Item() {}

    public Item(int itemId, String itemName, String itemContent) {

        this.itemId = itemId;
        this.itemName = itemName;
        this.itemContent = itemContent;
    }

    public Item(String itemName, String itemContent) {
        this.itemName = itemName;
        this.itemContent = itemContent;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}