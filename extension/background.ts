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

    chrome.contextMenus.create({
      "id": "shareroid_ctxmenu",
      "title": "shareroid",
      "type": "normal",
      "contexts": ["page"]
    });

    chrome.contextMenus.onClicked.addListener((info, tab) : void => {
      var url = info.pageUrl;
      shareroid.send(url);
    });
  }
}

BackgroundPage.start();
