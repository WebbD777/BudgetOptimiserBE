package co.za.dylanwebb.budgetoptimiserbe.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "categories")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private float budget;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CostType costType;

  public enum CostType {
    FIXED,
    VARIABLE
  }

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Spending> spendings = new ArrayList<>();

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  public Category(String categoryName, float budget, CostType costType) {
    this.name = categoryName;
    this.budget = budget;
    this.costType = costType;
  }
  public Category(){}
}
