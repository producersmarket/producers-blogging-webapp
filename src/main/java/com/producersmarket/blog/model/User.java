package com.producersmarket.blog.model;

import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class User {

    private static final Logger logger = LogManager.getLogger();

    private int id = -1;
    private String name = null;
    private String email = null;
    private String phone = null;
    private String hashedPassword = null;
    private String uuid = null;
    private boolean emailVerified = false;
    private boolean profileComplete = false;
    private boolean subscribeNewsletter = false;
    private String activationCode = null;
    private String businessName = null;
    private String hyphenatedName = null;
    private String instagramUsername = null;
    private String location = null;
    private String themeColor = null;
    private String imageUrl = null;
    private List<Integer> groupIdList = null;
    private int orgId = -1;
    private List<BlogPost> blogPostList = null;

    public String toString() {
        return new StringBuilder()
          .append(getId())
          .append(" - ")
          .append(getName())
          .append(", ")
          .append(getEmail())
          .toString();
    }

    public User() {}

    public User(int id) {
        this.id = id;
    }

    public List<BlogPost> getBlogPostList() {
        return blogPostList;
    }

    public void setBlogPostList(List<BlogPost> blogPostList) {
        this.blogPostList = blogPostList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean getProfileComplete() {
        return profileComplete;
    }
    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public boolean getSubscribeNewsletter() {
        return subscribeNewsletter;
    }
    public void setSubscribeNewsletter(boolean subscribeNewsletter) {
        this.subscribeNewsletter = subscribeNewsletter;
    }

    public String getActivationCode() {
        return activationCode;
    }
    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getEmailAddress() {

        StringBuilder sb = new StringBuilder();

        if(email == null || email.equals("")) return null;

        String name = getName();
        if(name != null) {
            sb.append(name).append(" ").append("<").append(email).append(">");
        } else {
            sb.append(email);
        }

        return sb.toString();
    }

    public String getEmailAddressForHtml() {

        StringBuilder sb = new StringBuilder();

        String email = getEmail();

        if(email == null || email.equals("")) return null;

        String name = getName();
        if(name != null) {
            sb.append(name).append(" ").append("&lt;").append(email).append("&gt;");
        } else {
            sb.append(email);
        }

        return sb.toString();
    }

    public String getBusinessName() {
        return businessName;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getHyphenatedName() {
        return hyphenatedName;
    }
    public void setHyphenatedName(String hyphenatedName) {
        this.hyphenatedName = hyphenatedName;
    }

    public String getInstagramUsername() {
        return instagramUsername;
    }
    public void setInstagramUsername(String instagramUsername) {
        this.instagramUsername = instagramUsername;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThemeColor() {
        return themeColor;
    }
    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public List<Integer> getGroupIdList() {
        return groupIdList;
    }
    public void setGroupIdList(List<Integer> groupIdList) {
        this.groupIdList = groupIdList;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

}
