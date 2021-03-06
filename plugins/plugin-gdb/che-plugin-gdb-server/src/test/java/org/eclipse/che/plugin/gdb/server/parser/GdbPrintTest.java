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
package org.eclipse.che.plugin.gdb.server.parser;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Anatoliy Bazko
 */
public class GdbPrintTest {

    @Test
    public void testParse() throws Exception {
        GdbOutput gdbOutput = GdbOutput.of("$9 = 0\n");

        GdbPrint gdbPrint = GdbPrint.parse(gdbOutput);

        assertEquals(gdbPrint.getValue(), "0");
    }

    @Test(expectedExceptions = GdbParseException.class)
    public void testParseFail() throws Exception {
        GdbOutput gdbOutput = GdbOutput.of("some text");
        GdbPrint.parse(gdbOutput);
    }
}
