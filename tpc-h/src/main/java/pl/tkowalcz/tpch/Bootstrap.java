package pl.tkowalcz.tpch;

import com.google.common.collect.Iterables;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tpch.datamodel.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Bootstrap {

    record TypedFile<KEY, T>(
            String fileName,
            BiFunction<Database, String[], Pair<KEY, T>> creator,
            Consumer<Map<KEY, T>> databaseSetter) {
    }

    public static Database createDatabase() throws URISyntaxException, IOException {
        Database database = new Database();

        TypedFile[] tables = {
                new TypedFile<>("region.csv", Region::fromCsv, database::setRegions),
                new TypedFile<>("nation.csv", Nation::fromCsv, database::setNations),
                new TypedFile<>("part.csv", Part::fromCsv, database::setParts),
                new TypedFile<>("supplier.csv", Supplier::fromCsv, database::setSuppliers),
                new TypedFile<>("partsupp.csv", Partsupp::fromCsv, database::setPartsupps),
                new TypedFile<>("customer.csv", Customer::fromCsv, database::setCustomers),
                new TypedFile<>("orders.csv", Order::fromCsv, database::setOrders),
                new TypedFile<>("lineitem.csv", LineItem::fromCsv, database::setLineItems),
        };

        for (TypedFile table : tables) {
            processTable(database, table);
        }

        return database;
    }

    private static <KEY, T> void processTable(Database database, TypedFile<KEY, T> table) {
        Map<KEY, T> map = parseData(
                table.fileName(),
                database,
                table.creator()
        );

        table.databaseSetter().accept(map);
    }

    private static <KEY, T> Map<KEY, T> parseData(
            String csv,
            Database database,
            BiFunction<Database, String[], Pair<KEY, T>> creator) {
        try (FileReader reader = new FileReader(csv)) {
            CSVParser csvReader = new CSVParser(reader, CSVFormat.DEFAULT
                    .withDelimiter(',')
                    .withIgnoreEmptyLines(true)
                    .withQuote('\'')
                    .withRecordSeparator('\n')
                    .withQuoteMode(QuoteMode.ALL_NON_NULL)
            );

            return StreamSupport.stream(csvReader.spliterator(), false)
                    .map(strings -> Iterables.toArray(strings, String.class))
                    .map(strings -> creator.apply(database, strings))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
