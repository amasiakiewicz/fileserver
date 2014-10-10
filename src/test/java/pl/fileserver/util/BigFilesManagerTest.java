package pl.fileserver.util;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import pl.fileserver.util.dataprovider.BigFilesManagerProvider;

@RunWith(JUnitParamsRunner.class)
public class BigFilesManagerTest {

    @Test
    public void shouldQuitGentlyOnEmptyFileName() throws IOException {
        //given
        final BigFilesManager bigFilesManager = new BigFilesManager(null);

        //when
        bigFilesManager.init();

        //then
    }

    @Test
    @Parameters(source = BigFilesManagerProvider.class, method = "testFileLines")
    public void shouldGiveProperLines(String fileName, int lineNumber, String expectedLine) throws IOException {
        //given
        final BigFilesManager bigFilesManager = new BigFilesManager(fileName);
        bigFilesManager.init();

        //when
        final String actualLine = bigFilesManager.getLine(lineNumber);

        //then
        then(actualLine).isEqualTo(expectedLine);
    }

}