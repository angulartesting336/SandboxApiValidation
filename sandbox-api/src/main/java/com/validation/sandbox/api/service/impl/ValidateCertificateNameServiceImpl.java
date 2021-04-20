package com.validation.sandbox.api.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

@Service
public class ValidateCertificateNameServiceImpl {

	String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";

	String END_CERTIFICATE = "-----END CERTIFICATE-----";

	String SANDBOX_CN = "Sandbox-TPP";

	String X509 = "X509";

	String UNKNOWN_CN = "UNKNOWN_CERTIFICATE";

	String CN = "CN=";

	public String validateCertificateName(String signatureCertificate) throws CertificateException, IOException {

		X509Certificate certificate1 = generateCertificate(signatureCertificate);

		return (getCommonName(certificate1).startsWith(SANDBOX_CN)) ? null : UNKNOWN_CN;

	}

	public X509Certificate generateCertificate(String signatureCertificate) throws CertificateException {

		signatureCertificate = signatureCertificate.replaceAll(BEGIN_CERTIFICATE, "");
		signatureCertificate = signatureCertificate.replaceAll(END_CERTIFICATE, "");

		CertificateFactory cf1 = CertificateFactory.getInstance(X509);

		byte[] bytes1 = Base64.decodeBase64(signatureCertificate);

		return (X509Certificate) cf1.generateCertificate(new ByteArrayInputStream(bytes1));

	}

	private String getCommonName(X509Certificate certificate) {
		String name = certificate.getSubjectX500Principal().getName();
		int start = name.indexOf(CN);
		int end = name.indexOf(",", start);
		if (end == -1) {
			end = name.length();
		}
		return name.substring(start + 3, end);
	}

}
