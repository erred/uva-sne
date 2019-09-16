# Security of Systems and Networks

## Textbooks

Primary: [Information Security](Information_Security.pdf)

Secondary: [Network Security](Network_Security.pdf)

## Days

### 2019-09-02 Classical Crypto

- Substitution ciphers
- Transposition ciphers
- one time pads
- [lab](lab1)
- rainbow tables:
  - trade storage for runtime compute
  - store start/end of hash chains, compute new chain for target, compare each
  - different reduction function per step to reduce collisions

### 2019-09-05

- Numbers:
  - Natural **N**: 0, 1, 2, ...
  - Integers **Z**: ..., -2, -1, 0, 1, 2, ...
- Groups **G**:
  - &lt; elements/{except_these}, operator, result &gt;
  - Commutative (order doesn't matter with single operator)
  - Associative (order doesn't matter with multiple operators)
  - Neutral element (noop element)
  - Inverse exists
- Fields **F**:
  - &lt; elements/{except_these}, addition, multiplication, addition_result, multiplication_result &gt;
  - combines an additive and a multiplicative group
  - Distributive (multiplcation spreads over addition)
- Other
  - each number has a unique prime factorization
  - a &gt; b, gcd(a, b) = gcd(a-b, b) || b (if a % b == 0)
  - coprimes, relatively prime: gcd(a, b) = 1
- Modular (Clock) Arithmetic:
  - **Z<sub>n</sub>**, where n = mod n on everthing
  - apply _(mod n)_ on everthing
  - Fields only exist when _n_ is prime: **F<sub>n</sub>**
  - Prime fields, all elements are coprime with n: **Z<sup>\*</sup><sub>n</sub>**
  - phi(n) = number of elements in field
    - a<sup>phi(n)</sup> === 1 (mod n), where a is in **Z<sup>\*</sup><sub>n</sub>**
    - n is prime: phi(n) = n -1
    - n is prime, k > 0: phi(n<sup>k</sup>) = n<sup>k-1</sup>(n-1)
    - coprime m, n: phi(m _ n) = phi(m) _ phi(n)
    - different primes m, n: phi(m _ n) = (m-1) _ (n-1)
  - cyclic e^k will cycle with order m, m = n - 1 for prime n
  - primitive roots: e^k will generate all elements in the field
  - chinese remainder theorem: refer to slides
- RSA
  - Public - Private key
  - generate primes p, q
    - N = pq
    - phi(N) = (p-1)(q-1)
    - E in **Z<sup>\*</sup><sub>phi(N)</sub>**
    - d in **Z<sup>\*</sup><sub>phi(N)</sub>**
    - - Ed (mod phi(N)) = 1
    - - create from gcd/euclid
    - Public Key: N, E
    - Private Key: p, q, d
    - message m, m &lt; N
      - m<sup>E</sup> = C (encrypted)
      - m = C<sup>d</sup>
      - (m<sup>E</sup>)<sup>d</sup> = m<sup>Ed</sup> = m<sup>1</sup>
        - Ed (mod phi(N)) = 1
        - Ed = 1 + k\*phi(N)
- Diffie-Hellman
  - Find shared secret
  - G: public generative root
  - X = G<sup>x</sup>
  - Y = G<sup>y</sup>
  - Shared = (G<sup>x</sup>)<sup>y</sup> = G<sup>xy</sup> = (G<sup>y</sup>)<sup>x</sup>
  - uncronstructable from X and Y
- Elliptical
  - same concept but uses points on elliptical curve as generator

### 2019-09-09 Enigma
- History
- Not max combinations
  - plug 11 wires(?)
- Eventually cyclic
- Never match self

### 2019-09-12 Modern Cryto DES and AES
- Stream Cipher
  - stretch key, use like one time pad
  - A5/1, A5/2, RC4 (read, use in cell)
- Block cipher
  - manipulation on matrix
  - DES
  - AES, shift, mix, sub
