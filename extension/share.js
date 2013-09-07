var Share = (function () {
    function Share() {
    }
    Share.prototype.getDB = function (callback) {
        if (!_.isFunction(callback))
            throw new Error("callback isn`t a function");

        var req = indexedDB.open("shares");

        req.onupgradeneeded = function () {
            var db = req.result;
            var store = db.createObjectStore("share", { "keyPath": "id", "autoincrement": true });
            store.createIndex("by_url", "url", { "unique": true });
        };

        req.onsuccess = function () {
            callback(req.result);
        };
    };

    Share.prototype.getObjectStore = function (name, callback, perm) {
        if (typeof perm === "undefined") { perm = "readonly"; }
        if (!_.isString(name) || _.isEmpty(name))
            throw new Error("name isn`t a string");

        if (!_.isFunction(callback))
            throw new Error("callback isn`t a function");

        this.getDB(function (db) {
            var tx = db.transaction(name, perm);
            callback(tx.objectStore(name), tx);
        });
    };

    Share.prototype.count = function (callback) {
        if (!_.isFunction(callback))
            throw new Error("callback isn`t a function");

        this.getObjectStore("share", function (store) {
            var req = store.count();
            req.onsuccess = function () {
                callback(req.result);
            };
        });
    };

    Share.prototype.urls = function (callback) {
        if (!_.isFunction(callback))
            throw new Error("callback isn`t a function");

        var d = $.Deferred();

        this.getObjectStore("share", function (store) {
            var counter = 0;

            var req = store.openCursor();
            req.onsuccess = function () {
                var csr = req.result;

                if (!csr) {
                    d.resolve(counter);
                    return;
                }

                var value = csr.value;
                callback(value.url);
                csr.continue();

                counter++;
            };
        });

        return d.promise();
    };

    Share.prototype.save = function (url, callback) {
        if (!_.isString(url) || _.isEmpty(url))
            throw new Error("url isn`t a string");

        if (!_.isFunction(callback))
            throw new Error("callback isn`t a function");

        this.getObjectStore("share", function (store) {
            var req = store.put({ "url": url });
            req.onsuccess = function () {
                callback(req.result, url);
            };
        }, "readwrite");
    };

    Share.prototype.clear = function (callback) {
        if (!_.isFunction(callback))
            callback = function (result) {
            };

        this.getObjectStore("share", function (store) {
            var req = store.clear();
            req.onsuccess = function () {
                callback(req.result);
            };
        }, "readwrite");
    };
    return Share;
})();
