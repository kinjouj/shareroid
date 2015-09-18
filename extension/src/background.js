import OAuth2 from "./oauth2";
import Shareroid from "./shareroid";

const SYNC_INTERVAL = 60;

var cntObserve = Object.observe({ value: null }, (changes) => {
  changes.forEach((change) => {
    chrome.browserAction.setBadgeText({ text: String(change.object.value) });
  });
});

function start_app(token) {
  let shareroid = new Shareroid();
  shareroid.sync().then((cnt) => {
    cntObserve.value = cnt;
  });
  shareroid.read();

  chrome.browserAction.onClicked.addListener(async () => {
    let entries = await shareroid.read();
    entries.forEach((entry) => {
      chrome.tabs.create({ url: entry.url });
    });

    shareroid.clear();
    cntObserve.value = 0;
  });

  chrome.alarms.onAlarm.addListener((alarm) => {
    OAuth2.authorize().then((token) => {
      shareroid.sync().then((cnt) => {
        cntObserve.value = cnt;
        (async () => {
          let entries = await shareroid.read();
          console.debug(entries);
        })();
      });
    });
  });
  chrome.alarms.create("shareroid-alarm", { periodInMinutes: SYNC_INTERVAL });
}

OAuth2.authorize().then((token) => {
  try {
    start_app(token);
  } catch(e) {
    console.error(e);
  }
}).catch((err) => {
  OAuth2.start_chrome_authorization().then((token) => {
    start_app(token);
  }).catch((err) => {
    alert(err);
  });
});
