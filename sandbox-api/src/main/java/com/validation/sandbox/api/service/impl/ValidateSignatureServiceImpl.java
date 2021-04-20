package com.validation.sandbox.api.service.impl;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.validation.sandbox.api.model.PaymentInitiationRequest;

@Service
public class ValidateSignatureServiceImpl {

	@Autowired
	ValidateCertificateNameServiceImpl validateCertificateNameServiceImpl;

	String INVALID_SIGNATURE = "INVALID_SIGNATURE";

	String SHA_CONSTANT = "SHA256withRSA";

	String X509 = "X509";

	String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";

	String END_CERTIFICATE = "-----END CERTIFICATE-----";

	public String validateSignature(PaymentInitiationRequest paymentInitiationRequest, String signature,
			String signatureCertificate, String xRequestId) throws CertificateException, GeneralSecurityException {

		boolean bool = true;

		X509Certificate certificate = validateCertificateNameServiceImpl.generateCertificate(signatureCertificate);

		/*
		 * signatureCertificate = signatureCertificate.replaceAll(BEGIN_CERTIFICATE,
		 * ""); signatureCertificate = signatureCertificate.replaceAll(END_CERTIFICATE,
		 * "");
		 * 
		 * CertificateFactory cf1 = null; X509Certificate certificate1 = null; cf1 =
		 * CertificateFactory.getInstance(X509);
		 * 
		 * byte[] bytes = Base64.decodeBase64(signatureCertificate); certificate1 =
		 * (X509Certificate) cf1.generateCertificate(new ByteArrayInputStream(bytes));
		 */

		Signature sign = Signature.getInstance(SHA_CONSTANT);

		sign.initVerify(certificate.getPublicKey());
		JSONObject json = new JSONObject(paymentInitiationRequest);

		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(json.toString().getBytes());

		byte[] data = md.digest();
		sign.update(xRequestId.getBytes());
		sign.update(data);

		bool = sign.verify(Base64.decodeBase64(signature));

		return (bool) ? INVALID_SIGNATURE : null;
	}

}
