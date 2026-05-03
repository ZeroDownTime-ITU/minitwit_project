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
- How do you monitor your systems and what precisely do you monitor?
- What do you log in your systems and how do you aggregate logs?
- How do you handle availability and scaling in your systems?


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

*Written by Claude*

ITU's rules on the use of generative AI apply for this report too. They are described here and in detail here. Please follow them. For your report that means that you have to state which generative AI tools have been used for which task(s) in your projects. Additionally, describe how generative AI tools have been used and briefly reflect and discuss how they supported or hindered your work process.