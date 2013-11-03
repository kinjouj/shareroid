package shareroid.service;

import java.util.List;
import java.util.Map;

import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;
import org.slim3.util.BeanUtil;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;

import shareroid.meta.ShareMeta;
import shareroid.model.Direction;
import shareroid.model.Share;

public class ShareService {

    private ShareMeta meta = ShareMeta.get();

    public List<Share> getSharesByDirection(Direction direction) {
        List<Share> shares = null;
        
        if (direction != null) {
            shares = getQuery(false)
                .filter(meta.user.equal(getCurrentUser()))
                .filter(meta.direction.equal(direction))
                .asList();
    
            for (Share share : shares) {
                share.setPublished(true);
                Datastore.putAsync(share);
            }
        }

        return shares;
    }

    public List<Share> getHistories() {
        return getQuery(true)
            .filter(meta.user.equal(getCurrentUser()))
            .sort(meta.createdAt.desc)
            .asList();
    }

    public boolean save(Map<String, Object> request) {
        if (request != null) {
            Validators v = new Validators(request);
            v.add(meta.url, v.required());
            v.add(meta.direction, v.required());
    
            if (v.validate()) {
                Share share = new Share();
                BeanUtil.copy(request, share);
    
                Datastore.put(share);
    
                return true;
            }
        }

        return false;
    }

    public void cleanup() {
        List<Key> keys = getQuery(true).asKeyList();
        Datastore.delete(keys);
    }


    public String modelsToJson(List<Share> shares) {
        if (shares != null) {
            return meta.modelsToJson(shares);
        }

        return null;
    }

    public OAuthService getOAuthService() {
        return OAuthServiceFactory.getOAuthService();
    }

    public User getCurrentUser() {
        OAuthService service = getOAuthService();
        User user = null;

        try {
            user = service.getCurrentUser();
        } catch (OAuthRequestException e) {
            e.printStackTrace();
        }

        return user;
    }

    private ModelQuery<Share> getQuery(boolean published) {
        return Datastore.query(meta)
            .filter(meta.published.equal(published));
    }
}
