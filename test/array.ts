var printf = function(x) { print x; };

var Array = function() {
  var a = new "Array";
  a.length = 0;
  return a;
};

Array.push = function(a, x) {
  var n = a.length;
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

Array.some = function(a, p) {
  var n = a.length, i = 0, x;
  while (i < n) {
    x = a[i];
    if (p(x)) {
      return x;
    }
    i = i + 1;
  }
  return false;
};

Array.rest = function(o) {
  var a = Array(), l = o.length, x = 1;
  while(x < l) {
    Array.push(a, o[x]);
    x = x + 1;
  }
  return a;
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

var Analyzer = function ctor() {
  var a = new "Analyzer";
  a.nullDeriving = Array();
  a.productions = Array();
  a.nonTerms = Array();
  a.terms = Array();
  a.startSymbol = null;
  a.prototype = ctor;
  return a;
};

Analyzer.isEmptyProd = function(prod) {
  if (prod.length == 1) {
    return true;
  }
};

Analyzer.analyzeProd = function(alz, line) {
  var nonTerm, prodSlot = Array(), prod = String.split(line, " ");
  prod = Array.map(prod, function(s) { return String.trim(s); });

  alz.analyzeSyms(prod, alz.terms, isTerm);
  alz.analyzeSyms(prod, alz.nonTerms, isNonTerm);

  nonTerm = prod[0];
  prodSlot.lhs = nonTerm;
  if (alz['productions'][nonTerm] == undefined) {
    alz['productions'][nonTerm] = Array();
  }
  Array.push(prodSlot, Array.rest(prod));
  Array.push(alz['productions'][nonTerm], prodSlot);

  if (alz.isEmptyProd(prod)) {
    Array.push(alz.nullDeriving, nonTerm);
  }
};

Analyzer.isNullDeriving = function(alz) {
  return function(sym) {
    return Array.some(alz.nullDeriving, function(x) {
      return sym == x;
    });
  };
};

Analyzer.analyzeNullDeriving = function(alz) {
  var infoGained = false, nullp = alz.isNullDeriving(alz);
  while (true) {
    infoGained = false;
    // non-terminals
    Array.forEach(alz.nonTerms, function(t) {
      // non-terminals' production slots
      // ex:
      // A B C
      // A a b
      Array.forEach(alz['productions'][t], function(prodSlot) {
        // productions
        // ex:
        // A B C
        Array.forEach(prodSlot, function(prod) {
          // only check if the nonterminal isn't null deriving
          if (!nullp(prodSlot.lhs)) {
            // length of production's rhs equals
            // number of null deriving syms in rhs
            // means the lhs (nonterminal) is null deriving
            // ie, all syms in rhs are null deriving
            if (prod.length == Array.filter(prod, nullp).length) {
              Array.push(alz.nullDeriving, prodSlot.lhs);
              infoGained = true;
            }
          }
        });
      });
    });
    if (!infoGained) {
      break;
    }
  }
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

Analyzer.analyze = function(alz) {
  alz.analyzeNullDeriving(alz);
};

Analyzer.printResults = function(alz) {
  var printSyms = function(syms) {
    print Array.reduce(syms, '', function(x, y) {
      return x + y + ' ';
    });
  };
  alz.analyze(alz);

  print "Start Symbol";
  print alz.startSymbol;

  print "\nNonterminals";
  printSyms(alz.nonTerms);

  print "\nTerminals";
  printSyms(alz.terms);

  print "\nNull-Deriving Nonterminals";
  printSyms(alz.nullDeriving);
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
