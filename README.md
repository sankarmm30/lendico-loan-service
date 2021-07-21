## Lendico Loan Service

 - This project has solution for generating the pre-calculated loan repayment plans throughout the lifetime of a loan. This will help in informing the borrowers about the final repayment schedule.
 - This application provides below endpoint for posting the loan details and generating the repayment plan.
    - <b>POST /generate-plan</b>
        - Accepts the loan details in below format,
        ```json
            {
                "loanAmount": 1000,
                "nominalRate": 20,
                "duration": 2,
                "startDate": "2018-01-01T00:00:00Z"
            }
         ```
        - here,
            1. <b>loanAmount</b>: Principal amount
            2. <b>nominalRate</b>: Annual interest rate
            3. <b>duration</b>: Number of installments in months
            4. <b>startDate</b>: Date of Disbursement/Payout

        - Returns repayment plans in below format, <b>Response Status: 200 OK</b>
        ```json
            {
                "borrowerPayments": [
                    {
                        "borrowerPaymentAmount": 512.53,
                        "date": "2018-01-01T00:00:00Z",
                        "initialOutstandingPrincipal": 1000,
                        "interest": 16.67,
                        "principal": 495.87,
                        "remainingOutstandingPrincipal": 504.13
                    },
                    {
                        "borrowerPaymentAmount": 512.53,
                        "date": "2018-02-01T00:00:00Z",
                        "initialOutstandingPrincipal": 504.13,
                        "interest": 8.4,
                        "principal": 504.13,
                        "remainingOutstandingPrincipal": 0
                    }
                ]
            }
        ```     
        - Returns below response in case of bad request, <b>Response Status: 400 Bad Request</b> 
        ```json
            {
                "timestamp": "2021-07-21T17:58:36Z",
                "status": 400,
                "message": "Bad Request",
                "errors": [
                    "Start date should be provided",
                    "Loan amount should be greater than zero"
                ],
                "path": "/generate-plan"
            }
        ```        

#### Setup Local Environment

- *Prerequisites*
    - Java (JDK) 1.8
    - Apache Maven 3.6.3
    - Docker (Version: 2.5.0.1)
    
#### Build

- You can build the project below command,

```bash
mvn clean install

# In case, you want to skip executing the tests,

mvn clean install -DskipTests
```

#### Run as Docker Container

- Build the docker image in local by executing the below command from project folder,

```bash
docker build . --tag=lendico/lendico-loan-service:1.0.0

# Execute the below command when you want to push the image to docker registry

docker push lendico/lendico-loan-service:1.0.0
```

- Verify the docker image by executing `docker image ls`
- Execute command: `docker-compose up -d` which will create docker container with name: `lendico-loan-service`

#### Run in Local (In command line)

- Execute the below command after building the project successfully

```bash
java -jar target/lendico-loan-service-1.0.0.jar
```

#### Test

- Please import the postman collection: [Lendico.postman_collection.json](doc/Lendico.postman_collection.json) and send request to service and verify the response.
- Please use excel loan calculator: [excel-loan-calculator.xlsx](doc/excel-loan-calculator.xlsx) for verifying the repayment plan. Source: [excel-loan-calculator](https://www.mortgagecalculator.org/download/excel-loan.php)