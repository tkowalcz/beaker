package pl.tkowalcz.tcph;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.internal.functions.Functions;
import org.agrona.collections.Int2ObjectHashMap;
import pl.tkowalcz.tcph.datamodel.LineItem;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TpcHQuery1 {

    public ProcessingItem[] executeStream(Database database) {
        long threshold = getThresholdDate();

        List<ProcessingItem> items = database.getLineItems()
                .values().stream()
                .filter(lineItem -> lineItem.shipdate() <= threshold)
                .map(ProcessingItem::fromLineItem)
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.reducing(ProcessingItem::add)
                        )
                )
                .values()
                .stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        return finalSort(items);
    }

    public ProcessingItem[] executeParallelStream(Database database) {
        long threshold = getThresholdDate();

        List<ProcessingItem> items = database.getLineItems()
                .values().parallelStream()
                .filter(lineItem -> lineItem.shipdate() <= threshold)
                .map(ProcessingItem::fromLineItem)
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.reducing(ProcessingItem::add)
                        )
                )
                .values()
                .stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        return finalSort(items);
    }

    public List<ProcessingItem> executeRx(Database database) {
        long threshold = getThresholdDate();

        return Observable.fromIterable(database.getLineItems().values())
                .filter(lineItem -> lineItem.shipdate() <= threshold)
                .groupBy(Functions.identity())
                .flatMapSingle(group -> group.reduce(new ProcessingItem(), ProcessingItem::add))
                .sorted(
                        Comparator
                                .comparing(ProcessingItem::getReturnflag)
                                .thenComparing(ProcessingItem::getLinestatus)
                ).toList()
                .blockingGet();
    }

    public ProcessingItem[] executeForLoop(Database database) {
        long threshold = getThresholdDate();

        Map<Identifiable, ProcessingItem> map = new HashMap<>();

        LineItem[] lineItems = database.getLineItemsArray();
        for (LineItem lineItem : lineItems) {
            if (lineItem.shipdate() <= threshold) {
                ProcessingItem oldItem = map.get(lineItem);
                if (oldItem != null) {
                    oldItem.add(lineItem);
                } else {
                    ProcessingItem processingItem = ProcessingItem.fromLineItem(lineItem);
                    map.put(processingItem, processingItem);
                }
            }
        }

        return finalSort(map.values());
    }

    public ProcessingItem[] executeColumnar(Database database) {
        long threshold = getThresholdDate();

        char[] linestatus = database.getLinestatus();
        char[] returnflag = database.getReturnflag();
        long[] shipdate = database.getShipdate();

        float[] discount = database.getDiscount();
        float[] extendedprice = database.getExtendedprice();
        float[] quantity = database.getQuantity();
        float[] tax = database.getTax();

        Int2ObjectHashMap<ProcessingItem> map = new Int2ObjectHashMap<>();
        for (int i = 0; i < linestatus.length; i++) {
            if (shipdate[i] <= threshold) {
                int key = linestatus[i] << 16 | returnflag[i];

                ProcessingItem oldItem = map.get(key);
                if (oldItem != null) {
                    oldItem.add(
                            quantity[i],
                            extendedprice[i],
                            discount[i],
                            tax[i]
                    );
                } else {
                    ProcessingItem processingItem = ProcessingItem.fromLineItem(
                            linestatus[i],
                            returnflag[i],
                            quantity[i],
                            extendedprice[i],
                            discount[i],
                            tax[i]
                    );

                    map.put(key, processingItem);
                }
            }
        }

        return finalSort(map.values());
    }

    private long getThresholdDate() {
        return LocalDate
                .parse("1998-12-01")
                .minusDays(90)
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC) * 1000;
    }

    private ProcessingItem[] finalSort(Collection<ProcessingItem> values) {
        return values
                .stream()
                .sorted(
                        Comparator
                                .comparing(ProcessingItem::getReturnflag)
                                .thenComparing(ProcessingItem::getLinestatus)
                ).toArray(ProcessingItem[]::new);
    }
}
