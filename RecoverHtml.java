import java.nio.file.*;
import java.util.*;

public class RecoverHtml {
    public static void main(String[] args) throws Exception {
        String logPath = "C:\\Users\\haduc\\.gemini\\antigravity\\brain\\cd674b50-a428-4447-8385-5fcb15413f66\\.system_generated\\logs\\transcript.jsonl";
        List<String> lines = Files.readAllLines(Paths.get(logPath));
        for (String line : lines) {
            if (line.contains("\"write_to_file\"") && line.contains("\"index.html\"")) {
                int start = line.indexOf("\"CodeContent\":\"") + 15;
                if (start > 14) {
                    int end = line.lastIndexOf("\",\"Description\"");
                    if (end > start) {
                        String content = line.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
                        Files.write(Paths.get("recovered_index.html"), content.getBytes());
                        System.out.println("Recovered!");
                    }
                }
            }
        }
    }
}
