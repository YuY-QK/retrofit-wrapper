package com.yu.testapp.bean;

/**
 * Created by yu on 2020/6/28.
 * 新闻实体
 */
public class NewsBean {

    /**
     * path : 新闻地址
     * image : 图片地址
     * title : 标题
     * passtime : 2020-06-28 10:00:40
     */

    private String path;
    private String image;
    private String title;
    private String passtime;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPasstime() {
        return passtime;
    }

    public void setPasstime(String passtime) {
        this.passtime = passtime;
    }

    @Override
    public String toString() {
        return "NewsBean{" +
            "path='" + path + '\'' +
            ", image='" + image + '\'' +
            ", title='" + title + '\'' +
            ", passtime='" + passtime + '\'' +
            '}';
    }
}
