# UvA SNE

Backup for course data

## Courses

| Block/Course | Notes           | Course            | Other               |
| ------------ | --------------- | ----------------- | ------------------- |
| **3/RP1**    |                 |                   | [Project][project3] |
| **2/LS**     | [Notes](LS.md)  | [Course][course4] | [Project][project2] |
| **2/INR**    | [Notes](INR.md) | [Course][course3] |                     |
| **1/SSN**    | [Notes](SSN.md) | [Course][course2] | [Project][project1] |
| **1/CIA**    | [Notes](CIA.md) | [Course][course1] |                     |

[course4]: https://www.os3.nl/2019-2020/courses/ls/start
[course3]: https://www.os3.nl/2019-2020/courses/inr/start
[course2]: https://www.os3.nl/2019-2020/courses/ssn/start
[course1]: https://www.os3.nl/2019-2020/courses/cia/start
[project3]: https://github.com/seankhliao/uva-rp1
[project2]: https://github.com/seankhliao/uva-ls
[project1]: https://github.com/seankhliao/uva-sne-ssn-project

## markdown to dokuwiki

```
GO111MODULE=off go get -u github.com/seankhliao/blackfriday-doku/cmd/md2doku

md2doku file.md | wl-copy
```

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
