/// <reference path="shareroid.ts"/>

declare var $;

declare var Date: {
  new(value: Number): Date;
}

class OptionPage {

  shareroid : Shareroid;

  constructor() {
    this.shareroid = new Shareroid();
  }

  start() {
    this.shareroid.authorize(() => {
      $("#sync_btn").click(() => {
        this.shareroid.sync();
      });

      var p = this.shareroid.fetchHistories();
      p.done(
        (histories : Array<JSONObject>) => {
          histories.forEach(
            (history : JSONObject) => {
              var d = new Date(history.createdAt);

              $("#histories").append(
                $("<tr>").append(
                  $("<td>").text(d.toString()),
                  $("<td>").append(
                    $("<a>").attr("href", history.url).text(history.url)
                  )
                )
              )
            }
          );

          $("#history_block").css("display", "block");
        }
      );
    });
  }
}

var page = new OptionPage();
page.start();
