package com.konraddinges.smarteklingel;

public class Item {
    private String time, msg, image, bigimage, button_name, key;

    public Item() {

    }

    public Item(String name, String time, String image, String bigimage, String button_name, String key) {
        this.time = time;
        this.msg = name;
        this.image = image;
        this.bigimage = bigimage;
        this.button_name = button_name;
        this.key = key;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public String getBigimage() {
        return bigimage;
    }

    public String getName() {
        return msg;
    }

    public String getButton_MSG() {
        return button_name;
    }

    public void setName(String name) {
        this.msg = name;
    }

    public String getKey() {
        return key;
    }
}
