package util;

public class SliderData {

    // image url is used to
    // store the url of image
    private String imgUrl;
    private String ID;
    private String type;

    // Constructor method.
    public SliderData(String imgUrl, String ID, String type)
    {
        this.imgUrl = imgUrl;
        this.ID = ID;
        this.type = type;
    }

    // Getter method
    public String getImgUrl() {
        return imgUrl;
    }
    public String getID() {return ID;}
    public String getType() {return type;}

    // Setter method
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public void setID(String ID){ this.ID = ID;}
    public void setType(String type){ this.type = type;}
}
