var Shareroid = (function () {
    function Shareroid() {
        this.oauth = ChromeExOAuth.initBackgroundPage({
            "request_url": "https://shareroid.appspot.com/_ah/OAuthGetRequestToken",
            "authorize_url": "https://shareroid.appspot.com/_ah/OAuthAuthorizeToken",
            "access_url": "https://shareroid.appspot.com/_ah/OAuthGetAccessToken",
            "consumer_key": OAUTH_CONSUMER_KEY,
            "consumer_secret": OAUTH_CONSUMER_SECRET,
            "app_name": "shareroid",
            "scope": "shareroid"
        });
        this.share = new Share();
        this.listeners = {};
        this.oauth.callback_page = "callback.html";
    }
    Shareroid.prototype.authorize = function (callback) {
        if (!_.isFunction(callback))
            throw new Error("callback isn`t a function");

        this.oauth.authorize(callback);
    };

    Shareroid.prototype.getEntries = function (callback) {
        return this.share.urls(callback);
    };

    Shareroid.prototype.fetchEntries = function () {
        var d = $.Deferred();

        this.oauth.sendSignedRequest('https://shareroid.appspot.com/read/chrome', function (data) {
            d.resolve(JSON.parse(data));
        });

        return d.promise();
    };

    Shareroid.prototype.fetchHistories = function () {
        var d = $.Deferred();

        this.oauth.sendSignedRequest('https://shareroid.appspot.com/history', function (data) {
            d.resolve(JSON.parse(data));
        });

        return d.promise();
    };

    Shareroid.prototype.send = function (url) {
        this.oauth.sendSignedRequest('https://shareroid.appspot.com/push/android', function (data, xhr) {
            console.log(xhr.status);
        }, {
            "method": "POST",
            "parameters": { "url": url }
        });
    };

    Shareroid.prototype.addAlarmListener = function (name, options, callback) {
        if (!_.isString(name) || _.isEmpty(name))
            throw new Error("name isn`t a string");

        if (!_.isObject(options) || _.isEmpty(options))
            throw new Error("options isn`t a object");

        if (!_.isFunction(callback))
            throw new Error("callback isn`t a function");

        if (name in this.listeners)
            throw new Error("already exists alarm event: " + name);

        this.listeners[name] = [options, callback];
    };

    Shareroid.prototype.start = function () {
        var _this = this;
        this.authorize(function (token, tokenSecret) {
            chrome.alarms.onAlarm.addListener(function (alarm) {
                var name = alarm.name;

                if (!(name in _this.listeners))
                    return;

                var callback = _this.listeners[name][1];
                callback(alarm);
            });

            for (var key in _this.listeners) {
                var listener = _this.listeners[key];
                var options = listener[0];

                chrome.alarms.create(key, options);
            }
        });
    };

    Shareroid.prototype.sync = function () {
        var _this = this;
        var p = this.fetchEntries();
        p.done(function (entries) {
            entries.forEach(function (entry) {
                if (!("url" in entry))
                    return;

                _this.share.save(entry.url, function (id, url) {
                    _this.notify(id, url);
                });
            });

            setTimeout(function () {
                _this.updateBadge();
            }, 3000);
        });
    };

    Shareroid.prototype.flush = function () {
        var _this = this;
        this.share.clear(function (result) {
            _this.updateBadge();
        });
    };

    Shareroid.prototype.updateBadge = function () {
        this.share.count(function (cnt) {
            chrome.browserAction.setBadgeText({ "text": String(cnt) });
        });
    };

    Shareroid.prototype.createTab = function (url) {
        chrome.tabs.create({ "url": url, "selected": false });
    };

    Shareroid.prototype.notify = function (id, url) {
        chrome.notifications.create(String(id), {
            "iconUrl": "img/icon_48.png",
            "title": "Update",
            "type": "basic",
            "message": String(url)
        }, function (id) {
            setTimeout(function () {
                chrome.notifications.clear(id, function () {
                });
            }, 3000);
        });
    };
    return Shareroid;
})();
