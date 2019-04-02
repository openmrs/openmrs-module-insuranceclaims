package org.openmrs.module.insuranceclaims.api.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Model class that represents a code of item (product or service)
 */
@Entity(name = "iclm.ItemCode")
@Table(name = "iclm_item_code")
public class ItemCode extends BaseOpenmrsData {

	private static final long serialVersionUID = -3696155497057221967L;

	@Id
	@GeneratedValue
	@Column(name = "iclm_item_code_id")
	private Integer id;

	@Basic
	@Column(name = "code", length = 255, nullable = false)
	private String code;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "item", nullable = false)
	private Item item;

	public ItemCode() {
	}

	/**
	 * Creates the representation of an item's code
	 *
	 * @param code - value of code
	 * @param item - related item object
	 */
	public ItemCode(String code, Item item) {
		super();
		this.code = code;
		this.item = item;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ItemCode itemCode = (ItemCode) o;

		return new EqualsBuilder()
				.appendSuper(super.equals(o))
				.append(id, itemCode.id)
				.append(code, itemCode.code)
				.append(item, itemCode.item)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(InsuranceClaimsConstants.HASH_CODE_INITIAL_NON_ZERO_ODD_NUMBER,
				InsuranceClaimsConstants.HASH_CODE_MULTIPLIER_NON_ZERO_ODD_NUMBER)
				.appendSuper(super.hashCode())
				.append(id)
				.append(code)
				.append(item)
				.toHashCode();
	}
}
