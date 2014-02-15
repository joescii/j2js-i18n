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
      expect(test1.ok).toEqual("OK");
      expect(test1.cancel).toEqual("Cancel");
      expect(test1["ok"]).toEqual("OK");
      expect(test1["cancel"]).toEqual("Cancel");
    });
  });

  describe('A JsResourceBundle with keys that are other-wise illegal JS identifiers', function() {
    it('should render an object nonetheless', function() {
      expect(test2["class"]).toEqual("Clazz");
      expect(test2["com.joescii"]).toEqual("Joe Barnes");
    });
  });

  describe('A JsResourceBundle with a parameterized value of cardinality 1', function() {
    it('should render a function taking one argument', function() {
      expect(test3.params(5)).toEqual("This has 5 param(s).");
      expect(test3.leading(3)).toEqual("3 param(s) in the lead.");
      expect(test3.trailing(7)).toEqual("Number of params trailing is 7");
    });
  });

  describe('A JsResourceBundle with multiple parameter values', function (){
    // test4
  });

  describe('A JsResourceBundle with out-of-order and missing parameter values', function () {
    // test5
  });
}).call(this);