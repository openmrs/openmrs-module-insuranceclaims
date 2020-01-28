package org.openmrs.module.insuranceclaims.api.service.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.insuranceclaims.api.dao.BillDao;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.ProvidedItem;
import org.openmrs.module.insuranceclaims.api.service.BillService;
import org.openmrs.module.insuranceclaims.api.service.ProvidedItemService;
import org.openmrs.module.insuranceclaims.util.ConstantValues;
import org.openmrs.module.insuranceclaims.util.DateUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BillServiceImpl extends BaseOpenmrsDataService<Bill> implements BillService {
    private DateUtil dateUtil;
    private BillDao billDao;
    private ProvidedItemService providedItemService;

    @Override
    public Bill generateBill(List<ProvidedItem> providedItems) {
        Bill bill = new Bill();

        Date date = dateUtil.now();

        bill.setStartDate(date);
        bill.setEndDate(dateUtil.plusDays(date, ConstantValues.DEFAULT_DURATION_BILL_DAYS));
        bill.setDateCreated(date);
        bill.setPatient(providedItems.get(0).getPatient());

        BigDecimal sumProvidedItems = providedItems.stream().map(ProvidedItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        bill.setTotalAmount(sumProvidedItems);

        providedItemService.updateStatusProvidedItems(providedItems);
        return billDao.saveOrUpdate(bill);
    }

    @Override
    public List<Bill> getAllBills(Integer patientId) throws APIException {
        return this.billDao.getAllBills(patientId);
    }

    public void setBillDao(BillDao billDao) {
        this.billDao = billDao;
    }

    public void setDateUtil(DateUtil dateUtil) {
        this.dateUtil = dateUtil;
    }

    public void setProvidedItemService(ProvidedItemService providedItemService) {
        this.providedItemService = providedItemService;
    }
}
