package co.za.dylanwebb.budgetoptimiserbe.service;

import co.za.dylanwebb.budgetoptimiserbe.dto.MonteCarloDTO;
import co.za.dylanwebb.budgetoptimiserbe.model.Category;
import co.za.dylanwebb.budgetoptimiserbe.model.Spending;
import co.za.dylanwebb.budgetoptimiserbe.repository.CategoryRepository;
import co.za.dylanwebb.budgetoptimiserbe.repository.SpendingRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.time.YearMonth;
import java.util.*;

@Service
public class MathsService {
    private String pythonExecutable = "python3";
    private String scriptPath = "src/main/java/co/za/dylanwebb/budgetoptimiserbe/scripts/MC.py";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CategoryRepository categoryRepository;
    private final SpendingRepository spendingRepository;

    public MathsService(CategoryRepository categoryRepository, SpendingRepository spendingRepository) {
        this.categoryRepository = categoryRepository;
        this.spendingRepository = spendingRepository;
    }

    public List<String> runMCSimulation() throws IOException, InterruptedException {

        // getting the budgets
        List<BudgetRecord> budgetRecordList = new ArrayList<>();
        Map<String, Double> budgetData = new LinkedHashMap<>();

        CategoryService categoryService = new CategoryService(categoryRepository);
        var categories = categoryService.fetchAllCategories();

        for (Category row : categories) {
            budgetData.put(row.getCategoryName(), (double) row.getBudget());
            budgetRecordList.add(new BudgetRecord(row.getCategoryName(), row.getBudget(), row.getId()));
        }

        // getting the spendings

        SpendingService spendingService = new SpendingService(spendingRepository, categoryRepository);
        var spendings = spendingService.fetchSpendingByMonthRange(2026, 11, 2026, 11);

        // 3. Initialize historical data
        Map<String, Map<String, Double>> historicalData =
                new LinkedHashMap<>();

        String month = "2026-11";

        Map<String, Double> monthlyData = new LinkedHashMap<>();

        // Initialize every category to zero
        for (String category : budgetData.keySet()) {
            monthlyData.put(category, 0.0);
        }

        historicalData.put(month, monthlyData);

        // 4. Aggregate spending by category
        for (Spending spending : spendings) {

            if (spending.getDate() == null ||
                    spending.getCategory() == null) {
                continue;
            }

            String categoryName =
                    spending.getCategory().getCategoryName();

            if (!monthlyData.containsKey(categoryName)) {
                throw new IllegalArgumentException(
                        "No budget found for category: " + categoryName
                );
            }

            monthlyData.merge(
                    categoryName,
                    (double) spending.getAmount(),
                    Double::sum
            );
        }

        // 5. Package data into DTO
        MonteCarloDTO request =
                new MonteCarloDTO(historicalData, budgetData);

        // 6. Convert data to JSON
        ObjectMapper objectMapper = new ObjectMapper();

        String jsonInput = objectMapper.writeValueAsString(request);

        // 7. Start Python process
        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonExecutable,
                scriptPath
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // 8. Send JSON to Python
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream()))) {

            writer.write(jsonInput);
            writer.newLine();
        }

        // 9. Read Python output
        StringBuilder pythonOutput = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                pythonOutput.append(line).append("\n");
            }
        }

        // 10. Check Python execution
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException(
                    "Python execution failed: " + pythonOutput
            );
        }

        return Collections.singletonList(
                pythonOutput.toString().trim()
        );
    }
    }

//@Service
//public class DataPipelineService {
//
//    @Value("${python.executable}")
//    private String pythonExecutable;
//
//    @Value("${python.script.path}")
//    private String scriptPath;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    // Assuming you have an Entity or DTO called ExpenseRecord
//    public String sendFormattedDataToPython(List<ExpenseRecord> databaseRows) throws IOException, InterruptedException {
//
//        // 1. Structure the data into the exact format requested
//        Map<String, List<Double>> rawData = new HashMap<>();
//        rawData.put("Electricity", new ArrayList<>());
//        rawData.put("Fuel", new ArrayList<>());
//        rawData.put("Food", new ArrayList<>());
//        rawData.put("Household", new ArrayList<>());
//        rawData.put("Data", new ArrayList<>());
//
//        // 2. Populate the lists from your database rows
//        for (ExpenseRecord row : databaseRows) {
//            rawData.get("Electricity").add(row.getElectricity());
//            rawData.get("Fuel").add(row.getFuel());
//            rawData.get("Food").add(row.getFood());
//            rawData.get("Household").add(row.getHousehold());
//            rawData.get("Data").add(row.getDataExpense());
//        }
//
//        // 3. Set up the Python process
//        List<String> command = List.of(pythonExecutable, scriptPath);
//        ProcessBuilder processBuilder = new ProcessBuilder(command);
//        processBuilder.redirectErrorStream(true);
//
//        Process process = processBuilder.start();
//
//        // 4. Pipe the formatted JSON to Python's STDIN
//        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
//            objectMapper.writeValue(writer, rawData);
//            writer.flush();
//        }
//
//        // 5. Capture the response back from Python
//        StringBuilder pythonOutput = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                pythonOutput.append(line).append("\n");
//            }
//        }
//
//        int exitCode = process.waitFor();
//        if (exitCode != 0) {
//            throw new RuntimeException("Python execution failed. Log: " + pythonOutput);
//        }
//
//        return pythonOutput.toString().trim();
//    }
//}

