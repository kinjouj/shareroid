/// <reference path="promise.ts"/>

declare var _;
declare var $;

class Share {

  getDB(callback: (db: any) => void) : void {
    if (!_.isFunction(callback))
      throw new Error("callback isn`t a function");

    var req = indexedDB.open("shares");

    req.onupgradeneeded = () : void => {
      var db = req.result;
      var store = db.createObjectStore(
        "share",
        { "keyPath": "id", "autoincrement": true }
      );
      store.createIndex("by_url", "url", { "unique": true });
    };

    req.onsuccess = () : void => {
      callback(req.result);
    };
  }

  getObjectStore(
    name : string,
    callback : (store : any, tx : any) => void,
    perm : string = "readonly") : void {

    if (!_.isString(name) || _.isEmpty(name))
      throw new Error("name isn`t a string");

    if (!_.isFunction(callback))
      throw new Error("callback isn`t a function");

    this.getDB((db) : void => {
      var tx = db.transaction(name, perm);
      callback(tx.objectStore(name), tx);
    });
}

  count(callback : (cnt : Number) => void) : void {
    if (!_.isFunction(callback))
      throw new Error("callback isn`t a function");

    this.getObjectStore(
      "share",
      (store) : void => {
        var req = store.count();
        req.onsuccess = () : void => {
          callback(req.result);
        };
      }
    );
  }

  urls(callback : (url : String) => void) : Promise {
    if (!_.isFunction(callback))
      throw new Error("callback isn`t a function");

    var d = $.Deferred();

    this.getObjectStore(
      "share",
      (store) : void => {
        var counter = 0;

        var req = store.openCursor();
        req.onsuccess = () : void => {
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
      }
    );

    return d.promise();
  }

  save(url : String, callback: (id : Number, url : String) => void) : void {
    if (!_.isString(url) || _.isEmpty(url))
      throw new Error("url isn`t a string");

    if (!_.isFunction(callback))
      throw new Error("callback isn`t a function");

    this.getObjectStore(
      "share",
      (store) : void => {
        var req = store.put({ "url": url });
        req.onsuccess = () : void => {
          callback(req.result, url);
        };
      },
      "readwrite"
    );
  }

  clear(callback? : (result : any) => void) : void {
    if (!_.isFunction(callback))
      callback = (result : any) => {};

    this.getObjectStore(
      "share",
      (store) : void => {
        var req = store.clear();
        req.onsuccess = () : void => {
          callback(req.result);
        };
      },
      "readwrite"
    );
  }
}
