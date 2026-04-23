import UIKit

enum TopToastStyle {
    case success
    case error

    var backgroundColor: UIColor {
        switch self {
        case .success:
            return UIColor(
                red: Constants.successBackgroundRed,
                green: Constants.successBackgroundGreen,
                blue: Constants.successBackgroundBlue,
                alpha: Constants.backgroundAlpha
            )
        case .error:
            return UIColor(
                red: Constants.errorBackgroundRed,
                green: Constants.errorBackgroundGreen,
                blue: Constants.errorBackgroundBlue,
                alpha: Constants.backgroundAlpha
            )
        }
    }

    var icon: UIImage? {
        let config = UIImage.SymbolConfiguration(
            pointSize: Constants.iconPointSize,
            weight: Constants.iconWeight
        )
        switch self {
        case .success:
            return UIImage(systemName: Constants.successIconName, withConfiguration: config)
        case .error:
            return UIImage(systemName: Constants.errorIconName, withConfiguration: config)
        }
    }
}

// MARK: - Constants

private extension TopToastStyle {
    enum Constants {
        static let backgroundAlpha: CGFloat = 1

        static let successBackgroundRed: CGFloat = 0
        static let successBackgroundGreen: CGFloat = 0.522
        static let successBackgroundBlue: CGFloat = 0.052

        static let errorBackgroundRed: CGFloat = 0.78
        static let errorBackgroundGreen: CGFloat = 0.12
        static let errorBackgroundBlue: CGFloat = 0.0

        static let iconPointSize: CGFloat = 18
        static let iconWeight: UIImage.SymbolWeight = .bold

        static let successIconName: String = "checkmark.circle.fill"
        static let errorIconName: String = "xmark.octagon.fill"
    }
}

