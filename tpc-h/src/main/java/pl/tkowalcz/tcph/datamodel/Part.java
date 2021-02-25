package pl.tkowalcz.tcph.datamodel;

//import org.apache.calcite.sql.SqlBasicCall;
//import org.apache.calcite.sql.SqlCharStringLiteral;
//import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tcph.Database;

/**
 * CREATE TABLE part  (
 * P_PARTKEY     INTEGER NOT NULL,
 * P_NAME        VARCHAR(55) NOT NULL,
 * P_MFGR        CHAR(25) NOT NULL,
 * P_BRAND       CHAR(10) NOT NULL,
 * P_TYPE        VARCHAR(25) NOT NULL,
 * P_SIZE        INTEGER NOT NULL,
 * P_CONTAINER   CHAR(10) NOT NULL,
 * P_RETAILPRICE DECIMAL(15,2) NOT NULL,
 * P_COMMENT     VARCHAR(23) NOT NULL,
 * <p>
 * PRIMARY KEY (P_PARTKEY)
 * );
 */
public record Part(
        String name,
        String mfgr,
        String brand,
        String type,
        int size,
        String container,
        double retailprice,
        String comment) {
    public static Pair<Integer, Part> fromCsv(Database database, String[] values) {
        assert values.length == 9;

        String partKey = values[0];
        String name = values[1];
        String mfgr = values[2];
        String brand = values[3];
        String type = values[4];
        String size = values[5];
        String container = values[6];
        String retailplace = values[7];
        String comment = values[8];

        return Pair.of(
                Integer.parseInt(partKey),
                new Part(
                        name,
                        mfgr,
                        brand,
                        type,
                        Integer.parseInt(size),
                        container,
                        Double.parseDouble(retailplace),
                        comment
                )
        );
    }

//    public static Pair<Integer, Part> fromSql(Database database, SqlBasicCall basicCall) {
//        assert basicCall.operandCount() == 9;
//
//        SqlNumericLiteral partKey = basicCall.operand(0);
//        SqlCharStringLiteral name = basicCall.operand(1);
//        SqlCharStringLiteral mfgr = basicCall.operand(2);
//        SqlCharStringLiteral brand = basicCall.operand(3);
//        SqlCharStringLiteral type = basicCall.operand(4);
//        SqlNumericLiteral size = basicCall.operand(5);
//        SqlCharStringLiteral container = basicCall.operand(6);
//        SqlNumericLiteral retailplace = basicCall.operand(7);
//        SqlCharStringLiteral comment = basicCall.operand(8);
//
//        return Pair.of(
//                partKey.intValue(true),
//                new Part(
//                        name.toString(),
//                        mfgr.toString(),
//                        brand.toString(),
//                        type.toString(),
//                        size.intValue(true),
//                        container.toString(),
//                        retailplace.bigDecimalValue().doubleValue(),
//                        comment.toString()
//                )
//        );
//    }
}
