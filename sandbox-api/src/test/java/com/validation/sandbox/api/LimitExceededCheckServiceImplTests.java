package com.validation.sandbox.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.validation.sandbox.api.service.impl.LimitExceededCheckServiceimpl;

@ExtendWith(MockitoExtension.class)
public class LimitExceededCheckServiceImplTests {
	
	@InjectMocks
	private LimitExceededCheckServiceimpl limitExceededCheckServiceimpl;
	
	String amount =null;
	
	String debtorIban = null;
	
	@BeforeEach
	public void setUp() {
		
		amount = "100.00";
		
		debtorIban ="NL02RABO7134384112";
		
	}

	@Test
	public void paymentValidationRequestLimitExceededTest() {
		
		String expected ="LIMIT_EXCEEDED";
		
		String actual = limitExceededCheckServiceimpl.checkLimitExceeded(amount, debtorIban);
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void paymentValidationRequestLimitNotExceededTest() {
		
		String expected =null;
		
		debtorIban ="NL02RABO7134384113";
		
		String actual = limitExceededCheckServiceimpl.checkLimitExceeded(amount, debtorIban);
		
		assertEquals(actual, expected);
	}

}
