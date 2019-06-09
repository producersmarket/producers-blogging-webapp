package com.producersmarket.blog.model;

public interface Menuable {

    /*
    private int id;
    private String name;
    private String hyphenatedName;
    private String parentPath;
    private String pathInfo; // the HTTP Path Info that was used to select this product
    private String servletPath; // the HTTP Servlet Path that was used to select this product
    private String imageUrl;
    private String alternativeUrl;

    public Menuable();
    public Menuable(int id);
    */

    public int getId();
    public void setId(int id);

    public String getHyphenatedName();
    public void setHyphenatedName(String hyphenatedName);

    public String getName();
    public void setName(String name);

    /*
    public String getParentPath() {
        return this.parentPath;
    }
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }
    */

    public String getPathInfo();
    public void setPathInfo(String pathInfo);

    public String getServletPath();
    public void setServletPath(String servletPath);

    public String getAlternativeUrl();
    public void setAlternativeUrl(String alternativeUrl);

    public String getImagePath();
    public void setImagePath(String imageUrl);

    public String getImagePathHover();
    public void setImagePathHover(String imageUrl);

}
