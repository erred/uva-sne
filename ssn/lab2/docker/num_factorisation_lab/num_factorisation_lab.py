#!/usr/bin/python

import sys, time, gc, random, inspect


def testForInt(n):
    ret = True
    try:
        n = int(n)
    except ValueError:
        print("Usage: The function takes an integer as an argument.")
        ret = False
    return ret


def prime_factors1(n):
    '''
    https://stackoverflow.com/questions/15347174/python-finding-prime-factors
    "prime_factors1() and prime_factors2() take an arbitrary integer, attempts to factor it, and return it's factors as a list. Usage: Within the python interpreter, type 'print num_factorisation.prime_factors1(n)' where 'n' is your arbitrary integer."
    '''
    testForInt(n)

    i = 2
    factors = []
    while i * i <= n:
        if n % i:
            i += 1
        else:
            n //= i
            factors.append(i)
    if n > 1:
        factors.append(n)
    return factors


def prime_factors2(n):
    '''
    Implement your own algorithm! Prove that it works. BONUS for more efficient implementation.
    '''
    testForInt(n)

    i = 2
    factors = []
    while i * i <= n:
        if n % i:
            i += 1 + i % 2
        else:
            n //= i
            factors.append(i)
    if n > 1:
        factors.append(n)
    return factors


def randRun(REPS):
    '''
    The function takes an integer, that represents the number of digits of the pseudo-random number that the program should try to factor with above declared factorisation algorithm. The algorithm starts factoring from 10 digits up, defined by MIN.
    '''
    MIN = 10

    if REPS <= MIN:
        print(
            "Usage: randRun() takes an integer LARGER than 10 as an argument.")
        exit(2)

    num = ""
    for i in range(1, REPS + 1):
        gc.collect()
        if i == 1:
            #let's not kill it with a zero at the beginning
            num += str(random.randrange(1, 10))
        else:
            num += str(random.randrange(0, 10))
        num = int(num)
        if i >= MIN:
            print "For digits: ", num
            t1 = time.time()
            prime_factors1(num)
            t2 = time.time()
            prime_factors2(num)
            t3 = time.time()
            print i, ",f1,", t2 - t1, ",f2,", t3 - t2

        num = str(num)


def testrun():
    randRun(30)
    randRun(30)
    randRun(30)


if __name__ == "__main__":
    '''
    You can run the script either from Python 2.7 or compile with Cython.
    '''
    testrun()
