package pl.tkowalcz.tcph.datamodel;

//import org.apache.calcite.sql.SqlBasicCall;
//import org.apache.calcite.sql.SqlCharStringLiteral;
//import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tcph.Database;

/**
 * CREATE TABLE partsupp (
 * PS_PARTKEY     INTEGER NOT NULL,
 * PS_SUPPKEY     INTEGER NOT NULL,
 * PS_AVAILQTY    INTEGER NOT NULL,
 * PS_SUPPLYCOST  DECIMAL(15,2)  NOT NULL,
 * PS_COMMENT     VARCHAR(199) NOT NULL,
 * <p>
 * PRIMARY KEY (PS_PARTKEY,PS_SUPPKEY),
 * <p>
 * CONSTRAINT PARTSUPP_FK1 FOREIGN KEY (PS_SUPPKEY) references supplier(S_SUPPKEY),
 * CONSTRAINT PARTSUPP_FK2 FOREIGN KEY (PS_PARTKEY) references part(P_PARTKEY)
 * );
 */
public record Partsupp(
        Part part,
        Supplier supplier,
        int availqty,
        double supplycost,
        String comment) {
    public static Pair<Pair<Part, Supplier>, Partsupp> fromCsv(Database database, String[] values) {
        assert values.length == 5;

        String partKey = values[0];
        String supKey = values[1];
        String availqty = values[2];
        String supplycost = values[3];
        String comment = values[4];

        Part part = database.getParts().get(Integer.parseInt(partKey));
        Supplier supplier = database.getSuppliers().get(Integer.parseInt(supKey));

        return Pair.of(
                Pair.of(
                        part,
                        supplier
                ),
                new Partsupp(
                        part,
                        supplier,
                        Integer.parseInt(availqty),
                        Double.parseDouble(supplycost),
                        comment
                )
        );
    }

//    public static Pair<Pair<Part, Supplier>, Partsupp> fromSql(Database database, SqlBasicCall basicCall) {
//        assert basicCall.operandCount() == 5;
//
//        SqlNumericLiteral partKey = basicCall.operand(0);
//        SqlNumericLiteral supKey = basicCall.operand(1);
//        SqlNumericLiteral availqty = basicCall.operand(2);
//        SqlNumericLiteral supplycost = basicCall.operand(3);
//        SqlCharStringLiteral comment = basicCall.operand(4);
//
//        Part part = database.getParts().get(partKey.intValue(true));
//        Supplier supplier = database.getSuppliers().get(supKey.intValue(true));
//
//        return Pair.of(
//                Pair.of(
//                        part,
//                        supplier
//                ),
//                new Partsupp(
//                        part,
//                        supplier,
//                        availqty.intValue(true),
//                        supplycost.bigDecimalValue().doubleValue(),
//                        comment.toString()
//                )
//        );
//    }
}
