package pl.tkowalcz.tpch.datamodel;

//import org.apache.calcite.sql.*;
import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tpch.Database;

/**
 * CREATE TABLE nation  (
 * N_NATIONKEY  INTEGER NOT NULL,
 * N_NAME       CHAR(25) NOT NULL,
 * N_REGIONKEY  INTEGER NOT NULL,
 * N_COMMENT    VARCHAR(152),
 * <p>
 * PRIMARY KEY (N_NATIONKEY),
 * <p>
 * CONSTRAINT NATION_FK1 FOREIGN KEY (N_REGIONKEY) references region(R_REGIONKEY)
 * );
 */
public record Nation(
        String name,
        Region region,
        String comment) {

    public static Pair<Integer, Nation> fromCsv(Database database, String[] values) {
        assert values.length == 4;

        String nationKey = values[0];
        String name = values[1];
        String regionKey = values[2];
        String comment = values[3];

        Region region = database.getRegions().get(Integer.parseInt(regionKey));

        return Pair.of(
                Integer.parseInt(nationKey),
                new Nation(
                        name,
                        region,
                        comment
                )
        );
    }

//    public static Pair<Integer, Nation> fromSql(Database database, SqlBasicCall basicCall) {
//        assert basicCall.operandCount() == 4;
//
//        SqlNumericLiteral nationKey = basicCall.operand(0);
//        SqlCharStringLiteral name = basicCall.operand(1);
//        SqlNumericLiteral regionKey = basicCall.operand(2);
//        SqlCharStringLiteral comment = basicCall.operand(3);
//
//        Region region = database.getRegions().get(regionKey.intValue(true));
//
//        return Pair.of(
//                nationKey.intValue(true),
//                new Nation(
//                        name.toString(),
//                        region,
//                        comment.toString()
//                )
//        );
//    }
}
