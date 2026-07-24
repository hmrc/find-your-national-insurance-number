
# find-your-national-insurance-number
=====================================

Allows the Find My NINO journey to trigger NPS letter handling and retrieve individual details by NINO.

Requirements
------------

This service is written in [Scala 3.x](http://www.scala-lang.org/) and [Play 3.x](http://playframework.com/), so needs at least a [JRE 21](http://www.oracle.com/technetwork/java/javase/downloads/index.html) to run.

API
---

| *Task* | *Supported Methods* | *Description* | Status |
|--------|----------------------|---------------|--------|
| `/find-your-national-insurance-number/nps-json-service/nps/itmp/find-my-nino/api/v1/individual/:nino` | POST | Sends a Find My NINO request to the NPS FMN API for the provided NINO. | Live |
| `/find-your-national-insurance-number/individuals/details/NINO/:nino/:resolveMerge` | GET | Retrieves individual details for the provided NINO from the configured individual-details downstream service. | Live |

Configuration
-------------

All downstream services require host and port settings, for example:

| *Key* | *Description* |
|-------|---------------|
| `microservice.services.auth.host` | Host of the Auth service |
| `microservice.services.auth.port` | Port of the Auth service |
| `microservice.services.nps-fmn-api.protocol` | Protocol for the NPS FMN API service |
| `microservice.services.nps-fmn-api.host` | Host of the NPS FMN API service |
| `microservice.services.nps-fmn-api.port` | Port of the NPS FMN API service |
| `microservice.services.nps-fmn-api.token` | Authorization token for the NPS FMN API service |
| `microservice.services.nps-fmn-api.correlationId.key` | Header name used for correlation ID |
| `microservice.services.nps-fmn-api.govUkOriginatorId.key` | Header name used for gov-uk-originator-id |
| `microservice.services.nps-fmn-api.govUkOriginatorId.value` | Header value used for gov-uk-originator-id |
| `microservice.services.individual-details.auth-token` | Auth token used for individual-details DES-style headers |
| `microservice.services.individual-details.environment` | Environment header value for individual-details requests |
| `microservice.services.individual-details.originator-id` | OriginatorId header value for individual-details requests |
| `external-url.individual-details.protocol` | Protocol for the individual-details service URL |
| `external-url.individual-details.host` | Host of the individual-details service URL |
| `external-url.individual-details.port` | Port of the individual-details service URL |

For local development:
- run dependencies via service manager: `sm2 --start FIND_YOUR_NATIONAL_INSURANCE_NUMBER SCA_NINO_STUBS`
- default local port is `14022`
- dummy local values are provided for downstream tokens and headers and should be replaced for non-local environments

How to test the project
=======================

Unit Tests
----------
- **Unit test the entire test suite:** `sbt test`
- **Unit test a single spec file:** `sbt "testOnly *fileName"` (for example: `sbt "testOnly *NPSFMNControllerSpec"`)

Integration tests
-----------------
- **Run integration tests:** `sbt it/test`

Acronyms
--------

In the context of this service we use the following acronyms:

* NINO: National Insurance Number
* NPS: National Insurance and PAYE Service
* API: Application Programming Interface
* JRE: Java Runtime Environment
* JSON: JavaScript Object Notation
* URL: Uniform Resource Locator

License
-------

This code is open source software licensed under the Apache 2.0 License.