package shareroid.controller.test;

import java.io.IOException;

import javax.servlet.ServletException;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.Before;
import org.slim3.controller.Controller;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;
import org.slim3.tester.ControllerTestCase;
import org.slim3.tester.MockHttpServletResponse;

import com.google.appengine.api.oauth.OAuthServiceFactory;

import shareroid.meta.ShareMeta;
import shareroid.model.Share;
import shareroid.service.ShareService;

public abstract class AbstractControllerTestCase extends ControllerTestCase {

    protected ShareMeta meta;
    protected ModelQuery<Share> query;
    protected ShareService service;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        tester.environment.setEmail("admin@gmail.com");

        meta = ShareMeta.get();
        query = Datastore.query(meta);
        service = new ShareService();
    }

    protected void initOAuthService() {
        new Expectations() {

            @Mocked(methods = "getOAuthService")
            final OAuthServiceFactory factory = null;

            {
                OAuthServiceFactory.getOAuthService();
                times = 1;
                result = new MockOAuthService();
            };
        };
    }

    protected void start(String path) throws ServletException, IOException {
        tester.start(path);
    }

    protected void start(String path, String method)
        throws ServletException, IOException {

        tester.request.setMethod(method);
        start(path);
    }

    protected void setParameter(String key, String value) {
        tester.request.setParameter(key, value);
    }

    protected MockHttpServletResponse getResponse() {
        return tester.response;
    }

    protected Controller getController() {
        return tester.getController();
    }

    protected int getStatus() {
        return getResponse().getStatus();
    }

    protected String getContentType() {
        return getResponse().getContentType();
    }

    protected String getOutputAsString() throws IOException {
        return getResponse().getOutputAsString();
    }

    protected int entryCount() {
        return query.count();
    }
}