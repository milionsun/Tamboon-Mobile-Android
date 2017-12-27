package com.tamboon.tamboon.tamboon_mobile;

import org.json.JSONObject;

/**
 * Created by Milion on 12/27/2017.
 */

public class CharityObject {
    private int id;
    private String name;
    private String logoUrl;

//    public CharityObject(int id, String name, String logoUrl) {
//        this.id = id;
//        this.name = name;
//        this.logoUrl = logoUrl;
//    }

    public CharityObject(JSONObject object) {
        this.id = object.optInt("id");
        this.name = object.optString("name");
        this.logoUrl = object.optString("logo_url");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
