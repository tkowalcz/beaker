package pl.tkowalcz.tpch.datamodel;

//import org.apache.calcite.linq4j.Ord;
//import org.apache.calcite.sql.SqlBasicCall;
//import org.apache.calcite.sql.SqlCharStringLiteral;
//import org.apache.calcite.sql.SqlDateLiteral;
//import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tpch.Database;

/**
 * CREATE TABLE orders  (
 * O_ORDERKEY       INTEGER NOT NULL,
 * O_CUSTKEY        INTEGER NOT NULL,
 * O_ORDERSTATUS    CHAR(1) NOT NULL,
 * O_TOTALPRICE     DECIMAL(15,2) NOT NULL,
 * O_ORDERDATE      DATE NOT NULL,
 * O_ORDERPRIORITY  CHAR(15) NOT NULL,
 * O_CLERK          CHAR(15) NOT NULL,
 * O_SHIPPRIORITY   INTEGER NOT NULL,
 * O_COMMENT        VARCHAR(79) NOT NULL,
 * <p>
 * PRIMARY KEY (O_ORDERKEY),
 * <p>
 * CONSTRAINT ORDERS_FK1 FOREIGN KEY (O_CUSTKEY) references customer(C_CUSTKEY)
 * );
 */
public record Order(
        Customer customer,
        char orderstatus,
        double totalprice,
        String orderdate,
        String orderpriority,
        String clerk,
        int shippriority,
        String comment) {
    public static Pair<Integer, Order> fromCsv(Database database, String[] values) {
        assert values.length == 9;

        String orderKey = values[0];
        String custKey = values[1];
        String orderStatus = values[2];
        String totalprice = values[3];
        String orderDate = values[4];
        String orderPriority = values[5];
        String clerk = values[6];
        String shippriority = values[7];
        String comment = values[8];

        Customer customer = database.getCustomers().get(Integer.parseInt(custKey));

        return Pair.of(
                Integer.parseInt(orderKey),
                new Order(
                        customer,
                        orderStatus.charAt(0),
                        Double.parseDouble(totalprice),
                        orderDate,
                        orderPriority,
                        clerk,
                        Integer.parseInt(shippriority),
                        comment
                )
        );
    }

//    public static Pair<Integer, Order> fromSql(Database database, SqlBasicCall basicCall) {
//        assert basicCall.operandCount() == 9;
//
//        SqlNumericLiteral orderKey = basicCall.operand(0);
//        SqlNumericLiteral custKey = basicCall.operand(1);
//        SqlCharStringLiteral orderStatus = basicCall.operand(2);
//        SqlNumericLiteral totalprice = basicCall.operand(3);
//        SqlCharStringLiteral orderDate = basicCall.operand(4);
//        SqlCharStringLiteral orderPriority = basicCall.operand(5);
//        SqlCharStringLiteral clerk = basicCall.operand(6);
//        SqlNumericLiteral shippriority = basicCall.operand(7);
//        SqlCharStringLiteral comment = basicCall.operand(8);
//
//        Customer customer = database.getCustomers().get(custKey.intValue(true));
//
//        return Pair.of(
//                orderKey.intValue(true),
//                new Order(
//                        customer,
//                        orderStatus.toString().charAt(0),
//                        totalprice.bigDecimalValue().doubleValue(),
//                        orderDate.toString(),
//                        orderPriority.toString(),
//                        clerk.toString(),
//                        shippriority.intValue(true),
//                        comment.toString()
//                )
//        );
//    }
}
