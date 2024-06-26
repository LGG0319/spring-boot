/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package smoketest.data.couchbase;

import org.junit.jupiter.api.Test;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.couchbase.DataCouchbaseTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.testsupport.container.TestImage;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests for Couchbase with SSL.
 *
 * @author Scott Frederick
 */
@Testcontainers(disabledWithoutDocker = true)
@DataCouchbaseTest(properties = { "spring.couchbase.env.ssl.bundle=client", "spring.couchbase.env.timeouts.connect=2m",
		"spring.data.couchbase.bucket-name=cbbucket",
		"spring.ssl.bundle.pem.client.keystore.certificate=classpath:ssl/test-client.crt",
		"spring.ssl.bundle.pem.client.keystore.private-key=classpath:ssl/test-client.key",
		"spring.ssl.bundle.pem.client.truststore.certificate=classpath:ssl/test-ca.crt" })
class SampleCouchbaseApplicationSslTests {

	private static final String BUCKET_NAME = "cbbucket";

	@Container
	@ServiceConnection
	static final CouchbaseContainer couchbase = TestImage.container(SecureCouchbaseContainer.class)
		.withBucket(new BucketDefinition(BUCKET_NAME));

	@Autowired
	private CouchbaseTemplate couchbaseTemplate;

	@Autowired
	private SampleRepository repository;

	@Test
	void testRepository() {
		SampleDocument document = new SampleDocument();
		document.setText("Look, new @DataCouchbaseTest!");
		document = this.repository.save(document);
		assertThat(document.getId()).isNotNull();
		assertThat(this.couchbaseTemplate.getBucketName()).isEqualTo(BUCKET_NAME);
		this.repository.deleteAll();
	}

}
