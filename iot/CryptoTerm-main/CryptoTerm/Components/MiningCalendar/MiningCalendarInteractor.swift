import Foundation

protocol MiningCalendarInteractorInput: AnyObject {
    func cheduleRegimeChange(
        minerIds: [String],
        dates: [HardwareAccordionCell.Mode: Set<Date>],
        completion: @escaping () -> Void
    )

    func loadPlannedCalendar(rpId: String, minerId: String)
}

typealias MiningCalendarInteractorOutput = MiningCalendarPresenterInput

final class MiningCalendarInteractor {

    // MARK: Public properties

    var presenter: MiningCalendarInteractorOutput?

    // MARK: Private properties

    private let msk = TimeZone(identifier: "Europe/Moscow")!

    private let deviceService: DeviceServiceProtocol
    private let authService: AuthServiceProtocol
    private let commandService: CommandServiceProtocol
    private let store: AuthSessionStoring

    // MARK: Init

    init(
        deviceService: DeviceServiceProtocol,
        authService: AuthServiceProtocol,
        commandService: CommandServiceProtocol,
        store: AuthSessionStoring
    ) {
        self.deviceService = deviceService
        self.authService = authService
        self.commandService = commandService
        self.store = store
    }
}

// MARK: - Time helpers (MSK)

private extension MiningCalendarInteractor {

    func mskCalendar() -> Calendar {
        var cal = Calendar(identifier: .gregorian)
        cal.timeZone = msk
        return cal
    }

    func dayMSK(_ date: Date) -> Date {
        mskCalendar().startOfDay(for: date)
    }

    func tomorrowStartMSK() -> Date {
        let cal = mskCalendar()
        let todayStart = cal.startOfDay(for: Date())
        return cal.date(byAdding: .day, value: 1, to: todayStart)!
    }

    func isStartOfDayMSK(_ date: Date) -> Bool {
        let cal = mskCalendar()
        let comps = cal.dateComponents([.hour, .minute, .second], from: date)
        return (comps.hour == 0 && comps.minute == 0 && comps.second == 0)
    }

    func endOfDayMskMinus30s(from startOfDay: Date) -> Date {
        var cal = Calendar(identifier: .gregorian)
        cal.timeZone = msk
        let nextDayStart = cal.date(byAdding: .day, value: 1, to: startOfDay)!
        return cal.date(byAdding: .second, value: -30, to: nextDayStart)!
    }
}

// MARK: - Mapping helpers

private extension MiningCalendarInteractor {

    func modeFromPowerMode(_ s: String?) -> HardwareAccordionCell.Mode? {
        guard let s else { return nil }
        switch s {
        case "ECO":
            return .economy
        case "STANDARD":
            return .standart
        case "OVERCLOCK":
            return .maximal
        default:
            return nil
        }
    }

    func buildServerState(
        history: [AsicCommandResponse]
    ) -> (modeByDay: [Date: HardwareAccordionCell.Mode], cmdIdsByDay: [Date: [UUID]]) {

        var modeByDay: [Date: HardwareAccordionCell.Mode] = [:]
        var cmdIdsByDay: [Date: [UUID]] = [:]

        let minDay = tomorrowStartMSK()

        for item in history {
            guard let at = item.scheduledAt else { continue }

            let day = dayMSK(at)
            if day < minDay { continue }

            cmdIdsByDay[day, default: []].append(item.cmdId)

            guard isStartOfDayMSK(at) else { continue }

            if let mode = modeFromPowerMode(item.powerMode) {
                modeByDay[day] = mode
            }
        }

        return (modeByDay, cmdIdsByDay)
    }

    func desiredModeByDay(_ dates: [HardwareAccordionCell.Mode: Set<Date>]) -> [Date: HardwareAccordionCell.Mode] {
        var map: [Date: HardwareAccordionCell.Mode] = [:]
        let minDay = tomorrowStartMSK()

        for (mode, set) in dates {
            for d in set {
                let day = dayMSK(d)
                if day < minDay { continue }
                map[day] = mode
            }
        }
        return map
    }
}

// MARK: - Network helpers

private extension MiningCalendarInteractor {

    func deleteCommandsBatch(
        accessToken: String,
        ids: [UUID],
        completion: @escaping (Bool) -> Void
    ) {
        guard !ids.isEmpty else { completion(true); return }

        let unique = Array(Set(ids))

        let group = DispatchGroup()
        var hasFailure = false
        let lock = DispatchQueue(label: "com.app.deleteCommands.lock")

        for id in unique {
            group.enter()
            commandService.deleteCommand(accessToken: accessToken, cmdId: id.uuidString.lowercased()) { result in
                if case .failure(let e) = result {
                    print(e.localizedDescription)
                    lock.async { hasFailure = true }
                }
                group.leave()
            }
        }

        group.notify(queue: .main) {
            lock.sync { }
            completion(!hasFailure)
        }
    }

    func scheduleDatesBatch(
        accessToken: String,
        datesToAddByMiner: [String: [HardwareAccordionCell.Mode: Set<Date>]],
        completion: @escaping (Bool) -> Void
    ) {
        let hasAny = datesToAddByMiner.values.contains { dict in
            dict.values.contains { !$0.isEmpty }
        }
        guard hasAny else { completion(true); return }

        let group = DispatchGroup()
        var hasFailure = false
        let lock = DispatchQueue(label: "com.app.schedule.lock")

        func markFailure() { lock.async { hasFailure = true } }

        func scheduleOne(
            minerId: String,
            mode: HardwareAccordionCell.Mode,
            at date: Date,
            alsoPlanEndOfDayTo standard: Bool
        ) {
            let correctMode: String
            switch mode {
            case .economy:
                correctMode = "ECO"
            case .standart:
                correctMode = "STANDARD"
            case .maximal:
                correctMode = "OVERCLOCK"
            }
            group.enter()
            deviceService.scheduleRegimeChange(
                accessToken: accessToken,
                minerId: minerId,
                mode: correctMode,
                scheduledAt: date
            ) { [weak self] result in
                guard let self else { group.leave(); return }

                switch result {
                case .success:
                    if standard {
                        group.enter()
                        let end = self.endOfDayMskMinus30s(from: date)
                        self.deviceService.scheduleRegimeChange(
                            accessToken: accessToken,
                            minerId: minerId,
                            mode: "STANDARD",
                            scheduledAt: end
                        ) { r in
                            if case .failure(let e) = r {
                                print(e.localizedDescription)
                                markFailure()
                            }
                            group.leave()
                        }
                    }

                case .failure(let e):
                    print(e.localizedDescription)
                    markFailure()
                }

                group.leave()
            }
        }

        for (minerId, dict) in datesToAddByMiner {
            for day in dict[.economy] ?? [] {
                scheduleOne(minerId: minerId, mode: .economy, at: day, alsoPlanEndOfDayTo: true)
            }
            for day in dict[.standart] ?? [] {
                scheduleOne(minerId: minerId, mode: .standart, at: day, alsoPlanEndOfDayTo: false)
            }
            for day in dict[.maximal] ?? [] {
                scheduleOne(minerId: minerId, mode: .maximal, at: day, alsoPlanEndOfDayTo: true)
            }
        }

        group.notify(queue: .main) {
            lock.sync { }
            completion(!hasFailure)
        }
    }
}

// MARK: - MiningCalendarInteractorInput

extension MiningCalendarInteractor: MiningCalendarInteractorInput {

    func cheduleRegimeChange(
        minerIds: [String],
        dates: [HardwareAccordionCell.Mode: Set<Date>],
        completion: @escaping () -> Void
    ) {
        guard let session = store.readSession() else {
            store.clear()
            presenter?.showStartScreen()
            completion()
            return
        }

        guard !minerIds.isEmpty else {
            presenter?.successCheduleRegimeChange(success: true)
            completion()
            return
        }

        let desired = desiredModeByDay(dates)
        let minDay = tomorrowStartMSK()

        authService.refresh(refreshToken: session.refreshToken) { [weak self] result in
            guard let self else { return }

            switch result {
            case .success(let auth):
                self.store.save(auth)

                let group = DispatchGroup()
                let lock = DispatchQueue(label: "com.app.calendar.state.lock")

                var perMinerState: [String: (modeByDay: [Date: HardwareAccordionCell.Mode], cmdIdsByDay: [Date: [UUID]])] = [:]
                var hasHistoryFailure = false

                for minerId in minerIds {
                    group.enter()
                    self.commandService.getCommandsHistory(
                        accessToken: auth.accessToken,
                        minerId: minerId,
                        commandStatus: .scheduled
                    ) { [weak self] histRes in
                        defer { group.leave() }
                        guard let self else { return }

                        switch histRes {
                        case .success(let history):
                            let state = self.buildServerState(history: history)
                            lock.async { perMinerState[minerId] = state }

                        case .failure(let e):
                            print(e.localizedDescription)
                            lock.async { hasHistoryFailure = true }
                        }
                    }
                }

                group.notify(queue: .main) {
                    lock.sync { }

                    if hasHistoryFailure {
                        self.presenter?.successCheduleRegimeChange(success: false)
                        completion()
                        return
                    }

                    var idsToDelete: [UUID] = []
                    var datesToAddByMiner: [String: [HardwareAccordionCell.Mode: Set<Date>]] = [:]

                    for minerId in minerIds {
                        let state = perMinerState[minerId] ?? ([:], [:])

                        let allDays = Set(state.cmdIdsByDay.keys).union(desired.keys)

                        var toAdd: [HardwareAccordionCell.Mode: Set<Date>] = [
                            .economy: [],
                            .standart: [],
                            .maximal: []
                        ]

                        for day in allDays {
                            if day < minDay { continue }

                            let desiredMode = desired[day]
                            let currentMode = state.modeByDay[day]
                            let existingIds = state.cmdIdsByDay[day] ?? []
                            let hasExisting = !existingIds.isEmpty

                            if hasExisting, (desiredMode == nil || desiredMode != currentMode) {
                                idsToDelete.append(contentsOf: existingIds)
                            }

                            if let dm = desiredMode {
                                if !hasExisting || dm != currentMode {
                                    toAdd[dm, default: []].insert(day)
                                }
                            }
                        }

                        datesToAddByMiner[minerId] = toAdd
                    }

                    self.deleteCommandsBatch(accessToken: auth.accessToken, ids: idsToDelete) { okDelete in
                        guard okDelete else {
                            self.presenter?.successCheduleRegimeChange(success: false)
                            completion()
                            return
                        }

                        self.scheduleDatesBatch(
                            accessToken: auth.accessToken,
                            datesToAddByMiner: datesToAddByMiner
                        ) { okPlan in
                            let success = okPlan
                            self.presenter?.successCheduleRegimeChange(success: success)
                            completion()
                        }
                    }
                }

            case .failure(let e):
                print(e.localizedDescription)
                self.store.clear()
                self.presenter?.showStartScreen()
                completion()
            }
        }
    }

    func loadPlannedCalendar(rpId: String, minerId: String) {
        guard let session = store.readSession() else {
            store.clear()
            presenter?.showStartScreen()
            return
        }

        authService.refresh(refreshToken: session.refreshToken) { [weak self] result in
            guard let self else { return }

            switch result {
            case .failure(let e):
                print(e.localizedDescription)
                self.store.clear()
                self.presenter?.showStartScreen()

            case .success(let auth):
                self.store.save(auth)

                self.commandService.getCommandsHistory(
                    accessToken: auth.accessToken,
                    minerId: minerId,
                    commandStatus: .scheduled
                ) { [weak self] histRes in
                    guard let self else { return }

                    switch histRes {
                    case .failure(let e):
                        print(e.localizedDescription)
                        self.presenter?.applyPlannedModeDates(
                            [.economy: [], .standart: [], .maximal: []],
                            forDeviceId: rpId
                        )

                    case .success(let history):
                        let server = self.buildServerState(history: history)

                        var modeDates: [HardwareAccordionCell.Mode: Set<Date>] = [
                            .economy: [],
                            .standart: [],
                            .maximal: []
                        ]

                        for (day, mode) in server.modeByDay {
                            modeDates[mode, default: []].insert(day)
                        }

                        self.presenter?.applyPlannedModeDates(modeDates, forDeviceId: rpId)
                    }
                }
            }
        }
    }
}
