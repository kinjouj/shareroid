import db from "db.js"
import request from "superagent"
import Notify from "./notify"
import OAuth2 from "./oauth2"

export default class Shareroid {

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
        (async () => {
          let entries = await server.share.query().all().execute();
          server.close();
          resolve(entries);
        })();
      });
    });
  }

  count() {
    return new Promise((resolve, reject) => {
      (async () => {
        let entries = await this.read();
        resolve(entries.length);
      })();
    });
  }

  sync() {
    return new Promise((resolve, reject) => {
      let token = OAuth2.getAccessToken();

      request
        .get("https://shareroid.appspot.com/read/chrome")
        .set("Authorization", `Bearer ${token}`)
        .end((err, res) => {
          if (!res.ok) {
            reject(err);
            return;
          }

          this.open().then((server) => {
            res.body.forEach((v) => {
              server.share.add({ url: v.url }).then((entries) => {
                entries.forEach((entry) => {
                  Notify.send(entry.id, "added", `add ${entry.url}`);
                });
              });
            });
            server.close();

            (async () => {
              let entries = await this.read();
              resolve(entries.length);
            })();
          });
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
