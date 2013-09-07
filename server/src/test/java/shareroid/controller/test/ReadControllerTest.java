package shareroid.controller.test;

import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import shareroid.controller.ReadController;
import shareroid.model.Share;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ReadControllerTest extends AbstractControllerTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Share share = new Share();
        share.setUrl("http://localhost:8080");

        Datastore.put(share);
    }

    @Test
    public void test_run() throws Exception {
        assertThat(entryCount(), is(1));

        initOAuthService();
        start("/read");
        assertThat(getController(), instanceOf(ReadController.class));
        assertThat(getStatus(), is(200));
        assertThat(getContentType(), is("application/json; charset=utf-8"));
        assertThat(entryCount(), is(0));

        Share[] shares = meta.jsonToModels(getOutputAsString());
        assertThat(shares.length, is(1));

        Share share = shares[0];
        assertThat(share, notNullValue());
        assertThat(share.getUrl(), is("http://localhost:8080"));
    }

    @Test
    public void test_run_method_isnt_get() throws Exception {
        initOAuthService();
        start("/read", "POST");
        assertThat(getStatus(), is(400));
    }

    @Override
    protected int entryCount() {
        query = query.filter(meta.published.equal(false));
        return super.entryCount();
    }
}