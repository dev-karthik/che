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
package org.eclipse.che.api.project.server;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.project.server.handlers.CreateProjectHandler;
import org.eclipse.che.api.project.server.handlers.ProjectHandlerRegistry;
import org.eclipse.che.api.project.server.type.AttributeValue;
import org.eclipse.che.api.project.server.type.ProjectTypeDef;
import org.eclipse.che.api.project.server.type.ProjectTypeRegistry;
import org.eclipse.che.api.project.server.type.ValueProvider;
import org.eclipse.che.api.project.server.type.ValueProviderFactory;
import org.eclipse.che.api.vfs.impl.file.DefaultFileWatcherNotificationHandler;
import org.eclipse.che.api.vfs.impl.file.FileTreeWatcher;
import org.eclipse.che.api.vfs.impl.file.FileWatcherNotificationHandler;
import org.eclipse.che.api.vfs.impl.file.LocalVirtualFileSystemProvider;
import org.eclipse.che.api.vfs.search.impl.FSLuceneSearcherProvider;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.api.workspace.shared.dto.UsersWorkspaceDto;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.dto.server.DtoFactory;

import java.io.File;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gazarenkov
 */
public class WsAgentTestBase {

    protected final static String FS_PATH = "target/fs";
    protected final static String INDEX_PATH = "target/fs_index";

    protected TestWorkspaceHolder workspaceHolder;

    protected File root;

    protected ProjectManager pm;

    protected LocalVirtualFileSystemProvider vfsProvider;

    protected EventService eventService;

    protected ProjectRegistry projectRegistry;

    protected FileWatcherNotificationHandler fileWatcherNotificationHandler;

    protected FileTreeWatcher fileTreeWatcher;

    protected ProjectTypeRegistry projectTypeRegistry;

    protected ProjectHandlerRegistry projectHandlerRegistry;

    public void setUp() throws Exception {

        root = new File(FS_PATH);

        if (root.exists()) {
            IoUtil.deleteRecursive(root);
        }
        root.mkdir();

        File indexDir = new File(INDEX_PATH);

        if (indexDir.exists()) {
            IoUtil.deleteRecursive(indexDir);
        }
        indexDir.mkdir();

        Set<PathMatcher> filters = new HashSet<>();
        filters.add(path -> true);
        FSLuceneSearcherProvider sProvider = new FSLuceneSearcherProvider(indexDir, filters);

        vfsProvider = new LocalVirtualFileSystemProvider(root, sProvider);

        workspaceHolder = new TestWorkspaceHolder();
        projectTypeRegistry = new ProjectTypeRegistry(new HashSet<>());
        projectTypeRegistry.registerProjectType(new PT1());

        projectHandlerRegistry = new ProjectHandlerRegistry(new HashSet<>());

        this.eventService = new EventService();

        this.projectRegistry = new ProjectRegistry(workspaceHolder, vfsProvider, projectTypeRegistry);

        fileWatcherNotificationHandler = new DefaultFileWatcherNotificationHandler(vfsProvider);
        fileTreeWatcher = new FileTreeWatcher(root, new HashSet<>(), fileWatcherNotificationHandler);

        pm = new ProjectManager(vfsProvider, eventService, projectTypeRegistry, projectHandlerRegistry,
                                null, projectRegistry, fileWatcherNotificationHandler, fileTreeWatcher);
    }


    protected static class TestWorkspaceHolder extends WorkspaceHolder {

        //ArrayList <RegisteredProject> updatedProjects = new ArrayList<>();

        protected TestWorkspaceHolder() throws ServerException {
            super(DtoFactory.newDto(UsersWorkspaceDto.class).
                    withId("id").withName("name"));
        }


        protected TestWorkspaceHolder(List<ProjectConfigDto> projects) throws ServerException {
            super(DtoFactory.newDto(UsersWorkspaceDto.class).
                    withId("id").withName("name")
                            .withProjects(projects));
        }

        @Override
        public void updateProjects(Collection<RegisteredProject> projects) throws ServerException {

            workspace.setProjects(new ArrayList<>(projects));
            //setProjects(new ArrayList<>(projects));
        }
    }

    protected static class PT1 extends ProjectTypeDef {

        protected PT1() {
            super("primary1", "primary1", true, false);

            addVariableDefinition("var1", "", false);
            addConstantDefinition("const1", "", "my constant");


        }
    }


    protected static class PT2 extends ProjectTypeDef {

        protected PT2() {
            super("pt2", "pt2", true, false);

            addVariableDefinition("pt2-var1", "", false);
            addVariableDefinition("pt2-var2", "", true);
            addConstantDefinition("pt2-const1", "", "my constant");

        }
    }

    protected static class M2 extends ProjectTypeDef {

        protected M2() {
            super("m2", "m2", false, true);

//            addVariableDefinition("pt2-var1", "", false);
//            addVariableDefinition("pt2-var2", "", true);
            addConstantDefinition("pt2-const1", "", "my constant");

        }
    }

    protected static class PT3 extends ProjectTypeDef {

        protected PT3() {
            super("pt3", "pt3", true, false);

            addVariableDefinition("pt2-var1", "", false);
            addVariableDefinition("pt2-var2", "", true);
            addConstantDefinition("pt2-const1", "", "my constant");
            addVariableDefinition("pt2-provided1", "", true, new F());

        }

        protected class F implements ValueProviderFactory {

            FolderEntry project;

            @Override
            public ValueProvider newInstance(final FolderEntry projectFolder) {

                return new ValueProvider() {

                    @Override
                    public List<String> getValues(String attributeName) throws ValueStorageException {

                        List<String> values = new ArrayList<>();

                        VirtualFileEntry file1;
                        try {
                            file1 = projectFolder.getChild("/file1");
                        } catch (Exception e) {
                            throw new ValueStorageException(e.getMessage());
                        }

                        if(file1 != null)
                            values.add(attributeName);

                        return values;

                    }
                };
            }
        }

        protected static class SrcGenerator implements CreateProjectHandler {

            @Override
            public void onCreateProject(FolderEntry baseFolder, Map<String, AttributeValue> attributes, Map<String, String> options)
                    throws ForbiddenException, ConflictException, ServerException {

                baseFolder.createFolder("file1");

            }

            @Override
            public String getProjectType() {
                return "pt3";
            }
        }

    }

    protected static class PT4NoGen extends ProjectTypeDef {

        protected PT4NoGen() {
            super("pt4", "pt4", true, false);

            addVariableDefinition("pt4-provided1", "", true, new F4());

        }

        protected class F4 implements ValueProviderFactory {

            @Override
            public ValueProvider newInstance(final FolderEntry projectFolder) {

                return new ValueProvider() {

                    @Override
                    public List<String> getValues(String attributeName) throws ValueStorageException {

                        List<String> values = new ArrayList<>();

                        VirtualFileEntry file1;
                        try {
                            file1 = projectFolder.getChild("/file1");
                        } catch (Exception e) {
                            throw new ValueStorageException(e.getMessage());
                        }

                        if(file1 != null)
                            values.add(attributeName);

                        return values;

                    }
                };
            }
        }

    }

}