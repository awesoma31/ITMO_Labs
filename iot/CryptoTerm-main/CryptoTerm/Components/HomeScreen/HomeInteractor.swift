import Alamofire
import Dispatch
import Foundation

protocol HomeInteractorInput: AnyObject {
    func getDevices(completion: (() -> Void)?)
    func getPoints(timeRange: TimeRange, currentMetric: Metrics, deviceId: String)
    func getUsername()
    func changeMode(minerIds: [String], mode: String, completion: @escaping () -> Void)
    func calculateProfit(price: Double, period: Period, completion: @escaping () -> Void)
    func logout()
}

typealias HomeInteractorOutput = HomePresenterInput

class HomeInteractor {

    // MARK: Private properties

    private let store: AuthSessionStoring
    private let authService: AuthServiceProtocol
    private let metricService: MetricServiceProtocol
    private let devicesService: DeviceServiceProtocol
    private let profitService: ProfitServiceProtocol

    // MARK: Init

    init(
        store: AuthSessionStoring,
        authService: AuthServiceProtocol,
        metricService: MetricServiceProtocol,
        devicesService: DeviceServiceProtocol,
        profitService: ProfitServiceProtocol
    ) {
        self.store = store
        self.authService = authService
        self.metricService = metricService
        self.devicesService = devicesService
        self.profitService = profitService
    }

    // MARK: Public properties

    var presenter: HomeInteractorOutput?

    // MARK: Private methods

    private func handleForbiddenError(refreshToken: String, action: @escaping () -> Void) {
        authService.refresh(refreshToken: refreshToken) { [weak self] result in
            switch result {
            case .success(let response):
                self?.store.save(response)
                action()
            case .failure(_):
                self?.store.clear()
                self?.presenter?.showStartScreen()
            }
        }
    }

    private func fetchMetricsForDevices(devices: [DeviceResponse], accessToken: String, completion: (() -> Void)? = nil) {
        let moscowTimeZone = TimeZone(identifier: "Europe/Moscow") ?? TimeZone.current
        var calendar = Calendar.current
        calendar.timeZone = moscowTimeZone
        let currentDate = Date()
        let fromDate = calendar.date(byAdding: .year, value: -20, to: currentDate)

        let group = DispatchGroup()
        var deviceDtos: [DeviceDto] = []

        for device in devices {
            group.enter()

            let metricsGroup = DispatchGroup()
            var thr: Double?
            var kw: Double?
            var temp: Double?

            metricsGroup.enter()
            metricService.fetchHashrate(
                accessToken: accessToken,
                deviceId: device.id,
                from: fromDate,
                to: nil,
                bucket: "1 hour"
            ) { result in
                switch result {
                case .success(let points):
                    thr = points.last?.value
                case .failure(let error):
                    print("Error fetching hashrate for device \(device.id): \(error)")
                }
                metricsGroup.leave()
            }

            metricsGroup.enter()
            metricService.fetchPowerConsumption(
                accessToken: accessToken,
                deviceId: device.id,
                from: fromDate,
                to: nil,
                bucket: "1 hour"
            ) { result in
                switch result {
                case .success(let points):
                    kw = points.last?.value
                case .failure(let error):
                    print("Error fetching power for device \(device.id): \(error)")
                }
                metricsGroup.leave()
            }

            metricsGroup.enter()
            metricService.fetchTemperature(
                accessToken: accessToken,
                deviceId: device.id,
                from: fromDate,
                to: nil,
                bucket: "1 hour"
            ) { result in
                switch result {
                case .success(let points):
                    temp = points.last?.value
                case .failure(let error):
                    print("Error fetching temperature for device \(device.id): \(error)")
                }
                metricsGroup.leave()
            }

            metricsGroup.notify(queue: .global()) {
                let deviceDto = DeviceDto(
                    deviceId: device.id,
                    thr: thr ?? 0.0,
                    kw: kw ?? 0.0,
                    temp: temp ?? 0.0,
                    asicIds: device.minerIds
                )

                deviceDtos.append(deviceDto)

                group.leave()
            }
        }

        group.notify(queue: .main) {
            self.presenter?.configureDevices(devices: deviceDtos)
            completion?()
        }
    }
}

// MARK: - HomeInteractorInput

extension HomeInteractor: HomeInteractorInput {
    func getDevices(completion: (() -> Void)? = nil) {
        guard let session = store.readSession() else {
            store.clear()
            presenter?.showStartScreen()
            return
        }

        authService.refresh(refreshToken: session.refreshToken) { [weak self] result in
            guard let self else { return }

            switch result {
            case .success(let response):
                self.store.save(response)
                self.devicesService.getUserDevices(
                    accessToken: response.accessToken,
                    userId: response.userId
                ) { res in
                    switch res {
                    case .success(let devices):
                        self.fetchMetricsForDevices(devices: devices, accessToken: session.accessToken, completion: completion)
                    case .failure(let error):
                        print(error.localizedDescription)
                    }
                }
            case .failure(let error):
                print(error.localizedDescription)
                self.store.clear()
                self.presenter?.showStartScreen()
            }
        }
    }

    func getPoints(timeRange: TimeRange, currentMetric: Metrics, deviceId: String) {
        guard let session = store.readSession() else {
            presenter?.showStartScreen()
            return
        }

        let moscowTimeZone = TimeZone(identifier: "Europe/Moscow") ?? TimeZone.current
        var calendar = Calendar.current
        calendar.timeZone = moscowTimeZone

        let currentDate = Date()

        var fromDate: Date?
        var bucket: String

        switch timeRange {
        case .day:
            fromDate = calendar.date(byAdding: .hour, value: -23, to: currentDate)
            bucket = "1 hour"
        case .week:
            fromDate = calendar.date(byAdding: .day, value: -6, to: currentDate)
            bucket = "12 hours"
        case .month:
            fromDate = calendar.date(byAdding: .day, value: -30, to: currentDate)
            bucket = "1 day"
        case .year:
            fromDate = calendar.date(byAdding: .year, value: -1, to: currentDate)
            bucket = "1 month"
        }

        let completion: (Result<[MetricPoint], Error>) -> Void = { [weak self] result in
            guard let self = self else { return }

            switch result {
            case .success(let points):
                self.presenter?.configurePoints(points: points, deviceId: deviceId)
            case .failure(let error):
                if let afError = error as? AFError,
                   let statusCode = afError.responseCode,
                   statusCode == 403 || statusCode == 401 {
                    self.handleForbiddenError(refreshToken: session.refreshToken, action: { self.getPoints(timeRange: timeRange, currentMetric: currentMetric, deviceId: deviceId) })
                } else {
                    print(error.localizedDescription)
                }
            }
        }

        switch currentMetric {
        case .temperature:
            metricService.fetchTemperature(
                accessToken: session.accessToken,
                deviceId: deviceId,
                from: fromDate,
                to: nil,
                bucket: bucket,
                completion: completion
            )
        case .hashrate:
            metricService.fetchHashrate(
                accessToken: session.accessToken,
                deviceId: deviceId,
                from: fromDate,
                to: nil,
                bucket: bucket,
                completion: completion
            )
        case .consumption:
            metricService.fetchPowerConsumption(
                accessToken: session.accessToken,
                deviceId: deviceId,
                from: fromDate,
                to: nil,
                bucket: bucket,
                completion: completion
            )
        }
    }

    func getUsername() {
        let session = store.readSession()
        if let session {
            presenter?.configureUsername(username: session.username)
        } else {
            store.clear()
            presenter?.showStartScreen()
        }
    }

    func changeMode(
        minerIds: [String],
        mode: String,
        completion: @escaping () -> Void
    ) {
        guard let session = store.readSession() else {
            store.clear()
            presenter?.showStartScreen()
            completion()
            return
        }

        guard !minerIds.isEmpty else {
            presenter?.successChangeMode(isSuccess: true)
            completion()
            return
        }

        authService.refresh(refreshToken: session.refreshToken) { [weak self] result in
            guard let self else { return }

            switch result {
            case .success(let response):
                self.store.save(response)

                let group = DispatchGroup()
                var hasFailure = false
                let stateQueue = DispatchQueue(label: "com.app.changeMode.stateQueue")

                for minerId in minerIds {
                    group.enter()

                    self.devicesService.changeDeviceMode(
                        accessToken: session.accessToken,
                        minerId: minerId,
                        mode: mode
                    ) { result in
                        if case .failure(let error) = result {
                            print(error.localizedDescription)
                            stateQueue.async {
                                hasFailure = true
                            }
                        }
                        group.leave()
                    }
                }

                group.notify(queue: .main) {
                    stateQueue.sync { }
                    let isSuccess = !hasFailure

                    self.presenter?.successChangeMode(isSuccess: isSuccess)
                    completion()
                }

            case .failure(_):
                self.store.clear()
                self.presenter?.showStartScreen()
                completion()
            }
        }
    }

    func calculateProfit(
        price: Double,
        period: Period,
        completion: @escaping () -> Void
    ) {
        guard let session = store.readSession() else {
            store.clear()
            presenter?.showStartScreen()
            return
        }

        let calendar = Calendar.current

        let currentDate = Date()

        var fromDate: Date?

        switch period {
        case .hour:
            fromDate = calendar.date(byAdding: .hour, value: -1, to: currentDate)
        case .day:
            fromDate = calendar.date(byAdding: .day, value: -1, to: currentDate)
        case .week:
            fromDate = calendar.date(byAdding: .day, value: -7, to: currentDate)
        case .month:
            fromDate = calendar.date(byAdding: .month, value: -1, to: currentDate)
        case .year:
            fromDate = calendar.date(byAdding: .year, value: -1, to: currentDate)
        }

        profitService.calculateProfit(accessToken: session.accessToken, userId: session.userId, from: fromDate, to: nil) { [weak self] result in
            guard let self else { return }

            switch result {
            case .success(let response):
                self.presenter?.showProfit(response: response, price: price)
                completion()
            case .failure(let error):
                if let afError = error as? AFError,
                   let statusCode = afError.responseCode,
                   statusCode == 403 || statusCode == 401 {
                    self.handleForbiddenError(refreshToken: session.refreshToken) { self.calculateProfit(price: price, period: period, completion: completion) }
                } else {
                    print(error.localizedDescription)
                }
            }
        }
    }

    func logout() {
        guard let session = store.readSession() else {
            store.clear()
            presenter?.showStartScreen()
            return
        }

        authService.logout(refreshToken: session.refreshToken) { [weak self] result in
            guard let self else { return }

            if case .failure(let error) = result {
                print(error.localizedDescription)
            }

            self.store.clear()
            self.presenter?.showStartScreen()
        }
    }
}
