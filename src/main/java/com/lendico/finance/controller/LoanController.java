package com.lendico.finance.controller;

import com.lendico.finance.model.GeneratePlanRequestDto;
import com.lendico.finance.model.GeneratePlanResponseDto;
import com.lendico.finance.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RestController("loanController")
public class LoanController {

    private LoanService loanService;

    public LoanController(final LoanService loanService) {

        this.loanService = loanService;
    }

    @PostMapping(value = "/generate-plan", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeneratePlanResponseDto> generatePlan(
            final @Valid @RequestBody GeneratePlanRequestDto generatePlanRequestDto) {

        return new ResponseEntity<>(this.loanService.generatePlan(generatePlanRequestDto), HttpStatus.OK);
    }
}
