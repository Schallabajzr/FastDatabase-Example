import lombok.Data;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurencyTest {

    private Stream<String> readFile() throws IOException {
        Path path = Paths.get("src/test/resources/test.txt");
        assert Files.exists(path);

        return Files.lines(path);
    }

    @Test
    void testConcurencyparallelProcess() throws IOException {
        Map<String, Integer> result = new HashMap<>();

        List<Event> collect = readFile()
                .map(event -> {
                    String[] split = event.split("\\|");
                    String matchID = split[0];
                    Integer integer = result.putIfAbsent(matchID, 1);
                    if (integer != null) {
                        result.put(matchID, ++integer);
                    } else {
                        integer = 1;
                    }
                    return new Event(Integer.valueOf(split[0]), split[1], integer, Integer.valueOf(split[2]));
                }).collect(Collectors.toList());

        List<Event> collect1 = collect.stream().parallel().map(event -> {
            if ("B".equals(event.getType())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            event.setProcessed(true);
            return event;
        }).collect(Collectors.toList());

        collect1.forEach(event -> {
            assertEquals(event.getExpectedIndex(), event.getActualIndex());
            assertTrue(event.isProcessed());
        });
    }
}

@Data
class Event {
    private int matchId;
    private String type;
    private int actualIndex;
    private int expectedIndex;
    private boolean processed = false;

    public Event(int matchId, String type, int actualIndex, int counter) {
        this.matchId = matchId;
        this.type = type;
        this.actualIndex = actualIndex;
        this.expectedIndex = counter;
    }
}
