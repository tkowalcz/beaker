package pl.tkowalcz.tpch.datamodel;

//import org.apache.calcite.sql.SqlBasicCall;
//import org.apache.calcite.sql.SqlCharStringLiteral;
//import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tpch.Database;

/**
 * CREATE TABLE supplier (
 * S_SUPPKEY     INTEGER NOT NULL,
 * S_NAME        CHAR(25) NOT NULL,
 * S_ADDRESS     VARCHAR(40) NOT NULL,
 * S_NATIONKEY   INTEGER NOT NULL,
 * S_PHONE       CHAR(15) NOT NULL,
 * S_ACCTBAL     DECIMAL(15,2) NOT NULL,
 * S_COMMENT     VARCHAR(101) NOT NULL,
 * <p>
 * PRIMARY KEY (S_SUPPKEY),
 * <p>
 * CONSTRAINT SUPPLIER_FK1 FOREIGN KEY (S_NATIONKEY) references nation(N_NATIONKEY)
 * );
 */
public record Supplier(
        String name,
        String address,
        Nation nation,
        String phone,
        double acctbal,
        String comment) {
    public static Pair<Integer, Supplier> fromCsv(Database database, String[] values) {
        assert values.length == 7;

        String suppKey = values[0];
        String name = values[1];
        String address = values[2];
        String nationKey = values[3];
        String phone = values[4];
        String acctbal = values[5];
        String comment = values[6];

        Nation nation = database.getNations().get(Integer.parseInt(nationKey));

        return Pair.of(
                Integer.parseInt(suppKey),
                new Supplier(
                        name,
                        address,
                        nation,
                        phone,
                        Double.parseDouble(acctbal),
                        comment
                )
        );
    }

//    public static Pair<Integer, Supplier> fromSql(Database database, SqlBasicCall basicCall) {
//        assert basicCall.operandCount() == 7;
//
//        SqlNumericLiteral suppKey = basicCall.operand(0);
//        SqlCharStringLiteral name = basicCall.operand(1);
//        SqlCharStringLiteral address = basicCall.operand(2);
//        SqlNumericLiteral nationKey = basicCall.operand(3);
//        SqlCharStringLiteral phone = basicCall.operand(4);
//        SqlNumericLiteral acctbal = basicCall.operand(5);
//        SqlCharStringLiteral comment = basicCall.operand(6);
//
//        Nation nation = database.getNations().get(nationKey.intValue(true));
//
//        return Pair.of(
//                suppKey.intValue(true),
//                new Supplier(
//                        name.toString(),
//                        address.toString(),
//                        nation,
//                        phone.toString(),
//                        acctbal.bigDecimalValue().doubleValue(),
//                        comment.toString()
//                )
//        );
//    }
}
