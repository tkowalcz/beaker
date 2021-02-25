package pl.tkowalcz.tcph;

import pl.tkowalcz.tcph.datamodel.LineItem;

public class ProcessingItem implements Identifiable {

    private char returnflag;
    private char linestatus;

    private float sum_qty;
    private float sum_base_price;
    private float sum_disc_price;
    private float sum_charge;

    private float avg_qty_sum;
    private float avg_qty_count;

    private float avg_price_sum;
    private float avg_price_count;

    private float avg_disc_sum;
    private float avg_disc_count;

    private long count_order;

    public static ProcessingItem add(ProcessingItem processingItem1, ProcessingItem processingItem2) {
        ProcessingItem result = new ProcessingItem();

        result.returnflag = processingItem1.returnflag;
        result.linestatus = processingItem1.linestatus;

        result.sum_qty = processingItem1.sum_qty + processingItem2.sum_qty;
        result.sum_base_price = processingItem1.sum_base_price + processingItem2.sum_base_price;
        result.sum_disc_price = processingItem1.sum_disc_price + processingItem2.sum_disc_price;
        result.sum_charge = processingItem1.sum_charge + processingItem2.sum_charge;

        result.avg_qty_sum = processingItem1.avg_qty_sum + processingItem2.avg_qty_sum;
        result.avg_qty_count = processingItem1.avg_qty_count + processingItem2.avg_qty_count;

        result.avg_price_sum = processingItem1.avg_price_sum + processingItem2.avg_price_sum;
        result.avg_price_count = processingItem1.avg_price_count + processingItem2.avg_price_count;

        result.avg_disc_sum = processingItem1.avg_disc_sum + processingItem2.avg_disc_sum;
        result.avg_disc_count = processingItem1.avg_disc_count + processingItem2.avg_disc_count;

        result.count_order = processingItem1.count_order + processingItem2.count_order;

        return result;
    }

    public ProcessingItem add(LineItem lineItem) {
        sum_qty += lineItem.quantity();
        sum_base_price += lineItem.extendedprice();

        double partial = lineItem.extendedprice() * (1 - lineItem.discount());
        sum_disc_price += partial;
        sum_charge += partial * (1 + lineItem.tax());

        avg_qty_sum += lineItem.quantity();
        avg_qty_count += 1;

        avg_price_sum += lineItem.extendedprice();
        avg_price_count += 1;

        avg_disc_sum += lineItem.discount();
        avg_disc_count += 1;

        count_order++;
        return this;
    }

    public void add(
            float quantity,
            float extendedprice,
            float discount,
            float tax
    ) {
        sum_qty += quantity;
        sum_base_price += extendedprice;

        double partial = extendedprice * (1 - discount);
        sum_disc_price += partial;
        sum_charge += partial * (1 + tax);

        avg_qty_sum += quantity;
        avg_qty_count += 1;

        avg_price_sum += extendedprice;
        avg_price_count += 1;

        avg_disc_sum += discount;
        avg_disc_count += 1;

        count_order++;
    }

    public static ProcessingItem fromLineItem(LineItem lineItem) {
        ProcessingItem processingItem = new ProcessingItem();
        processingItem.returnflag = lineItem.returnflag();
        processingItem.linestatus = lineItem.linestatus();

        processingItem.sum_qty = lineItem.quantity();
        processingItem.sum_base_price = lineItem.extendedprice();
        processingItem.sum_disc_price = lineItem.extendedprice() * (1 - lineItem.discount());
        processingItem.sum_charge = processingItem.sum_disc_price * (1 + lineItem.tax());

        processingItem.avg_qty_sum = lineItem.quantity();
        processingItem.avg_qty_count = 1;

        processingItem.avg_price_sum = lineItem.extendedprice();
        processingItem.avg_price_count = 1;

        processingItem.avg_disc_sum = lineItem.discount();
        processingItem.avg_disc_count = 1;

        processingItem.count_order = 1;

        return processingItem;
    }

    public static ProcessingItem fromLineItem(
            char returnflag,
            char linestatus,
            float quantity,
            float extendedprice,
            float discount,
            float tax
    ) {
        ProcessingItem processingItem = new ProcessingItem();
        processingItem.returnflag = returnflag;
        processingItem.linestatus = linestatus;

        processingItem.sum_qty = quantity;
        processingItem.sum_base_price = extendedprice;
        processingItem.sum_disc_price = extendedprice * (1 - discount);
        processingItem.sum_charge = processingItem.sum_disc_price * (1 + tax);

        processingItem.avg_qty_sum = quantity;
        processingItem.avg_qty_count = 1;

        processingItem.avg_price_sum = extendedprice;
        processingItem.avg_price_count = 1;

        processingItem.avg_disc_sum = discount;
        processingItem.avg_disc_count = 1;

        processingItem.count_order = 1;

        return processingItem;
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

    @Override
    public String toString() {
        return "returnflag=" + returnflag +
                ", linestatus=" + linestatus +
                ", sum_qty=" + sum_qty +
                ", sum_base_price=" + sum_base_price +
                ", sum_disc_price=" + sum_disc_price +
                ", sum_charge=" + sum_charge +
                ", avg_qty=" + (avg_qty_sum / avg_qty_count) +
                ", avg_price=" + (avg_price_sum / avg_qty_count) +
                ", avg_disc=" + (avg_disc_sum / avg_disc_count) +
                ", count_order=" + count_order;
    }
}
