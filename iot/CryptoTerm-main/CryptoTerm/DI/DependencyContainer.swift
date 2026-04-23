final class DependencyContainer {
    lazy var authServicce: AuthServiceProtocol = AuthService()
    lazy var authSessionStore: AuthSessionStoring = AuthSessionStore()
    lazy var metricService: MetricServiceProtocol = MetricService()
    lazy var deviceService: DeviceServiceProtocol = DeviceService()
    lazy var profitService: ProfitServiceProtocol = ProfitService()
    lazy var commandService: CommandServiceProtocol = CommandService()

    // MARK: Auth
    func makeAuthInteractor() -> AuthInteractor {
        AuthInteractor(store: authSessionStore, authService: authServicce)
    }
    
    // MARK: Home
    func makeHomeInteractor() -> HomeInteractor {
        HomeInteractor(
            store: authSessionStore,
            authService: authServicce,
            metricService: metricService,
            devicesService: deviceService,
            profitService: profitService
        )
    }

    // MARK: Calendar
    func makeMiningCalendarInteractor() -> MiningCalendarInteractor {
        MiningCalendarInteractor(
            deviceService: deviceService,
            authService: authServicce,
            commandService: commandService,
            store: authSessionStore
        )
    }
}
