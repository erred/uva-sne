# Security of Systems and Networks

## Textbooks

Primary: [Information Security](Information_Security.pdf)

Secondary: [Network Security](Network_Security.pdf)

## Notes

### Classical Crypto

- Shift, ROT, Caesar, Monoalphabetic
  - frequency analysis
  - atbash = reverse
  - playfair = digraph lookup, split similar, replace below / right
  - adfgvx = 6x6 grid, lookup row/col, grid by key, rearrange cols by alpha
- Codebook / lookup table
- Polyalphabetic
  - Vigenere: left shift rows
- Double transposition (rows, cols)
- One time pad

### Enigma

3 wheels + reflector, 26^3 period

Repeated messgae, standard message, never self translate, operational constraint 10e114 -> 10e23

- plugboard (characters, wires): C!/((c-2w)!w!(2^w))
- reflectors \* (characters ^ rotors) \* (rotor_total!/(rotor_total - rotors)!) \* (characters \* (rotors-1))

### A5/1

Shift registers: m = maj(x8, y10, z10), step if reg == m

- X / 19bits, x0 = x13 ^ x16 ^ x17 ^ x18
- Y / 22bits, y0 = y20 ^ y21
- Z / 23bits, z0 = z7 ^ z21 ^ z22 ^ z23
- keystream = x18 ^ y21 ^ z22

### RC4

self modifying lookup table: swap, get

discard first 256 bytes

### Feistel / DES / TDES

64(56) bits

1. mix
2. split A B
3. A = A ^ F(B), B = B
4. swap(A, B)
5. repeat 16 rounds
6. unmix

### Block chaining

- ECB: unlinked
- CBC: (N-1) ^ N --E+K--> N
- CFB: (N-1) --E+K--> ^ N --> N
- OFB: (NpreXOR-1) --E+K--> ^ N --> N
- CTR: Nonce+Counter --E+K--> ^ N --> N

### AES

1. AddRoundKey
2. Repeat 9/11/13 rounds
   1. SubBytes: lookup table: nonlinear
   2. ShiftRows: leftshift row#: diffusion
   3. MixColumn: maxtrix mult: diffusion
   4. AddRoundKey
3. Sub, Shift, AddRoundKey

### RSA

integer factorization, pq from N,
all in mod **N** space,
phi = number of elements in prime field,
a ^ phi(N) = 1 mod N

- **N** = _pq_, phi( **N** ) = ( _p_ - 1 )( _q_ - 1 )
- **E** _d_ = 1 mod phi( **N** )
- public key: **N**, **E**
- pivate key: _p_, _q_, _d_
- encryption: C = m^ **E** mod **N**
- decryption: m = C^ **d** mod **N**

### Diffie-Hellman

discrete logarithm, reconstruct x from G ^ x,
all in mod **P** space

- _a_, _b_ secret
- **G** ^ _a_, **G** ^ _b_ public
- **G** ^ _ab_ secret

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

#### Shibboleth
