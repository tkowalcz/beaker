package pl.tkowalcz.tcph.datamodel;

//import org.apache.calcite.sql.SqlBasicCall;
//import org.apache.calcite.sql.SqlCharStringLiteral;
//import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tcph.Database;

/**
 * CREATE TABLE customer (
 * C_CUSTKEY     INTEGER NOT NULL,
 * C_NAME        VARCHAR(25) NOT NULL,
 * C_ADDRESS     VARCHAR(40) NOT NULL,
 * C_NATIONKEY   INTEGER NOT NULL,
 * C_PHONE       CHAR(15) NOT NULL,
 * C_ACCTBAL     DECIMAL(15,2)   NOT NULL,
 * C_MKTSEGMENT  CHAR(10) NOT NULL,
 * C_COMMENT     VARCHAR(117) NOT NULL,
 * <p>
 * PRIMARY KEY (C_CUSTKEY),
 * <p>
 * CONSTRAINT CUSTOMER_FK1 FOREIGN KEY (C_NATIONKEY) references nation(N_NATIONKEY)
 * );
 */
public record Customer(
        String name,
        String address,
        Nation nation,
        String phone,
        double acctbal,
        String mktsegment,
        String comment) {
    public static Pair<Integer, Customer> fromCsv(Database database, String[] values) {
        assert values.length == 8;

        String custKey = values[0];
        String name = values[1];
        String address = values[2];
        String nationKey = values[3];
        String phone = values[4];
        String acctbal = values[5];
        String mktsegment = values[6];
        String comment = values[7];

        Nation nation = database.getNations().get(Integer.parseInt(nationKey));

        return Pair.of(
                Integer.parseInt(custKey),
                new Customer(
                        name,
                        address,
                        nation,
                        phone,
                        Double.parseDouble(acctbal),
                        mktsegment,
                        comment
                )
        );
    }

//    public static Pair<Integer, Customer> fromSql(Database database, SqlBasicCall basicCall) {
//        assert basicCall.operandCount() == 8;
//
//        SqlNumericLiteral custKey = basicCall.operand(0);
//        SqlCharStringLiteral name = basicCall.operand(1);
//        SqlCharStringLiteral address = basicCall.operand(2);
//        SqlNumericLiteral nationKey = basicCall.operand(3);
//        SqlCharStringLiteral phone = basicCall.operand(4);
//        SqlNumericLiteral acctbal = basicCall.operand(5);
//        SqlCharStringLiteral mktsegment = basicCall.operand(6);
//        SqlCharStringLiteral comment = basicCall.operand(7);
//
//        Nation nation = database.getNations().get(nationKey.intValue(true));
//
//        return Pair.of(
//                custKey.intValue(true),
//                new Customer(
//                        name.toString(),
//                        address.toString(),
//                        nation,
//                        phone.toString(),
//                        acctbal.bigDecimalValue().doubleValue(),
//                        mktsegment.toString(),
//                        comment.toString()
//                )
//        );
//    }
}
