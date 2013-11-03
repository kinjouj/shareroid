package shareroid.controller.test;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class MockOAuthService implements OAuthService {

    @Override
    public boolean isUserAdmin(String arg0) throws OAuthRequestException {
        return true;
    }
    
    @Override
    public boolean isUserAdmin() throws OAuthRequestException {
        return true;
    }
    
    @Override
    public String getOAuthConsumerKey() throws OAuthRequestException {
        return null;
    }
    
    @Override
    public User getCurrentUser(String arg0) throws OAuthRequestException {
        return getCurrentUser();
    }
    
    @Override
    public User getCurrentUser() throws OAuthRequestException {
        return UserServiceFactory.getUserService().getCurrentUser();
    }
    
    @Override
    public String getClientId(String arg0) throws OAuthRequestException {
        return null;
    }
}