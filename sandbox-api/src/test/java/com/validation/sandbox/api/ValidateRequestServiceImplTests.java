package com.validation.sandbox.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.validation.sandbox.api.model.PaymentInitiationRequest;
import com.validation.sandbox.api.service.impl.ValidateRequestServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ValidateRequestServiceImplTests {

	@InjectMocks
	private ValidateRequestServiceImpl validateRequestServiceImpl;
	
	PaymentInitiationRequest paymentInitiationRequest =null;
	
	@BeforeEach
	public void setUp() {
		
paymentInitiationRequest = new PaymentInitiationRequest();

paymentInitiationRequest.setAmount("1.00");
paymentInitiationRequest.setCreditorIBAN("NL94ABNA1008270121");
paymentInitiationRequest.setDebtorIBAN("NL02RABO7134384551");

	}
	
	@Test
	public void paymentValidationRequestValidRequestTest() {
		
		
		String actual = validateRequestServiceImpl.validateRequest( paymentInitiationRequest);
		
		assertEquals(actual, null);
		
		
	}
	
	@Test
	public void paymentValidationRequestInValidRequestTest() {
		
		paymentInitiationRequest.setDebtorIBAN("NLNL02RABO7134384551");
		String actual = validateRequestServiceImpl.validateRequest( paymentInitiationRequest);
		
		assertEquals(actual, "INVALID_REQUEST");
		
		
	}

}
