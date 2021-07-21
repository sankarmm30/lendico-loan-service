package com.lendico.finance.controller;

import com.lendico.finance.LoanServiceApp;
import com.lendico.finance.model.BorrowerPaymentDto;
import com.lendico.finance.model.GeneratePlanRequestDto;
import com.lendico.finance.model.GeneratePlanResponseDto;
import com.lendico.finance.model.GenericExceptionResponseDto;
import com.lendico.finance.service.LoanService;
import org.decimal4j.util.DoubleRounder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanServiceApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class LoanControllerIntegrationTest {

    private static final String BASE_URL = "http://localhost:";
    private static final String POST_GENERATE_PLAN_PATH = "/generate-plan";

    private static final Double LOAN_AMOUNT = 5000.0;
    private static final Double NOMINAL_RATE = 5.0;
    private static final Integer DURATION = 24;

    @Autowired
    private LoanService loanService;
    @Autowired
    private Environment environment;

    @LocalServerPort
    private Integer port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private static final HttpHeaders HEADERS = new HttpHeaders();
    static {
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    @DisplayName("Post valid loan details for generating plan and verify total principal, interest and payment")
    @Test
    public void testPostGeneratePlanValid() {

        // Given
        HttpEntity<Object> entity = new HttpEntity<>(
                GeneratePlanRequestDto.builder()
                        .loanAmount(LOAN_AMOUNT)
                        .nominalRate(NOMINAL_RATE)
                        .duration(DURATION)
                        .startDate(LocalDateTime.now())
                        .build()
                , HEADERS);

        ResponseEntity<GeneratePlanResponseDto> response = restTemplate.exchange(
                BASE_URL + port + POST_GENERATE_PLAN_PATH, HttpMethod.POST, entity,
                GeneratePlanResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertNotNull(response.getBody().getBorrowerPayments());
        Assert.assertFalse(response.getBody().getBorrowerPayments().isEmpty());
        Assert.assertEquals(24, response.getBody().getBorrowerPayments().size());

        List<BorrowerPaymentDto> borrowerPaymentList = response.getBody().getBorrowerPayments();

        // Verifying the total principal
        double totalPrincipal = DoubleRounder.round(borrowerPaymentList
                .stream()
                .mapToDouble(BorrowerPaymentDto::getPrincipal)
                .sum(), 0);

        Assert.assertEquals(5000.0, totalPrincipal, 0);

        // Verifying the total interest
        double totalInterest = borrowerPaymentList
                .stream()
                .mapToDouble(BorrowerPaymentDto::getInterest)
                .sum();

        Assert.assertEquals(264.56, totalInterest, 0);

        // Verifying the total payment
        double totalPayment = borrowerPaymentList
                .stream()
                .mapToDouble(BorrowerPaymentDto::getBorrowerPaymentAmount)
                .sum();

        Assert.assertEquals(5264.56, totalPayment, 0.08);
    }

    @DisplayName("Post valid loan amount for generating annuity as Zero and verify the bad request response")
    @Test
    public void testAnnuityZeroAndVerifyBadRequestResponse() {

        // Given
        HttpEntity<Object> entity = new HttpEntity<>(
                GeneratePlanRequestDto.builder()
                        .loanAmount(0.1)
                        .nominalRate(NOMINAL_RATE)
                        .duration(DURATION)
                        .startDate(LocalDateTime.now())
                        .build()
                , HEADERS);

        ResponseEntity<GenericExceptionResponseDto> response = restTemplate.exchange(
                BASE_URL + port + POST_GENERATE_PLAN_PATH, HttpMethod.POST, entity,
                GenericExceptionResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals("400", String.valueOf(response.getBody().getStatus()));
        Assert.assertEquals("Bad Request", String.valueOf(response.getBody().getMessage()));
        Assert.assertFalse(response.getBody().getErrors().isEmpty());
        Assert.assertEquals("Annuity calculated as zero. There is no plan available for the given input", response.getBody().getErrors().get(0));
        Assert.assertEquals("/generate-plan", response.getBody().getPath());
    }

    @DisplayName("Post invalid loan amount and verify the bad request response")
    @Test
    public void testPostInvalidLoanAmountAndVerifyBadRequestResponse() {

        // Given
        HttpEntity<Object> entity = new HttpEntity<>(
                GeneratePlanRequestDto.builder()
                        .loanAmount(-1.0)
                        .nominalRate(NOMINAL_RATE)
                        .duration(DURATION)
                        .startDate(LocalDateTime.now())
                        .build()
                , HEADERS);

        ResponseEntity<GenericExceptionResponseDto> response = restTemplate.exchange(
                BASE_URL + port + POST_GENERATE_PLAN_PATH, HttpMethod.POST, entity,
                GenericExceptionResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals("400", String.valueOf(response.getBody().getStatus()));
        Assert.assertEquals("Bad Request", String.valueOf(response.getBody().getMessage()));
        Assert.assertFalse(response.getBody().getErrors().isEmpty());
        Assert.assertEquals("Loan amount should be greater than zero", response.getBody().getErrors().get(0));
        Assert.assertEquals("/generate-plan", response.getBody().getPath());
    }

    @DisplayName("Post request body as null and verify the bad request response")
    @Test
    public void testPostRequestBodyAsNullAndVerifyBadRequestResponse() {

        // Given
        HttpEntity<Object> entity = new HttpEntity<>(
                null
                , HEADERS);

        ResponseEntity<GenericExceptionResponseDto> response = restTemplate.exchange(
                BASE_URL + port + POST_GENERATE_PLAN_PATH, HttpMethod.POST, entity,
                GenericExceptionResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals("400", String.valueOf(response.getBody().getStatus()));
        Assert.assertEquals("Bad Request", String.valueOf(response.getBody().getMessage()));
    }
}
