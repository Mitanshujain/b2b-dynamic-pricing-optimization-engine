package com.mitanshu.pricing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class DataTest {
    @TempDir Path folder;

    @Test void generatorIsReproducibleAndDoesNotOverwrite() throws Exception {
        Path first = folder.resolve("first.csv");
        Path second = folder.resolve("second.csv");
        SyntheticData.generate(first, 200, 42);
        SyntheticData.generate(second, 200, 42);
        assertEquals(Files.readString(first), Files.readString(second));
        assertEquals(200, CsvData.load(first).size());
        assertThrows(FileAlreadyExistsException.class, () -> SyntheticData.generate(first, 200, 42));
    }

    @Test void rejectsDuplicateDates() throws Exception {
        Path file = write("2024-01-01,P1,120,125,1,1,80,50\n2024-01-01,P1,121,125,1,1,80,49\n");
        assertTrue(assertThrows(IllegalArgumentException.class, () -> CsvData.load(file)).getMessage().contains("Duplicate"));
    }

    @Test void rejectsMissingNonfiniteAndMixedProductValues() throws Exception {
        for (String body : new String[]{
                "2024-01-01,P1,,125,1,1,80,50\n",
                "2024-01-01,P1,NaN,125,1,1,80,50\n",
                "2024-01-01,P1,120,125,2,1,80,50\n",
                "2024-01-01,P1,120,125,1,1,80,50\n2024-01-02,P2,120,125,1,1,80,50\n"}) {
            assertThrows(IllegalArgumentException.class, () -> CsvData.load(write(body)));
        }
    }

    @Test void parsesQuotedProductAndSortsDates() throws Exception {
        Path file = write("2024-01-02,\"Bolts, pack\",120,125,1,1,80,0\n2024-01-01,\"Bolts, pack\",120,125,1,1,80,50\n");
        var rows = CsvData.load(file);
        assertEquals("Bolts, pack", rows.get(0).productId());
        assertEquals("2024-01-01", rows.get(0).date().toString());
        assertEquals(0, rows.get(1).quantity());
    }

    @Test void rejectsHeaderOnlyAndWrongHeader() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> CsvData.load(write("")));
        Path file = folder.resolve("wrong.csv");
        Files.writeString(file, "date,price\n2024-01-01,100\n");
        assertThrows(IllegalArgumentException.class, () -> CsvData.load(file));
    }

    private Path write(String body) throws Exception {
        Path file = folder.resolve("test.csv");
        Files.writeString(file, String.join(",", CsvData.HEADER) + "\n" + body);
        return file;
    }
}
