/*global chrome*/

import OAuth2 from "./oauth2";
import Shareroid from "./shareroid";

const SYNC_INTERVAL = 60;

var observer = Object.observe({ value: null }, (changes) => {
  changes.forEach((change) => {
    chrome.browserAction.setBadgeText({ text: String(change.object.value) });
  });
});

function start_app() {
  let shareroid = new Shareroid();
  shareroid.sync().then((cnt) => {
    observer.value = cnt;
  });

  chrome.browserAction.onClicked.addListener(async () => {
    let entries = await shareroid.read();
    entries.forEach((entry) => {
      chrome.tabs.create({ url: entry.url });
    });

    shareroid.clear();
    observer.value = 0;
  });

  chrome.alarms.onAlarm.addListener(() => {
    OAuth2.authorize().then(() => {
      shareroid.sync().then((cnt) => {
        observer.value = cnt;
        (async () => {
          let entries = await shareroid.read();
          console.debug(entries);
        })();
      });
    });
  });
  chrome.alarms.create("shareroid-alarm", { periodInMinutes: SYNC_INTERVAL });
}

OAuth2.authorize().then(() => {
  try {
    start_app();
  } catch(e) {
    console.error(e);
  }
}).catch((err) => {
  console.error(err);

  OAuth2.start_chrome_authorization().then(() => {
    start_app();
  }).catch((err) => {
    alert(err);
  });
});
