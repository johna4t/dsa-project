package com.sharedsystemshome.dsa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "SharedDataContent")
@Table(name = "SHARED_DATA_CONTENT")
public class SharedDataContent {

    @EmbeddedId
    private SharedDataContentId id;

    @ManyToOne
    @MapsId("dfId")
    private DataFlow dataFlow;

    @ManyToOne
    @MapsId("dcdId")
    private DataContentDefinition dataContentDefinition;

    public SharedDataContent() {}

    // Constructor without prebuilding ID
    public SharedDataContent(DataFlow dataFlow, DataContentDefinition dataContentDefinition) {
        this.dataFlow = dataFlow;
        this.dataContentDefinition = dataContentDefinition;
        this.id = new SharedDataContentId(this.dataFlow.getId(), this.dataContentDefinition.getId());
        // Defer ID construction to a separate method after both IDs are guaranteed
    }

//    public void buildId() {
//        if (dataFlow == null || dataFlow.getId() == null ||
//                dataContentDefinition == null || dataContentDefinition.getId() == null) {
//            throw new IllegalStateException("Both DataFlow and DataContentDefinition must be managed before building ID");
//        }
//        this.id = new SharedDataContentId(dataFlow.getId(), dataContentDefinition.getId());
//    }
//
    public void setId(DataFlow dataflow, DataContentDefinition dcd){

        if(null != dataflow){
            this.id.setDfId(dataFlow.getId());
        }

        if(null != dcd){
            this.id.setDcdId(dcd.getId());
        }
    }
}
