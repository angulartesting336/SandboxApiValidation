package com.validation.sandbox.api.service.impl;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.validation.sandbox.api.model.PaymentAcceptedResponse;
import com.validation.sandbox.api.model.PaymentInitiationRequest;
import com.validation.sandbox.api.model.PaymentRejectedResponse;
import com.validation.sandbox.api.model.ResponseDTO;
import com.validation.sandbox.api.service.SandboxValidationService;

@Service
public class SandboxValidationServiceImpl implements SandboxValidationService {

	@Autowired
	ValidateCertificateNameServiceImpl validateCertificateNameServiceImpl;

	@Autowired
	ValidateSignatureServiceImpl validateSignatureServiceImpl;

	@Autowired
	ValidateRequestServiceImpl validateRequestServiceImpl;

	@Autowired
	LimitExceededCheckServiceimpl limitExceededCheckServiceimpl;

	String ACCEPTED = "Accepted";

	String CREATED = "CREATED";

	String LIMIT_EXCEEDED = "Got rejected due to Amount limit exceeded";

	String UNPROCESSABLE_ENTITY = "UNPROCESSABLE_ENTITY";

	String REQUEST_VALIDATE = "Got rejected due to IBAN or Amount validation failed";

	String BAD_REQUEST = "BAD_REQUEST";

	String SIGNATURE_VALIDATION = "Got rejected due to signature validation failed";

	String UNKNOWN_CERTIFICATE = "Got rejected due to Unknown Certificate CN";

	String COMMON_ERROR = "something went wrong on the application,please try again later";

	String GENERAL_ERROR = "GENERAL_ERROR";

	String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

	String REJECTED = "Rejected";
	
	private static Logger log = LoggerFactory.getLogger(SandboxValidationServiceImpl.class);

	@Override
	public ResponseDTO paymentValidationRequest(PaymentInitiationRequest paymentInitiationRequest, String signature,
			String signatureCertificate, String xRequestId) throws CertificateException, IOException {

		ResponseDTO responseDTO = new ResponseDTO();

		try {

			PaymentAcceptedResponse paymentAcceptedResponse = null;

			String value = validateCertificateNameServiceImpl.validateCertificateName(signatureCertificate);

			if (value == null) {

				value = validateSignatureServiceImpl.validateSignature(paymentInitiationRequest, signature,
						signatureCertificate, xRequestId);

				if (value == null) {
					value = validateRequestServiceImpl.validateRequest(paymentInitiationRequest);
					if (value == null) {

						value = limitExceededCheckServiceimpl.checkLimitExceeded(paymentInitiationRequest.getAmount(),
								paymentInitiationRequest.getDebtorIBAN());
						if (value == null) {
							paymentAcceptedResponse = new PaymentAcceptedResponse();
							paymentAcceptedResponse.setPaymentId((UUID.randomUUID()).toString());
							paymentAcceptedResponse.setStatus(ACCEPTED);
							responseDTO = createResponseDTOObj(paymentAcceptedResponse, CREATED);
						} else {
							responseDTO = createRejectedResponseObj(value, LIMIT_EXCEEDED, UNPROCESSABLE_ENTITY);
						}

					} else {
						responseDTO = createRejectedResponseObj(value, REQUEST_VALIDATE, BAD_REQUEST);
					}
				} else {
					responseDTO = createRejectedResponseObj(value, SIGNATURE_VALIDATION, BAD_REQUEST);
				}
			} else {
				responseDTO = createRejectedResponseObj(value, UNKNOWN_CERTIFICATE, BAD_REQUEST);

			}
		} catch (Exception ex) {

			log.error(COMMON_ERROR, ex);
			responseDTO = createRejectedResponseObj(GENERAL_ERROR, COMMON_ERROR, INTERNAL_SERVER_ERROR);
		}

		return responseDTO;

	}

	private ResponseDTO createRejectedResponseObj(String value, String reason, String statusCode) {

		PaymentRejectedResponse paymentRejectedResponse = new PaymentRejectedResponse();

		paymentRejectedResponse.setReason(reason);
		paymentRejectedResponse.setReasonCode(value);
		paymentRejectedResponse.setStatus(REJECTED);

		return createResponseDTOObj(paymentRejectedResponse, statusCode);

	}

	private ResponseDTO createResponseDTOObj(Object paymentResponse, String statusCode) {

		ResponseDTO responseDTO = new ResponseDTO();

		responseDTO.setResponse(paymentResponse);
		responseDTO.setStatusCode(statusCode);

		return responseDTO;
	}

}
