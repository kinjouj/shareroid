import OAuth2 from "./oauth2"
import Shareroid from "./shareroid"

var cntObserve = Object.observe({ value: null }, (changes) => {
  changes.forEach((change) => {
    console.info(change);
    chrome.browserAction.setBadgeText({ text: String(change.object.value) });
  });
});

function start_app(token) {
  var shareroid = new Shareroid(token);
  shareroid.sync();

  shareroid.count().then((count) => {
    cntObserve.value = count;
  })
  .catch((err) => {
    console.error(err);
    cntObserve.value = 0;
  });

  chrome.browserAction.onClicked.addListener(() => {
    shareroid.read().then((entries) => {
      entries.forEach((entry) => {
        chrome.tabs.create({ url: entry.url });
      });

      shareroid.clear();
      cntObserve.value = 0;
    }).catch((error) => {
      alert(error);
    });
  });

  chrome.alarms.onAlarm.addListener((alarm) => shareroid.sync());
  chrome.alarms.create("shareroid-alarm", { periodInMinutes: 30 });
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
