package shareroid.controller.test;

import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import shareroid.controller.ReadController;
import shareroid.model.Direction;
import shareroid.model.Share;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ReadControllerTest extends AbstractControllerTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Share share1 = new Share();
        share1.setUrl("http://localhost:8080");
        share1.setDirection(Direction.CHROME);

        Share share2 = new Share();
        share2.setUrl("http://localhost:8080");
        share2.setDirection(Direction.ANDROID);

        Datastore.put(share1, share2);
    }

    @Test
    public void test_run() throws Exception {
        initOAuthService();
        setParameter("direction", Direction.CHROME.toString());
        start("/read");

        assertThat(getController(), instanceOf(ReadController.class));
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
        start("/read", "POST");
        assertThat(getStatus(), is(400));
    }
}