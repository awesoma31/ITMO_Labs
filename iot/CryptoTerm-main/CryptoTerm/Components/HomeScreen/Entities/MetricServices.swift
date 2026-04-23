import UIKit

struct Points: Identifiable, Equatable {
    let id: String
    let name: String
    let color: UIColor
    var points: [MetricPoint]
}
