//
// test precedence and associativity of operators
//

print 1+2*3;
print 3*2+1;
print (1+2)*3;
print 1+2+3;
print 2*3*4;

var abc;
var def;

print abc = def = 42;
print abc + def;

print abc = 1 + 2 * 3;

print abc = 1 - 2 + 3;
print 2 - 2 * 2;

// sanity check for literals blowing up

0;
0.0;
.0;
0.5e+10;
123E-14;

0xabcdef;
0xABCEDF;
0x01234;
0x56789;