# SkyBook AI — Pinned intentionally vulnerable dependency versions
#
# TRAINING ONLY. Exact pins for SCA detection. Do not use version ranges.
# Source of truth mirrors:
#   - backend/pom.xml (skybook.vuln.* properties + default-classpath SCA pins)
#   - backend/training-sca-artifacts/pom.xml
#   - ai-service/requirements-training.txt

## Java (Maven) — pinned

| Category | Coordinates | Pinned version | Example CVE / note |
|----------|-------------|----------------|--------------------|
| Logging | `org.apache.logging.log4j:log4j-core` | **2.14.1** | CVE-2021-44228 |
| JSON | `com.fasterxml.jackson.core:jackson-databind` | **2.9.10** | Multiple CVEs |
| File upload | `commons-fileupload:commons-fileupload` | **1.3.1** | CVE-2016-1000031 |
| HTTP client | `org.apache.httpcomponents:httpclient` | **4.3.6** | Outdated client |
| Serialization | `commons-collections:commons-collections` | **3.2.1** | Deserialization gadgets |

Declared in `backend/pom.xml` on the default classpath (log4j-core, commons-fileupload, httpclient, commons-collections). `jackson-databind` **2.9.10** remains `provided` so Boot’s Jackson is not overridden at runtime.

## Python (pip) — pinned (`==` only)

| Category | Package | Pinned version |
|----------|---------|----------------|
| YAML | `PyYAML` | **5.1** |
| HTTP | `urllib3` | **1.24.2** |
| HTTP | `requests` | **2.19.0** |
| Imaging / files | `Pillow` | **8.3.0** |
| Crypto | `pycrypto` | **2.6.1** |
| JWT | `pyjwt` | **1.7.1** |

File: `ai-service/requirements-training.txt`

## Verify pins

```bash
# Java — should list the five vulnerable artifacts at exact versions
cd backend && mvn -s /tmp/skybook-maven-settings.xml dependency:tree -Dincludes='org.apache.logging.log4j:log4j-core,com.fasterxml.jackson.core:jackson-databind,commons-fileupload:commons-fileupload,org.apache.httpcomponents:httpclient,commons-collections:commons-collections'

# SCA-only module
cd backend/training-sca-artifacts && mvn -q dependency:tree

# Python pins
grep -E '^[A-Za-z].*==' ai-service/requirements-training.txt
```
