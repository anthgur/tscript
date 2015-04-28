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

var rest = function(o) {
  var a = Array(), l = o.length, x = 1;
  while(x < l) {
    Array.push(a, o[x]);
    x = x + 1;
  }
  return a;
};

var productions, terms, nonTerms;
productions = Array();
nonTerms = Array();
terms = Array();

var isTerm = function(s) {
  return s == String.toLowerCase(s);
};

var isNonTerm = function(s) {
  return s == String.toUpperCase(s);
};

var isEmpty = function(s) {
  return s == "";
};

var analyzeSyms = function(prod, syms, p) {
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

var analyzeProd = function(line) {
  var split = String.split(line, " ");
  split = Array.map(split, function(s) { return String.trim(s); });
  analyzeSyms(split, terms, isTerm);
  analyzeSyms(split, nonTerms, isNonTerm);
};

var line = readln();
analyzeProd(line);
var startSymbol = nonTerms[0];

while(true) {
  var line = readln();
  if (isEmpty(line)) {
    break;
  }
  analyzeProd(line);
}

(function() {
  print "Start Symbol";
  print startSymbol;
  print '';
  print "Nonterminals";
  print Array.reduce(nonTerms, '', function(x, y) {
      return x + y + ' ';
  });
  print '';
  print "Terminals";
  print Array.reduce(terms, '', function(x, y) {
    return x + y + ' ';
  });
})
()
;


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
