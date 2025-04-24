package com.sharedsystemshome.dsa.service;

import com.sharedsystemshome.dsa.model.DataContentDefinition;
import com.sharedsystemshome.dsa.model.DataSharingParty;
import com.sharedsystemshome.dsa.repository.DataSharingPartyRepository;
import com.sharedsystemshome.dsa.util.BusinessValidationException;
import com.sharedsystemshome.dsa.util.CustomValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataSharingPartyServiceTest {

    @Mock
    private DataSharingPartyRepository dspMockRepo;

    @Mock
    private CustomValidator<DataSharingParty> validator;

    private DataSharingPartyService dspService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.dspService = new DataSharingPartyService(
                this.dspMockRepo,
                this.validator);
    }

    @Test
    public void testCreateDataSharingParty_ValidDsp() {
        Long dspId = 1L;
        DataSharingParty dsp = DataSharingParty.builder()
                .build();

        DataSharingParty savedDsp = DataSharingParty.builder()
                .id(dspId)
                .build();

        when(this.dspMockRepo.save(dsp)).thenReturn(savedDsp);

        Long result = this.dspService.createDataSharingParty(dsp);

        assertNotNull(result);
        assertEquals(dspId, result);
        verify(this.dspMockRepo, times(1)).save(dsp);
    }

    @Test
    public void testCreateDataSharingParty_DspIsNull() {

        DataSharingParty dsp = null;

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dspService.createDataSharingParty(dsp));

        assertEquals("Data Sharing Party is null or empty.", e.getMessage());

        verify(this.dspMockRepo, times(0)).save(dsp);

    }

    @Test
    public void testCreateDataSharingParty_SaveThrowsException() {

        DataSharingParty dsp = DataSharingParty.builder()
                .build();

//        when(this.dspMockRepo.save(dsp)).thenThrow(IllegalArgumentException.class);
        when(this.dspMockRepo.save(dsp)).thenThrow(IllegalArgumentException.class);

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dspService.createDataSharingParty(dsp));

        assertEquals("Unable to add or update Data Sharing Party.", e.getMessage());

        verify(this.dspMockRepo, times(1)).save(dsp);

    }

    @Test
    public void testGetDataSharingPartyById_ValidId() {
        Long id = 1L;
        DataSharingParty dsp = DataSharingParty.builder()
                .id(id)
                .build();

        when(this.dspMockRepo.existsById(id)).thenReturn(true);
        when(this.dspMockRepo.findById(id)).thenReturn(Optional.of(dsp));

        DataSharingParty result = this.dspService.getDataSharingPartyById(id);

        assertNotNull(result);
        assertEquals(dsp, result);
        verify(this.dspMockRepo, times(1)).existsById(id);
        verify(this.dspMockRepo, times(1)).findById(id);
    }

    @Test
    public void testGetDataSharingPartyById_InvalidId() {
        Long id = 1L;
        when(this.dspMockRepo.existsById(id)).thenReturn(false);

        assertThrows(BusinessValidationException.class,
                () -> this.dspService.getDataSharingPartyById(id));
        verify(this.dspMockRepo, times(1)).existsById(id);
    }

    @Test
    public void testGetDataSharingParties() {
        List<DataSharingParty> dsps = new ArrayList<>();
        when(dspMockRepo.findAll()).thenReturn(dsps);

        List<DataSharingParty> result = dspService.getDataSharingParties();

        assertNotNull(result);
        assertEquals(dsps, result);
        verify(dspMockRepo, times(1)).findAll();
    }

    @Test
    public void testUpdateDataSharingParty_ValidValues() {

        Long id = 1L;
        DataSharingParty dsp = DataSharingParty.builder()
                .id(id)
                .description("Old Description")
                .build();

        String newDesc = "New Description";

        DataSharingParty updateDsp = DataSharingParty.builder()
                .id(id)
                .description(newDesc)
                .build();

        when(this.dspMockRepo.findById(id)).thenReturn(Optional.of(dsp));

        this.dspService.updateDataSharingParty(updateDsp);

        verify(this.dspMockRepo, times(1)).findById(id);
        verify(this.dspMockRepo, times(0)).save(dsp);

        assertEquals(newDesc, dsp.getDescription());
    }

    @Test
    public void testUpdateDataSharingParty_InvalidId(){

        Long id = 1L;
        DataSharingParty updatedDsp = DataSharingParty.builder()
                .id(id)
                .build();

        when(this.dspMockRepo.findById(id)).thenReturn(Optional.empty());

        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dspService.updateDataSharingParty(updatedDsp));

        assertEquals("Data Sharing Party with id = " + id + " not found.", e.getMessage());

        verify(this.dspMockRepo, times(1)).findById(id);

    }

    @Test
    public void testUpdateDataSharingParty_InvalidValues() {
        Long id = 1L;
        String oldName ="Old Name";
        String oldDesc = "Old Description";
        String oldUrl = "oldurl.com";

        DataSharingParty dsp = DataSharingParty.builder()
                .id(id)
                .description(oldDesc)
                .build();

        DataSharingParty updatedDsp = DataSharingParty.builder()
                .id(id)
                .description("") // zero length string
                .build();

        when(this.dspMockRepo.findById(id)).thenReturn(Optional.of(dsp));

        this.dspService.updateDataSharingParty(updatedDsp);

        verify(this.dspMockRepo, times(1)).findById(id);

        assertEquals(oldDesc, dsp.getDescription());
    }

    @Test
    public void testDeleteDataSharingParty_Exists() {
        Long id = 1L;

        when(this.dspMockRepo.existsById(id)).thenReturn(true);

        this.dspService.deleteDataSharingParty(id);

        verify(this.dspMockRepo, times(1)).existsById(id);
        verify(this.dspMockRepo, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteDataSharingParty_NotExists() {
        Long id = 1L;

        when(this.dspMockRepo.existsById(id)).thenReturn(false);

        assertThrows(BusinessValidationException.class,
                () -> this.dspService.deleteDataSharingParty(id));

        verify(this.dspMockRepo, times(1)).existsById(id);
        verify(this.dspMockRepo, times(0)).deleteById(id);
    }

    @Test
    void testDeleteDataContentDefinition() {

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();
        when(this.dspMockRepo.findById(provId)).thenReturn(Optional.of(prov));

        Long dcdId = 4L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                /**
                 * DCD passed to method includes an id (which it would not contain in practice),
                 * however, saved Data Flow is created by DCD repo
                 */
                .id(dcdId)
                .provider(prov)
                .build();

        assertEquals(1, prov.getProviderDcds().size());

        // Then
        this.dspService.deleteDataContentDefinition(provId, dcdId);
        assertEquals(0, prov.getProviderDcds().size());
        verify(this.dspMockRepo, times(1)).save(prov);

    }

    @Test
    void testDeleteDataContentDefinition_WithInvalidDcdId() {

        Long provId = 1L;
        DataSharingParty prov = DataSharingParty.builder()
                .id(provId)
                .build();
        when(this.dspMockRepo.findById(provId)).thenReturn(Optional.of(prov));

        Long dcdId = 4L;
        DataContentDefinition dcd = DataContentDefinition.builder()
                /**
                 * DCD passed to method includes an id (which it would not contain in practice),
                 * however, saved Data Flow is created by DCD repo
                 */
                .id(dcdId)
                .provider(prov)
                .build();

        assertEquals(1, prov.getProviderDcds().size());

        // Then
        Long invalidDcdId = 5L;
        Exception e = assertThrows(BusinessValidationException.class,
                () -> this.dspService.deleteDataContentDefinition(provId, invalidDcdId));
        assertEquals("Data Content Definition with id = " + invalidDcdId + " not found.", e.getMessage());
        assertEquals(1, prov.getProviderDcds().size());
        verify(this.dspMockRepo, times(0)).save(prov);

    }

}
