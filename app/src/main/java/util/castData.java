package util;

import org.json.JSONObject;

public class castData {

    private String imgUrl;
    private String name;

    // Constructor method.
    public castData(String imgUrl, String name) {
        this.imgUrl = imgUrl;
        this.name = name;
    }

    public String getImgUrl(){return imgUrl;}
    public String getName(){return name;}

    public void setImgUrl(String imgUrl){this.imgUrl = imgUrl;}
    public void setName(String name){this.name = name;}

}
