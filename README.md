# UvA SNE Courses

Backup for course data

## Dokuwiki Log format

### Cheatsheet

```
====== Page Title Here ======

==== Q1 ====
=== a ===
answer here

=== formatting ===
**bold**

//italic//

__underline__

''monospace''

<del>strikethrough</del>

<hi #fff200>highlight</hi>

<sup>super</sup>

<sub>sub</sub>

> quote
>> here

<code>
block
</code>

<spoiler | name prefix>
hidden behind a button
</spoiler>

[[https://example.com|link text]]

 - ordered
 - list

 * unordered
 * list

ruler:
----

^ table   ^ header ^ here ^
^ header  | here   | too  |
| data    | in     | here |
| span                  |||
| or vertical | | |
| :::         | | |

```

## Block 1

### Security of Systems and Networks

- [Notes](ssn)
- [Project](https://github.com/seankhliao/uva-sne-ssn-project)
- [Course](https://www.os3.nl/2019-2020/courses/ssn/start)

### Classical Internet Applications

- [Notes](cia)
- [Course](https://www.os3.nl/2019-2020/courses/cia/start)

## wiki-fuse

mount dokuwiki as a FUSE filesystem,
requires privileged docker (and access to private docker repo)

```
make mount
```
