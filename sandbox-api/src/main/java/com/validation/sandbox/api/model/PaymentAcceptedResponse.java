package com.validation.sandbox.api.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Data;

@Data
public class PaymentAcceptedResponse {
	
	private String paymentId;
	
	private String status;
	
	@Override
	public boolean equals(Object o) {
		String exclude = "paymentId";
		return EqualsBuilder.reflectionEquals(this, o,exclude);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
