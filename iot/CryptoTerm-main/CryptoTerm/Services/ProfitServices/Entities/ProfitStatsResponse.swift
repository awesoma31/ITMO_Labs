import Foundation

struct ProfitStatsResponse: Decodable {
    let from: Date?
    let to: Date
    let userId: UUID

    let avgHashrateThs: Double
    let avgPowerConsumptionW: Double
    let workedHours: Double

    let btcMined: Double
    let revenueUsd: Double

    let btcPriceUsd: Double
    let usdRubRate: Double
    let difficulty: Double

    enum CodingKeys: String, CodingKey {
        case from
        case to
        case userId = "user_id"
        case avgHashrateThs = "avg_hashrate_ths"
        case avgPowerConsumptionW = "avg_power_consumption_w"
        case workedHours = "worked_hours"
        case btcMined = "btc_mined"
        case revenueUsd = "revenue_usd"
        case btcPriceUsd = "btc_price_usd"
        case usdRubRate = "usd_rub_rate"
        case difficulty
    }
}
