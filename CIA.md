# Classical Internet Applications

## Notes

### History

- 1957 sputnik
- 1958 Advanced Projects Reseach Agency (ARPA)
- 1963 leonard kleinrock: message switching
- 1964 paul baran: packet switching (message blocks)
- 1965 donald davies: packet switching
- 1967 ARPANET
  - Interface Message Processor (IMP): early switch/router
- 1968
  - doug engelbart: hypermedia
- 1969
  - ARPANET IMP / Network Control Program (NCP)
  - BBN: company
  - steve crocker: RFC #1
  - ken thompson: B
  - dennis ritchie: C
  - Unics: PDP-7, Multics, interactive, time sharing
  - kleinrock - engelbart: TELNET
- 1971
  - FTP
  - Tomlinson: @ email
  - Unix V1
- 1973
  - Unix V4
  - C / pipes
  - HOSTS.TXT
- 1974
  - TCP/IP: replace NCP
  - RFC #675 Internet
- 1975: Unix V6 / ARPANET
- 1976: Mailing lists
- 1977: TCP/IP vs OSI
- 1979
  - Unix V7 / Unix to Unix Copy: UUCP
  - Unix to VAX
  - 3BSD
  - UUCP ! email
- 1982
  - Simple Mail Transfer Protocol (SMTP)
- 1983
  - NCP -> TCP/IP
  - System V
  - sendmail
  - paul mockapetris: Domain Name System: DNS
- 1984
  - Top Level Domains: TLD
  - USSR on usenet
- 1986
  - MX records
  - MB MG MR MINFO: failed
  - Network News Transfer Protocol: NNTP
  - 4.3BSD
  - Stanford Research Institute SRI runs DNS
- 1988
  - NSFNET upgrade to T1
  - SURFnet
  - jarkko oikarinen: Internet Relay Chat: IRC
- 1990
  - NSFNET upgrade to T3
  - World Wide Web: WWW
- 1991
  - Gopher
- 1993
  - NCSA mosaic
- 1994
  - Linux 1.0
  - W3C
  - Netscape
- 1995
  - graphical web
  - Internet Explorer
- 1997
  - DNSSEC 1
- 1998
  - ICANN
  - IP: Address Supporting Organization ASO
  - Domains: G/cc Names Supporting Organization NSO
  - Technical: Internet Assigned Numbers Authority IANA
- 1999
  - XMPP
  - blogging / napster
  - DNSSEC 2
- 2000
  - Dotcom bubble
- 2003: SNE
- 2004: Facebook
- 2005: DNSSEC 3
- 2006: Twitter

#### X server / SSH

- user
- OS
- X server
- xterm
- ptmx
- pty / pts
- shell
  - fork/exec
- ssh
- sshd
- ptmx
- pts / pty
- shell

#### exec

- strace / ptrace
- stat / execve
- fork / vfork / clone / wait
  - Thread Group ID / Thread ID

#### ELF / binary / compile

- assember out / Common Object File Format / Executable and Linkable Format / Mach Obj / Portable Executable
- interpreter

#### Compilation

- preprocessing
- assembler (assembly)
- compiler (object)
- linker
  - sections / segments: runtime load: .got .plt

#### CPU internals

- 8008: A 8bit
- 8080: A 8bit / BC pair
- 8086: AX 16bit
- 80386: EAX 32bit
- amd64: RAX 64bit

#### Assembly

- intel left assign
- at&t right assign
- call / ret
- int / syscall

### DNS

- owner ttl class type data
- CNAME: only record allowed (except DNSSEC)
- DNAME: subtree not including self, no CNAME
- MX: mail exchanger, no CNAME/DNAME
- NS: no CNAME/DNAME
- PTR: anything
- SOA: hostname, email, (serial refresh retry expire minimum)
- SRV: services
- DNSKEY: ksk/zsk_flag 3 algo key_b64
- DS: keytag algo hash_algo digest_hex
- RRSIG: type algo labels ttl valid2 keytag zone signature_b64
- NSEC: next_domain (current_types)
- NSEC3: hash_algo flags iter salt_len salt next_hashed (current_types)
- "13" root servers

### Querying

- Header, Question, Answer, Authority, Additional
- Header
  - ID, flags, QDCOUNT, ANCOUNT, NSCOUNT, ARCOUNT
  - flags: q/r, opcode, AA (autoritative), tc (truncated), rd (recurse des), ra (recuse avail), ad (authentic), cd (check disable), rcode

### EDNS0

- bigger, more codes/flags
- OPT RR
- TSIG: signs packets
- SIG0: dnssec packets

#### DNSSEC

- protects against non authoritative servers
- sign authoritative data

### Email

- ESMTP sesion: envelope
  - EHLO addr
  - MAIL FROM
  - RCPT TO
  - DATA: .
  - QUIT
- Message Format
  - 7bit ascii CRLF
  - 998text + CRLF
  - headers: Sender, Cc, Bcc, To, Message-ID, Received
- AUTH, STARTTLS, PGP, S/MIME
  - Multipurpose Internet Mail Extensions MIME
- DANE, MTA-STS, SPF, DKIM

### Web

- layout: gecko / khtml / webkit / blink / trident / edgehtml / presto
- js: spidermonkey / kjs / jscore / nitro / v8 / chakra / carakan
- CGI / SSI (php jsp) / servlets / asp.net
- DOM / DHTML / ecmascript / asynchronous javascript and xml AJAX / XMLHttpRequest
- h2 server push
- http methods / headers /
- \*ml
