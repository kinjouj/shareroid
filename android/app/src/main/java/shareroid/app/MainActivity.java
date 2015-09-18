package shareroid.app;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class MainActivity extends Activity {

    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.email";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startAuthorization();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void startAuthorization() {
        final AccountManager accountManager = AccountManager.get(MainActivity.this);
        accountManager.getAuthTokenByFeatures(
            "com.google",
            SCOPE,
            null,
            MainActivity.this,
            null,
            null,
            new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> result) {
                    String token = null;

                    try {
                        token = result.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                        share(token);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (token != null) {
                            accountManager.invalidateAuthToken("com.google", token);
                        }
                    }
                }
            },
            null
        );
    }

    private void share(String token) throws Exception {
        Intent intent = getIntent();

        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();

            if (extras != null) {
                String url = extras.getString(Intent.EXTRA_TEXT);

                if (url != null) {
                    send(token, url);
                }
            }
        }

        finish();
    }

    private void send(String token, String url) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("url", url));

        HttpPost request = new HttpPost("https://shareroid.appspot.com/push/chrome");
        request.setEntity(new UrlEncodedFormEntity(params));
        request.setHeader("Authorization", "Bearer " + token);

        HttpClient httpClient = new DefaultHttpClient();
        int statusCode = httpClient.execute(
            request,
            new ResponseHandler<Integer>() {
                @Override
                public Integer handleResponse(HttpResponse response)
                    throws ClientProtocolException, IOException {
                    return response.getStatusLine().getStatusCode();
                }
            }
        );
    }
}
