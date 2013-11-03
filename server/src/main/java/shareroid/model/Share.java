package shareroid.model;

import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.json.Json;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@Model
public class Share {

    @Attribute(primaryKey = true)
    @Json(ignore = true)
    private Key key;

    @Json(ignore = false)
    private String url;

    @Json(ignore = true)
    private boolean published;

    @Json(ignore = false)
    private Direction direction;

    @Attribute(listener = CreationDate.class)
    private Date createdAt;

    @Attribute(listener = CreationUser.class)
    @Json(ignore = true)
    private User user;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}