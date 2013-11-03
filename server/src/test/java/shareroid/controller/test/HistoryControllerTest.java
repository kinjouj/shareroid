package shareroid.controller.test;

import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import shareroid.controller.HistoryController;
import shareroid.model.Share;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class HistoryControllerTest extends AbstractControllerTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Share share = new Share();
        share.setPublished(true);
        share.setUrl("http://localhost:8080");

        Datastore.put(share);
    }

    @Test
    public void test_run() throws Exception {
        initOAuthService();
        start("/history");

        assertThat(getController(), instanceOf(HistoryController.class));
        assertThat(getStatus(), is(200));
        assertThat(getContentType(), is("application/json; charset=utf-8"));

        Share[] shares = meta.jsonToModels(getOutputAsString());
        assertThat(shares.length, is(1));

        Share share = shares[0];
        assertThat(share, notNullValue());
        assertThat(share.getUrl(), is("http://localhost:8080"));
    }

    @Test
    public void test_run_method_isnt_get() throws Exception {
        initOAuthService();
        start("/history", "POST");

        assertThat(getStatus(), is(400));
    }
}