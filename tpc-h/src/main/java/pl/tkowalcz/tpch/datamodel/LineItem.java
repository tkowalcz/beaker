package pl.tkowalcz.tpch.datamodel;

//import org.apache.calcite.sql.SqlBasicCall;
//import org.apache.calcite.sql.SqlCharStringLiteral;
//import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tpch.Database;
import pl.tkowalcz.tpch.Identifiable;

import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * CREATE TABLE lineitem (
 * L_ORDERKEY    INTEGER NOT NULL,
 * L_PARTKEY     INTEGER NOT NULL,
 * L_SUPPKEY     INTEGER NOT NULL,
 * L_LINENUMBER  INTEGER NOT NULL,
 * L_QUANTITY    DECIMAL(15,2) NOT NULL,
 * L_EXTENDEDPRICE  DECIMAL(15,2) NOT NULL,
 * L_DISCOUNT    DECIMAL(15,2) NOT NULL,
 * L_TAX         DECIMAL(15,2) NOT NULL,
 * L_RETURNFLAG  CHAR(1) NOT NULL,
 * L_LINESTATUS  CHAR(1) NOT NULL,
 * L_SHIPDATE    DATE NOT NULL,
 * L_COMMITDATE  DATE NOT NULL,
 * L_RECEIPTDATE DATE NOT NULL,
 * L_SHIPINSTRUCT CHAR(25) NOT NULL,
 * L_SHIPMODE     CHAR(10) NOT NULL,
 * L_COMMENT      VARCHAR(44) NOT NULL,
 * <p>
 * PRIMARY KEY (L_ORDERKEY,L_LINENUMBER),
 * <p>
 * CONSTRAINT LINEITEM_FK1 FOREIGN KEY (L_ORDERKEY)  references orders(O_ORDERKEY),
 * CONSTRAINT LINEITEM_FK2 FOREIGN KEY (L_PARTKEY,L_SUPPKEY) references partsupp(PS_PARTKEY, PS_SUPPKEY)
 * );
 */
public record LineItem(
        Order order,
        Part part,
        Supplier supplier,
        int linenumber,
        float quantity,
        float extendedprice,
        float discount,
        float tax,
        char returnflag,
        char linestatus,
        long shipdate,
        long commitdate,
        long receiptdate,
        String shipinstruct,
        String shipmode,
        String comment) implements Identifiable {
    public static Pair<Pair<Order, Integer>, LineItem> fromCsv(Database database, String[] values) {
        assert values.length == 16;

        String orderKey = values[0];
        String partKey = values[1];
        String suppKey = values[2];
        String linenumber = values[3];
        String quantity = values[4];
        String extendedprice = values[5];
        String discount = values[6];
        String tax = values[7];

        String returnflag = values[8];
        String linestatus = values[9];
        String shipdate = values[10];
        String commitdate = values[11];
        String receiptdate = values[12];
        String shipinstruct = values[13];
        String shipmode = values[14];
        String comment = values[15];

        Order order = database.getOrders().get(Integer.parseInt(orderKey));
        Part part = database.getParts().get(Integer.parseInt(partKey));
        Supplier supplier = database.getSuppliers().get(Integer.parseInt(suppKey));

        return Pair.of(
                Pair.of(
                        order,
                        Integer.parseInt(linenumber)
                ),
                new LineItem(
                        order,
                        part,
                        supplier,
                        Integer.parseInt(linenumber),
                        Float.parseFloat(quantity),
                        Float.parseFloat(extendedprice),
                        Float.parseFloat(discount),
                        Float.parseFloat(tax),
                        returnflag.charAt(0),
                        linestatus.charAt(0),
                        toEpochTime(shipdate),
                        toEpochTime(commitdate),
                        toEpochTime(receiptdate),
                        shipinstruct,
                        shipmode,
                        comment
                )
        );
    }

//    public static Pair<Pair<Order, Integer>, LineItem> fromSql(Database database, SqlBasicCall basicCall) {
//        assert basicCall.operandCount() == 16;
//
//        SqlNumericLiteral orderKey = basicCall.operand(0);
//        SqlNumericLiteral partKey = basicCall.operand(1);
//        SqlNumericLiteral suppKey = basicCall.operand(2);
//        SqlNumericLiteral linenumber = basicCall.operand(3);
//        SqlNumericLiteral quantity = basicCall.operand(4);
//        SqlNumericLiteral extendedprice = basicCall.operand(5);
//        SqlNumericLiteral discount = basicCall.operand(6);
//        SqlNumericLiteral tax = basicCall.operand(7);
//
//        SqlCharStringLiteral returnflag = basicCall.operand(8);
//        SqlCharStringLiteral linestatus = basicCall.operand(9);
//        SqlCharStringLiteral shipdate = basicCall.operand(10);
//        SqlCharStringLiteral commitdate = basicCall.operand(11);
//        SqlCharStringLiteral receiptdate = basicCall.operand(12);
//        SqlCharStringLiteral shipinstruct = basicCall.operand(13);
//        SqlCharStringLiteral shipmode = basicCall.operand(14);
//        SqlCharStringLiteral comment = basicCall.operand(15);
//
//        Order order = database.getOrders().get(orderKey.intValue(true));
//        Part part = database.getParts().get(partKey.intValue(true));
//        Supplier supplier = database.getSuppliers().get(suppKey.intValue(true));
//
//        return Pair.of(
//                Pair.of(
//                        order,
//                        linenumber.intValue(true)
//                ),
//                new LineItem(
//                        order,
//                        part,
//                        supplier,
//                        linenumber.intValue(true),
//                        quantity.bigDecimalValue().floatValue(),
//                        extendedprice.bigDecimalValue().floatValue(),
//                        discount.bigDecimalValue().floatValue(),
//                        tax.bigDecimalValue().floatValue(),
//                        returnflag.toString().charAt(0),
//                        linestatus.toString().charAt(0),
//                        toEpochTime(shipdate.toString()),
//                        toEpochTime(commitdate.toString()),
//                        toEpochTime(receiptdate.toString()),
//                        shipinstruct.toString(),
//                        shipmode.toString(),
//                        comment.toString()
//                )
//        );
//    }

    private static long toEpochTime(String yearMonthDate) {
        String dateText = yearMonthDate;
        dateText = dateText.replaceAll("'", "");

        return LocalDate
                .parse(dateText)
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    @Override
    public char getReturnflag() {
        return returnflag;
    }

    @Override
    public char getLinestatus() {
        return linestatus;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Identifiable that) {
            return returnflag == that.getReturnflag() && linestatus == that.getLinestatus();
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = returnflag;
        result = 31 * result + (int) linestatus;
        return result;
    }
}
