package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.dao.BillDao;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.PaymentStatus;
import org.openmrs.module.insuranceclaims.api.model.ProcessStatus;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.BillService;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("insuranceclaims.BillService")
@Transactional
public class BillServiceImpl extends BaseOpenmrsDataService<Bill> implements BillService {

    private static final int DAYS = 7;

    @Autowired
    private ProvidedItemService providedItemService;

    @Autowired
    private BillDao billDao;

    @Override
    public Bill generateBill(List<ProvidedItem> providedItems) {

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, DAYS);

        BigDecimal sumProvideItems = providedItems.stream().map(ProvidedItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Bill bill = new Bill();
        bill.setStartDate(date);
        bill.setEndDate(c.getTime());
        bill.setTotalAmount(sumProvideItems);
        bill.setPaymentStatus(PaymentStatus.ENTERED);
        bill.setPaymentType(null);

        updateStatusProvidedItems(providedItems);

        return billDao.saveOrUpdate(bill);
    }

    private void updateStatusProvidedItems(List<ProvidedItem> providedItems) {

        for (ProvidedItem item : providedItems) {
            item.setStatus(ProcessStatus.PROCESSED);
            providedItemService.saveOrUpdate(item);
        }
    }
}
