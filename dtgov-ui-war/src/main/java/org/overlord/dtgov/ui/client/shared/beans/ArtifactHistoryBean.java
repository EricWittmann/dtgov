/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.dtgov.ui.client.shared.beans;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * The bean returned when the history of an artifact is requested.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class ArtifactHistoryBean {

    private String artifactUuid;
    private String artifactName;
    private String artifactType;
    private String artifactVersion;
    private List<HistoryEventSummaryBean> events = new ArrayList<HistoryEventSummaryBean>();

    /**
     * Constructor.
     */
    public ArtifactHistoryBean() {
    }

    /**
     * @return the artifactUuid
     */
    public String getArtifactUuid() {
        return artifactUuid;
    }

    /**
     * @return the artifactName
     */
    public String getArtifactName() {
        return artifactName;
    }

    /**
     * @return the artifactVersion
     */
    public String getArtifactVersion() {
        return artifactVersion;
    }

    /**
     * @return the events
     */
    public List<HistoryEventSummaryBean> getEvents() {
        return events;
    }

    /**
     * @param artifactUuid the artifactUuid to set
     */
    public void setArtifactUuid(String artifactUuid) {
        this.artifactUuid = artifactUuid;
    }

    /**
     * @param artifactName the artifactName to set
     */
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    /**
     * @param artifactVersion the artifactVersion to set
     */
    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    /**
     * @param events the events to set
     */
    public void setEvents(List<HistoryEventSummaryBean> events) {
        this.events = events;
    }

    /**
     * @return the artifactType
     */
    public String getArtifactType() {
        return artifactType;
    }

    /**
     * @param artifactType the artifactType to set
     */
    public void setArtifactType(String artifactType) {
        this.artifactType = artifactType;
    }
}
