# Producers Weblogging Web-Application

An open source blogging application written in Java, Java Servlets and JSP.

## GitHub access

On Windows make sure you are using the GIT Bash console not the CMD prompt.

### Configure the user email address
git config --global user.email "INSERT GITHUB EMAIL ADDRESS HERE"

### Configure the username
```
git config --global user.name "INSERT GITHUB USERNAME HERE"
```

### Start the SSH agent.
```
eval $(ssh-agent -s)
```

### Add the SSH key to the running agent.
```
ssh-add ~/.ssh/id_rsa
```

## Release Notes

* 0.0.1.1 - Removed servlet annotations
* 0.0.1.2 - Added login/authentication
* 0.0.1.3 - Added secure reset password
* 0.0.1.4 - Added edit blog post
* 0.0.1.5 - Added SessionListener
* 0.0.1.6 - Added UserServlet
* 0.0.1.7 - Added insert new blog post
* 0.0.1.8 -
* 0.0.1.9 -
* 0.0.1.10 - Added BlogPostListNodeRenderer.java for markdown rendering
* 0.0.1.11 - Added CustomImagesAndLinksRenderer
* 0.0.1.13 - ParentServlet made servletContent non-static, private
* 0.0.1.14 - EditPostServlet changed how blog post is loaded after edit
* 0.0.1.15 - Fixed bug in CustomImagesAndLinksRenderer
* 0.0.1.16 - New table naming scheme
* 0.0.1.17 - Added getConnectionManager() and updated database access classes
* 0.0.1.18 - Added `PublishPostServlet` and `UnpublishPostServlet`
* 0.0.1.19 - New ispaces-dbcp jar
* 0.0.1.20 - New load statements
* 0.0.1.21 - Now passing ConnectionPool object to database manager classes
* 0.0.1.22 - Dynamic Google Keys
* 0.0.1.23 - Fixed contextUrl, ends with forward slash
* 0.0.1.24 - Added Menuable. Updated selectBlogCategories
