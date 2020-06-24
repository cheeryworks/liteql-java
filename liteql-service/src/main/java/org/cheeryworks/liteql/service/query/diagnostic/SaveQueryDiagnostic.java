package org.cheeryworks.liteql.service.query.diagnostic;

import java.io.Serializable;

public class SaveQueryDiagnostic implements Serializable {

    private long transformingSaveQueryDuration;

    private long auditingEntitiesDuration;

    private long linkingReferencesDuration;

    private long beforeSaveEventProcessingDuration;

    private long prePersistDuration;

    private long persistDuration;

    private long persistCount;

    private long afterSaveEventProcessingDuration;


    public long getTransformingSaveQueryDuration() {
        return transformingSaveQueryDuration;
    }

    public void setTransformingSaveQueryDuration(long transformingSaveQueryDuration) {
        this.transformingSaveQueryDuration = transformingSaveQueryDuration;
    }

    public long getAuditingEntitiesDuration() {
        return auditingEntitiesDuration;
    }

    public void setAuditingEntitiesDuration(long auditingEntitiesDuration) {
        this.auditingEntitiesDuration = auditingEntitiesDuration;
    }

    public long getLinkingReferencesDuration() {
        return linkingReferencesDuration;
    }

    public void setLinkingReferencesDuration(long linkingReferencesDuration) {
        this.linkingReferencesDuration = linkingReferencesDuration;
    }

    public long getBeforeSaveEventProcessingDuration() {
        return beforeSaveEventProcessingDuration;
    }

    public void setBeforeSaveEventProcessingDuration(long beforeSaveEventProcessingDuration) {
        this.beforeSaveEventProcessingDuration = beforeSaveEventProcessingDuration;
    }

    public long getPrePersistDuration() {
        return prePersistDuration;
    }

    public void setPrePersistDuration(long prePersistDuration) {
        this.prePersistDuration = prePersistDuration;
    }

    public long getPersistDuration() {
        return persistDuration;
    }

    public void setPersistDuration(long persistDuration) {
        this.persistDuration = persistDuration;
    }

    public long getPersistCount() {
        return persistCount;
    }

    public void setPersistCount(long persistCount) {
        this.persistCount = persistCount;
    }

    public long getAfterSaveEventProcessingDuration() {
        return afterSaveEventProcessingDuration;
    }

    public void setAfterSaveEventProcessingDuration(long afterSaveEventProcessingDuration) {
        this.afterSaveEventProcessingDuration = afterSaveEventProcessingDuration;
    }

    public void add(SaveQueryDiagnostic saveQueryDiagnostic) {
        this.transformingSaveQueryDuration += saveQueryDiagnostic.transformingSaveQueryDuration;
        this.auditingEntitiesDuration += saveQueryDiagnostic.auditingEntitiesDuration;
        this.linkingReferencesDuration += saveQueryDiagnostic.linkingReferencesDuration;
        this.beforeSaveEventProcessingDuration += saveQueryDiagnostic.beforeSaveEventProcessingDuration;
        this.prePersistDuration += saveQueryDiagnostic.prePersistDuration;
        this.persistDuration += saveQueryDiagnostic.persistDuration;
        this.persistCount += saveQueryDiagnostic.persistCount;
        this.afterSaveEventProcessingDuration += saveQueryDiagnostic.afterSaveEventProcessingDuration;
    }

    public long getTotalDuration() {
        return this.transformingSaveQueryDuration
                + this.auditingEntitiesDuration
                + this.linkingReferencesDuration
                + this.beforeSaveEventProcessingDuration
                + this.prePersistDuration
                + this.persistDuration
                + this.afterSaveEventProcessingDuration;
    }

    @Override
    public String toString() {
        return "SaveQueryDiagnostic{"
                + "transformingSaveQueryDuration=" + transformingSaveQueryDuration
                + ", auditingEntitiesDuration=" + auditingEntitiesDuration
                + ", linkingReferencesDuration=" + linkingReferencesDuration
                + ", beforeSaveEventProcessingDuration=" + beforeSaveEventProcessingDuration
                + ", prePersistDuration=" + prePersistDuration
                + ", persistDuration=" + persistDuration
                + ", persistCount=" + persistCount
                + ", afterSaveEventProcessingDuration=" + afterSaveEventProcessingDuration
                + '}';
    }
}
