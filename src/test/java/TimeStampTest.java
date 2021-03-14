import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
class TimeStampTest {
    @BeforeAll
    private static void initLog() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(Level.DEBUG);
        ctx.updateLoggers();
    }

    private Stream<String> readFile() throws IOException {
        Path path = Paths.get("src/test/resources/test.txt");
        assert Files.exists(path);

        return Files.lines(path);
    }

    @Test
    void testConcurencyparallelProcess() throws IOException {

        List<EventTimestamp> expected = readFile().map(this::toEvent).collect(Collectors.toList());
        List<EventTimestamp> actual = new ArrayList<>();
        readFile().parallel().forEachOrdered(s -> actual.add(toEvent(s)));

        assertEquals(expected, actual);
        for (int i = 1; i < actual.size(); i++) {
            assertTrue(actual.get(i).getTimestamp().isAfter(actual.get(i - 1).getTimestamp()));
        }
    }

    private void print(String event) {
        String[] split = event.split("\\|");
        log.info(String.format("Match ID: %s Event type: %s Event of such type:%s Timestamp %s", split[0], split[1], split[2], LocalDateTime.now().toString()));
    }

    @SneakyThrows
    private EventTimestamp toEvent(String event) {
        Thread.sleep(100);
        String[] split = event.split("\\|");
        return new EventTimestamp(Integer.valueOf(split[0]), split[1], Integer.valueOf(split[2]));
    }
}

@Data
class EventTimestamp {
    private int matchId;
    private String type;
    private int eventOfSuchType;
    private LocalDateTime timestamp;

    public EventTimestamp(int matchId, String type, int eventOfSuchType) {
        this.matchId = matchId;
        this.type = type;
        this.eventOfSuchType = eventOfSuchType;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventTimestamp)) return false;
        EventTimestamp that = (EventTimestamp) o;
        return getMatchId() == that.getMatchId() && getEventOfSuchType() == that.getEventOfSuchType() && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMatchId(), getType(), getEventOfSuchType());
    }
}
