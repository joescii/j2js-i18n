(function() {
  describe('An empty JsResourceBundle', function() {
    it('should render an empty object', function() {
      expect(test0).toEqual({});
    });
  });

  describe('A JsResourceBundle with two simple properties', function() {
    it('should render an object with two fields', function() {
      expect(test1).toEqual({
        ok: "OK",
        cancel: "Cancel"
      });
    });
  });
}).call(this);