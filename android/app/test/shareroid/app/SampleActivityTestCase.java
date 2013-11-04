package shareroid.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@PrepareForTest(SampleActivity.class)
@PowerMockIgnore({ "android.*", "org.robolectric.*" })
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SampleActivityTestCase {

    @Test
    public void onStartTest() throws Exception {
        ActivityController<SampleActivity> controller = Robolectric
            .buildActivity(SampleActivity.class);

        SampleActivity activity = controller.attach().get();
        activity = spy(activity);

        doAnswer(
            new Answer<String>() {
                @Override
                public String answer(InvocationOnMock invocation) throws Throwable {
                    return "hoge";
                }
            }
        ).when(activity).buildText();

        activity.onStart();
        assertThat(activity.mTextView, notNullValue());
        assertThat(activity.mTextView.getText().toString(), isEmptyOrNullString());

        verify(activity).buildText();
    }
}
