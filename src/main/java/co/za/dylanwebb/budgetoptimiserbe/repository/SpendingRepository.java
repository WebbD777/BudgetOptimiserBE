package co.za.dylanwebb.budgetoptimiserbe.repository;

import co.za.dylanwebb.budgetoptimiserbe.model.Spending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpendingRepository extends JpaRepository<Spending, Long> {
    List<Spending> findByDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    List<Spending> findAll();
}
