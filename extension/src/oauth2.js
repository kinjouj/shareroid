import request from "superagent"
import {client_id, client_secret} from "./secret"

global.Promise = require("bluebird");

const ACCESS_TOKEN_KEY = "access_token";
const REFRESH_TOKEN_KEY = "refresh_token";

class AccessToken {

  static getAccessToken() {
    return localStorage[ACCESS_TOKEN_KEY] || null;
  }

  static saveAccessToken(access_token) {
    return localStorage[ACCESS_TOKEN_KEY] = access_token;
  }

  static getRefreshToken() {
    return localStorage[REFRESH_TOKEN_KEY] || null;
  }

  static saveRefreshToken(refresh_token) {
    return localStorage[REFRESH_TOKEN_KEY] = refresh_token;
  }

  static isPrivileged() {
    return AccessToken.getAccessToken() !== null && AccessToken.getRefreshToken() !== null;
  }
}

export default class OAuth2 {

  static getAccessToken() {
    return AccessToken.getAccessToken();
  }

  static authorize() {
    return new Promise((resolve, reject) => {
      OAuth2.check_token()
        .then(() => {
          resolve(AccessToken.getAccessToken());
        })
        .catch((err) => {
          OAuth2.refresh_access_token()
            .then(() => {
              resolve(AccessToken.getAccessToken());
            })
            .catch((err) => {
              reject(err);
            });
        });
    });
  }

  static check_token() {
    return new Promise((resolve, reject) => {
      if (!AccessToken.isPrivileged()) {
        throw new Error("token isn`t privileged");
      }

      request
        .get(`https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=${AccessToken.getAccessToken()}`)
        .end((err, res) => {
          if (res.ok) {
            resolve();
          } else {
            reject(err);
          }
        });
    });
  }

  static get_access_token(code) {
    return new Promise((resolve, reject) => {
      request
        .post("https://www.googleapis.com/oauth2/v3/token")
        .type("form")
        .send({
          client_id: client_id,
          client_secret: client_secret,
          code: code,
          grant_type: "authorization_code",
          redirect_uri: "urn:ietf:wg:oauth:2.0:oob"
        })
        .end((err, res) => {
          if (!res.ok) {
            reject(err);
          }

          var { access_token, refresh_token } = res.body;
          AccessToken.saveAccessToken(access_token);
          AccessToken.saveRefreshToken(refresh_token);
          resolve(access_token);
        });
    });
  }

  static refresh_access_token() {
    return new Promise((resolve, reject) => {
      if (!AccessToken.isPrivileged()) {
        throw new Error("token isn`t privileged");
      }

      request
        .post("https://www.googleapis.com/oauth2/v3/token")
        .type("form")
        .send({
          client_id: client_id,
          client_secret: client_secret,
          grant_type: "refresh_token",
          refresh_token: AccessToken.getRefreshToken()
        })
        .end((err, res) => {
          if (!res.ok) {
            reject(err);
            return;
          }

          var { access_token } = res.body;
          AccessToken.saveAccessToken(access_token);
          resolve(AccessToken.getAccessToken());
        });
    });
  }

  static start_chrome_authorization() {
    return new Promise((resolve) => {
      chrome.identity.launchWebAuthFlow(
        {
          url: `https://accounts.google.com/o/oauth2/auth?client_id=${client_id}&redirect_uri=urn:ietf:wg:oauth:2.0:oob&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email`,
          interactive: true
        },
        () => {
          setTimeout(() => {
            var code = prompt("please input authorization code");
            if (!code) {
              throw new Error("invalid code");
            }

            OAuth2.get_access_token(code).then((token) => {
              resolve(token);
            });
          }, 1000);
        }
      );
    });
  }
}
