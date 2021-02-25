package pl.tkowalcz.tcph;

import org.apache.commons.lang3.tuple.Pair;
import pl.tkowalcz.tcph.datamodel.*;

import java.util.Map;

public class Database {

    private Map<Integer, Region> regions;
    private Map<Integer, Nation> nations;
    private Map<Integer, Supplier> suppliers;
    private Map<Integer, Part> parts;
    private Map<Pair<Part, Supplier>, Partsupp> partsupps;
    private Map<Integer, Customer> customers;
    private Map<Integer, Order> orders;

    private Map<Pair<Order, Integer>, LineItem> lineItems;
    private LineItem[] lineItemsArray;

    private char[] returnflag;
    private char[] linestatus;
    private long[] shipdate;
    private float[] quantity;
    private float[] extendedprice;
    private float[] discount;
    private float[] tax;

    public Map<Integer, Region> getRegions() {
        return regions;
    }

    public void setRegions(Map<Integer, Region> regions) {
        this.regions = regions;
    }

    public Map<Integer, Nation> getNations() {
        return nations;
    }

    public void setNations(Map<Integer, Nation> nations) {
        this.nations = nations;
    }

    public Map<Integer, Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Map<Integer, Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public Map<Integer, Part> getParts() {
        return parts;
    }

    public void setParts(Map<Integer, Part> parts) {
        this.parts = parts;
    }

    public Map<Pair<Part, Supplier>, Partsupp> getPartsupps() {
        return partsupps;
    }

    public void setPartsupps(Map<Pair<Part, Supplier>, Partsupp> partsupps) {
        this.partsupps = partsupps;
    }

    public Map<Integer, Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Map<Integer, Customer> customers) {
        this.customers = customers;
    }

    public Map<Integer, Order> getOrders() {
        return orders;
    }

    public void setOrders(Map<Integer, Order> orders) {
        this.orders = orders;
    }

    public Map<Pair<Order, Integer>, LineItem> getLineItems() {
        return lineItems;
    }

    public LineItem[] getLineItemsArray() {
        return lineItemsArray;
    }

    public void setLineItems(Map<Pair<Order, Integer>, LineItem> lineItems) {
        this.lineItems = lineItems;
        this.lineItemsArray = lineItems.values().toArray(LineItem[]::new);

        this.returnflag = new char[lineItems.size()];
        for (int i = 0; i < returnflag.length; i++) {
            returnflag[i] = lineItemsArray[i].returnflag();
        }

        this.linestatus = new char[lineItems.size()];
        for (int i = 0; i < linestatus.length; i++) {
            linestatus[i] = lineItemsArray[i].linestatus();
        }

        this.shipdate = new long[lineItems.size()];
        for (int i = 0; i < shipdate.length; i++) {
            shipdate[i] = lineItemsArray[i].shipdate();
        }

        this.quantity = new float[lineItems.size()];
        for (int i = 0; i < quantity.length; i++) {
            quantity[i] = lineItemsArray[i].quantity();
        }

        this.extendedprice = new float[lineItems.size()];
        for (int i = 0; i < extendedprice.length; i++) {
            extendedprice[i] = lineItemsArray[i].extendedprice();
        }

        this.discount = new float[lineItems.size()];
        for (int i = 0; i < discount.length; i++) {
            discount[i] = lineItemsArray[i].discount();
        }

        this.tax = new float[lineItems.size()];
        for (int i = 0; i < tax.length; i++) {
            tax[i] = lineItemsArray[i].tax();
        }
    }

    public char[] getLinestatus() {
        return linestatus;
    }

    public char[] getReturnflag() {
        return returnflag;
    }

    public float[] getDiscount() {
        return discount;
    }

    public float[] getExtendedprice() {
        return extendedprice;
    }

    public float[] getQuantity() {
        return quantity;
    }

    public float[] getTax() {
        return tax;
    }

    public long[] getShipdate() {
        return shipdate;
    }
}
