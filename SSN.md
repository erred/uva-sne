# Security of Systems and Networks

## Textbooks

Primary: [Information Security](Information_Security.pdf)

Secondary: [Network Security](Network_Security.pdf)

## Notes

### Classical Crypto

#### Substitution

#### Transposition

#### CryptoAnalysis

#### Enigma

### Stream Ciphers

#### A5/1

#### RC4

### Block Ciphers

#### Feistel / DES / TDES

#### AES

### Asymmetric

#### RSA

#### Diffie-Hellman

#### Elliptical Curve

#### Confidentiality / Non repudiation

### Hash

Compression, Efficiency, One Way, Weak collision (given 1), Strong collision (any)

### Protocols

Confidentiality, Integrity, Availability

#### TCP

SEQ random or guessable: DOS

1. **Alice** -> **Bob**: SYN, SEQa
2. **Bob** -> **Alice**: SYN, ACKa+1, SEQb
3. **Alice** -> **Bob**: ACKb+1

#### Biometrics

Something you are: fingerprint, hand/palm, signature, facial, speech, iris, gait, odor

ideal: universal, distinguishing, permanent, collectable

**Fraud Rate**: A misauth as B.
**Insult Rate**: A authfail as A
**Equal Error Rate**: FR = IR

EER: hand 10e-3, fingerprint 5e-2, iris 10e-6

#### SSH

- SSH
  1. **Alice** -> **Bob**: "Alice", CryptoProposed, Ra
  2. **Bob** -> **Alice**: CryptoSelected, Rb
  3. **Alice** -> **Bob**: ga%p
  4. **Bob** -> **Alice**: gb%p, certb, [H]b
     - H: h("Alice", "Bob", CryptoProposed, CryptoSelected, Ra, Rb, ga%p, gb%p, K)
     - K: gab%p
  5. **Alice** -> **Bob**: E("Alice", certa, [H, "Alice", certa]a, K)

#### TLS / SSL

- Session
  1. **Alice** -> **Bob**: Request, CryptoProposed, Ra
  2. **Bob** -> **Alice**: Certificate, CryptoSelected, Rb
  3. **Alice** -> **Bob**: {PreMaster}b, E(h(msgs, "CLNT", K), K)
     - Unnecessary encryption
  4. **Bob** -> **Alice**: h(msgs, "SRVR", K)
     - K: h(PreMaster, Ra, Rb)
     - Derive 2 encrypt, 2 integrity, 2 iv
- Connection
  1. **Alice** -> **Bob**: SessionID, CryptoProposed, Ra
  2. **Bob** -> **Alice**: SessionID, CryptoSelected, Rb, h(msgs, "SRVR", K)
     - K: h(PreMaster, Ra, Rb)
  3. **Alice** -> **Bob**: h(msgs, "CLNT", K)

#### IPSec

- IKE / phase1: IKE SA
  - eDH session key: perfect forward secrecy
  - IC / RC cookies: useless stateless
  - PK encrypt / main
    1. **Alice** -> **Bob**: IC, CryptoProposed
    2. **Bob** -> **Alice**: IC, RC, CryptoSelected
    3. **Alice** -> **Bob**: IC, RC, ga%p, {Ra}b, {"Alice"}b
    4. **Bob** -> **Alice**: IC, RC, gb%p, {Rb}a, {"Bob"}a
    5. **Alice** -> **Bob**: IC, RC, E(Proofa, K)
       - SkeyID: h(Ra, Rb, gab%p)
       - Proofx: h(SkeyID, ga%p, gb%p, IC, RC, CryptoX, "X")
       - K: h(IC, RC, gab%p, Ra, Rb)
    6. **Bob** -> **Alice**: IC, RC, E(Proofb, K)
  - PK encrypt / aggressive: hidden id, plausible deniability
    1. **Alice** -> **Bob**: IC, ga%p, {Ra}b, {"Alice"}b, CryptoProposed
    2. **Bob** -> **Alice**: IC, RC, gb%p, {Rb}a, {"Bob"}a, CryptoSelected, Proofb
    3. **Alice** -> **Bob**: IC, RC, Proofa
  - PK sign / main: id can be found
    1. **Alice** -> **Bob**: IC, CryptoProposed
    2. **Bob** -> **Alice**: IC, RC, CryptoSelected
    3. **Alice** -> **Bob**: IC, RC, ga%p, Ra
    4. **Bob** -> **Alice**: IC, RC, gb%b, Rb
    5. **Alice** -> **Bob**: IC, RC, E("Alice", Proofa, K)
       - SKeyID: h(Ra, Rb, gab%p)
       - Proofx: [h(SkeyID, ga%p, gb%p, IC, RC, CryptoX, "X")]x
       - K: h(IC, RC, gab%p, Ra, Rb)
    6. **Bob** -> **Alice**: IC, RC, E("Bob", Proofb, K)
  - PK sign / aggressive: no attempt to hide
    1. **Alice** -> **Bob**: IC, ga%p, Ra, "Alice", CryptoProposed
    2. **Bob** -> **Alice**: IC, RC, gb%p, Rb, "Bob", CryptoSelected, Proofb
    3. **Alice** -> **Bob**: IC, RC, Proofa
  - Symmetric / main: id must be known to decrypt
    1. **Alice** -> **Bob**: IC, CryptoProposed
    2. **Bob** -> **Alice**: IC, RC, CryptoSelected
    3. **Alice** -> **Bob**: IC, RC, ga%p, Ra
    4. **Bob** -> **Alice**: IC, RC, gb%b, Rb
    5. **Alice** -> **Bob**: IC, RC, E("Alice", Proofa, K)
       - Kab: shared in advance
       - K: h(IC, RC, gab%p, Ra, Rb, Kab)
       - SKeyID: h(K, gab%p)
         Proofx: h(SkeyID, ga%p, gb%p, IC, RC, CryptoX, "X")
    6. **Bob** -> **Alice**: IC, RC, E("Bob", Proofb, K)
  - Symmetric / aggressive
    - see PK sign / aggressive
    - no main problems
- IKE / phase2: IPSec SA
  1. **Alice** -> **Bob**: IC, RC, CryptoProposed, E(hash1, SA, Ra, K)
     - IC, RC, SA, K: phase1
     - hashx: SkeyID, SA, Ra, Rb
     - key from: h(SkeyID, Ra, Rb, junk)
  2. **Bob** -> **Alice**: IC, RC, CryptoSelected, E(hash2, SA, Rb, K)
  3. **Alice** -> **Bob**: IC, RC, E(hash3, K)
- Comms
  - Transport Mode: insert extra header, between hosts
  - Tunnel Mode: Wrap in new header, between firewalls
  - Authentication Header: sign partial header + body
  - Encapsulating Security Protocol: encrypt body

#### Kerberos

Key Distribution Center **KDC**: Trusted Third Party **TTP**,
N (users) + 1 (master) key, DES,
Time security critical, replay window,
Ticket Granting Ticket **TGT** = E("User", Sa, Kkdc)

- Login
  1. **Alice** -> **KDC**: request login
  2. **KDC** -> **Alice**: E(Sa, TGT, Ka)
- Request resource:
  1. **Alice** -> **KDC**: TGT, E(timestamp, Sa)
  2. **KDC** -> **Alice**: E("Bob", Kab, E("Alice", Kab, Kb), Sa)
  3. **Alice** -> E("Alice", Kab, Kb), E(timestamp, Kab)
  4. **Bob** -> E(timestamp+1, Kab)

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

### 2019-09-12 Stream ciphers

- Stream Cipher
  - stretch key, use like one time pad
  - A5/1, A5/2, RC4 (read, use in cell)

### 2019-09-16 Block ciphers

- Block cipher
  - manipulation on matrix
  - DES
  - AES, shift, mix, sub
