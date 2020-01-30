package org.openmrs.module.insuranceclaims.api.mother;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.module.insuranceclaims.api.model.Bill;
import org.openmrs.module.insuranceclaims.api.model.PaymentStatus;
import org.openmrs.module.insuranceclaims.api.model.PaymentType;

import java.math.BigDecimal;
import java.util.Date;

public final class BillMother {

    private static final String EXAMPLE_TOTAL_AMOUNT = "123456.78";

    /**
     * Creates the Bill's test instance
     * @param concept - related diagnosis object
     * @return - the Bill instance
     */
    public static Bill createTestInstance(Concept concept, Patient patient) {
        Bill bill = new Bill();
        bill.setStartDate(new Date());
        bill.setEndDate(new Date());
        bill.setTotalAmount(new BigDecimal(EXAMPLE_TOTAL_AMOUNT));
        bill.setPaymentStatus(PaymentStatus.COMPLETED);
        bill.setPaymentType(PaymentType.CASH);
        bill.setDiagnosis(concept);
        bill.setPatient(patient);
        return bill;
    }

    /**
     * Creates the Bill's test instance
     * @param totalAmount - value of total amount price
     * @return - the Bill instance
     */
    public static Bill createTestInstanceWithAmount(BigDecimal totalAmount, Date startDate, Date endDate,
                                                    Patient patient) {
        Bill bill = new Bill();
        bill.setStartDate(startDate);
        bill.setEndDate(endDate);
        bill.setDateCreated(startDate);
        bill.setTotalAmount(totalAmount);
        bill.setPaymentStatus(PaymentStatus.ENTERED);
        bill.setPatient(patient);
        return bill;
    }
}
