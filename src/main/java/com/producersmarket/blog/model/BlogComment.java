package com.producersmarket.blog.model;

import java.util.Date;
import com.producersmarket.model.User;

public class BlogComment {

    private static final int SUMMARY_LENGTH = 45;     // 45 chars long for summary

    private String entryId = null;
    private String title = null;
    private String comment = null;
    private String description = null;
    private String userId = null;
    private User user;
    private String postedBy = null;
    private String email = null;
    private String url = null;
    private boolean display = false;
    private int priority = -1;

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }
    public String getEntryId(){
        return entryId;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCommentBegin(){
        return getCommentBegin(SUMMARY_LENGTH);
    }
    public String getCommentBegin(int length){
        if(comment.length() > length) {
            StringBuilder commentBeginBuffer = new StringBuilder();
            commentBeginBuffer.append(comment.substring(0, length));
            commentBeginBuffer.append("...");
            return commentBeginBuffer.toString();
        } else {
            return comment;
        }
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getUserId(){
        return userId;
    }
    public String getUserIdString(){
        return String.valueOf(userId);
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getPostedBy(){
        return postedBy;
    }
    public void setPostedBy(String postedBy){
        this.postedBy = postedBy;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setDisplay(boolean display) {
        this.display = display;
    }
    public void setDisplay(int display) {
        if(display == 1)
            this.display = true;
        else
            this.display = false;
    }
    public boolean getDisplay() {
        return display;
    }
    public String getDisplayYesNo() {
        if(display) {
            return "Yes";
        } else {
            return "No";
        }
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public int getPriority() {
        return priority;
    }
    public String getPriorityString() {
        return String.valueOf(getPriority());
    }

}
