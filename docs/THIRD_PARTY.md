# Third-party components

Original application code in src/ calls external libraries through their public APIs. The source ZIP does not bundle Smile or other dependency JARs. Maven downloads dependencies from their declared repositories.

| Component | Role | Upstream |
| --- | --- | --- |
| Smile 3.1.1 | Random Forest and SVR implementation; upstream source is GPL-3.0-or-later | https://github.com/haifengl/smile/tree/v3.1.1 |
| Apache Commons CSV 1.14.1 | CSV parsing/writing; Apache-2.0 | https://commons.apache.org/proper/commons-csv/ |
| SLF4J Simple 2.0.12 | Runtime logging; MIT | https://www.slf4j.org/ |
| JUnit Jupiter 5.11.4 | Automated tests; EPL-2.0 | https://junit.org/junit5/ |
| Maven Wrapper scripts | Build bootstrap; Apache-2.0 | https://maven.apache.org/wrapper/ |

The supplied Maven wrapper scripts retain their upstream copyright/license headers. The Apache license text is included as APACHE_LICENSE.txt.

An executable shaded JAR produced by Maven package includes runtime libraries. Their upstream distribution terms apply to those components. This document does not assign a new licence to the project's original application code.
