package com.sharedsystemshome.dsa.enums;

/**
 * Enum representing various actions a Data Processor may perform
 * when handling data on behalf of a controller or organisation.
 */
public enum DataProcessingActionType {

    ACCESS("Controls access to data or use of data."),
    AGGREGATE("Aggregates multiple datasets to produce summaries."),
    ALTER("Modifies, updates or corrects data."),
    ANALYSE("Analyses data for patterns, insights or profiling."),
    ANONYMISE("Removes identifiers irreversibly."),
    ARCHIVE("Archives data for long-term retention."),
    AUDIT("Reviews how data was processed (e.g., logs, trails)."),
    BACKUP("Creates backups of data for disaster recovery."),
    CLEANSE("Removes irrelevant, outdated, incorrect or duplicate data."),
    COLLECT("Collects personal or sensitive data from individuals or systems."),
    COMBINE("Links data from multiple sources or systems."),
    DECRYPT("Decrypts data to make it readable again."),
    DELETE("Deletes data manually or programmatically."),
    DESTROY("Destroys data irreversibly, often for compliance."),
    ENCRYPT("Encrypts data using cryptographic methods."),
    EXTRACT("Retrieves data from one or more source systems (e.g., databases, APIs, files)."),
    FILTER("Selects or excludes subsets of data based on defined conditions or rules."),
    LOAD("Inserts transformed data into a target system (e.g., database, data lake, CRM)."),
    MASK("Masks parts of the data for confidentiality."),
    MONITOR("Monitors data use, access or flows in real time or over time."),
    ORGANISE("Organises or categorises data."),
    PROFILE("Builds behavioural or preference-based profiles."),
    PSEUDONYMISE("Pseudonymises data while maintaining linkage capability."),
    RECEIVE("Receives data from third parties or external systems."),
    RECORD("Records data for the first time in systems or logs."),
    RESTRICT("Restricts data access or purpose."),
    RESTORE("Restores data from backups."),
    SHARE("Shares data with other internal departments or authorised parties."),
    STORE("Stores data in databases, filesystems or repositories."),
    TRANSFER("Transfers data to another location, organisation or system."),
    TRANSFORM("Converts data into a usable format — includes cleansing, mapping, enriching, normalising, or reformatting."),
    USE("Uses data for analytics, decision-making or service delivery.");


    private final String dataProcessingActionType;

    DataProcessingActionType(String dataProcessingActionType) {
        this.dataProcessingActionType = dataProcessingActionType;
    }

    public String getDataProcessingActionType() {
        return this.dataProcessingActionType;
    }
}

