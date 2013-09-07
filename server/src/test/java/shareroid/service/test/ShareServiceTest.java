package shareroid.service.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;
import org.slim3.controller.ControllerConstants;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;
import org.slim3.tester.AppEngineTestCase;
import org.slim3.util.ApplicationMessage;

import com.google.appengine.api.users.UserServiceFactory;

import shareroid.model.Share;
import shareroid.service.ShareService;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ShareServiceTest extends AppEngineTestCase {

    private ShareService service;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        service = new ShareService();

        Share share1 = new Share();
        share1.setUrl("http://localhost:8080");

        Share share2 = new Share();
        share2.setUrl("https://localhost:8080");
        share2.setPublished(true);

        Datastore.put(share1, share2);

        ApplicationMessage.setBundle(
            ControllerConstants.DEFAULT_LOCALIZATION_CONTEXT,
            new Locale("en")
        );
    }

    @Test
    public void test_getShares() {
        List<Share> shares = service.getShares();
        assertThat(shares, hasSize(1));
    }

    @Test
    public void test_getHistory() {
        List<Share> shares = service.getHistory();
        assertThat(shares, hasSize(1));
    }

    @Test
    public void test_validate() {
        assertThat(service.validate(null), is(false));

        Map<String, Object> request = new HashMap<String, Object>();
        assertThat(service.validate(request), is(false));

        request.put("url", "http://localhost:8080");
        assertThat(service.validate(request), is(true));
    }

    @Test
    public void test_save() {
        assertThat(service.save(null), is(false));

        Map<String, Object> request = new HashMap<String, Object>();
        assertThat(service.save(request), is(false));

        request.put("url", "http://localhost:8080");
        assertThat(service.save(request), is(true));
        assertThat(service.getShares(), hasSize(2));
        assertThat(service.getHistory(), hasSize(3));
    }

    @Test
    public void test_cleanup() {
        assertThat(service.getHistory(), hasSize(1));
        service.cleanup();
        assertThat(service.getHistory(), hasSize(0));
    }

    @Test
    public void test_modelsToJson() {
        List<Share> shares = service.getShares();
        assertThat(shares, hasSize(1));
        assertThat(service.modelsToJson(shares), notNullValue());
        assertThat(service.modelsToJson(null), nullValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_getQuery() throws Exception {
        Method getQueryMethod = ShareService.class.getDeclaredMethod(
            "getQuery",
            new Class[] { boolean.class }
        );
        getQueryMethod.setAccessible(true);

        ModelQuery<Share> trueQuery = (ModelQuery<Share>)getQueryMethod.invoke(
            service,
            new Object[] { true }
        );
        assertThat(trueQuery.count(), is(1));

        ModelQuery<Share> falseQuery = (ModelQuery<Share>)getQueryMethod.invoke(
            service,
            new Object[] { false }
        );
        assertThat(falseQuery.count(), is(1));
    }
}