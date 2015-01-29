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

// sanity check for hex literals blowing up

0xabcdef;
0xABCEDF;
0x01234;
0x56789;