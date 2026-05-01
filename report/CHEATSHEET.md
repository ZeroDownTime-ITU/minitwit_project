# Markdown Cheat Sheet

A quick reference for writing in `report.md`. Open the Markdown preview in
VS Code with `Cmd+Shift+V` (or `Cmd+K V` to pin it side-by-side) to see
your changes live as you type.

## Headings (auto-numbered by pandoc)

```markdown
# Top-level section          → "1 Section"
## Subsection                → "1.1 Subsection"
### Sub-subsection           → "1.1.1 Sub-subsection"
#### Deeper                  → "1.1.1.1 Deeper"
```

Don't write the numbers yourself — `--number-sections` does it for you.

## Paragraphs and line breaks

A blank line separates paragraphs. That's it.

```markdown
This is one paragraph. Linebreaks
in the source don't matter.

This is a new paragraph because of the blank line above.
```

## Page breaks

Most of the time you don't need them — pandoc/LaTeX flows text across
pages automatically. Only use this if you explicitly want a fresh page:

```markdown
\newpage
```

## Bold and italic

```markdown
**bold**
*italic*
***bold and italic***
```

## Lists

```markdown
- Bullet point
- Another point
  - Nested point (2-space indent)
  - Another nested point

1. Numbered item
2. Next item
3. Next item
```

## Code

Inline code uses backticks: `` `like this` ``

Code blocks with syntax highlighting:

````markdown
```bash
docker compose up -d
```

```java
public class UserRepository {
    // ...
}
```

```yaml
services:
  api:
    image: minitwit-api
```
````

## Links

```markdown
[Link text](https://pandoc.org/MANUAL.html)
```

## Images

Place the file in `report/images/` and reference it:

```markdown
![Caption text](images/architecture.png)
```

## Tables

```markdown
| Component | Technology    |
|-----------|---------------|
| Frontend  | Svelte        |
| Backend   | Java/Javalin  |
| Database  | PostgreSQL    |
```

Alignment is controlled by colons in the separator row:

```markdown
| Left | Center | Right |
|:-----|:------:|------:|
| a    |   b    |     c |
```

## Quotes

```markdown
> A quoted block — useful for citing sources
> or highlighting a key idea.
```

## Horizontal rule (visual divider)

```markdown
---
```

## Comments (won't appear in PDF)

```markdown
<!-- This text is invisible in the rendered output. -->
```

Useful for TODOs, notes to teammates, or reminders.

## "Written by" convention

Each section we write should have a byline directly under the heading:

```markdown
## System design

*Written by Mathias Søgaard*

The actual content of the section starts here...
```

## A complete example

```markdown
# Systems Perspective

## System design

*Written by Mathias Søgaard*

Our MiniTwit application consists of a **web app** and an **API**.
The web app is written in *Svelte* and communicates with a Java/Javalin
backend over HTTP.

Key components:

- Frontend (Svelte SPA)
- Backend (Java + Javalin)
- Database (PostgreSQL)
- Reverse proxy (nginx)

The deployment runs on a 3-node Docker Swarm cluster on DigitalOcean.

![Architecture overview](images/architecture.png)

## Dependencies

*Written by Corbijn*

We rely on a small set of well-maintained libraries:

| Library  | Purpose            |
|----------|--------------------|
| Javalin  | HTTP routing       |
| HikariCP | Connection pooling |
| Logback  | Logging            |
```

## Tips

- **Use the live preview** — `Cmd+Shift+V` in VS Code shows you the
  rendered output as you type. No need to build the PDF for every change.
- **Keep source lines short** — wrap around 80 characters. It makes Git
  diffs much cleaner when reviewing PRs.
- **Don't write section numbers manually** — pandoc adds them.
- **Don't worry about page breaks** — pandoc handles flow automatically.
- **Build the PDF occasionally** — to sanity-check formatting, but day-to-day
  the live preview is enough.