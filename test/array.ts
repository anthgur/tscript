Array = function() {
  var a = new "";
  a.length = 0;
  return a;
};

Array.push = function(a, x) {
  var n = a.length;
  if (n == undefined) {
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

var startProd, startSymbol, productions;
productions = Array();

startProd = readln();
Array.push(productions, startProd);

while(true) {
  var line = readln();
  if (line == "") {
    break;
  }
  Array.push(productions, line);
}

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
