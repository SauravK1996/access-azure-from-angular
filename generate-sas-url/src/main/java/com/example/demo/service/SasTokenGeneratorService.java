package com.example.demo.service;

import java.security.InvalidKeyException;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;

@Component
public class SasTokenGeneratorService {

	@Value("${connection-string}")
	private String storageConnectionString;

	public String generateSasToken(String containername) {

		try {

			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			CloudBlobContainer container = blobClient.getContainerReference(containername);

			return container.getUri().toString().replace(containername, "") + "?" + generateSAS(container);
//			return String.format("%s?%s", container.getUri(), generateSAS(container));
//			return generateSAS(container);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String generateSAS(CloudBlobContainer container) throws StorageException, InvalidKeyException {

		SharedAccessBlobPolicy sasConstraints = new SharedAccessBlobPolicy();
		Date expirationDate = Date.from(Instant.now().plus(Duration.ofDays(1)));
		sasConstraints.setSharedAccessExpiryTime(expirationDate);
		EnumSet<SharedAccessBlobPermissions> permissions = EnumSet.of(SharedAccessBlobPermissions.WRITE,
				SharedAccessBlobPermissions.LIST, SharedAccessBlobPermissions.READ, SharedAccessBlobPermissions.DELETE);
		sasConstraints.setPermissions(permissions);

		// Generate the shared access signature on the container, setting the
		// constraints directly on the signature.
//		String sasContainerToken = container.generateSharedAccessSignature(sasConstraints, null);

		// Return the URI string for the container, including the SAS token.
//		return container.getUri() + "?" + sasContainerToken;
		return container.generateSharedAccessSignature(sasConstraints, null);

//		container.createIfNotExists();
//		SharedAccessBlobPolicy sasPolicy = new SharedAccessBlobPolicy();
//		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
//		calendar.setTime(new Date());
//		sasPolicy.setSharedAccessStartTime(calendar.getTime());
//		calendar.add(Calendar.HOUR, 10);
//		sasPolicy.setSharedAccessExpiryTime(calendar.getTime());
//
//		sasPolicy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ, SharedAccessBlobPermissions.WRITE,
//				SharedAccessBlobPermissions.LIST));
//
//		BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
//
//		containerPermissions.setPublicAccess(BlobContainerPublicAccessType.OFF);
//
//		container.uploadPermissions(containerPermissions);
//
//		return container.generateSharedAccessSignature(sasPolicy, null);
	}
}
