package com.validation.sandbox.api.service.impl;

import java.util.Arrays;

import org.springframework.stereotype.Service;

@Service
public class LimitExceededCheckServiceimpl {

	String FORMAT_AMOUNT = "[^0-9]";

	String AMOUNT_EXCEEDED = "LIMIT_EXCEEDED";

	public String checkLimitExceeded(String amount, String debtorIban) {

		return ((Double.parseDouble(amount) > 0)
				&& (Arrays.stream(debtorIban.replaceAll(FORMAT_AMOUNT, "").replaceAll("\\B", " ").split(" "))
						.mapToInt(Integer::parseInt).sum() % debtorIban.length() == 0)) ? AMOUNT_EXCEEDED : null;

	}

}
