package com.lendico.finance.controller;

import com.lendico.finance.model.GeneratePlanRequestDto;
import com.lendico.finance.model.GeneratePlanResponseDto;
import com.lendico.finance.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class LoanController {

    private LoanService loanService;

    public LoanController(final LoanService loanService) {

        this.loanService = loanService;
    }

    @PostMapping(value = "/generate-plan", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeneratePlanResponseDto> recordEvent(
            final @Valid @NotBlank(message = "Keyword cannot be null or empty") @RequestBody GeneratePlanRequestDto generatePlanRequestDto) {

        return new ResponseEntity<>(this.loanService.generatePlan(generatePlanRequestDto), HttpStatus.OK);
    }
}
