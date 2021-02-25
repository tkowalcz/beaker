package pl.tkowalcz.tpch.datamodel;

//import org.apache.calcite.sql.*;
import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tpch.Database;

import java.io.PrintStream;

/**
 * CREATE TABLE region  (
 * R_REGIONKEY  INTEGER NOT NULL,
 * R_NAME       CHAR(25) NOT NULL,
 * R_COMMENT    VARCHAR(152),
 * <p>
 * PRIMARY KEY (R_REGIONKEY)
 * );
 */
public record Region(
        String name,
        String comment) {

//    public static Pair<Integer, Region> fromSql(Database database, SqlBasicCall basicCall) {
//        assert basicCall.operandCount() == 3;
//
//        SqlNumericLiteral regionKey = basicCall.operand(0);
//        SqlCharStringLiteral name = basicCall.operand(1);
//        SqlCharStringLiteral comment = basicCall.operand(2);
//
//        return Pair.of(
//                regionKey.intValue(true),
//                new Region(
//                        name.toString(),
//                        comment.toString()
//                )
//        );
//    }

    public static Pair<Integer, Region> fromCsv(Database database, String[] values) {
        assert values.length == 3;

        String regionKey = values[0];
        String name = values[1];
        String comment = values[2];

        return Pair.of(
                Integer.parseInt(regionKey),
                new Region(
                        name,
                        comment
                )
        );
    }

    public void toFile(PrintStream stream, Integer key) {
        stream.printf(
                "%d,%s,%s\n",
                key,
                name.replace(",", ""),
                comment.replace(",", "")
        );
    }
}
