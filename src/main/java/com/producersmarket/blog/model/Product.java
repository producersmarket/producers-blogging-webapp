package com.producersmarket.blog.model;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class Product {

    private static final Logger logger = LogManager.getLogger();

    private int id = -1;
    private String name = null;
    private String description = null;
    private String certifications = null;
    private String hyphenatedName = null;
    private String parentPath = null;
    private String pathInfo = null; // the HTTP Path Info that was used to select this product
    private String servletPath = null; // the HTTP Servlet Path that was used to select this product
    private String imageUrl = null;
    private String alternativeUrl = null;
    private boolean isOrganic = false;
    private boolean isGlobalGap = false;
    private boolean isRainforest = false;
    private boolean isFairTrade = false;
    private String countryCode = null;
    private String packStyle = null;
    private String windowAvailable = null;
    private String volumeAvailable = null;
    private String location = null;
    private String markdown = null;
    private int productTypeId = -1;

    List<Product> productList;
    List<String> imageList;

    public Product() {}

    public Product(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getHyphenatedName() {
        return this.hyphenatedName;
    }
    public void setHyphenatedName(String hyphenatedName) {
        this.hyphenatedName = hyphenatedName;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return this.description;
    }
    public String getDescriptionAsHtml() {

        if(this.description != null) {
            Parser parser = Parser.builder().build();
            Node document = parser.parse(this.description);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            return renderer.render(document);
        }

        return null;
    }

    public String getCertifications() {
        return this.certifications;
    }
    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public int getProductTypeId() {
        return this.productTypeId;
    }
    public void setProductTypeId(int productTypeId) {
        this.productTypeId = productTypeId;
    }

    public boolean getIsOrganic() {
        return this.isOrganic;
    }
    public void setIsOrganic(boolean isOrganic) {
        this.isOrganic = isOrganic;
    }

    public boolean getIsGlobalGap() {
        return this.isGlobalGap;
    }
    public void setIsGlobalGap(boolean isGlobalGap) {
        this.isGlobalGap = isGlobalGap;
    }

    public boolean getIsRainforest() {
        return this.isRainforest;
    }
    public void setIsRainforest(boolean isRainforest) {
        this.isRainforest = isRainforest;
    }

    public boolean getIsFairTrade() {
        return this.isFairTrade;
    }
    public void setIsFairTrade(boolean isFairTrade) {
        this.isFairTrade = isFairTrade;
    }

    /*
    public String getParentPath() {
        return this.parentPath;
    }
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }
    */

    public String getPathInfo() {
        return this.pathInfo;
    }
    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public String getServletPath() {
        return this.servletPath;
    }
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public String getImageUrl() {

        if(this.imageUrl == null) {
            if(this.imageList != null && this.imageList.size() > 0) {
                this.imageUrl = this.imageList.get(0);
            }
        }

        return this.imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAlternativeUrl() {
        return this.alternativeUrl;
    }

    public void setAlternativeUrl(String alternativeUrl) {
        this.alternativeUrl = alternativeUrl;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public List<Product> getProductList() {
        return this.productList;
    }

    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getMarkdown() {
        return this.markdown;
    }
    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }
    public String getMarkdownHtml() {

        if(this.markdown != null) {
            Parser parser = Parser.builder().build();
            Node document = parser.parse(this.markdown);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            return renderer.render(document);
        }

        return null;
    }

    public String getVolumeAvailable() {
        return this.volumeAvailable;
    }
    public void setVolumeAvailable(String volumeAvailable) {
        this.volumeAvailable = volumeAvailable;
    }

    public String getWindowAvailable() {
        return this.windowAvailable;
    }
    public void setWindowAvailable(String windowAvailable) {
        this.windowAvailable = windowAvailable;
    }

    public String getPackStyle() {
        return this.packStyle;
    }
    public void setPackStyle(String packStyle) {
        this.packStyle = packStyle;
    }

    public void addImageUrl(String imageUrl) {

        if(this.imageList == null) imageList = new java.util.ArrayList<String>();

        imageList.add(imageUrl);
    }

    public List<String> getImageList() {
        return this.imageList;
    }

    public String getImageListJson() {

        if(this.imageList != null) {

            StringBuilder jsonBuilder = new StringBuilder();
            int x = 0;
            jsonBuilder.append("[");
            for(String imageUrl: this.imageList) {
                if(x++ > 0) jsonBuilder.append(",");
                //jsonBuilder.append("'").append(imageUrl).append("'");
                //jsonBuilder.append("\"").append(imageUrl).append("\"");
                jsonBuilder.append("&quot;").append(imageUrl).append("&quot;");
 
            }
            jsonBuilder.append("]");

            return jsonBuilder.toString();
        }

        return null;
    }

}
