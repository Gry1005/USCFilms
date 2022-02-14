package util;

import org.json.JSONObject;

public class HorRvData {

    // image url is used to
    // store the url of image
    private String imgUrl;

    private String type;

    private JSONObject item;

    // Constructor method.
    public HorRvData(String imgUrl, String type, JSONObject item) {
        this.imgUrl = imgUrl;
        this.item = item;
        this.type = type;
    }

    // Getter method
    public String getImgUrl() {
        return imgUrl;
    }

    public JSONObject getItem(){return item;}

    public String getType(){return type;}

    // Setter method
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public void setItem(JSONObject item){this.item = item;}
    public void setType(String type){this.type=type;}
}
