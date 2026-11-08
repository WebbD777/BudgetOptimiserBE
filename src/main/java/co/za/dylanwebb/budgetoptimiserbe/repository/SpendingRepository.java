package co.za.dylanwebb.budgetoptimiserbe.repository;

import co.za.dylanwebb.budgetoptimiserbe.model.Spending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpendingRepository extends JpaRepository<Spending, Long> { }
