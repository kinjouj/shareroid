"use strict";

describe('Share', function() {

  var share;

  beforeEach(function() {
    share = new Share();
  });

  afterEach(function(done) {
    share.clear(function() {
      done();
    });
  });

  it("#getDB", function(done) {
    share.getDB(function(db) {
      expect(db).not.to.be.null;
      done();
    });
  });

  it("#getDB (missing argument `callback`)", function() {
    expect(function() { share.getDB(); }).to.be.throw(Error);
  });

  it("#count", function(done) {
    share.save("test", function() {
      share.count(function(cnt) {
        expect(cnt).to.be.eq(1);

        done();
      });
    });
  });

  it("#count (missing argument `callback`)", function() {
    expect(function() { share.count(); }).to.be.throw(Error);
  });

  it("#urls", function(done) {
    share.save("test", function() {
      var p = share.urls(function(url) {
        expect(url).to.be.eq("test");
      });
      expect(p).not.to.be.null;

      p.done(function(num) {
        expect(num).to.be.eq(1);
        done();
      });
    });
  });

  it("#urls (missing argument `callback`)", function() {
    expect(function() { share.urls(); }).to.be.throw(Error);
  });

  it("#save", function(done) {
    share.save("test", function(id, url) {
      expect(url).to.be.eq("test");
      done();
    });
  });

  it("#save (invalid argument `url`)", function() {
    expect(function() { share.save(); }).to.be.throw(Error);
    expect(function() { share.save(''); }).to.be.throw(Error);
  });

});
