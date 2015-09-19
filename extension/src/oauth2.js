/*global chrome*/

import request from "superagent";
import {client_id, client_secret} from "./secret";

global.Promise = require("bluebird");

const ACCESS_TOKEN_KEY = "access_token";
const REFRESH_TOKEN_KEY = "refresh_token";
const REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

class AccessToken {

  static get token() {
    return localStorage[ACCESS_TOKEN_KEY] || null;
  }

  static set token(token) {
    return localStorage[ACCESS_TOKEN_KEY] = token;
  }

  static get refreshToken() {
    return localStorage[REFRESH_TOKEN_KEY] || null;
  }

  static set refreshToken(token) {
    return localStorage[REFRESH_TOKEN_KEY] = token;
  }

  static isPrivileged() {
    return AccessToken.token !== null && AccessToken.refreshToken !== null;
  }
}

export default class OAuth2 {

  static getAccessToken() {
    return AccessToken.token;
  }

  static authorize() {
    return new Promise((resolve, reject) => {
      OAuth2.check_token()
        .then(() => {
          resolve();
        })
        .catch(() => {
          OAuth2.refresh_access_token()
            .then(() => {
              resolve();
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
        .get(`https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=${AccessToken.token}`)
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
          redirect_uri: REDIRECT_URI
        })
        .end((err, res) => {
          if (!res.ok) {
            reject(err);
          }

          var { access_token, refresh_token } = res.body;
          AccessToken.token = access_token;
          AccessToken.refreshToken = refresh_token;
          resolve();
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
          refresh_token: AccessToken.refreshToken
        })
        .end((err, res) => {
          if (!res.ok) {
            reject(err);
            return;
          }

          var { access_token } = res.body;
          AccessToken.token = access_token;
          resolve(AccessToken.token);
        });
    });
  }

  static start_chrome_authorization() {
    return new Promise((resolve) => {
      chrome.identity.launchWebAuthFlow(
        {
          url: `https://accounts.google.com/o/oauth2/auth?client_id=${client_id}&redirect_uri=${REDIRECT_URI}&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email`,
          interactive: true
        },
        () => {
          setTimeout(() => {
            var code = prompt("please input authorization code");
            if (!code) {
              throw new Error("invalid code");
            }

            OAuth2.get_access_token(code).then(() => {
              resolve();
            });
          }, 1000);
        }
      );
    });
  }
}
