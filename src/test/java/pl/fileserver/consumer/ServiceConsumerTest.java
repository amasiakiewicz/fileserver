package pl.fileserver.consumer;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import pl.fileserver.util.BigFilesManager;
import reactor.net.NetChannel;

@RunWith(JUnitParamsRunner.class)
public class ServiceConsumerTest {

    @Mock
    private CountDownLatch closeLatch;

    @Mock
    private NetChannel<String, String> conn;

    private ServiceConsumer serviceConsumer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        BigFilesManager bigFilesManager = new BigFilesManager("src/test/resources/test.txt");
        bigFilesManager.init();

        serviceConsumer = new ServiceConsumer(bigFilesManager, conn, closeLatch);
    }

    @Test
    @Parameters({"QUIT\n", "QUIT\r\n"})
    public void shouldProcessQuitCommand(String commandQuit) {
        //given

        //when
        serviceConsumer.accept(commandQuit);

        //then
        verify(conn).close();

    }

    @Test
    @Parameters({"SHUTDOWN\n", "SHUTDOWN\r\n"})
    public void shouldProcessShutdownCommand(String shutdownCommand) {
        //given

        //when
        serviceConsumer.accept(shutdownCommand);

        //then
        verify(closeLatch).countDown();

    }

    @Test
    @Parameters({"GET 1\n, the", "GET 1\r\n, the", " GET 1\n, the", "GET 1 \n, the", "GET  1\n, the"})
    public void shouldProcessGetCommand(String getCommand, String expectedLine) {
        //given

        //when
        serviceConsumer.accept(getCommand);

        //then
        verify(conn).send("OK\r");
        verify(conn).send(expectedLine);
    }

    @Test
    @Parameters({"\n", "\r\n", "GET\n", "GET 1 la\n", "GET ala\n", "get 1\n", "PUT\n"})
    public void shouldIgnoreCommand(String getCommand) {
        //given

        //when
        serviceConsumer.accept(getCommand);

        //then
        verify(conn, never()).send(anyString());
    }

}