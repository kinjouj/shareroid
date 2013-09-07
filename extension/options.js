var OptionPage = (function () {
    function OptionPage() {
        this.shareroid = new Shareroid();
    }
    OptionPage.prototype.start = function () {
        var _this = this;
        this.shareroid.authorize(function () {
            $("#sync_btn").click(function () {
                _this.shareroid.sync();
            });

            var p = _this.shareroid.fetchHistories();
            p.done(function (histories) {
                histories.forEach(function (history) {
                    var d = new Date(history.createdAt);

                    $("#histories").append($("<tr>").append($("<td>").text(d.toString()), $("<td>").append($("<a>").attr("href", history.url).text(history.url))));
                });

                $("#history_block").css("display", "block");
            });
        });
    };
    return OptionPage;
})();

var page = new OptionPage();
page.start();
