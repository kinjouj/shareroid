package shareroid.model;

import org.slim3.datastore.AttributeListener;

import shareroid.service.ShareService;

import com.google.appengine.api.users.User;

public class CreationUser implements AttributeListener<User> {

    private ShareService service = new ShareService();

    @Override
    public User prePut(User user) {
        if (user != null)
            return user;

        return service.getCurrentUser();
    }
}