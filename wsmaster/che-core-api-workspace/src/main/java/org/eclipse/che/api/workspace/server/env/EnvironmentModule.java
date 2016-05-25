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
package org.eclipse.che.api.workspace.server.env;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * author Alexander Garagatyi
 */
public class EnvironmentModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<EnvironmentManager> envManagers = Multibinder.newSetBinder(binder(), EnvironmentManager.class);
    }
}