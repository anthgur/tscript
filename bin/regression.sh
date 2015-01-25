#!/bin/bash

# do regression testing of ts
#
# if nothing printed, then all tests passed
#
# this script needs to be run from the directory where it is stored,
# which is tscript/bin
#

cd ../test

# test programs with semantic errors
for prog in illegalAssignment
do
  ../build/bin/ts $prog.ts >&$prog.err
  cmp $prog.err results/$prog.err
  if [ $? -ne 0 ]
  then echo $prog.ts failed
  fi
  rm -f $prog.err
done

# test programs that should execute
for prog in assign exp first undeclaredLval undeclaredRval bool negation
do
  ../build/bin/ts $prog.ts >&$prog.out
  cmp $prog.out results/$prog.out
  if [ $? -ne 0 ]
  then echo $prog.ts failed
  fi
  rm -f $prog.out
done

cd ../bin
