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
package org.eclipse.che.api.workspace.server.env.spi;

import org.eclipse.che.api.core.model.machine.Machine;
import org.eclipse.che.api.core.model.workspace.Environment;

import java.util.List;

/**
 * author Alexander Garagatyi
 */
public interface EnvironmentEngine {
    String getType();

    // todo consider removal of env type from start method
    // todo snapshot?
    List<Machine> start(String workspaceId, Environment env);

    void stop(String workspaceId);

//    void startMachine();
}
