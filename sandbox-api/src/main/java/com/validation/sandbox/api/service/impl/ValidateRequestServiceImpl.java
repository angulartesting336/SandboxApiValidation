package com.validation.sandbox.api.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.validation.sandbox.api.model.PaymentInitiationRequest;

@Service
public class ValidateRequestServiceImpl {

	String INVALID_REQUEST = "INVALID_REQUEST";

	String IBAN_FORMAT = "[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}";

	String AMOUNT_FORMAT = "-?[0-9]+(.[0-9]{1,3})?";

	public String validateRequest(PaymentInitiationRequest paymentInitiationRequest) {

		Stream<String> initialStream = Stream.of(paymentInitiationRequest.getCreditorIBAN(),
				paymentInitiationRequest.getDebtorIBAN(), paymentInitiationRequest.getAmount());

		return (formatValidator(initialStream)) ? null : INVALID_REQUEST;

	}

	private boolean formatValidator(Stream<String> initialStream) {

		Map<String, Boolean> scopedBoolean = new HashMap<String, Boolean>();

		scopedBoolean.put("match", true);

		CustomForEachServiceImpl.forEach(initialStream, (str, breaker) -> {
			if ((!str.contains(".") && !Pattern.matches(IBAN_FORMAT, str))
					|| (str.contains(".") && !Pattern.matches(AMOUNT_FORMAT, str) /* && str.contains("EUR" ) */)) {
				scopedBoolean.put("match", false);
				breaker.stop();
			}

		});

		return scopedBoolean.get("match");
	}

}
