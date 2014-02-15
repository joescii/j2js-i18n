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
      expect(test2["a 'key'"]).toEqual("a 'value'");
      expect(test2["with.newline"]).toEqual("another \n newline");
      expect(test2['dbl"quote']).toEqual("ridiculous, but should work");
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
    it('should render a function taking multiple arguments', function() {
      expect(test4.params2("one", "two")).toEqual("This one has two two params.");
      expect(test4.params3("one", "two", "three")).toEqual("This one has two three three params.");
      expect(test4.params9(1,2,3,4,5,6,7,8,9)).toEqual("Just 1 for 2 really 3 good 4 measure 5 this 6 string 7 has 8 nine! 9");
    });
  });

  describe('A JsResourceBundle with out-of-order and missing parameter values', function () {
    it('should render a function that correctly handles the order of arguments', function() {
      expect(test5.order2("arg0", "arg1")).toEqual("Out arg1 of arg0 order");
      expect(test5.order3("arg0", "arg1", "arg2")).toEqual("Out arg1 of arg0 order arg2");
    });

    it('should render a function that handles missing parameters', function() {
      expect(test5.missing2("arg0", "arg1", "arg2")).toEqual("Missing arg2 one arg0");
    });
  });
}).call(this);