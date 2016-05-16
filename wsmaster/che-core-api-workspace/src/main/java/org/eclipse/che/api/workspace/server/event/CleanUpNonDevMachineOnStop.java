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
package org.eclipse.che.api.workspace.server.event;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.api.machine.server.exception.MachineException;
import org.eclipse.che.api.machine.shared.dto.event.MachineStatusEvent;
import org.eclipse.che.api.workspace.server.WorkspaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import static org.eclipse.che.api.machine.shared.dto.event.MachineStatusEvent.EventType.DESTROYING;

/**
 * The class listens changing of machine status and performs clean up of non-dev machines before its removing.
 *
 * @author Mykola Morhun
 */
public class CleanUpNonDevMachineOnStop implements EventSubscriber<MachineStatusEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(StopWorkspaceOnDestroyDevMachine.class);

    private final WorkspaceManager workspaceManager;
    private final EventService     eventService;

    @Inject
    public CleanUpNonDevMachineOnStop(WorkspaceManager workspaceManager, EventService eventService) {
        this.workspaceManager = workspaceManager;
        this.eventService = eventService;
    }

    @Override
    public void onEvent(MachineStatusEvent event) {
        if (DESTROYING.equals(event.getEventType()) && !event.isDev()) {
            try {
                workspaceManager.removeMachineFromRuntime(event.getMachineId());
            } catch (NotFoundException | MachineException | ConflictException exception) {
                LOG.error(exception.getLocalizedMessage(), exception);
            }
        }
    }

    @PostConstruct
    private void subscribe() {
        eventService.subscribe(this);
    }

    @PreDestroy
    private void unsubscribe() {
        eventService.unsubscribe(this);
    }
}