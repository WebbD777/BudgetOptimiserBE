package co.za.dylanwebb.budgetoptimiserbe.controller;

import co.za.dylanwebb.budgetoptimiserbe.dto.CategoryDTO;
import co.za.dylanwebb.budgetoptimiserbe.model.Category;
import co.za.dylanwebb.budgetoptimiserbe.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody CategoryDTO request) {
        categoryService.createCategory(request);
        String savedMessage = "Category '" + request.getCategory() + "' created with budget $" + request.getBudget();
        return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.fetchAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
