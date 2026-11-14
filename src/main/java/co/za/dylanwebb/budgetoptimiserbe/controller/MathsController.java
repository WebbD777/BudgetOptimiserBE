package co.za.dylanwebb.budgetoptimiserbe.controller;

import co.za.dylanwebb.budgetoptimiserbe.model.Category;

import java.io.IOException;
import java.util.List;

import co.za.dylanwebb.budgetoptimiserbe.service.MathsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maths")
public class MathsController {

    private final MathsService mathsService;

    public MathsController(MathsService mathsService) {
        this.mathsService = mathsService;
    }

    @GetMapping("/monte-carlo")
    public ResponseEntity<List<String>> getMonteCarloSim(
            @RequestParam int startYear,
            @RequestParam int startMonth,
            @RequestParam int endYear,
            @RequestParam int endMonth
    ) throws IOException, InterruptedException {
        List<String> monteCarlo = mathsService.runMCSimulation(
                startYear, startMonth, endYear, endMonth
        );
        return new ResponseEntity<List<String>>(monteCarlo, HttpStatus.OK);
    }
}