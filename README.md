# UvA SNE

Backup for course data

## Courses

| Block/Course | Notes        | Course                                                   | Other                                                        |
| ------------ | ------------ | -------------------------------------------------------- | ------------------------------------------------------------ |
| **1/SSN**    | [Notes](ssn) | [Course](https://www.os3.nl/2019-2020/courses/ssn/start) | [Project](https://github.com/seankhliao/uva-sne-ssn-project) |
| **1/CIA**    | [Notes](cia) | [Course](https://www.os3.nl/2019-2020/courses/cia/start) |                                                              |

## Dokuwiki Cheatsheet

| Style                      | Code                                | Style                  | Code                         |
| -------------------------- | ----------------------------------- | ---------------------- | ---------------------------- |
| **bold**                   | `**bold**`                          | _italic_               | `//italic//`                 |
| **under**                  | `__under__`                         | ~~strike~~             | `<del>strike</strike>`       |
| `mono`                     | `''mono''`                          | <mark>highlight</mark> | `<hi #fff200>highlight</hi>` |
| <sup>sup</sup>             | `<sup>sup</sup>`                    | <sub>sub</sub>         | `<sup>sup</sup>`             |
| ordered list               | `- ordered`                         | unordered list         | `* unordered`                |
| [link](https://google.com) | `[[https://google.com\|link text]]` | ruler                  | `----`                       |
| > quote                    | `> quote`                           | code block             | `<code>code block</code>`    |

```
<spoiler | name prefix>
hidden behind a button
</spoiler>

^ table   ^ header ^ here ^
^ header  | here   | too  |
| data    | in     | here |
| span                  |||
| or vertical | | |
| :::         | | |

```

## wiki-fuse

mount dokuwiki as a FUSE filesystem,
requires privileged docker (and access to private docker repo)

```
make mount
```
