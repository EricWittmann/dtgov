/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.dtgov.karaf.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.felix.gogo.commands.Command;
import org.overlord.commons.codec.AesEncrypter;
import org.overlord.commons.karaf.commands.configure.AbstractConfigureCommand;
import org.overlord.dtgov.karaf.commands.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Virgil Naranjo
 */
@Command(scope = "overlord:dtgov", name = "configure")
public class ConfigureCommand extends AbstractConfigureCommand {

    private static final Logger logger = LoggerFactory.getLogger(ConfigureCommand.class);

    @Override
    protected Object doExecute() throws Exception {
        logger.info(Messages.getString("configure.command.executed")); //$NON-NLS-1$

        super.doExecute();

        logger.debug(Messages.getString("configure.command.copying.files")); //$NON-NLS-1$
        copyFile("dtgov-ui.properties"); //$NON-NLS-1$
        copyFile("dtgov.properties"); //$NON-NLS-1$
        File dir = new File(karafConfigPath + "overlord-apps"); //$NON-NLS-1$
        if (!dir.exists()) {
            dir.mkdir();
        }
        copyFile("dtgovui-overlordapp.properties", "overlord-apps/dtgovui-overlordapp.properties"); //$NON-NLS-1$ //$NON-NLS-2$

        logger.debug(Messages.getString("configure.command.copying.files.end")); //$NON-NLS-1$
        String randomWorkflowUserPassword = UUID.randomUUID().toString();

        logger.debug(Messages.getString("configure.command.adding.jms.user")); //$NON-NLS-1$
        Properties usersProperties = new Properties();
        File srcFile = new File(karafConfigPath + "users.properties"); //$NON-NLS-1$
        FileInputStream fis = new FileInputStream(srcFile);
        try {
            usersProperties.load(fis);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        // Adding the jms user to the users.properties
        String randomWorkflowPassword = DigestUtils.sha256Hex(randomWorkflowUserPassword);
        String encryptedPassword = "{CRYPT}" + randomWorkflowPassword + "{CRYPT}"; //$NON-NLS-1$ //$NON-NLS-2$
        StringBuilder workflowUserValue = new StringBuilder();
        workflowUserValue.append(encryptedPassword).append(",").append(ConfigureConstants.DTGOV_WORKFLOW_USER_GRANTS); //$NON-NLS-1$
        usersProperties.setProperty(ConfigureConstants.DTGOV_WORKFLOW_USER, workflowUserValue.toString());

        InputStream is = this.getClass().getResourceAsStream("/" + ConfigureConstants.DTGOV_PROPERTIES_FILE_NAME); //$NON-NLS-1$
        OutputStream os = null;
        try {
            String aesEncryptedValue=AesEncrypter.encrypt(randomWorkflowUserPassword);
            StringBuilder aesEncrypterBuilder = new StringBuilder();
            aesEncrypterBuilder.append("${crypt:").append(aesEncryptedValue).append("}"); //$NON-NLS-1$ //$NON-NLS-2$
            aesEncryptedValue = aesEncrypterBuilder.toString();
            Properties dtgovProps = new Properties();
            dtgovProps.load(is);
            for (Object key : dtgovProps.keySet()) {
                String value = (String) dtgovProps.get(key);
                if (value.contains(ConfigureConstants.DTGOV_WORKFLOW_PASSWORD)) {
                    dtgovProps.put(key, aesEncryptedValue);
                }
            }
            File dtgovFile = new File(karafConfigPath + ConfigureConstants.DTGOV_PROPERTIES_FILE_NAME);

            os = new FileOutputStream(dtgovFile);
            dtgovProps.store(os, ""); //$NON-NLS-1$
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }

        logger.debug(Messages.getString("configure.command.adding.user.end")); //$NON-NLS-1$

        // Adding to the admin user the dtgov grants:
        String adminUser = (String) usersProperties.get("admin"); //$NON-NLS-1$
        if (!adminUser.contains("dev,qa")) { //$NON-NLS-1$
            adminUser += ",dev,qa,stage,prod,ba,arch"; //$NON-NLS-1$
            usersProperties.setProperty("admin", adminUser); //$NON-NLS-1$
        }

        logger.debug(Messages.getString("configure.command.modify.admin.roles")); //$NON-NLS-1$
        // Storing the users.properties changes
        os = new FileOutputStream(srcFile);
        try {
            usersProperties.store(os, ""); //$NON-NLS-1$
        } finally {
            IOUtils.closeQuietly(os);
        }

        logger.info(Messages.getString("configure.command.end.execution")); //$NON-NLS-1$
        return null;
    }
}
