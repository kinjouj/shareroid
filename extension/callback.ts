declare var ChromeExOAuth;

class ChromeExOAuthCallback {
  static start() : void {
    ChromeExOAuth.initCallbackPage();
  }
}

ChromeExOAuthCallback.start();
