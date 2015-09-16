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

Array.union = function(a, b) {
  var added = false;
  Array.forEach(b, function(x) {
    if (!Array.some(a, eq(x))) {
      added = true;
      Array.push(a, x);
    }
  });
  return added;
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

var eq = function(x) {
  return function(y) { return x == y; };
};

var Analyzer = function ctor() {
  var a = new "Analyzer";
  a.productions = Array();
  a.startSymbol = null;

  a.nonTerms = Array();
  a.terms = Array();

  a.nullDeriving = Array();
  a.follow = Array();
  a.first = Array();
  a.predict = Array();

  a.prototype = ctor;
  return a;
};

Analyzer.some = function(alz, key) {
  return function(v) {
    return Array.some(alz[key], eq(v));
  };
};

Analyzer.isEmptyProd = function(prod) {
  if (prod.length == 1) {
    return true;
  }
};

// functional abstraction where
// f is a callback of type
// str * str list -> unit
// ie, a production's lhs * rhs
Analyzer.forProductions = function(alz, f) {
  Array.forEach(alz.nonTerms, function(t) {
    Array.forEach(alz['productions'][t], function(prodSlot) {
      Array.forEach(prodSlot, function(rhs) {
        f(prodSlot.lhs, rhs);
      });
    });
  });
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
    alz['first'][nonTerm] = Array();
    alz['follow'][nonTerm] = Array();
  }
  Array.push(prodSlot, Array.rest(prod));
  Array.push(alz['productions'][nonTerm], prodSlot);

  if (alz.isEmptyProd(prod)) {
    Array.push(alz.nullDeriving, nonTerm);
  }
};

Analyzer.analyzeNullDeriving = function(alz) {
  var infoGained, nullp = alz.some(alz, 'nullDeriving');

  while (true) {
    infoGained = false;
    alz.forProductions(alz, function(lhs, rhs) {
      if (!nullp(lhs)) {
        // all syms in rhs are null deriving
        if (rhs.length == Array.filter(rhs, nullp).length) {
          Array.push(alz.nullDeriving, lhs);
          infoGained = true;
        }
      }
    });
    if (!infoGained) {
      break;
    }
  }
};

Analyzer.analyzeFirst = function(alz) {
  var infoGained
    , nullp = alz.some(alz, 'nullDeriving')
    , checkFirst = function recur(rhs) {
      if (rhs.length == 0) {
        return null;
      }
      var t = rhs[0];
      if (isTerm(t)) {
        var a = Array();
        Array.push(a, t);
        return a;
      }
      var ret = null, tFirst = alz['first'][t];

      if (tFirst.length > 0) {
        ret = Array();
        Array.union(ret, tFirst);
      }
      if (nullp(t)) {
        var r = recur(Array.rest(rhs));
        if (r) {
          if (!ret) {
            ret = Array();
          }
          Array.union(ret, r);
        }
      }
      return ret;
    };

  while (true) {
    infoGained = false;
    alz.forProductions(alz, function(lhs, rhs) {
      var set = checkFirst(rhs);
      if (set) {
        if (Array.union(alz['first'][lhs], set)) {
          infoGained = true;
        }
      }
    });
    if (!infoGained) {
      break;
    }
  }
};

var printSyms = function(syms) {
  print Array.reduce(syms, '', function(x, y) {
    return x + y + ' ';
  });
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
  var fmtSyms = function(syms) {
    return Array.reduce(syms, '', function(x, y) {
      return x + y + ' ';
    });
  };

  print "Start Symbol";
  print alz.startSymbol;

  print "\nNonterminals";
  print fmtSyms(alz.nonTerms);

  print "\nTerminals";
  print fmtSyms(alz.terms);

  print "\nNull-Deriving Nonterminals";
  print fmtSyms(alz.nullDeriving);

  print "\nFirst";
  Array.forEach(alz.nonTerms, function(lhs) {
    print lhs + ": " + fmtSyms(alz['first'][lhs]);
  });

  print "\nFollow";
  printSyms(alz.follow);

  print "\nPredict";
  printSyms(alz.predict);
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

  alz.analyzeNullDeriving(alz);
  alz.analyzeFirst(alz);

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
