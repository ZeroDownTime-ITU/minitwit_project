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
Your final report should be maximum 2500 words long, approx 5-6. So, try to be brief and concise, but be sure to include all necessary information listed below. Note, images do not count as words.

Make sure that you link all artifacts that you consider constitutional to your projects together with short descriptions of the linked artifacts from your reports, i.e., link all necessary repositories, issue trackers, monitoring/logging systems, etc.

Since this is a group project and the report is written by a group make sure to indicate for each section the respective author(s).

Build:  ./build-report.sh
-->
 
# Systems Perspective

*Written by Insert Name*

A description and illustration of the:

- Design and architecture of your ITU-MiniTwit systems.
- All dependencies of your ITU-MiniTwit systems on all levels of abstraction and development stages. That is, list and briefly describe all technologies and tools you applied and depend on.
- Describe the current state of your systems, for example using results of static analysis and quality assessments.

\newpage

# Process Perspective

*Written by Insert Name*

This perspective should clarify how code or other artifacts come from idea into the running system and everything that happens on the way.

In particular, the following descriptions should be included:

- A complete description and illustration of stages and tools included in the CI/CD pipelines, including deployment and release of your systems.
- What do you log in your systems and how do you aggregate logs?
- How do you handle availability and scaling in your systems?
## CI/CD Pipline 
### Github Workflows | CI/CD as code

![Caption text](report/diagrams/CICD_Pipeline.png)

## Monitoring Architecture and Data Flow
Our monitoring setup consists of three components:
Application - Javalin app - produces metrics
Prometheus -collects and stores metrics over time
Grafana - visualizes metrics

### How metrics are stored (labels + time)
Each metric is not just one value.
It is split into multiple counters based on labels, and each of those is tracked over time.

#### Labels (different counters)
When we define: 	.labelNames("method", "path", "status")
we are creating a separate counter for each combination of method, path and status so at one point in time, the application exposes:

minitwit_http_requests_total{method="GET", path="/api/msgs", status="200"} = 10
minitwit_http_requests_total{method="POST", path="/api/msgs", status="200"} = 5
minitwit_http_requests_total{method="GET", path="/api/fllws", status="404"} = 2

Each of these is its own counter. Prometheus calls /metrics repeatedly every 15 seconds and stores the values as such: 

(GET, /api/msgs, 200)
00:00 → 5
00:15 → 7
00:30 → 10

(POST, /api/msgs, 200)
00:00 → 2
00:15 → 3
00:30 → 5

For every label combination, Prometheus stores a timeline of values. Many counters (labels). Each with its own history - hence each unique set of labels creates its own time series that Prometheus tracks over time.

#### How Queries Work: 
Prometheus stores all collected snapshots as a time series [100, 120, 150, ...]
Historical data is created by Prometheus repeatedly sampling the application. 
Functions like: rate(minitwit_http_requests_total[5m]) work by comparing stored values over time: 	(150 - 120) / time 

This allows Prometheus to compute:
- request rate (requests per second)
- trends over time
- error rates etc

#### Role of Grafana: 
Grafana does not store or compute metrics it queries Prometheus then visualizes the returned time series data. The system works because of the following separation:

Application - only knows the current value
Prometheus - builds history by sampling repeatedly

Without Prometheus, there is no history
Without history, there are no rates or trends

###  Dashboard Structure Basic Model 
Building these dashboards has been critical for understanding how metrics are collected, stored, and interpreted in Grafana. Debugging incorrect metrics, understanding why counters reset (application restarts) and understanding how rate-based queries work. 

Grafana is deployed on a separate droplet. This ensures that monitoring remains available even if the application becomes unresponsive, allowing us to detect both errors and silence in the system. We divide our dashboards into two categories. 

#### Business dashboards: 
focus on how the platform is being used, identify trends in user activity and usage patterns, answering questions such as how endpoints are used, where users interact most, and where issues such as errors or timeouts occur. 

#### Operational dashboards: 
monitors system-level metrics such as CPU usage, memory usage, disk usage, and disk I/O.

From an operational perspective, both errors and absence of data are important signals. An error code indicates failure, but a complete lack of incoming data can also indicate that the system is down.

### Logging Architecture and Data Flow
Our logging setup consists of three components:
 Application - Javalin app - produces logs
 Loki - collects and stores logs over time
 Grafana - visualizes logs

#### How logs are stored: 
Logs are not numeric values like metrics but textual events describing what happens in the system. Each log entry represents a specific event such as an error, request, or system message.
The application produces logs using a logger, for example:

    log.error(...)
    log.info(...)

At one point in time, the application may produce logs such as:

    User login failed for user X
    Request to /api/msgs returned 500
    User Y followed user Z

Each of these is an individual log entry.
Loki collects these logs and stores them over time, similar to how Prometheus stores metrics, but without aggregating them into numeric values.

#### How logs are queried: 
Logs are stored as a timeline of events rather than a sequence of numbers.
Instead of computing rates or averages, logs are queried to:
find specific events
trace errors
understand what happened at a given point in time

#### Loki’s Role of Grafana: 
Grafana queries Loki and visualizes logs in a searchable format.
The system works because of the following separation:
Application - produces log messages
Loki - builds history by storing logs over time
Without Loki, there is no log history
Without log history, debugging and tracing issues becomes significantly harder

#### Basic Model summary: 
Application = produces events (log messages)
Loki = stores events over time
Grafana = visualization and search



## Security

### Git Break In {#git-break-in}
Risk level: **High** (Impact: High, Probability: Medium.)  

#### Description
If a team member’s GitHub account is compromised, an attacker can grant themselves admin rights, push malicious code, and approve pull requests.

#### Mitigation & Scenarios
We enforce two-factor authentication and restrict admin privileges through RBAC, including a super-admin role.


### Java Dependencies {#java-dependencies}
Risk level: **High** (Impact: High, Probability: Medium.) 

#### Description
Our system relies heavily on the Javalin framework and third-party libraries for all endpoints and HTTP(S) traffic.

#### Mitigation & Scenarios
We keep all dependencies updated to stable versions and monitor for known vulnerabilities.


### Java Database {#java-database}
Risk level: **Medium** (Impact: Medium, Probability: Medium.)  

#### Description
We use JOOQ ORM and the PostgreSQL JDBC driver to interact with the database, which can introduce SQL-related risks.

#### Mitigation & Scenarios
We avoid raw SQL concatenation and ensure all database-related libraries are kept up to date.


### Digital Ocean {#digital-ocean}
Risk level: **High** (Impact: High, Probability: Medium.)  

#### Description
Deletion of droplets or volumes can lead to downtime and data loss.

#### Mitigation & Scenarios
We perform daily backups and use Terraform to recreate infrastructure if resources are deleted.


### Node Modules (NPM) {#node-modules}
Risk level: **Medium** (Impact: Medium, Probability: Medium.)  

#### Description
Third-party Node dependencies may introduce vulnerabilities or be compromised through supply chain attacks.

#### Mitigation & Scenarios
We audit dependencies (e.g. npm audit), keep packages updated, and review new additions carefully.


### UFW {#ufw}
Risk level: **High** (Impact: High, Probability: Medium.)  

#### Description
If the firewall is misconfigured, unnecessary ports may be exposed. Docker port mappings can bypass firewall rules.

#### Mitigation & Scenarios
We deny incoming traffic by default, allow only required ports, restrict SSH access, and ensure Docker does not bypass UFW.




# Reflection Perspective

*Written by Insert Name*

Describe the biggest issues, how you solved them, and which are major lessons learned with regards to:

- evolution and refactoring
- operation, and
- maintenance

of your ITU-MiniTwit systems. Link back to respective commit messages, issues, tickets, etc. to illustrate these.

Also reflect and describe what was the "DevOps" style of your work. For example, what did you do differently to previous development projects and how did it work?

# Use of Generative AI

*Written by Mathias*
<!--
ITU's guidelines on generative AI apply to this report. For projects like this one, GenAI is allowed as long as we 
(1) state which tools have been used, 
(2) describe how they have been used, and 
(3) respect GDPR and copyright — meaning no personal data and no copyrighted material (textbooks, articles, proprietary source code) should be uploaded to a chatbot without consent. The full guidelines are available on [ITU Student](https://itustudent.itu.dk/Study-Administration/Generative-AI).
-->

## Tools used

Throughout the project we used **Claude Code** (Anthropic), **ChatGPT** (OpenAI), **Gemini** (Google), and **GitHub Copilot** as coding assistants.

## How they were used

We've been using AI as a sparring partner throughout the whole project. It was something we could discuss our ideas with and ask questions like "why does this not work?". We also used it as a coding assistant, and we had a setup on GitHub with Copilot that helped us clean up the code and catch bad patterns or potential bugs before they made it in.

All generated code was read, tested, and adapted before being committed.

## Reflection

We all come from the Software Design programme and took this course in our second semester, so the learning curve was a bit steep. Despite some prior production experience in the group, we leaned on AI tools to explain the system, walk us through the components we had to implement, and act as a coding assistant.

Looking back, the tools clearly accelerated how quickly we could implement and understand things, but the trade-off was that we sometimes skipped the exercises and went straight into implementing on the project. If we did something similar again, we would probably spend more time working through the exercises first and only then move on to the project implementation. Because AI output always looks like it has the answer, we also over-relied on it at times. Towards the end of the project we got better at discussing problems in the group and putting them on the whiteboard rather than turning to an AI first, and that approach really helped us land a good setup for our Docker Swarm migration.