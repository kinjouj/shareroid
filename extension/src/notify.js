export default class Notify {
  static send(id, title, message) {
    chrome.notifications.create(
      String(id),
      {
        "type": "basic",
        "iconUrl": "img/icon_48.png",
        "title": title,
        "message": message
      },
      (id) => {
        setTimeout(() => {
          chrome.notifications.clear(id, () => {});
        }, 3000);
      }
    );
  }
}
