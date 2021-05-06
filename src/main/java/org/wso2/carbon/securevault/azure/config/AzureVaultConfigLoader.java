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
package org.wso2.carbon.securevault.azure.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.securevault.azure.exception.AzureVaultException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.wso2.carbon.securevault.azure.common.AzureVaultConstants.CONFIG_FILE_PATH;

/**
 * Configuration Loader for Vault Configurations.
 */
public class AzureVaultConfigLoader {

    private static final Log LOG = LogFactory.getLog(AzureVaultConfigLoader.class);

    private static AzureVaultConfigLoader instance = null;

    private Properties properties;

    private AzureVaultConfigLoader() {

        try {
            loadConfigurations();
        } catch (AzureVaultException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static AzureVaultConfigLoader getInstance() {

        if (instance == null) {
            instance = new AzureVaultConfigLoader();
        }
        return instance;
    }

    /**
     * Load configurations.
     */
    public void loadConfigurations() throws AzureVaultException {

        properties = new Properties();
        try (InputStream inputStream = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new AzureVaultException("Error while loading configurations from " + CONFIG_FILE_PATH, e);
        }
    }

    /**
     * Get the config value for a given key.
     *
     * @param key Key of the property
     * @return
     * @throws AzureVaultException
     */
    public String getProperty(String key) throws AzureVaultException {

        return properties.getProperty(key);
    }
}
