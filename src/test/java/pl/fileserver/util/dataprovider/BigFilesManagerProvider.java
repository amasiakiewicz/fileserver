package pl.fileserver.util.dataprovider;

import static junitparams.JUnitParamsRunner.$;

public class BigFilesManagerProvider {

    public Object[] testFileLines() {
        return $(
                $("src/test/resources/emptyFile.txt", -1, null),
                $("src/test/resources/emptyFile.txt", 0, null),
                $("src/test/resources/emptyFile.txt", 1, null),

                $("src/test/resources/oneLineFile.txt", -1, null),
                $("src/test/resources/oneLineFile.txt", 0, "the"),
                $("src/test/resources/oneLineFile.txt", 1, null),

                $("src/test/resources/test.txt", -1, null),
                $("src/test/resources/test.txt", 0, "the"),
                $("src/test/resources/test.txt", 1, "quick brown"),
                $("src/test/resources/test.txt", 2, "fox jumps over the"),
                $("src/test/resources/test.txt", 3, "lazy dog"),
                $("src/test/resources/test.txt", 4, null)
        );
    }
}
