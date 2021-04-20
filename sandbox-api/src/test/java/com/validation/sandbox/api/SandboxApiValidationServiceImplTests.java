package com.validation.sandbox.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.validation.sandbox.api.model.PaymentAcceptedResponse;
import com.validation.sandbox.api.model.PaymentInitiationRequest;
import com.validation.sandbox.api.model.PaymentRejectedResponse;
import com.validation.sandbox.api.model.ResponseDTO;
import com.validation.sandbox.api.service.impl.LimitExceededCheckServiceimpl;
import com.validation.sandbox.api.service.impl.SandboxValidationServiceImpl;
import com.validation.sandbox.api.service.impl.ValidateCertificateNameServiceImpl;
import com.validation.sandbox.api.service.impl.ValidateRequestServiceImpl;
import com.validation.sandbox.api.service.impl.ValidateSignatureServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SandboxApiValidationServiceImplTests {
	
	@InjectMocks
	private SandboxValidationServiceImpl sandboxApiValidationServiceImpl;
	
	@Mock
	ValidateCertificateNameServiceImpl validateCertificateNameServiceImpl;

	@Mock
	ValidateSignatureServiceImpl validateSignatureServiceImpl;

	@Mock
	ValidateRequestServiceImpl validateRequestServiceImpl;

	@Mock
	LimitExceededCheckServiceimpl limitExceededCheckServiceimpl;
	
	
	private PaymentInitiationRequest paymentInitiationRequest = null;
	
	private PaymentAcceptedResponse paymentAcceptedResponse = null;
	
	private PaymentRejectedResponse paymentRejectedResponse =null;
	
	private ResponseDTO responseDTO = null;
	
    private String signature = "AlFr/WbYiekHmbB6XdEO/7ghKd0n6q/bapENAYsL86KoYHqa4eP34xfH9icpQRmTpH0qOkt1vfUPWnaqu+vHBWx/gJXiuVlhayxLZD2w41q8ITkoj4oRLn2U1q8cLbjUtjzFWX9TgiQw1iY0ezpFqyDLPU7+ZzO01JI+yspn2gtto0XUm5KuxUPK24+xHD6R1UZSCSJKXY1QsKQfJ+gjzEjrtGvmASx1SUrpmyzVmf4qLwFB1ViRZmDZFtHIuuUVBBb835dCs2W+d7a+icGOCtGQbFcHvW0FODibnY5qq8v5w/P9i9PSarDaGgYb+1pMSnF3p8FsHAjk3Wccg2a1GQ==";
	
	private String signatureCertificate = "-----BEGIN CERTIFICATE-----MIIDwjCCAqoCCQDxVbCjIKynQjANBgkqhkiG9w0BAQsFADCBojELMAkGA1UEBhMCTkwxEDAOBgNVBAgMB1V0cmVjaHQxEDAOBgNVBAcMB1V0cmVjaHQxETAPBgNVBAoMCFJhYm9iYW5rMRMwEQYDVQQLDApBc3Nlc3NtZW50MSIwIAYDVQQDDBlTYW5kYm94LVRQUDpleGNlbGxlbnQgVFBQMSMwIQYJKoZIhvcNAQkBFhRuby1yZXBseUByYWJvYmFuay5ubDAeFw0yMDAxMzAxMzIyNDlaFw0yMTAxMjkxMzIyNDlaMIGiMQswCQYDVQQGEwJOTDEQMA4GA1UECAwHVXRyZWNodDEQMA4GA1UEBwwHVXRyZWNodDERMA8GA1UECgwIUmFib2JhbmsxEzARBgNVBAsMCkFzc2Vzc21lbnQxIjAgBgNVBAMMGVNhbmRib3gtVFBQOmV4Y2VsbGVudCBUUFAxIzAhBgkqhkiG9w0BCQEWFG5vLXJlcGx5QHJhYm9iYW5rLm5sMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAryLyouTQr1dvMT4qvek0eZsh8g0DQQLlOgBzZwx7iInxYEAgMNxCKXiZCbmWHBYqh6lpPh+BBmrnBQzB+qrSNIyd4bFhfUlQ+htK08yyL9g4nyLt0LeKuxoaVWpInrB5FRzoEY5PPpcEXSObgr+pM71AvyJtQLxZbqTao4S7TRKecUm32Wwg+FWY/StSKlox3QmEaxEGU7aPkaQfQs4hrtuUePwKrbkQ2hQdMpvI5oXRWzTqafvEQvND+IyLvZRqf0TSvIwsgtJd2tch2kqPoUwng3AmUFleJbMjFNzrWM7TH9LkKPItYtSuMTzeSe9o0SmXZFgcEBh5DnETZqIVuQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQASFOkJiKQuL7fSErH6y5Uwj9WmmQLFnit85tjbo20jsqseTuZqLdpwBObiHxnBz7o3M73PJAXdoXkwiMVykZrlUSEy7+FsNZ4iFppoFapHDbfBgM2WMV7VS6NK17e0zXcTGySSRzXsxw0yEQGaOU8RJ3Rry0HWo9M/JmYFrdBPP/3sWAt/+O4th5Jyk8RajN3fHFCAoUz4rXxhUZkf/9u3Q038rRBvqaA+6c0uW58XqF/QyUxuTD4er9veCniUhwIX4XBsDNxIW/rwBRAxOUkG4V+XqrBb75lCyea1o/9HIaq1iIKI4Day0piMOgwPEg1wF383yd0x8hRW4zxyHcER-----END CERTIFICATE-----";
	
	private String unknownCertificate ="-----BEGIN CERTIFICATE-----MIIDBDCCAm2gAwIBAgIJAK8dGINfkSTHMA0GCSqGSIb3DQEBBQUAMGAxCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDQTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzETMBEGA1UEChMKR29vZ2xlIEluYzEXMBUGA1UEAxMOd3d3Lmdvb2dsZS5jb20wHhcNMDgxMDA4MDEwODMyWhcNMDkxMDA4MDEwODMyWjBgMQswCQYDVQQGEwJVUzELMAkGA1UECBMCQ0ExFjAUBgNVBAcTDU1vdW50YWluIFZpZXcxEzARBgNVBAoTCkdvb2dsZSBJbmMxFzAVBgNVBAMTDnd3dy5nb29nbGUuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQUV7ukIfIixbokHONGMW9+ed0E9X4m99I8upPQp3iAtqIvWs7XCbAbGqzQH1qX9Y00hrQ5RRQj8OI3tRiQs/KfzGWOdvLpIk5oXpdT58tg4FlYh5fbhIoVoVn4GvtSjKmJFsoM8NRtEJHL1aWd++dXzkQjEsNcBXwQvfDb0YnbQIDAQABo4HFMIHCMB0GA1UdDgQWBBSm/h1pNY91bNfW08ac9riYzs3cxzCBkgYDVR0jBIGKMIGHgBSm/h1pNY91bNfW08ac9riYzs3cx6FkpGIwYDELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRMwEQYDVQQKEwpHb29nbGUgSW5jMRcwFQYDVQQDEw53d3cuZ29vZ2xlLmNvbYIJAK8dGINfkSTHMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADgYEAYpHTr3vQNsHHHUm4MkYcDB20a5KvcFoXgCcYtmdyd8rh/FKeZm2me7eQCXgBfJqQ4dvVLJ4LgIQiU3R5ZDe0WbW7rJ3M9ADQFyQoRJP8OIMYW3BoMi0Z4E730KSLRh6kfLq4rK6vw7lkH9oynaHHWZSJLDAp17cPj+6znWkN9/g=-----END CERTIFICATE-----";
	
	private String wrongSignature = "YHqa4eP34xfH9icpQRmTpH0qOkt1vfUPWnaqKd0nbapENbapENAYsL86KoYHqa4ePYiekHmbB6XQjANBgkqhkiG9";

	@BeforeEach
	public void setUp() {
		
paymentInitiationRequest = new PaymentInitiationRequest();
		
		responseDTO = new ResponseDTO();
		
		paymentAcceptedResponse = new PaymentAcceptedResponse();
		
		paymentAcceptedResponse.setPaymentId("29318e25-cebd-498c-888a-f77672f66449");
		paymentAcceptedResponse.setStatus("Accepted");
		
		paymentRejectedResponse = new PaymentRejectedResponse();
		
		paymentRejectedResponse.setReason("Got rejected due to Unknown Certificate CN");
		paymentRejectedResponse.setReasonCode("UNKNOWN_CERTIFICATE");
		paymentRejectedResponse.setStatus("Rejected");
		
		paymentInitiationRequest.setAmount("1.00");
		paymentInitiationRequest.setCreditorIBAN("NL94ABNA1008270121");
		paymentInitiationRequest.setDebtorIBAN("NL02RABO7134384551");
	}
	
	
	@Test
	public void paymentValidationRequestSuccessfulTest() throws  IOException, GeneralSecurityException{
	
		ResponseDTO expected = new ResponseDTO();
		expected.setResponse(paymentAcceptedResponse);
		expected.setStatusCode("CREATED");
		
		responseDTO.setResponse(paymentAcceptedResponse);
		responseDTO.setStatusCode("CREATED");
		
		when(validateCertificateNameServiceImpl.validateCertificateName(signatureCertificate)).thenReturn(null);
		when(validateSignatureServiceImpl.validateSignature(paymentInitiationRequest, signature,
						signatureCertificate,"29318e25-cebd-498c-888a-f77672f66449")).thenReturn(null);
		when(validateRequestServiceImpl.validateRequest(paymentInitiationRequest)).thenReturn(null);
		when(limitExceededCheckServiceimpl.checkLimitExceeded(paymentInitiationRequest.getAmount(),
								paymentInitiationRequest.getDebtorIBAN())).thenReturn(null);
		
		ResponseDTO	actual = sandboxApiValidationServiceImpl.paymentValidationRequest(paymentInitiationRequest, signature, signatureCertificate, "29318e25-cebd-498c-888a-f77672f66449");
		
		assertEquals(actual, expected);
		
	}
	
	@Test
	public void paymentValidationRequestUnknownCNTest() throws  IOException, GeneralSecurityException{
	
		ResponseDTO expected = new ResponseDTO();
		expected.setResponse(paymentRejectedResponse);
		expected.setStatusCode("BAD_REQUEST");
		
		responseDTO.setResponse(paymentRejectedResponse);
		responseDTO.setStatusCode("BAD_REQUESTs");
		
		when(validateCertificateNameServiceImpl.validateCertificateName(unknownCertificate)).thenReturn("UNKNOWN_CERTIFICATE");
		
		
		ResponseDTO	actual = sandboxApiValidationServiceImpl.paymentValidationRequest(paymentInitiationRequest, signature, unknownCertificate, "29318e25-cebd-498c-888a-f77672f66449");
		
		assertEquals(actual, expected);
		
	}
	
	@Test
	public void paymentValidationRequestSignatureNotValidatedTest() throws  IOException, GeneralSecurityException{
	
		PaymentRejectedResponse invalidSignatureResponse = new PaymentRejectedResponse();
		
		invalidSignatureResponse.setReason("Got rejected due to signature validation failed");
		invalidSignatureResponse.setReasonCode("INVALID_SIGNATURE");
		invalidSignatureResponse.setStatus("Rejected");
		ResponseDTO expected = new ResponseDTO();
		expected.setResponse(invalidSignatureResponse);
		expected.setStatusCode("BAD_REQUEST");
		
		
		when(validateCertificateNameServiceImpl.validateCertificateName(signatureCertificate)).thenReturn(null);
		when(validateSignatureServiceImpl.validateSignature(paymentInitiationRequest, wrongSignature,
						signatureCertificate,"29318e25-cebd-498c-888a-f77672f66449")).thenReturn("INVALID_SIGNATURE");
		
		
		ResponseDTO	actual = sandboxApiValidationServiceImpl.paymentValidationRequest(paymentInitiationRequest, wrongSignature, signatureCertificate, "29318e25-cebd-498c-888a-f77672f66449");
		
		assertEquals(actual, expected);
		
	}
	
	@Test
	public void paymentValidationRequestInvalidRequestTest() throws  IOException, GeneralSecurityException{
	
		PaymentRejectedResponse invalidRequestResponse = new PaymentRejectedResponse();
		
		invalidRequestResponse.setReason("Got rejected due to IBAN or Amount validation failed");
		invalidRequestResponse.setReasonCode("INVALID_REQUEST");
		invalidRequestResponse.setStatus("Rejected");
		ResponseDTO expected = new ResponseDTO();
		expected.setResponse(invalidRequestResponse);
		expected.setStatusCode("BAD_REQUEST");
		
		
		when(validateCertificateNameServiceImpl.validateCertificateName(signatureCertificate)).thenReturn(null);
		when(validateSignatureServiceImpl.validateSignature(paymentInitiationRequest, signature,
						signatureCertificate,"29318e25-cebd-498c-888a-f77672f66449")).thenReturn(null);
		when(validateRequestServiceImpl.validateRequest(paymentInitiationRequest)).thenReturn("INVALID_REQUEST");
		
		
		ResponseDTO	actual = sandboxApiValidationServiceImpl.paymentValidationRequest(paymentInitiationRequest, signature, signatureCertificate, "29318e25-cebd-498c-888a-f77672f66449");
		
		assertEquals(actual, expected);
		
	}
	
	@Test
	public void paymentValidationRequestLimitExceededTest() throws  IOException, GeneralSecurityException{
	
		PaymentRejectedResponse invalidRequestResponse = new PaymentRejectedResponse();
		
		invalidRequestResponse.setReason("Got rejected due to Amount limit exceeded");
		invalidRequestResponse.setReasonCode("LIMIT_EXCEEDED");
		invalidRequestResponse.setStatus("Rejected");
		ResponseDTO expected = new ResponseDTO();
		expected.setResponse(invalidRequestResponse);
		expected.setStatusCode("UNPROCESSABLE_ENTITY");
		
		
		when(validateCertificateNameServiceImpl.validateCertificateName(signatureCertificate)).thenReturn(null);
		when(validateSignatureServiceImpl.validateSignature(paymentInitiationRequest, signature,
						signatureCertificate,"29318e25-cebd-498c-888a-f77672f66449")).thenReturn(null);
		when(validateRequestServiceImpl.validateRequest(paymentInitiationRequest)).thenReturn(null);
		when(limitExceededCheckServiceimpl.checkLimitExceeded(paymentInitiationRequest.getAmount(),
				paymentInitiationRequest.getDebtorIBAN())).thenReturn("LIMIT_EXCEEDED");
		
		
		ResponseDTO	actual = sandboxApiValidationServiceImpl.paymentValidationRequest(paymentInitiationRequest, signature, signatureCertificate, "29318e25-cebd-498c-888a-f77672f66449");
		
		assertEquals(actual, expected);
		
	}

	@Test
	public void paymentValidationRequestGeneralErrorTest() throws  IOException, GeneralSecurityException{
		

	
PaymentRejectedResponse generalErrorResponse = new PaymentRejectedResponse();
		
generalErrorResponse.setReason("something went wrong on the application,please try again later");
generalErrorResponse.setReasonCode("GENERAL_ERROR");
generalErrorResponse.setStatus("Rejected");
		ResponseDTO expected = new ResponseDTO();
		expected.setResponse(generalErrorResponse);
		expected.setStatusCode("INTERNAL_SERVER_ERROR");
		
		responseDTO.setResponse(paymentRejectedResponse);
		responseDTO.setStatusCode("BAD_REQUEST");
		
		when(validateCertificateNameServiceImpl.validateCertificateName(signatureCertificate)).thenReturn(null);
		
		
		ResponseDTO	actual = sandboxApiValidationServiceImpl.paymentValidationRequest(paymentInitiationRequest, signature, unknownCertificate, "29318e25-cebd-498c-888a-f77672f66449");
		
		assertEquals(actual, expected);
		
	}
	
}
