import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class JsonSpaceLoader<T> implements SpaceLoader<T> {
    private String filePath;
    private String spaceName;
    private Function<String, T> parser;

    public JsonSpaceLoader(String filePath, String spaceName, Function<String, T> parser) {
        this.filePath = filePath;
        this.spaceName = spaceName;
        this.parser = parser;
    }

    @Override
    public SpaceComponent<T> load() throws Exception {
        Map<T, double[]> data = new HashMap<>();
        File file = new File(filePath);

        if (!file.exists()) throw new Exception("שגיאה: הקובץ לא נמצא!");

        try (Scanner scanner = new Scanner(file)) {
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) sb.append(scanner.nextLine());

            String[] blocks = sb.toString().trim().split("\\{\"word\"");

            for (String block : blocks) {
                if (!block.contains("\"vector\"")) continue;
                try {
                    int colonIndex = block.indexOf(":");
                    int firstQuote = block.indexOf("\"", colonIndex);
                    int secondQuote = block.indexOf("\"", firstQuote + 1);
                    String rawString = block.substring(firstQuote + 1, secondQuote);

                    int bracketStart = block.indexOf("[", secondQuote);
                    int bracketEnd = block.indexOf("]", bracketStart);
                    String[] nums = block.substring(bracketStart + 1, bracketEnd).trim().split(",");
                    double[] vec = new double[nums.length];
                    for (int i = 0; i < nums.length; i++) vec[i] = Double.parseDouble(nums[i].trim());

                    T realObject = parser.apply(rawString);

                    data.put(realObject, vec);
                } catch (Exception e) { continue; }
            }
        }
        return new SingleSpace<>(spaceName, data);
    }
}