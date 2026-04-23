package com.cryptoterm.backend.command.infrastructure.scheduler;

import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.mqtt.CommandPublisher;
import com.cryptoterm.backend.service.AsicCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Выполняет команды ASIC, которые были запланированы на отложенное выполнение.
 * Запускается периодически и отправляет готовые к выполнению команды (scheduledAt <= now) через MQTT.
 */
@Component
public class ScheduledCommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(ScheduledCommandExecutor.class);

    private final AsicCommandService commandService;
    private final CommandPublisher commandPublisher;

    public ScheduledCommandExecutor(AsicCommandService commandService, CommandPublisher commandPublisher) {
        this.commandService = commandService;
        this.commandPublisher = commandPublisher;
    }

    @Scheduled(fixedDelayString = "${command.scheduler.interval-ms:60000}")
    public void executeDueScheduledCommands() {
        List<AsicHttpProxyCommand> due = commandService.getDueScheduledCommands();
        if (due.isEmpty()) {
            return;
        }
        log.info("Выполняется {} готовых к выполнению запланированных команд", due.size());
        for (AsicHttpProxyCommand command : due) {
            try {
                boolean sent = commandPublisher.sendAsicProxyCommand(command);
                if (sent) {
                    commandService.updateStatus(command.getCmdId(), AsicHttpProxyCommand.CommandStatus.SENT);
                    log.info("Запланированная команда {} отправлена на устройство {}", command.getCmdId(), command.getDeviceId());
                } else {
                    log.error("Не удалось отправить запланированную команду {} на устройство {}", command.getCmdId(), command.getDeviceId());
                }
            } catch (Exception e) {
                log.error("Ошибка при выполнении запланированной команды {}", command.getCmdId(), e);
            }
        }
    }
}
