package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.module.insuranceclaims.api.dao.BillDao;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.BillService;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;
import org.openmrs.module.insuranceclaims.util.ConstantValues;
import org.openmrs.module.insuranceclaims.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("insuranceclaims.BillService")
@Transactional
public class BillServiceImpl extends BaseOpenmrsDataService<Bill> implements BillService {

    @Autowired
    private ProvidedItemService providedItemService;

    @Autowired
    private BillDao billDao;

    @Override
    public Bill generateBill(List<ProvidedItem> providedItems) {
        Bill bill = new Bill();

        Date date = DateUtil.now();

        bill.setStartDate(date);
        bill.setEndDate(DateUtil.plusDays(date, ConstantValues.DEFAULT_DURATION_BILL_DAYS));

        BigDecimal sumProvidedItems = providedItems.stream().map(ProvidedItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        bill.setTotalAmount(sumProvidedItems);

        providedItemService.updateStatusProvidedItems(providedItems);

        return billDao.saveOrUpdate(bill);
    }
}
