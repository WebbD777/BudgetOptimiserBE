package co.za.dylanwebb.budgetoptimiserbe.controller;

import co.za.dylanwebb.budgetoptimiserbe.dto.SpendingDTO;
import co.za.dylanwebb.budgetoptimiserbe.model.Spending;
import co.za.dylanwebb.budgetoptimiserbe.service.SpendingService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spending")
public class SpendingController {

  private final SpendingService spendingService;

  public SpendingController(SpendingService spendingService) {
    this.spendingService = spendingService;
  }

  @PostMapping
  public ResponseEntity<String> createSpending(@RequestBody SpendingDTO request) {
    spendingService.createSpending(request);

    String confirmationMessage = "Spending of $" + request.getAmount() + " successfully recorded.";
    return new ResponseEntity<>(confirmationMessage, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Spending>> getAllSpending() {
    List<Spending> spending = spendingService.fetchAllSpending();
    return new ResponseEntity<>(spending, HttpStatus.OK);
  }
}
