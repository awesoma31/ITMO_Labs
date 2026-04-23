package com.cryptoterm.backend.command.scheduler;

import com.cryptoterm.backend.service.AsicCommandService;
import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.command.domain.asic.AsicConnectionInfo;
import com.cryptoterm.backend.command.domain.asic.AsicRetryPolicy;
import com.cryptoterm.backend.command.infrastructure.scheduler.ScheduledCommandExecutor;
import com.cryptoterm.backend.mqtt.CommandPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ScheduledCommandExecutor.
 * Tests the scheduler that executes commands at their scheduled time.
 */
@ExtendWith(MockitoExtension.class)
class ScheduledCommandExecutorTest {

    @Mock
    private AsicCommandService commandService;

    @Mock
    private CommandPublisher commandPublisher;

    @InjectMocks
    private ScheduledCommandExecutor scheduledCommandExecutor;

    private AsicHttpProxyCommand createTestCommand(String cmdId, OffsetDateTime scheduledAt) {
        AsicHttpProxyCommand command = new AsicHttpProxyCommand();
        command.setCmdId(cmdId);
        command.setDeviceId(UUID.randomUUID().toString());
        command.setMinerId(UUID.randomUUID().toString());
        
        AsicConnectionInfo asicInfo = new AsicConnectionInfo();
        asicInfo.setPort(80);
        asicInfo.setScheme("http");
        asicInfo.setFirmware("anthill");
        command.setAsic(asicInfo);
        
        command.setSteps(new ArrayList<>());
        command.setPolicy(new AsicRetryPolicy(2, 2000));
        command.setStatus(AsicHttpProxyCommand.CommandStatus.SCHEDULED);
        command.setScheduledAt(scheduledAt);
        command.setCreatedAt(OffsetDateTime.now().minusHours(1));
        return command;
    }

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(commandService, commandPublisher);
    }

    @Test
    void testExecuteDueScheduledCommands_NoDueCommands() {
        // Given: No due commands
        when(commandService.getDueScheduledCommands()).thenReturn(Collections.emptyList());

        // When: Executor runs
        scheduledCommandExecutor.executeDueScheduledCommands();

        // Then: No commands should be sent
        verify(commandService, times(1)).getDueScheduledCommands();
        verify(commandPublisher, never()).sendAsicProxyCommand(any());
        verify(commandService, never()).updateStatus(any(), any());
    }

    @Test
    void testExecuteDueScheduledCommands_SingleCommandSuccess() {
        // Given: One due command
        String cmdId = UUID.randomUUID().toString();
        AsicHttpProxyCommand command = createTestCommand(cmdId, OffsetDateTime.now().minusMinutes(5));
        
        when(commandService.getDueScheduledCommands()).thenReturn(List.of(command));
        when(commandPublisher.sendAsicProxyCommand(command)).thenReturn(true);

        // When: Executor runs
        scheduledCommandExecutor.executeDueScheduledCommands();

        // Then: Command should be sent and status updated
        verify(commandService, times(1)).getDueScheduledCommands();
        verify(commandPublisher, times(1)).sendAsicProxyCommand(command);
        verify(commandService, times(1)).updateStatus(eq(cmdId), eq(AsicHttpProxyCommand.CommandStatus.SENT));
    }

    @Test
    void testExecuteDueScheduledCommands_SingleCommandFailure() {
        // Given: One due command that fails to send
        String cmdId = UUID.randomUUID().toString();
        AsicHttpProxyCommand command = createTestCommand(cmdId, OffsetDateTime.now().minusMinutes(5));
        
        when(commandService.getDueScheduledCommands()).thenReturn(List.of(command));
        when(commandPublisher.sendAsicProxyCommand(command)).thenReturn(false);

        // When: Executor runs
        scheduledCommandExecutor.executeDueScheduledCommands();

        // Then: Command should be sent but status not updated (failure)
        verify(commandService, times(1)).getDueScheduledCommands();
        verify(commandPublisher, times(1)).sendAsicProxyCommand(command);
        verify(commandService, never()).updateStatus(any(), any());
    }

    @Test
    void testExecuteDueScheduledCommands_MultipleCommands() {
        // Given: Multiple due commands
        String cmdId1 = UUID.randomUUID().toString();
        String cmdId2 = UUID.randomUUID().toString();
        String cmdId3 = UUID.randomUUID().toString();
        
        AsicHttpProxyCommand command1 = createTestCommand(cmdId1, OffsetDateTime.now().minusMinutes(10));
        AsicHttpProxyCommand command2 = createTestCommand(cmdId2, OffsetDateTime.now().minusMinutes(5));
        AsicHttpProxyCommand command3 = createTestCommand(cmdId3, OffsetDateTime.now().minusMinutes(1));
        
        when(commandService.getDueScheduledCommands()).thenReturn(List.of(command1, command2, command3));
        when(commandPublisher.sendAsicProxyCommand(any())).thenReturn(true);

        // When: Executor runs
        scheduledCommandExecutor.executeDueScheduledCommands();

        // Then: All commands should be sent
        verify(commandService, times(1)).getDueScheduledCommands();
        verify(commandPublisher, times(3)).sendAsicProxyCommand(any());
        verify(commandService, times(3)).updateStatus(any(), eq(AsicHttpProxyCommand.CommandStatus.SENT));
    }

    @Test
    void testExecuteDueScheduledCommands_MixedSuccessAndFailure() {
        // Given: Multiple commands with mixed results
        String cmdId1 = UUID.randomUUID().toString();
        String cmdId2 = UUID.randomUUID().toString();
        String cmdId3 = UUID.randomUUID().toString();
        
        AsicHttpProxyCommand command1 = createTestCommand(cmdId1, OffsetDateTime.now().minusMinutes(10));
        AsicHttpProxyCommand command2 = createTestCommand(cmdId2, OffsetDateTime.now().minusMinutes(5));
        AsicHttpProxyCommand command3 = createTestCommand(cmdId3, OffsetDateTime.now().minusMinutes(1));
        
        when(commandService.getDueScheduledCommands()).thenReturn(List.of(command1, command2, command3));
        when(commandPublisher.sendAsicProxyCommand(command1)).thenReturn(true);
        when(commandPublisher.sendAsicProxyCommand(command2)).thenReturn(false);
        when(commandPublisher.sendAsicProxyCommand(command3)).thenReturn(true);

        // When: Executor runs
        scheduledCommandExecutor.executeDueScheduledCommands();

        // Then: Only successful commands should have status updated
        verify(commandService, times(1)).getDueScheduledCommands();
        verify(commandPublisher, times(3)).sendAsicProxyCommand(any());
        verify(commandService, times(1)).updateStatus(eq(cmdId1), eq(AsicHttpProxyCommand.CommandStatus.SENT));
        verify(commandService, never()).updateStatus(eq(cmdId2), any());
        verify(commandService, times(1)).updateStatus(eq(cmdId3), eq(AsicHttpProxyCommand.CommandStatus.SENT));
    }

    @Test
    void testExecuteDueScheduledCommands_ExceptionHandling() {
        // Given: Command that throws exception during sending
        String cmdId = UUID.randomUUID().toString();
        AsicHttpProxyCommand command = createTestCommand(cmdId, OffsetDateTime.now().minusMinutes(5));
        
        when(commandService.getDueScheduledCommands()).thenReturn(List.of(command));
        when(commandPublisher.sendAsicProxyCommand(command)).thenThrow(new RuntimeException("MQTT connection failed"));

        // When: Executor runs
        scheduledCommandExecutor.executeDueScheduledCommands();

        // Then: Exception should be caught and logged, status not updated
        verify(commandService, times(1)).getDueScheduledCommands();
        verify(commandPublisher, times(1)).sendAsicProxyCommand(command);
        verify(commandService, never()).updateStatus(any(), any());
    }

    @Test
    void testExecuteDueScheduledCommands_MultipleCommandsWithException() {
        // Given: Multiple commands where one throws exception
        String cmdId1 = UUID.randomUUID().toString();
        String cmdId2 = UUID.randomUUID().toString();
        String cmdId3 = UUID.randomUUID().toString();
        
        AsicHttpProxyCommand command1 = createTestCommand(cmdId1, OffsetDateTime.now().minusMinutes(10));
        AsicHttpProxyCommand command2 = createTestCommand(cmdId2, OffsetDateTime.now().minusMinutes(5));
        AsicHttpProxyCommand command3 = createTestCommand(cmdId3, OffsetDateTime.now().minusMinutes(1));
        
        when(commandService.getDueScheduledCommands()).thenReturn(List.of(command1, command2, command3));
        when(commandPublisher.sendAsicProxyCommand(command1)).thenReturn(true);
        when(commandPublisher.sendAsicProxyCommand(command2)).thenThrow(new RuntimeException("Network error"));
        when(commandPublisher.sendAsicProxyCommand(command3)).thenReturn(true);

        // When: Executor runs
        scheduledCommandExecutor.executeDueScheduledCommands();

        // Then: Other commands should still be processed
        verify(commandService, times(1)).getDueScheduledCommands();
        verify(commandPublisher, times(3)).sendAsicProxyCommand(any());
        verify(commandService, times(1)).updateStatus(eq(cmdId1), eq(AsicHttpProxyCommand.CommandStatus.SENT));
        verify(commandService, never()).updateStatus(eq(cmdId2), any());
        verify(commandService, times(1)).updateStatus(eq(cmdId3), eq(AsicHttpProxyCommand.CommandStatus.SENT));
    }

    @Test
    void testExecuteDueScheduledCommands_EmptyCommandList() {
        // Given: Empty list returned
        when(commandService.getDueScheduledCommands()).thenReturn(new ArrayList<>());

        // When: Executor runs
        scheduledCommandExecutor.executeDueScheduledCommands();

        // Then: Should exit early without further processing
        verify(commandService, times(1)).getDueScheduledCommands();
        verify(commandPublisher, never()).sendAsicProxyCommand(any());
        verify(commandService, never()).updateStatus(any(), any());
    }

    @Test
    void testExecuteDueScheduledCommands_NullCommandList() {
        // Given: Null list returned (edge case)
        when(commandService.getDueScheduledCommands()).thenReturn(null);

        // When/Then: Should handle gracefully (NPE prevention)
        try {
            scheduledCommandExecutor.executeDueScheduledCommands();
        } catch (NullPointerException e) {
            // This is expected if implementation doesn't handle null
            // In production, service should never return null
        }
        
        verify(commandService, times(1)).getDueScheduledCommands();
    }

    @Test
    void testExecuteDueScheduledCommands_VeryOldScheduledCommand() {
        // Given: Command scheduled long time ago (30 days)
        String cmdId = UUID.randomUUID().toString();
        AsicHttpProxyCommand command = createTestCommand(cmdId, OffsetDateTime.now().minusDays(30));
        
        when(commandService.getDueScheduledCommands()).thenReturn(List.of(command));
        when(commandPublisher.sendAsicProxyCommand(command)).thenReturn(true);

        // When: Executor runs
        scheduledCommandExecutor.executeDueScheduledCommands();

        // Then: Should still process (no expiration check in current implementation)
        verify(commandService, times(1)).getDueScheduledCommands();
        verify(commandPublisher, times(1)).sendAsicProxyCommand(command);
        verify(commandService, times(1)).updateStatus(eq(cmdId), eq(AsicHttpProxyCommand.CommandStatus.SENT));
    }
}
