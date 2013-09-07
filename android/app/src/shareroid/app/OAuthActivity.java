package shareroid.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import static shareroid.app.Constants.*;

public abstract class OAuthActivity extends Activity {

    private static OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
        OAUTH_CONSUMER_KEY,
        OAUTH_CONSUMER_SECRET
    );

    protected String acquireRequestToken(String query) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        return getProvider().retrieveRequestToken(
            consumer,
            OAUTH_CALLBACK + "?query=" + query
        );
    }

    protected void acquireAccessToken(String verifier) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
        getProvider().retrieveAccessToken(consumer, verifier);
        getSharedPreferences()
            .edit()
            .putString(PREFERENCE_KEY_OAUTH_TOKEN, consumer.getToken())
            .putString(PREFERENCE_KEY_OAUTH_TOKEN_SECRET, consumer.getTokenSecret())
            .commit();
    }

    protected boolean isAuthorized() {
        return getTokenByPreference() != null && getTokenSecretByPreference() != null;
    }

    protected void send(String query) {
        if (!isAuthorized())
            return;

        HttpClient httpClient = new DefaultHttpClient();

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>(1);
            params.add(new BasicNameValuePair("url", query));

            HttpPost request = new HttpPost("https://shareroid.appspot.com/push");
            request.setEntity(new UrlEncodedFormEntity(params));

            consumer.setTokenWithSecret(getTokenByPreference(), getTokenSecretByPreference());
            consumer.sign(request);

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

            if (statusCode == 200) {
                PendingIntent intent = PendingIntent.getActivity(
                    this,
                    0,
                    new Intent(Intent.ACTION_VIEW, Uri.parse(query)),
                    PendingIntent.FLAG_UPDATE_CURRENT
                );

                sendNotification(
                    getString(R.string.notification_ticker),
                    getString(R.string.notification_message, query),
                    intent
                );
            }
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private OAuthProvider getProvider() {
        return new CommonsHttpOAuthProvider(
            BASE_URL + "/_ah/OAuthGetRequestToken",
            BASE_URL + "/_ah/OAuthGetAccessToken",
            BASE_URL + "/_ah/OAuthAuthorizeToken"
        );
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private String getTokenByPreference() {
        return getSharedPreferences().getString(PREFERENCE_KEY_OAUTH_TOKEN, null);
    }

    private String getTokenSecretByPreference() {
        return getSharedPreferences().getString(PREFERENCE_KEY_OAUTH_TOKEN_SECRET, null);
    }

    private void sendNotification(String ticker, String text, PendingIntent intent) {
        Notification n = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher)
            .setTicker(ticker)
            .setContentText(text)
            .setContentIntent(intent)
            .build();

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, n);
    }
}