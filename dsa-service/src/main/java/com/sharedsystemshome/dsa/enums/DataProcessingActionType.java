package com.sharedsystemshome.dsa.enums;

/**
 * Enum representing various actions a Data Processor may perform
 * when handling data on behalf of a controller or organisation.
 */
public enum DataProcessingActionType {

    ACCESS("Controls access to data or use of data."),
    AGGREGATION("Aggregates multiple datasets to produce summaries or combined statistics."),
    ANALYSIS("Analyses data for patterns, insights, or profiling."),
    ANONYMISATION("Removes identifiers from data irreversibly."),
    ARCHIVING("Archives data for long-term retention."),
    AUDIT("Reviews how data was processed, including examining logs or audit trails."),
    BACKUP("Creates backups of data for disaster recovery or redundancy."),
    CLEANSING("Removes irrelevant, outdated, incorrect, or duplicate data."),
    COLLECTION("Collects personal or sensitive data from individuals or systems."),
    COMPOSITION("Combines or assembles data elements into new datasets."),
    CONVERSION("Converts data from one format or standard to another."),
    CREATION("Creates new data records or datasets."),
    CURATION("Selects, organises, and maintains data for accuracy and usefulness."),
    DEDUPLICATION("Identifies and removes duplicate records from datasets."),
    DECRYPTION("Decrypts data to make it readable again."),
    DELETION("Deletes data manually or programmatically."),
    DESTRUCTION("Destroys data irreversibly, often for compliance purposes."),
    DISPOSAL("Disposes of physical or digital data assets securely."),
    DISTRIBUTION("Distributes or delivers data to intended recipients."),
    EDITING("Modifies or updates data content."),
    ENCRYPTION("Encrypts data using cryptographic methods."),
    ENRICHMENT("Enhances data by adding new or supplementary information."),
    EXPORT("Sends or delivers data to an external system, location, or organisation."),
    EXTRACTION("Retrieves data from one or more source systems (e.g., databases, APIs, files)."),
    FILTERING("Selects or excludes subsets of data based on defined conditions or rules."),
    IMPORT("Brings or loads data from an external system, location, or organisation."),
    INGESTION("Acquires and imports data into a system or platform."),
    INSPECTION("Examines data to identify content, structure, or issues."),
    LINKING("Associates related data from multiple sources."),
    LOADING("Inserts transformed or raw data into a target system."),
    MANAGEMENT("Oversees the handling, storage, and usage of data."),
    MASKING("Hides or obscures parts of the data to protect confidentiality."),
    MONITORING("Observes and records data usage, access, or flow over time."),
    ORGANISATION("Groups data into logical structures or meaningful categories."),
    POSTPROCESSING("Performs additional data operations after the primary processing step."),
    PREPROCESSING("Prepares data for analysis or further processing."),
    PROFILING("Builds behavioural or preference-based profiles."),
    PRODUCTION(""),
    PROTECTION("Implements measures to safeguard data from unauthorised access, alteration, or loss."),
    PSEUDONYMISATION("Replaces identifiers with pseudonyms while maintaining linkage capability."),
    PUBLICATION("Makes data available to the public or specific audiences."),
    RECEPTION("Accepts or ingests data from third parties or external systems."),
    RECORDING("Captures and stores data for the first time."),
    REDACTION("Removes sensitive information from data before disclosure."),
    REPLICATION("Copies data to other systems or locations for backup or availability."),
    RESTRICTION("Limits the access, use, or distribution of data."),
    RESTORATION("Restores data from backups or archives."),
    REVIEW(""),
    SHARING("Provides data to other internal or authorised external parties."),
    STAGING("Holds data in a temporary location for processing or migration."),
    STANDARDISATION("Applies consistent formats or structures to data."),
    STORAGE("Stores data in databases, filesystems, or repositories."),
    SUMMARISATION("Produces condensed versions of data containing only key details."),
    TAGGING("Labels or marks data for classification or tracking."),
    TRANSFER("Moves data to another location, organisation, or system."),
    TRANSFORMATION("Changes data into a different structure, format, or representation."),
    USE("Utilises data for analytics, decision-making, or service delivery."),
    VALIDATION("Checks data for correctness, completeness, and compliance."),
    VERIFICATION("Confirms data accuracy and consistency with its source."),
    VIEWING("Views data without altering it.");

    private final String dataProcessingActionType;

    DataProcessingActionType(String dataProcessingActionType) {
        this.dataProcessingActionType = dataProcessingActionType;
    }

    public String getDataProcessingActionType() {
        return this.dataProcessingActionType;
    }
}

