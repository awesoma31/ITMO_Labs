package com.cryptoterm.backend.command.service;

import com.cryptoterm.backend.command.application.port.out.AsicCommandRepository;
import com.cryptoterm.backend.service.AsicCommandService;
import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.command.domain.asic.AsicConnectionInfo;
import com.cryptoterm.backend.command.domain.asic.AsicRetryPolicy;
import com.cryptoterm.backend.util.HmacSignatureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AsicCommandService scheduling functionality.
 */
@ExtendWith(MockitoExtension.class)
class AsicCommandServiceSchedulingTest {

    @Mock
    private AsicCommandRepository commandRepository;

    @Mock
    private HmacSignatureUtil signatureUtil;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AsicCommandService commandService;

    private AsicHttpProxyCommand createTestCommand() {
        AsicHttpProxyCommand command = new AsicHttpProxyCommand();
        command.setDeviceId(UUID.randomUUID().toString());
        command.setMinerId(UUID.randomUUID().toString());
        
        AsicConnectionInfo asicInfo = new AsicConnectionInfo();
        asicInfo.setPort(80);
        asicInfo.setScheme("http");
        asicInfo.setFirmware("anthill");
        command.setAsic(asicInfo);
        
        command.setSteps(new ArrayList<>());
        command.setPolicy(new AsicRetryPolicy(2, 2000));
        return command;
    }

    @BeforeEach
    void setUp() {
        // Set test values for @Value fields
        ReflectionTestUtils.setField(commandService, "proxySecret", "test-secret");
        ReflectionTestUtils.setField(commandService, "signatureEnabled", false);
        
        reset(commandRepository, signatureUtil, objectMapper);
    }

    @Test
    void testCreateCommand_WithoutScheduling() {
        // Given: Command without scheduledAt
        AsicHttpProxyCommand command = createTestCommand();
        
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenAnswer(invocation -> {
            AsicHttpProxyCommand saved = invocation.getArgument(0);
            saved.setCmdId(UUID.randomUUID().toString());
            return saved;
        });

        // When: Create command
        AsicHttpProxyCommand result = commandService.createCommand(command);

        // Then: Status should be PENDING
        assertNotNull(result);
        assertNotNull(result.getCmdId());
        assertEquals(AsicHttpProxyCommand.CommandStatus.PENDING, result.getStatus());
        assertNull(result.getScheduledAt());
        
        verify(commandRepository, times(1)).save(any(AsicHttpProxyCommand.class));
    }

    @Test
    void testCreateCommand_WithScheduling() {
        // Given: Command with scheduledAt
        AsicHttpProxyCommand command = createTestCommand();
        OffsetDateTime scheduledAt = OffsetDateTime.now().plusHours(2);
        
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenAnswer(invocation -> {
            AsicHttpProxyCommand saved = invocation.getArgument(0);
            saved.setCmdId(UUID.randomUUID().toString());
            return saved;
        });

        // When: Create scheduled command
        AsicHttpProxyCommand result = commandService.createCommand(command, scheduledAt);

        // Then: Status should be SCHEDULED
        assertNotNull(result);
        assertNotNull(result.getCmdId());
        assertEquals(AsicHttpProxyCommand.CommandStatus.SCHEDULED, result.getStatus());
        assertEquals(scheduledAt, result.getScheduledAt());
        
        ArgumentCaptor<AsicHttpProxyCommand> captor = ArgumentCaptor.forClass(AsicHttpProxyCommand.class);
        verify(commandRepository, times(1)).save(captor.capture());
        
        AsicHttpProxyCommand savedCommand = captor.getValue();
        assertEquals(AsicHttpProxyCommand.CommandStatus.SCHEDULED, savedCommand.getStatus());
        assertEquals(scheduledAt, savedCommand.getScheduledAt());
    }

    @Test
    void testCreateCommand_NullScheduledAt() {
        // Given: Command with null scheduledAt
        AsicHttpProxyCommand command = createTestCommand();
        
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenAnswer(invocation -> {
            AsicHttpProxyCommand saved = invocation.getArgument(0);
            saved.setCmdId(UUID.randomUUID().toString());
            return saved;
        });

        // When: Create command with null scheduledAt
        AsicHttpProxyCommand result = commandService.createCommand(command, null);

        // Then: Should behave like immediate command
        assertNotNull(result);
        assertEquals(AsicHttpProxyCommand.CommandStatus.PENDING, result.getStatus());
        assertNull(result.getScheduledAt());
    }

    @Test
    void testGetDueScheduledCommands_NoDueCommands() {
        // Given: No due commands
        when(commandRepository.findByStatusAndScheduledAtLessThanEqual(
            eq(AsicHttpProxyCommand.CommandStatus.SCHEDULED), any(OffsetDateTime.class)
        )).thenReturn(List.of());

        // When: Get due commands
        List<AsicHttpProxyCommand> result = commandService.getDueScheduledCommands();

        // Then: Should return empty list
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(commandRepository, times(1)).findByStatusAndScheduledAtLessThanEqual(
            eq(AsicHttpProxyCommand.CommandStatus.SCHEDULED), any(OffsetDateTime.class)
        );
    }

    @Test
    void testGetDueScheduledCommands_HasDueCommands() {
        // Given: Multiple due commands
        AsicHttpProxyCommand cmd1 = createTestCommand();
        cmd1.setCmdId(UUID.randomUUID().toString());
        cmd1.setStatus(AsicHttpProxyCommand.CommandStatus.SCHEDULED);
        cmd1.setScheduledAt(OffsetDateTime.now().minusMinutes(5));
        
        AsicHttpProxyCommand cmd2 = createTestCommand();
        cmd2.setCmdId(UUID.randomUUID().toString());
        cmd2.setStatus(AsicHttpProxyCommand.CommandStatus.SCHEDULED);
        cmd2.setScheduledAt(OffsetDateTime.now().minusMinutes(10));
        
        when(commandRepository.findByStatusAndScheduledAtLessThanEqual(
            eq(AsicHttpProxyCommand.CommandStatus.SCHEDULED), any(OffsetDateTime.class)
        )).thenReturn(List.of(cmd1, cmd2));

        // When: Get due commands
        List<AsicHttpProxyCommand> result = commandService.getDueScheduledCommands();

        // Then: Should return both commands
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(cmd1));
        assertTrue(result.contains(cmd2));
    }

    @Test
    void testGetDueScheduledCommands_OnlyReturnsScheduledStatus() {
        // Given: Repository returns only SCHEDULED status commands
        OffsetDateTime now = OffsetDateTime.now();
        
        commandService.getDueScheduledCommands();

        // Then: Should query with SCHEDULED status
        verify(commandRepository, times(1)).findByStatusAndScheduledAtLessThanEqual(
            eq(AsicHttpProxyCommand.CommandStatus.SCHEDULED),
            argThat(time -> time.isAfter(now.minusSeconds(5)) && time.isBefore(now.plusSeconds(5)))
        );
    }

    @Test
    void testCancelCommand_ScheduledCommand() {
        // Given: Scheduled command
        String cmdId = UUID.randomUUID().toString();
        AsicHttpProxyCommand command = createTestCommand();
        command.setCmdId(cmdId);
        command.setStatus(AsicHttpProxyCommand.CommandStatus.SCHEDULED);
        command.setScheduledAt(OffsetDateTime.now().plusHours(2));
        
        when(commandRepository.findById(cmdId)).thenReturn(Optional.of(command));
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenReturn(command);

        // When: Cancel command
        Optional<AsicHttpProxyCommand> result = commandService.cancelCommand(cmdId);

        // Then: Command should be cancelled
        assertTrue(result.isPresent());
        assertEquals(AsicHttpProxyCommand.CommandStatus.CANCELLED, result.get().getStatus());
        
        ArgumentCaptor<AsicHttpProxyCommand> captor = ArgumentCaptor.forClass(AsicHttpProxyCommand.class);
        verify(commandRepository, times(1)).save(captor.capture());
        assertEquals(AsicHttpProxyCommand.CommandStatus.CANCELLED, captor.getValue().getStatus());
    }

    @Test
    void testCancelCommand_PendingCommand() {
        // Given: Pending command (not scheduled)
        String cmdId = UUID.randomUUID().toString();
        AsicHttpProxyCommand command = createTestCommand();
        command.setCmdId(cmdId);
        command.setStatus(AsicHttpProxyCommand.CommandStatus.PENDING);
        
        when(commandRepository.findById(cmdId)).thenReturn(Optional.of(command));
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenReturn(command);

        // When: Cancel command
        Optional<AsicHttpProxyCommand> result = commandService.cancelCommand(cmdId);

        // Then: Command should be cancelled
        assertTrue(result.isPresent());
        assertEquals(AsicHttpProxyCommand.CommandStatus.CANCELLED, result.get().getStatus());
    }

    @Test
    void testCancelCommand_CannotCancelExecutingCommand() {
        // Given: Command currently executing
        String cmdId = UUID.randomUUID().toString();
        AsicHttpProxyCommand command = createTestCommand();
        command.setCmdId(cmdId);
        command.setStatus(AsicHttpProxyCommand.CommandStatus.EXECUTING);
        
        when(commandRepository.findById(cmdId)).thenReturn(Optional.of(command));

        // When: Try to cancel
        Optional<AsicHttpProxyCommand> result = commandService.cancelCommand(cmdId);

        // Then: Status should remain EXECUTING
        assertTrue(result.isPresent());
        assertEquals(AsicHttpProxyCommand.CommandStatus.EXECUTING, result.get().getStatus());
        verify(commandRepository, never()).save(any());
    }

    @Test
    void testCancelCommand_CannotCancelSuccessCommand() {
        // Given: Already successful command
        String cmdId = UUID.randomUUID().toString();
        AsicHttpProxyCommand command = createTestCommand();
        command.setCmdId(cmdId);
        command.setStatus(AsicHttpProxyCommand.CommandStatus.SUCCESS);
        
        when(commandRepository.findById(cmdId)).thenReturn(Optional.of(command));

        // When: Try to cancel
        Optional<AsicHttpProxyCommand> result = commandService.cancelCommand(cmdId);

        // Then: Status should remain SUCCESS
        assertTrue(result.isPresent());
        assertEquals(AsicHttpProxyCommand.CommandStatus.SUCCESS, result.get().getStatus());
        verify(commandRepository, never()).save(any());
    }

    @Test
    void testCancelCommand_NotFound() {
        // Given: Non-existent command
        String cmdId = UUID.randomUUID().toString();
        when(commandRepository.findById(cmdId)).thenReturn(Optional.empty());

        // When: Try to cancel
        Optional<AsicHttpProxyCommand> result = commandService.cancelCommand(cmdId);

        // Then: Should return empty
        assertFalse(result.isPresent());
        verify(commandRepository, never()).save(any());
    }

    @Test
    void testCreateCommand_GeneratesCmdId() {
        // Given: Command without cmdId
        AsicHttpProxyCommand command = createTestCommand();
        command.setCmdId(null);
        
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // When: Create command
        AsicHttpProxyCommand result = commandService.createCommand(command);

        // Then: CmdId should be generated
        assertNotNull(result.getCmdId());
        
        ArgumentCaptor<AsicHttpProxyCommand> captor = ArgumentCaptor.forClass(AsicHttpProxyCommand.class);
        verify(commandRepository, times(1)).save(captor.capture());
        assertNotNull(captor.getValue().getCmdId());
    }

    @Test
    void testCreateCommand_PreservesCmdId() {
        // Given: Command with existing cmdId
        AsicHttpProxyCommand command = createTestCommand();
        String existingCmdId = UUID.randomUUID().toString();
        command.setCmdId(existingCmdId);
        
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // When: Create command
        AsicHttpProxyCommand result = commandService.createCommand(command);

        // Then: Should preserve existing cmdId
        assertEquals(existingCmdId, result.getCmdId());
    }

    @Test
    void testCreateCommand_SetsCreatedAt() {
        // Given: Command
        AsicHttpProxyCommand command = createTestCommand();
        OffsetDateTime before = OffsetDateTime.now();
        
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // When: Create command
        AsicHttpProxyCommand result = commandService.createCommand(command);
        OffsetDateTime after = OffsetDateTime.now();

        // Then: CreatedAt should be set to current time
        assertNotNull(result.getCreatedAt());
        assertTrue(result.getCreatedAt().isAfter(before.minusSeconds(1)));
        assertTrue(result.getCreatedAt().isBefore(after.plusSeconds(1)));
    }

    @Test
    void testCreateCommand_ScheduledInDistantFuture() {
        // Given: Command scheduled far in future (1 year)
        AsicHttpProxyCommand command = createTestCommand();
        OffsetDateTime scheduledAt = OffsetDateTime.now().plusYears(1);
        
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenAnswer(invocation -> {
            AsicHttpProxyCommand saved = invocation.getArgument(0);
            saved.setCmdId(UUID.randomUUID().toString());
            return saved;
        });

        // When: Create scheduled command
        AsicHttpProxyCommand result = commandService.createCommand(command, scheduledAt);

        // Then: Should accept future date
        assertNotNull(result);
        assertEquals(AsicHttpProxyCommand.CommandStatus.SCHEDULED, result.getStatus());
        assertEquals(scheduledAt, result.getScheduledAt());
    }

    @Test
    void testCreateCommand_ScheduledInNearFuture() {
        // Given: Command scheduled in near future (1 minute)
        AsicHttpProxyCommand command = createTestCommand();
        OffsetDateTime scheduledAt = OffsetDateTime.now().plusMinutes(1);
        
        when(commandRepository.save(any(AsicHttpProxyCommand.class))).thenAnswer(invocation -> {
            AsicHttpProxyCommand saved = invocation.getArgument(0);
            saved.setCmdId(UUID.randomUUID().toString());
            return saved;
        });

        // When: Create scheduled command
        AsicHttpProxyCommand result = commandService.createCommand(command, scheduledAt);

        // Then: Should accept near future date
        assertNotNull(result);
        assertEquals(AsicHttpProxyCommand.CommandStatus.SCHEDULED, result.getStatus());
        assertEquals(scheduledAt, result.getScheduledAt());
    }
}
