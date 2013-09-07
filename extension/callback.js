var ChromeExOAuthCallback = (function () {
    function ChromeExOAuthCallback() {
    }
    ChromeExOAuthCallback.start = function () {
        ChromeExOAuth.initCallbackPage();
    };
    return ChromeExOAuthCallback;
})();

ChromeExOAuthCallback.start();
