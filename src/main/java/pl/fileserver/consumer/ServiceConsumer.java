package pl.fileserver.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import pl.fileserver.util.BigFilesManager;
import reactor.function.Consumer;
import reactor.net.NetChannel;

public class ServiceConsumer implements Consumer<String> {
    private static final String ERR_RESULT = "ERR\r";
    private static final String OK_RESULT = "OK\r";

    private final BigFilesManager bigFilesManager;
    private final NetChannel<String, String> conn;
    private CountDownLatch closeLatch;

    private Map<String, AbstractServiceStrategy> command2StrategyMap = new HashMap<>();
    private final AbstractServiceStrategy unknownCommandStrategy = new UnknownCommandStrategy();

    public ServiceConsumer(BigFilesManager bigFilesManager, NetChannel<String, String> conn, CountDownLatch closeLatch) {
        this.bigFilesManager = bigFilesManager;
        this.conn = conn;
        this.closeLatch = closeLatch;

        command2StrategyMap.put("GET", new GetServiceStrategy());
        command2StrategyMap.put("QUIT", new QuitServiceStrategy());
        command2StrategyMap.put("SHUTDOWN", new ShutdownServiceStrategy());
    }

    @Override
    public void accept(String line) {
        getCommand(line).ifPresent(command -> command2StrategyMap.getOrDefault(command, unknownCommandStrategy).process(line));
    }

    private Optional<String> getCommand(String line) {
        final String[] splitLine = StringUtils.split(line);

        return (splitLine.length > 0) ? Optional.ofNullable(splitLine[0]) : Optional.empty();
    }

    private static interface AbstractServiceStrategy {
        void process(String line);
    }

    private class GetServiceStrategy implements AbstractServiceStrategy {
        @Override
        public void process(String line) {
            getLineNumber(line).ifPresent(lineNumber -> {
                final String resultLine = bigFilesManager.getLine(lineNumber - 1);
                if (resultLine == null) {
                    conn.send(ERR_RESULT);
                } else {
                    conn.send(OK_RESULT);
                    conn.send(resultLine);
                }
            });
        }

        private Optional<Integer> getLineNumber(String line) {
            final String[] splitLine = StringUtils.split(line);
            if (splitLine.length != 2) {
                return Optional.empty();
            }

            Integer lineNumber = null;

            final String lineNumberStr = splitLine[1];
            if (NumberUtils.isNumber(lineNumberStr)) {
                lineNumber = NumberUtils.createInteger(lineNumberStr);
            }

            return Optional.ofNullable(lineNumber);
        }
    }

    private class QuitServiceStrategy implements AbstractServiceStrategy {
        @Override
        public void process(String line) {
            conn.close();
        }
    }

    private class ShutdownServiceStrategy implements AbstractServiceStrategy {
        @Override
        public void process(String line) {
            closeLatch.countDown();
        }
    }

    private class UnknownCommandStrategy implements AbstractServiceStrategy {
        @Override
        public void process(String line) {

        }
    }
}
