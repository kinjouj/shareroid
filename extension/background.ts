/// <reference path="shareroid.ts"/>

declare var UPDATE_INTERVAL_MINUTES;
declare var chrome;

class BackgroundPage {
  static start() {
    var shareroid = new Shareroid();
    shareroid.updateBadge();
    shareroid.addAlarmListener(
      "publish-check",
      { "periodInMinutes": UPDATE_INTERVAL_MINUTES },
      () : void => {
        shareroid.sync();
      }
    );
    shareroid.start();

    chrome.browserAction.onClicked.addListener(() => {
      var p = shareroid.getEntries((url) : void => {
        shareroid.createTab(url);
      });
      p.done((n) : void => {
        if (n > 0) {
          shareroid.flush();
        }
      });
    });
  }
}

BackgroundPage.start();
