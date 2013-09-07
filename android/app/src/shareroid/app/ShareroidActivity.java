package shareroid.app;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class ShareroidActivity extends OAuthActivity {

    private static final String TAG = ShareroidActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        final String query = getQuery(getIntent());

        if (!TextUtils.isEmpty(query)) {
            new Thread() {
                @Override
                public void run() {
                    if (isAuthorized()) {
                        send(query);
                    } else {
                        try {
                            Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(acquireRequestToken(query))
                            );
                            startActivity(intent);
                        } catch (OAuthMessageSignerException e) {
                            e.printStackTrace();
                        } catch (OAuthNotAuthorizedException e) {
                            e.printStackTrace();
                        } catch (OAuthExpectationFailedException e) {
                            e.printStackTrace();
                        } catch (OAuthCommunicationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

        finish();
    }

    private String getQuery(Intent intent) {
        String url = null;

        if (intent != null) {
            if(Intent.ACTION_SEND.equals(intent.getAction())) {
                if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                    url = intent.getStringExtra(Intent.EXTRA_TEXT);
                }
            }
        }

        return url;
    }
}