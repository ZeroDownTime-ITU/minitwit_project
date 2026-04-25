---
title: "DevOps, Software Evolution & Software Maintenance"
course: "KSDSESM1KU"
date: "May 2026"
students:
  - name: "Corbijn Bulsink"
    email: "jbul@itu.dk"
  - name: "Kasper Larsson"
    email: "kasla@itu.dk"
  - name: "Ymir Arnarson"
    email: "ymar@itu.dk"
  - name: "Magnus Bergstedt"
    email: "magnb@itu.dk"
  - name: "Mathias Søgaard"
    email: "msoeg@itu.dk"
---
 
<!--
Outline will be added next week.
The section below is an example showing how a section is structured.
Build:  ./build-report.sh
-->
 
# Systems Perspective

## System design

*Written by Insert Name*

Our MiniTwit application consists of a **web app** and an **API**.
The web app is written in *Svelte* and communicates with a Java/Javalin
backend over HTTP.

Key components:

- Frontend (Svelte SPA)
- Backend (Java + Javalin)
- Database (PostgreSQL)
- Reverse proxy (nginx)

The deployment runs on a 3-node Docker Swarm cluster on DigitalOcean.

## Dependencies

*Written by Insert Name*

We rely on a small set of well-maintained libraries:

| Library  | Purpose                  |
|----------|--------------------------|
| Javalin  | HTTP routing             |
| HikariCP | Connection pooling       |
| Logback  | Logging                  |

\newpage

# Process Perspective

## CI/CD chains

*Written by Insert Name*

Our pipeline triggers on every push to `main`...