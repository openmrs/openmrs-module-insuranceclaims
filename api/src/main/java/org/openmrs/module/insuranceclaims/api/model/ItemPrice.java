package org.openmrs.module.insuranceclaims.api.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;

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
 * Model class that represents a price of the item (product or service).
 */
@Entity(name = "iclm.ItemPrice")
@Table(name = "iclm_item_price")
public class ItemPrice extends BaseOpenmrsData {

	private static final long serialVersionUID = 5909523587080292308L;

	@Id
	@GeneratedValue
	@Column(name = "iclm_item_price_id")
	private Integer id;

	@Basic
	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@Basic
	@Column(name = "name", length = 255)
	private String name;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "item", nullable = false)
	private Item item;

	public ItemPrice() {
	}

	/**
	 * Creates the representation of an item's price
	 *
	 * @param price - value of price
	 * @param name  - the specific price name
	 * @param item  - related item object
	 */
	public ItemPrice(BigDecimal price, String name, Item item) {
		super();
		this.price = price;
		this.name = name;
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

		ItemPrice itemPrice = (ItemPrice) o;

		return new EqualsBuilder()
				.appendSuper(super.equals(o))
				.append(id, itemPrice.id)
				.append(price, itemPrice.price)
				.append(name, itemPrice.name)
				.append(item, itemPrice.item)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(InsuranceClaimsConstants.HASH_CODE_INITIAL_NON_ZERO_ODD_NUMBER,
				InsuranceClaimsConstants.HASH_CODE_MULTIPLIER_NON_ZERO_ODD_NUMBER)
				.appendSuper(super.hashCode())
				.append(id)
				.append(price)
				.append(name)
				.append(item)
				.toHashCode();
	}
}
