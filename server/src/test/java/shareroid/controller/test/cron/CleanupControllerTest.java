package shareroid.controller.test.cron;

import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import shareroid.controller.cron.CleanupController;
import shareroid.controller.test.AbstractControllerTestCase;
import shareroid.model.Share;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class CleanupControllerTest extends AbstractControllerTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Share share = new Share();
        share.setUrl("http://localhost:8080");
        share.setPublished(true);

        Datastore.put(share);
    }

    @Test
    public void test_run() throws Exception {
        assertThat(entryCount(), is(1));

        start("/cron/cleanup");

        assertThat(getStatus(), is(200));
        assertThat(getController(), instanceOf(CleanupController.class));
        assertThat(entryCount(), is(0));
    }

    @Test
    public void test_run_method_isnt_get() throws Exception {
        start("/cron/cleanup", "POST");
        assertThat(getStatus(), is(400));
    }

    @Override
    protected int entryCount() {
        query = query.filter(meta.published.equal(true));
        return super.entryCount();
    }
}