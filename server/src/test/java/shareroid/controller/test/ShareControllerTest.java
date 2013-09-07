package shareroid.controller.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.tester.AppEngineTestCase;
import org.slim3.tester.MockHttpServletResponse;

import shareroid.controller.ShareController;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ShareControllerTest extends AppEngineTestCase {

    private ShareController controller;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        tester.environment.setEmail("admin@gmail.com");

        controller = new ShareController() {
            @Override
            protected Navigation run() throws Exception {
                return null;
            }
        };
    }

    @Test
    public void test_sendError() throws Exception {
        MockHttpServletResponse response = injectResponse();
        assertThat(response.getStatus(), is(200));

        Method sendErrorMethod = ShareController.class.getDeclaredMethod(
            "sendError",
            new Class[] {}
        );
        sendErrorMethod.setAccessible(true);
        sendErrorMethod.invoke(controller, new Object[] {});

        assertThat(response.getStatus(), is(400));
    }

    @Test
    public void test_renderJSON() throws Exception {
        MockHttpServletResponse response = injectResponse();
        assertThat(response.getContentType(), nullValue());
        assertThat(response.getOutputAsString(), is(""));

        // SEE ALSO: org.slim3.tester.HeaderUtil.convertStringToInt
        assertThat(response.getContentLength(), is(-1));

        Method renderJSONMethod = ShareController.class.getDeclaredMethod(
            "renderJSON",
            new Class[] { String.class }
        );
        renderJSONMethod.setAccessible(true);
        renderJSONMethod.invoke(controller, new Object[] { "[]" });

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentType(), is("application/json; charset=utf-8"));
        assertThat(response.getContentLength(), is(2));
        assertThat(response.getOutputAsString(), is("[]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_renderJSON_invalid_argument() throws Exception {
        Method renderJSONMethod = ShareController.class.getDeclaredMethod(
            "renderJSON",
            new Class[] { String.class }
        );
        renderJSONMethod.setAccessible(true);

        try {
            renderJSONMethod.invoke(controller, new Object[] { null });
        } catch (Throwable t) {
            IllegalArgumentException e = (IllegalArgumentException)t.getCause();
            throw e;
        }
    }

    private MockHttpServletResponse injectResponse() 
        throws NoSuchFieldException, IllegalAccessException {

        MockHttpServletResponse response = new MockHttpServletResponse();

        Field responseField = Controller.class.getDeclaredField("response");
        responseField.setAccessible(true);
        responseField.set(controller, response);

        return response;
    }
}