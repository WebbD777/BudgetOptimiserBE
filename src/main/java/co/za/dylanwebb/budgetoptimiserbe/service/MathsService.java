package co.za.dylanwebb.budgetoptimiserbe.service;

import co.za.dylanwebb.budgetoptimiserbe.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Collections;
import java.util.List;

@Service
public class MathsService {
    private String pythonExecutable = "python3";
    private String scriptPath = "src/main/java/co/za/dylanwebb/budgetoptimiserbe/scripts/MC.py";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CategoryRepository categoryRepository;

    public MathsService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<String> runMCSimulation() throws IOException, InterruptedException {
        CategoryService categoryService = new CategoryService(categoryRepository);
        var categories = categoryService.fetchAllCategories();


        List<String> command = List.of(pythonExecutable, scriptPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        StringBuilder pythonOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                pythonOutput.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python execution failed. Log: " + pythonOutput);
        }

        return Collections.singletonList(pythonOutput.toString().trim());
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

