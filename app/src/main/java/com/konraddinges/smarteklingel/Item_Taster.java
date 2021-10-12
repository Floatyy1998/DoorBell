package com.konraddinges.smarteklingel;

public class Item_Taster {
    private String ID, Name, Key, Gateway;

    public Item_Taster(String ID, String name, String Key, String Gateway) {
        this.ID = ID;
        Name = name;
        this.Key = Key;
        this.Gateway = Gateway;
    }

    public Item_Taster() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getKey() {
        return Key;
    }
    public String getGateway(){return Gateway;}
}
