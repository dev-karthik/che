/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.workspace.server.env.impl.che;

import org.eclipse.che.api.core.model.machine.MachineConfig;
import org.eclipse.che.api.core.model.workspace.Environment;
import org.eclipse.che.api.workspace.server.env.spi.EnvironmentValidator;

import java.util.List;

/**
 * author Alexander Garagatyi
 */
public class CheEnvironmentValidator implements EnvironmentValidator {
    @Override
    public void validate(Environment env) {

    }

    // todo change to new entity that contains network
    public List<MachineConfig> parse(Environment env) {
        return null;
    }
}
