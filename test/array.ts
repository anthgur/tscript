var printf = function(x) { print x; };

var Array = function() {
  var a = new "";
  a.length = 0;
  return a;
};

Array.push = function(a, x) {
  var n = a.length;
  if (a == undefined) {
    a = Array();
  } else if (n == undefined) {
    n = 0;
  }
  a[n] = x;
  a.length = n + 1;
};

Array.pop = function(a) {
  var n = a.length - 1, x;
  if (n == undefined) {
    return;
  }
  x = a[n];
  a[n] = undefined;
  a.length = n;
  return x;
};

Array.map = function(a, f) {
  var b = new Array()
    , n = a.length
    , x = 0;
  while (x < n) {
    b[x] = f(a[x]);
    x = x + 1;
  }
  b.length = x;
  return b;
};

Array.forEach = function(a, f) {
  var n = a.length
    , x = 0;
  while (x < n) {
    f(a[x]);
    x = x + 1;
  }
};

Array.reduce = function(a, s, f) {
  var n = a.length
    , x = 0;
  while (x < n) {
    s = f(s, a[x]);
    x = x + 1;
  }
  return s;
};

Array.filter = function(a, p) {
  var r = Array();
  Array.forEach(a, function(x) {
    if (p(x)) {
      Array.push(r, x);
    }
  });
  return r;
};

Array.rest = function(o) {
  var a = Array(), l = o.length, x = 1;
  while(x < l) {
    Array.push(a, o[x]);
    x = x + 1;
  }
  return a;
};

var Analyzer = function ctor() {
  var a = new "Analyzer";
  a.productions = Array();
  a.nonTerms = Array();
  a.terms = Array();
  a.startSymbol = null;
  a.prototype = ctor;
  return a;
};

Analyzer.analyzeProd = function(alz, line) {
  var split = String.split(line, " ");
  split = Array.map(split, function(s) { return String.trim(s); });
  alz.analyzeSyms(split, alz.terms, isTerm);
  alz.analyzeSyms(split, alz.nonTerms, isNonTerm);
};

Analyzer.analyzeSyms = function(prod, syms, p) {
  Array.forEach(prod, function(s) {
    if (p(s)) {
      var ss = Array.filter(syms, function(x) {
        return s == x;
      });
      if (ss.length == 0) {
        Array.push(syms, s);
      }
    }
  });
};

Analyzer.printResults = function(alz) {
  print "Start Symbol";
  print alz.startSymbol;
  print '';
  print "Nonterminals";
  print Array.reduce(alz.nonTerms, '', function(x, y) {
      return x + y + ' ';
  });
  print '';
  print "Terminals";
  print Array.reduce(alz.terms, '', function(x, y) {
    return x + y + ' ';
  });
};

var isTerm = function(s) {
  return s == String.toLowerCase(s);
};

var isNonTerm = function(s) {
  return s == String.toUpperCase(s);
};

var isEmpty = function(s) {
  return s == "";
};

var runAnalysis = function() {
  var alz = Analyzer();
  var line = readln();
  alz.analyzeProd(alz, line);
  alz.startSymbol = alz.nonTerms[0];

  while(true) {
    var line = readln();
    if (isEmpty(line)) {
      break;
    }
    alz.analyzeProd(alz, line);
  }

  return alz;
};

var alz = runAnalysis();
alz.printResults(alz);

// test code here
var test = function() {
  var a = Array();
  Array.push(a, 42);
  Array.pop(a);

  var b = Array();
  Array.push(b, 1);
  Array.push(b, 2);
  Array.push(b, 3);
  var c = Array.map(b, function(x) { print(x);});
};

//test();
