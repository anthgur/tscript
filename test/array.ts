Array = new 42;

Array.push = function(a, x) {
  print (a.length);
  a.length = 42;
  print (a.length);
  //if (n == undefined) {
  //  n = 0;
  //}
  //a[n] = 42;
  //a.length = n + 1;
};

var a = new 42;
//a.lol = "lol";
Array.push(a, 42);
