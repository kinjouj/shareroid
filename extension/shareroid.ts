/// <reference path="share.ts" />

declare var $;
declare var _;
declare var chrome;
declare var ChromeExOAuth;
declare var OAUTH_CONSUMER_KEY;
declare var OAUTH_CONSUMER_SECRET;

interface JSONObject {
  url : string;
  createdAt : Number;
}

class Shareroid {

  oauth = ChromeExOAuth.initBackgroundPage({
      "request_url": "https://shareroid.appspot.com/_ah/OAuthGetRequestToken",
      "authorize_url": "https://shareroid.appspot.com/_ah/OAuthAuthorizeToken",
      "access_url": "https://shareroid.appspot.com/_ah/OAuthGetAccessToken",
      "consumer_key": OAUTH_CONSUMER_KEY,
      "consumer_secret": OAUTH_CONSUMER_SECRET,
      "app_name": "shareroid", // optional?
      "scope": "shareroid"
  });

  share = new Share();

  listeners = {};

  constructor() {
    this.oauth.callback_page = "callback.html";
  }

  authorize(callback : (token : string, tokenSecret : string) => void) : void {
    if (!_.isFunction(callback))
      throw new Error("callback isn`t a function");

    this.oauth.authorize(callback);
  }

  getEntries(callback : (url : string) => void) : Promise {
    return this.share.urls(callback);
  }

  fetchEntries() : Promise {
    var d = $.Deferred();

    this.oauth.sendSignedRequest(
      'https://shareroid.appspot.com/read',
      (data) : void => {
        d.resolve(JSON.parse(data));
      }
    );

    return d.promise();
  }

  fetchHistories() : Promise {
    var d = $.Deferred();

    this.oauth.sendSignedRequest(
      'https://shareroid.appspot.com/history',
      (data) : void => {
        d.resolve(JSON.parse(data));
      }
    );

    return d.promise();
  }

  addAlarmListener(name : string, options : Object, callback : (alarm : any) => void) : void {
    if (!_.isString(name) || _.isEmpty(name))
      throw new Error("name isn`t a string");

    if (!_.isObject(options) || _.isEmpty(options))
      throw new Error("options isn`t a object");

    if (!_.isFunction(callback))
      throw new Error("callback isn`t a function");

    if (name in this.listeners)
      throw new Error("already exists alarm event: " + name);

    this.listeners[name] = [options, callback];
  }

  start() : void {
    this.authorize(
      (token : string, tokenSecret: string) : void => {
        chrome.alarms.onAlarm.addListener((alarm) : void => {
          var name = alarm.name;

          if (!(name in this.listeners))
            return;

          var callback = this.listeners[name][1];
          callback(alarm);
        });

        for (var key in this.listeners) {
          var listener = this.listeners[key];
          var options = listener[0];

          chrome.alarms.create(key, options);
        }
      }
    );
  }

  sync() : void {
    var p = this.fetchEntries();
    p.done((entries : Array<JSONObject>) : void => {
      entries.forEach(
        (entry : JSONObject) : void => {
          if (!("url" in entry))
            return;

          this.share.save(entry.url, (id : Number, url : string) : void => {
            this.notify(id, url);
          });
        }
      );

      setTimeout(() : void => {
        this.updateBadge();
      }, 3000);
    });
  }

  flush() : void {
    this.share.clear((result) : void => {
      this.updateBadge();
    });
  }

  updateBadge() : void {
    this.share.count((cnt : Number) : void => {
      chrome.browserAction.setBadgeText({ "text": String(cnt) });
    });
  }

  createTab(url : string) {
    chrome.tabs.create({ "url": url, "selected": false });
  }

  notify(id : Number, url : string) {
    chrome.notifications.create(
      String(id),
      {
        "iconUrl": "img/icon_48.png",
        "title": "Update",
        "type": "basic",
        "message": String(url)
      },
      (id) : void => {
        setTimeout(() : void => {
          chrome.notifications.clear(id, () : void => {});
        }, 3000);
      }
    );
  }
}
