package co.za.dylanwebb.budgetoptimiserbe.repository;

import co.za.dylanwebb.budgetoptimiserbe.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {}
