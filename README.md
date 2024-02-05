
# find-your-national-insurance-number

This is the Find your National Insurance number API for the FMN service that allows users to find their National Insurance Number.

## How to run locally
To start the service API locally:
1. Ensure MongoDB is running
2. Run `sm2 -start FIND_YOUR_NATIONAL_INSURANCE_NUMBER SCA_NINO_STUBS`

## Endpoints
`POST    /nps-json-service/nps/itmp/find-my-nino/api/v1/individual/:nino`

`GET     /individuals/details/NINO/:nino/:resolveMerge`