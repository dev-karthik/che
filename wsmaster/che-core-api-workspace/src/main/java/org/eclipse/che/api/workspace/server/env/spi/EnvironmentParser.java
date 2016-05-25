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

import com.google.inject.ImplementedBy;

import org.eclipse.che.api.core.model.workspace.Environment;
import org.eclipse.che.api.workspace.server.env.impl.che.CheEnvironmentParser;

/**
 * author Alexander Garagatyi
 */
@ImplementedBy(CheEnvironmentParser.class)
public interface EnvironmentParser {
    void validate(Environment env);
}
