//package pl.tkowalcz.tcph;
//
//import org.apache.calcite.avatica.util.Casing;
//import org.apache.calcite.avatica.util.Quoting;
//import org.apache.calcite.sql.SqlBasicCall;
//import org.apache.calcite.sql.SqlInsert;
//import org.apache.calcite.sql.SqlNode;
//import org.apache.calcite.sql.parser.SqlParser;
//import org.apache.calcite.sql.parser.SqlParserImplFactory;
//import org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang3.tuple.Pair;
//import pl.tkowalcz.tcph.datamodel.*;
//
//import java.io.*;
//import java.net.URISyntaxException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.function.BiFunction;
//import java.util.stream.Collectors;
//
//import static org.apache.commons.io.FilenameUtils.removeExtension;
//
//public class SqlToCsv {
//
//    public static void main(String[] args) throws URISyntaxException, IOException {
//        SqlParser.Config config = getParserConfig(SqlDdlParserImpl.FACTORY);
//
//        Files.walk(Paths.get("tcp-h/src/main/resources/s-1.0/"))
//                .filter(path -> !path.toFile().isDirectory())
//                .peek(System.out::println)
//                .forEach(file -> transcodeSqlToCsv(
//                        file.toFile().getAbsolutePath(),
//                        removeExtension(file.toFile().getAbsolutePath()) + ".csv",
//                        config
//                ));
//    }
//
//    private static void transcodeSqlToCsv(String sql, String csv, SqlParser.Config config) {
//        try (PrintStream stream = new PrintStream(csv)) {
//            Files.lines(Paths.get(sql))
//                    .map(line -> parseSql(config, line))
//                    .forEach(stream::println);
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }
//
//    private static String parseSql(
//            SqlParser.Config config,
//            String line
//    ) {
//        try {
//            SqlParser sqlParser = SqlParser.create(line, config);
//            SqlInsert insert = (SqlInsert) sqlParser.parseStmt();
//
//            List<SqlNode> operandList = insert.getOperandList();
//            assert operandList.size() == 4;
//
//            SqlBasicCall sqlBasicCall = (SqlBasicCall) operandList.get(2);
//            assert sqlBasicCall.operandCount() == 1;
//
//            SqlBasicCall operand = sqlBasicCall.operand(0);
//
//            return operand.getOperandList()
//                    .stream()
//                    .map(Objects::toString)
//                    .map(__ -> __.replaceAll(",", ""))
//                    .collect(Collectors.joining(","));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static SqlParser.Config getParserConfig(SqlParserImplFactory factory) {
//        return SqlParser.config()
//                .withParserFactory(factory)
//                .withQuotedCasing(Casing.UNCHANGED)
//                .withUnquotedCasing(Casing.UNCHANGED)
//                .withQuoting(Quoting.DOUBLE_QUOTE);
//    }
//}
