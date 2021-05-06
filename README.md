
# Integrate Azure Key Vault with WSO2 Identity Server

## Overview
This is Azure Key Vault Connector which is compatible with the External Vault Support.
In order to use this version, it is required to have WSO2 Identity Server 5.12.0 or above. Any version lesser than

## Setting up

### Step 1: Setup Azure Vault

Steps of creating a Microsoft Azure Key Vault can be found under [https://docs.microsoft.com/en-us/java/api/overview/azure/security-keyvault-secrets-readme?view=azure-java-stable#createget-credentials](https://docs.microsoft.com/en-us/java/api/overview/azure/security-keyvault-secrets-readme?view=azure-java-stable#createget-credentials)

### Step 2: Configure Azure Vault extension

1. Build the Azure Vault Integration OSGI bundle using `mvn clean install` and copy
   the `target/org.wso2.carbon.securevault.azure-1.0.0.jar` file to `<IS_HOME>/repository/components/dropin/`
   directory.

2. Add **Azure Vault Java Driver** (Eg: `vault-java-driver-5.1.0.jar`) to the
   `<IS_HOME>/repository/components/lib/` directory.

3. Open `/repository/conf/security/secret-conf.properties` file and set following configurations.
    ```
    carbon.secretProvider=org.wso2.securevault.secret.handler.SecretManagerSecretCallbackHandler
    
    secretProviders = vault
    secretProviders.vault.provider=org.wso2.securevault.secret.repository.VaultSecretRepositoryProvider
    
    secretProviders.vault.repositories=azure
    secretProviders.vault.repositories.azure=org.wso2.carbon.securevault.azure.repository.AzureSecretRepository 
    secretProviders.vault.repositories.azure.properties.keyVaultUrl=<Vault URL>
    ```

   **Note:** In production, you should always use the vault address with TLS enabled.

4. Add following lines to the `<IS_HOME>/repository/conf/log4j2.properties` file
    ```
    logger.org-wso2-carbon-securevault-azure.name=org.wso2.carbon.securevault.azure
    logger.org-wso2-carbon-securevault-azure.level=INFO
    logger.org-wso2-carbon-securevault-azure.additivity=false
    logger.org-wso2-carbon-securevault-azure.appenderRef.CARBON_CONSOLE.ref = CARBON_CONSOLE
    ```
   Then append `org-wso2-carbon-securevault-azure` to the `loggers` list in the same file as follows.
   ```
   loggers = AUDIT_LOG, trace-messages, ... , org-wso2-carbon-securevault-azure
   ```

### Step 3: Update passwords with their aliases
1. Open the `deployment.toml` file in the `<IS_HOME>/repository/conf/` directory and add
   the `[runtime_secrets]` configuration property as shown below.

    ```toml
    [runtime_secrets]
    enable = "true"
    ```

2. Add the encrypted password alias to the relevant sections in the `deployment.toml`
   file by using a place holder: `$secret{alias}`. For example:

    ```toml
    [super_admin]
    username="admin"
    password="$secret{admin_password}"
    
    [keystore.primary]
    file_name = "wso2carbon.jks"
    password = "$secret{keystore_password}" 
    
    [database.identity_db]
    type = "h2"
    url = "jdbc:h2:./repository/database/WSO2IDENTITY_DB;DB_CLOSE_ON_EXIT=FALSE;LOCK_TIMEOUT=60000"
    username = "wso2carbon"
    password = "$secret{database_password}"
    ```
   NOTE: When there are multiple secret repositories configured (other than Azure vault), Modify the secret
   placeholder as,

   `$secret{vault:azure:<alias>}`.
   Example:
   ```toml
   [super_admin]
   username="admin"
   password="$secret{vault:azure:admin_password}"
   ```

### Step 4: Authentication

**Method 1**
Setup environment variables with following authentication parameters

     ``` 
	    export AZURE_CLIENT_ID=CLIENT ID  
        export AZURE_CLIENT_SECRET=CLIENT SECRET 
        export AZURE_TENANT_ID=TENANT ID
      ```

**Method 2**
Setup pod identities to authenticate Azure vault into your deployments
https://docs.microsoft.com/en-us/azure/aks/use-azure-ad-pod-identity

### Step 5 : Start the Server
Start the Identity Server as usual...
