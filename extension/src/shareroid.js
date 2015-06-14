import request from "superagent"
import db from "db.js"

export default class Shareroid {

  constructor(token) {
    this.token = token;
  }

  open() {
    return new Promise((resolve, reject) => {
      db.open({
        server: "shareroid",
        version: 1,
        schema: {
          share: {
            key: { keyPath: "id", autoIncrement: true },
            indexes: {
              url: { unique: true }
            }
          }
        }
      })
      .then((server) => {
        resolve(server);
      });
    });
  }

  read() {
    return new Promise((resolve, reject) => {
      this.open().then((server) => {
        server.share.query().all().execute().then((entries) => {
          server.close();
          resolve(entries);
        });
      });
    });
  }

  count() {
    return new Promise((resolve, reject) => {
      this.read().then((entries) => {
        resolve(entries.length);
      });
    });
  }

  sync() {
    return new Promise((resolve, reject) => {
      request
        .get("https://shareroid.appspot.com/read/chrome")
        .set("Authorization", `Bearer ${this.token}`)
        .end((err, res) => {
          if (!res.ok) {
            reject(err);
            return;
          }

          this.open().then((server) => {
            res.body.forEach((v) => {
              server.share.add({ url: v.url });
            });
            server.close();
          });

          resolve(res.body);
        });
    });
  }

  clear() {
    this.open().then((server) => {
      server.share.clear();
      server.close();
    });
  }
}
