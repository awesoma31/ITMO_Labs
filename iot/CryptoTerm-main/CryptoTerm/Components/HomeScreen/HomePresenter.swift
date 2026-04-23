import UIKit

protocol HomePresenterInput: AnyObject {
    func configureUsername(username: String)
    func configureDevices(devices: [DeviceDto])
    func configurePoints(points: [MetricPoint], deviceId: String)
    func showStartScreen()
    func successChangeMode(isSuccess: Bool)
    func showProfit(response: ProfitStatsResponse, price: Double)
}

typealias HomePresenterOutput = HomeViewControllerInput

class HomePresenter {

    // MARK: Public properties

    weak var viewController: HomePresenterOutput?

    // MARK: Private properties

    private var names: [String] = {
        var names = [String]()
        var num = 99
        while(num >= 10) {
            names.append("P\(num)")
            num -= 1
        }
        return names
    }()

    private let accentColors: [UIColor] = {
        let fixed: [UIColor] = [
            UIColor(red: 0.17, green: 0.211, blue: 0.583, alpha: 1),
            UIColor(red: 0.411, green: 0.462, blue: 0.921, alpha: 1),
            UIColor(red: 0.678, green: 0.706, blue: 0.953, alpha: 1),

            UIColor(red: 0.12, green: 0.15, blue: 0.45, alpha: 1),
            UIColor(red: 0.25, green: 0.29, blue: 0.65, alpha: 1),
            UIColor(red: 0.33, green: 0.38, blue: 0.72, alpha: 1),
            UIColor(red: 0.47, green: 0.51, blue: 0.85, alpha: 1),
            UIColor(red: 0.55, green: 0.58, blue: 0.89, alpha: 1),
            UIColor(red: 0.62, green: 0.65, blue: 0.92, alpha: 1)
        ]

        var colors = fixed

        let hueStart: CGFloat = 210.0 / 360.0
        let hueEnd: CGFloat = 276.0 / 360.0
        let hueSteps = 12

        let saturations: [CGFloat] = [0.80, 0.72]
        let brightnesses: [CGFloat] = [0.72, 0.64, 0.56]

        for i in 0..<hueSteps {
            let t = CGFloat(i) / CGFloat(max(hueSteps - 1, 1))
            let hue = hueStart + (hueEnd - hueStart) * t

            for s in saturations {
                for b in brightnesses {
                    colors.append(UIColor(hue: hue, saturation: s, brightness: b, alpha: 1))
                }
            }
        }

        return colors
    }()

    // MARK: Private methods

    private func parseRowDevice(response: [DeviceDto]) -> [DeviceRow] {
        var firstIndex = 0
        var colorIndex = 0
        var rowDevices = [DeviceRow]()
        for device in response {
            let rowDevice = DeviceRow(
                deviceId: device.deviceId,
                name: names[firstIndex],
                thr: device.thr,
                kw: device.kw,
                temp: device.temp,
                statusColor: device.thr == 0 ? UIColor(red: 0.878, green: 0.149, blue: 0, alpha: 1) : UIColor(red: 0, green: 0.522, blue: 0.052, alpha: 1),
                accentColor: accentColors[colorIndex],
                asicIds: device.asicIds
            )
            rowDevices.append(rowDevice)
            firstIndex == (names.count-1) ? (firstIndex = 0) : (firstIndex += 1)
            colorIndex == (accentColors.count-1) ? (colorIndex = 0) : (colorIndex += 1)
        }
        return rowDevices
    }
}

// MARK: - HomePresenterInput

extension HomePresenter: HomePresenterInput {
    func showStartScreen() {
        viewController?.showStartScreen()
    }

    func configureUsername(username: String) {
        viewController?.configureUsername(username: username)
    }

    func configureDevices(devices: [DeviceDto]) {
        let rowDevices = parseRowDevice(response: devices)
        viewController?.configureDevices(devices: rowDevices)
    }

    func configurePoints(points: [MetricPoint], deviceId: String) {
        viewController?.configurePoints(points: points, deviceId: deviceId)
    }

    func successChangeMode(isSuccess: Bool) {
        viewController?.successChangeMode(isSuccess: isSuccess)
    }

    func showProfit(response: ProfitStatsResponse, price: Double) {
        let btcPriceUSD = response.btcPriceUsd
        let revenueUSD = response.revenueUsd
        let btcMined = response.btcMined
        let avgPowerConsumptionW = response.avgPowerConsumptionW
        let workedHours = response.workedHours
        let usdRubRate = response.usdRubRate

        // Формула: (BTC добытые × цена BTC) - ((потребление в кВт × цена за кВт·ч × часы работы) / курс)
        let btcRevenue = btcMined * btcPriceUSD
        let powerConsumptionKW = avgPowerConsumptionW / 1000.0
        let electricityCost = powerConsumptionKW * price * workedHours
        let electricityCostUSD = usdRubRate != 0 ? electricityCost / usdRubRate : 0
        let profitWithExpenses = btcRevenue - electricityCostUSD

        viewController?.showProft(
            exchangeValue: btcPriceUSD,
            withoutValue: revenueUSD,
            withValue: profitWithExpenses
        )
    }
}
