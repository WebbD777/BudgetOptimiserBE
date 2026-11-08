package co.za.dylanwebb.budgetoptimiserbe.service;

import co.za.dylanwebb.budgetoptimiserbe.dto.SpendingDTO;
import co.za.dylanwebb.budgetoptimiserbe.model.Category;
import co.za.dylanwebb.budgetoptimiserbe.model.Spending;
import co.za.dylanwebb.budgetoptimiserbe.repository.CategoryRepository;
import co.za.dylanwebb.budgetoptimiserbe.repository.SpendingRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SpendingService {

  private final SpendingRepository spendingRepository;
  private final CategoryRepository categoryRepository;

  public SpendingService(
      SpendingRepository spendingRepository, CategoryRepository categoryRepository) {
    this.spendingRepository = spendingRepository;
    this.categoryRepository = categoryRepository;
  }

  public void createSpending(SpendingDTO spendingDTO) {
    Category category =
        categoryRepository
            .findById(spendingDTO.getCategoryId())
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Category not found with ID: " + spendingDTO.getCategoryId()));

    // 2. Map DTO to Spending Entity, injecting the actual Category object
    Spending spending = new Spending(spendingDTO.getDateTime(), spendingDTO.getAmount(), category);

    // 3. Save to database
    spendingRepository.save(spending);
  }

  public List<Spending> fetchAllSpending() {
    return spendingRepository.findAll();
  }
}
