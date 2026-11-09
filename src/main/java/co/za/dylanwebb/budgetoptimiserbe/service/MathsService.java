package co.za.dylanwebb.budgetoptimiserbe.service;

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
    public List<String> runMCSimulation() throws IOException, InterruptedException {

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
