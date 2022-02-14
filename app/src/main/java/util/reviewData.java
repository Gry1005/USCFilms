package util;

public class reviewData {

    private String rating;
    private String name_date;
    private String content;

    public reviewData(String rating,String name_date, String content)
    {
        this.rating = rating;
        this.name_date = name_date;
        this.content = content;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName_date() {
        return name_date;
    }

    public void setName_date(String name_date) {
        this.name_date = name_date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
