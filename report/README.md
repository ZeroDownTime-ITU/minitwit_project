# Report
 
This folder contains the project report for the DevOps, Software Evolution
and Software Maintenance course (KSDSESM1KU).
 
## Structure
 
```
report/
├── report.md          ← The actual report. Edit this.
├── template.tex       ← LaTeX template (defines the title page layout)
├── build-report.sh    ← Builds report.pdf from report.md
├── CHEATSHEET.md      ← Markdown syntax reference
├── images/            ← Diagrams, screenshots, charts
└── README.md          ← This file
```
 
## Writing
 
Edit `report.md` directly in VS Code. Open the Markdown preview side-by-side
with `Cmd+Shift+V` (or `Cmd+K V` to keep it pinned) to see the rendered
output as you type.
 
Each section in `report.md` has a placeholder `*Written by [name]*` line —
fill in your name when you take ownership of a section.
 
See `CHEATSHEET.md` for a quick Markdown syntax reference.
 
## Workflow
 
We treat the report like code:
 
1. Branch off `main`: `git checkout -b feature/report-<your-section>`
2. Edit your section in `report.md`
3. Commit and push
4. Open a PR — at least one teammate reviews
5. Merge to `main` when approved
If two people are editing different sections, no conflicts. If two people
edit the same section, resolve like any other Git merge conflict.
 
## Building the PDF
 
```bash
cd report
./build-report.sh
```
 
This produces `report.pdf` in the same folder.
 
`report.pdf` is **not** committed to Git — it is a build artifact, like
binaries. It is added to `.gitignore`.
 
## Requirements
 
You only need to build the PDF locally if you want to preview the final
formatting. Day-to-day writing only needs the Markdown preview in VS Code
(`Cmd+Shift+V`) — you can skip all of the LaTeX setup if you only plan
to write content.
 
To build the PDF you need pandoc and a LaTeX distribution.
 
### macOS
 
```bash
brew install pandoc
brew install --cask basictex
```
 
After installing BasicTeX, add the LaTeX binaries to your PATH so `xelatex`
can be found:
 
```bash
echo 'export PATH="/Library/TeX/texbin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```
 
Verify:
 
```bash
which xelatex   # should print /Library/TeX/texbin/xelatex
```
 
Install the LaTeX packages required by our template (BasicTeX is minimal
and ships with very few packages):
 
```bash
sudo tlmgr update --self
sudo tlmgr install collection-fontsrecommended \
                   csquotes microtype enumitem fvextra framed bookmark \
                   parskip setspace multirow titling upquote \
                   xcolor booktabs tabularx titlesec fancyhdr mdwtools
```
 
### Ubuntu/Linux (e.g. inside the UTM VM)
 
```bash
sudo apt install pandoc \
                 texlive-xetex \
                 texlive-fonts-recommended \
                 texlive-latex-extra \
                 texlive-luatex
```
 
`texlive-latex-extra` covers most of what BasicTeX needs `tlmgr` for on
macOS (csquotes, microtype, enumitem, fvextra, etc.).
 
### Windows
 
1. Download and install pandoc from https://pandoc.org/installing.html
2. Download and install MiKTeX from https://miktex.org/download
3. During MiKTeX install, set "Install missing packages on the fly" to *Yes*
To run `build-report.sh` on Windows, use **Git Bash** (comes with Git for
Windows) or **WSL**. PowerShell will not run the script directly.
 
```bash
cd report
./build-report.sh
```
 
MiKTeX will automatically download missing LaTeX packages on the first
build — just click "Install" if a popup appears.
 
### Troubleshooting
 
If `./build-report.sh` fails with a missing LaTeX package error like
`! LaTeX Error: File 'xxx.sty' not found`, install it with:
 
```bash
sudo tlmgr install <package-name-from-error>
```
 
If you get `xelatex: createProcess: find_executable: failed`, your PATH
does not include the LaTeX binaries — see the macOS PATH step above.
 
If you get a `\@parboxrestore has changed` warning, ignore it — it's
harmless and the PDF still builds correctly.
 
## Markdown crash course
 
For a full syntax reference, see `CHEATSHEET.md`. Here's the bare minimum:
 
| What you want         | How to write it                          |
|-----------------------|------------------------------------------|
| Heading level 1       | `# Heading`                              |
| Heading level 2       | `## Heading`                             |
| Bold                  | `**bold**`                               |
| Italic                | `*italic*`                               |
| Inline code           | `` `code` ``                             |
| Code block            | ` ```language ... ``` `                  |
| Bullet list           | `- item`                                 |
| Numbered list         | `1. item`                                |
| Link                  | `[text](https://url)`                    |
| Image                 | `![alt text](images/file.png)`           |
| Table                 | See `CHEATSHEET.md` for table syntax.    |
| Page break (PDF)      | `\newpage` on its own line               |