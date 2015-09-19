import db from "db.js";
import request from "superagent";
import Notify from "./notify";
import OAuth2 from "./oauth2";

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
      })
      .catch((err) => {
        reject(err);
      });
    });
  }

  read() {
    return new Promise((resolve) => {
      (async () => {
        let server = await this.open();
        let entries = await server.share.query().all().execute();
        server.close();
        resolve(entries);
      })();
    });
  }

  count() {
    return new Promise((resolve) => {
      (async () => {
        let entries = await this.read();
        resolve(entries.length);
      })();
    });
  }

  sync() {
    return new Promise((resolve, reject) => {
      request
        .get("https://shareroid.appspot.com/read/chrome")
        .set("Authorization", `Bearer ${OAuth2.getAccessToken()}`)
        .end((err, res) => {
          if (!res.ok) {
            reject(err);
            return;
          }

          (async () => {
            let server = await this.open();
            res.body.forEach((v) => {
              server.share.add({ url: v.url }).then((entries) => {
                entries.forEach((entry) => {
                  Notify.send(entry.id, "added", `add ${entry.url}`);
                });
              });
            });
            server.close();

            let entries = await this.read();
            resolve(entries.length);
          })();
        });
    });
  }

  clear() {
    (async () => {
      let server = await this.open();
      server.share.clear();
      server.close();
    })();
  }
}
