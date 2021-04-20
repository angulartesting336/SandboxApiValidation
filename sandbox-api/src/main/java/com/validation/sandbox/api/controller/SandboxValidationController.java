package com.validation.sandbox.api.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.validation.sandbox.api.model.PaymentInitiationRequest;
import com.validation.sandbox.api.model.ResponseDTO;
import com.validation.sandbox.api.service.SandboxValidationService;
import com.validation.sandbox.api.service.impl.SandboxValidationServiceImpl;

@RestController
@RequestMapping(value = "/paymentValidation")
public class SandboxValidationController {

	@Autowired
	SandboxValidationService sandboxValidationService;

	private static Logger log = LoggerFactory.getLogger(SandboxValidationServiceImpl.class);
	
	String RSA_CONSTANT = "RSA"; 
	
	String SHA_CONSTANT = "SHA256withRSA"; 

	@PostMapping(value = "/v1.0.0/initiate-payment")
	public ResponseEntity<Object> paymentValidationRequest(
			@RequestBody PaymentInitiationRequest paymentInitiationRequest,
			@RequestHeader(value = "Signature", required = true) String signature,
			@RequestHeader(value = "SignatureCertificate", required = true) String signatureCertificate,
			@RequestHeader(value = "XRequestId", required = true) String xRequestId)
			throws CertificateException, IOException, GeneralSecurityException {

		ResponseDTO response = sandboxValidationService.paymentValidationRequest(paymentInitiationRequest, signature,
				signatureCertificate, xRequestId);
		HttpHeaders headers = new HttpHeaders();

		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_CONSTANT);
		keyPairGen.initialize(2048);
		KeyPair pair = keyPairGen.generateKeyPair();

		PrivateKey privKey = pair.getPrivate();
		Signature sign = Signature.getInstance(SHA_CONSTANT);

		sign.initSign(privKey);

		JSONObject json = new JSONObject(response.getResponse());

		byte[] bytes = json.toString().getBytes();
		sign.update(bytes);

		headers.add("Signature", new String(Base64.encodeBase64(sign.sign())));
		headers.add("Signature-Certificate", signatureCertificate);
		headers.add("X-Request-Id", xRequestId);

		return ResponseEntity.status(getHttpStatus(response.getStatusCode())).headers(headers)
				.body(response.getResponse());
	}

	public HttpStatus getHttpStatus(String statusCode) {

		return HttpStatus.valueOf(statusCode);

	}

}
