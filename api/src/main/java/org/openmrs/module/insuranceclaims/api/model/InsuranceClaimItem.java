package org.openmrs.module.insuranceclaims.api.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Model class that represents an insurance claim item.
 * Represents the relation between claim and item.
 */
@Entity(name = "iclm.InsuranceClaimItem ")
@Table(name = "iclm_claim_item")
public class InsuranceClaimItem extends AbstractBaseOpenmrsData {

	private static final long serialVersionUID = -6769445113735423802L;

	@Id
	@GeneratedValue
	@Column(name = "iclm_claim_item_id")
	private Integer id;

	@Basic
	@Column(name = "quantity_provided")
	private Integer quantityProvided;

	@Basic
	@Column(name = "quantity_approved")
	private Integer quantityApproved;

	@Basic
	@Column(name = "price_approved")
	private BigDecimal priceApproved;

	@Basic
	@Column(name = "explanation", columnDefinition = "TEXT")
	private String explanation;

	@Basic
	@Column(name = "justification", columnDefinition = "TEXT")
	private String justification;

	@Basic
	@Column(name = "rejection_reason", length = 255)
	private String rejectionReason;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "item", nullable = false)
	private ProvidedItem item;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "insurance_claim", nullable = false)
	private InsuranceClaim insuranceClaim;

	@Basic
	@Column(name = "claim_item_status")
	private InsuranceClaimItemStatus claimItemStatus;

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuantityProvided() {
		return quantityProvided;
	}

	public void setQuantityProvided(Integer quantityProvided) {
		this.quantityProvided = quantityProvided;
	}

	public Integer getQuantityApproved() {
		return quantityApproved;
	}

	public void setQuantityApproved(Integer quantityApproved) {
		this.quantityApproved = quantityApproved;
	}

	public BigDecimal getPriceApproved() {
		return priceApproved;
	}

	public void setPriceApproved(BigDecimal priceApproved) {
		this.priceApproved = priceApproved;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public ProvidedItem getItem() {
		return item;
	}

	public void setItem(ProvidedItem item) {
		this.item = item;
	}

	public InsuranceClaim getInsuranceClaim() {
		return insuranceClaim;
	}

	public void setInsuranceClaim(InsuranceClaim insuranceClaim) {
		this.insuranceClaim = insuranceClaim;
	}

	public InsuranceClaimItemStatus getClaimItemStatus() {
		return claimItemStatus;
	}

	public void setClaimItemStatus(InsuranceClaimItemStatus claimItemStatus) {
		this.claimItemStatus = claimItemStatus;
	}
}
