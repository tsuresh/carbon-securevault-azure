/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.securevault.azure.repository;

import com.azure.core.exception.AzureException;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.securevault.azure.config.AzureVaultConfigLoader;
import org.wso2.carbon.securevault.azure.exception.AzureVaultException;
import org.wso2.securevault.secret.SecretRepository;

import java.util.Properties;

import static org.wso2.carbon.securevault.azure.common.AzureVaultConstants.*;

/**
 * Azure Secret Repository.
 */
public class AzureSecretRepository implements SecretRepository {

    private static final Log LOG = LogFactory.getLog(AzureSecretRepository.class);
    private static final String SLASH = "/";

    private SecretRepository parentRepository;

    private SecretClient secretClient;

    private String PROPERTY_PREFIX = "secretProviders.vault.repositories.azure.properties.";

    /**
     * Initializes the repository based on provided properties.
     *
     * @param properties Configuration properties
     * @param id         Identifier to identify properties related to the corresponding repository
     */
    @Override
    public void init(Properties properties, String id) {

        LOG.info("Initializing Azure Secure Vault");

        // Load Configurations
        AzureVaultConfigLoader azureVaultConfigLoader = AzureVaultConfigLoader.getInstance();
        try {

            String keyVaultUrl = azureVaultConfigLoader.getProperty(PROPERTY_PREFIX + KEY_VAULT_URL);

            try {
                // Azure secret client
                secretClient = new SecretClientBuilder()
                        .vaultUrl(keyVaultUrl)
                        .credential(new DefaultAzureCredentialBuilder().build())
                        .buildClient();
                LOG.info("Ready to read secret from vault");
            } catch (AzureException exception) {
                throw new AzureVaultException(exception.getLocalizedMessage());
            }

        } catch (AzureVaultException e) {
            LOG.error(e.getMessage(), e);
        }

    }

    /**
     * Get Secret from the Secret Repository
     *
     * @param alias Alias name for look up a secret
     * @return Secret if there is any, otherwise, alias itself
     * @see SecretRepository
     */
    @Override
    public String getSecret(String alias) {

        String secret = null;

        try {
            secret = secretClient.getSecret(alias).getValue();
        } catch (AzureException exception){
            LOG.error(exception.getLocalizedMessage());
        }

        return secret;
    }

    /**
     * Get Encrypted data.
     *
     * @param alias Alias of the secret
     * @return
     */
    @Override
    public String getEncryptedData(String alias) {

        throw new UnsupportedOperationException();
    }

    /**
     * Set parent repository.
     *
     * @param parent Parent secret repository
     */
    @Override
    public void setParent(SecretRepository parent) {

        this.parentRepository = parent;
    }

    /**
     * Get parent repository.
     *
     * @return
     */
    @Override
    public SecretRepository getParent() {

        return this.parentRepository;
    }

}
