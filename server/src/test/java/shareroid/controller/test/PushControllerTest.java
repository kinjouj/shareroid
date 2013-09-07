package shareroid.controller.test;

import org.junit.Test;

import shareroid.controller.PushController;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class PushControllerTest extends AbstractControllerTestCase {

    @Test
    public void test_run() throws Exception {
        setParameter("url", "http://localhost:8080");
        start("/push", "POST");
        assertThat(getStatus(), is(200));
        assertThat(getController(), instanceOf(PushController.class));
        assertThat(entryCount(), is(1));
    }

    @Test
    public void test_run_method_isnt_post() throws Exception {
        start("/push");
        assertThat(getStatus(), is(400));
    }
}