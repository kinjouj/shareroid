package shareroid.model.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import shareroid.model.Direction;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class DirectionTest extends AppEngineTestCase {

    @Test
    public void test_parse_chrome() {
        Map<String, Object> request = new HashMap<String, Object>(1);
        request.put("direction", "CHROME");

        assertThat(Direction.parse(request), is(Direction.CHROME));
    }

    @Test
    public void test_parse_android() {
        Map<String, Object> request = new HashMap<String, Object>(1);
        request.put("direction", "ANDROID");

        assertThat(Direction.parse(request), is(Direction.ANDROID));
    }

    @Test
    public void test_parse_unknown() {
        Map<String, Object> request = new HashMap<String, Object>(1);
        request.put("direction", "unknown");

        assertThat(Direction.parse(request), nullValue());
    }

    @Test
    public void test_parse_isnt_string() {
        Map<String, Object> request = new HashMap<String, Object>(1);
        request.put("direction", new Object());

        assertThat(Direction.parse(request), nullValue());
    }

    @Test
    public void test_parse_null() {
        assertThat(Direction.parse(null), nullValue());

        Map<String, Object> request = new HashMap<String, Object>(0);
        assertThat(Direction.parse(request), nullValue());
    }
}