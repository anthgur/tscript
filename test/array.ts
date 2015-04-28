Array = function() {
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
  var n = a.length - 1;
  if (n == undefined) {
    return;
  }
  a[n] = undefined;
  a.length = n;
};

Array.map = function(a, f) {
  var b = new 2
    , n = a.length
    , x = 0;
  while (x < n) {
    b[x] = f(a[x]);
    x = x + 1;
  }
  return b;
};

var startProd, startSymbol, rawProductions, productions, terms, nonTerms;
rawProductions = Array();
productions = Array();
nonTerms = Array();
terms = Array();

startProd = readln();
Array.push(productions, startProd);

var rest = function(o) {
  var a = Array(), l = o.length, x = 1;
  while(x < l) {
    Array.push(a, o[x]);
    x = x + 1;
  }
  return a;
};

var analyzeSyms = function(prod, syms, p) {
  var l = syms.length, x = 0;
  while (x < l) {
    var k = syms.length, y = 0, s = prod[x];
    if (p(s)) {
      while (y < k) {
        if (!(prod[x] == syms[y])) {
          Array.push(syms, prod[x]);
          break;
        }
        y = y + 1;
      }
    }
    x = x + 1;
  }
};

var lol = String.split("", " ");

while(true) {
  var line = readln();
  if (line == "") {
    break;
  }
  var split = String.split(line, " ");
  Array.push(terms, split[0]);
  //analyzeSyms(split, terms);
  //analyzeSyms(split, nonTerms);
}

print String.charCodeAt("a", 0);

//Array.map(terms, function(x) { print x; });

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
