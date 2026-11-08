package co.za.dylanwebb.budgetoptimiserbe.service;

import co.za.dylanwebb.budgetoptimiserbe.dto.CategoryDTO;
import co.za.dylanwebb.budgetoptimiserbe.model.Category;
import co.za.dylanwebb.budgetoptimiserbe.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE operation using built-in .save()
    public void createCategory(CategoryDTO categoryDTO) {
        Category category = new Category(
                categoryDTO.getCategory(),
                categoryDTO.getBudget(),
                categoryDTO.getCostType()
        );
        categoryRepository.save(category);
    }

    // READ operation using built-in .findAll()
    public List<Category> fetchAllCategories() {
        return categoryRepository.findAll();
    }
}
