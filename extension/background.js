var BackgroundPage = (function () {
    function BackgroundPage() {
    }
    BackgroundPage.start = function () {
        var shareroid = new Shareroid();
        shareroid.updateBadge();
        shareroid.addAlarmListener("publish-check", { "periodInMinutes": UPDATE_INTERVAL_MINUTES }, function () {
            shareroid.sync();
        });
        shareroid.start();

        chrome.browserAction.onClicked.addListener(function () {
            var p = shareroid.getEntries(function (url) {
                shareroid.createTab(url);
            });
            p.done(function (n) {
                if (n > 0) {
                    shareroid.flush();
                }
            });
        });
    };
    return BackgroundPage;
})();

BackgroundPage.start();
