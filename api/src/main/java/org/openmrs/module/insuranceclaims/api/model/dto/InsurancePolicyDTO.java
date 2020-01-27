package org.openmrs.module.insuranceclaims.api.model.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class InsurancePolicyDTO implements Serializable {

    private static final long serialVersionUID = -3152436976666791201L;

    private Date expiryDate;

    private String policyNumber;

    private BigDecimal allowedMoney;

    private String status;

    public Date getExpiryDate() {
        return expiryDate != null ? new Date(expiryDate.getTime()) : null;
    }

    public InsurancePolicyDTO setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate != null ? new Date(expiryDate.getTime()) : null;
        return this;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public InsurancePolicyDTO setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
        return this;
    }

    public BigDecimal getAllowedMoney() {
        return allowedMoney;
    }

    public InsurancePolicyDTO setAllowedMoney(BigDecimal allowedMoney) {
        this.allowedMoney = allowedMoney;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public InsurancePolicyDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
