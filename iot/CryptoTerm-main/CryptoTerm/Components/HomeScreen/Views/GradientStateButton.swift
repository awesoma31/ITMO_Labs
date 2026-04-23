import UIKit

final class GradientStateButton: UIButton {


    // MARK: Public properties

    var borderColor: UIColor? {
        didSet { updateAppearance() }
    }

    var isGradientOn: Bool = false {
        didSet {
            updateAppearance()
        }
    }

    var selectedFillStyle: SelectedFillStyle = .gradient {
        didSet { updateAppearance() }
    }

    // MARK: Private properties

    private let gradientLayer = CAGradientLayer()

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }

    // MARK: Lifecycle

    override func layoutSubviews() {
        super.layoutSubviews()
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        gradientLayer.frame = bounds
        CATransaction.commit()
        if let titleLabel { bringSubviewToFront(titleLabel) }
        if let imageView { bringSubviewToFront(imageView) }
    }

    // MARK: Private methods

    private func commonInit() {
        translatesAutoresizingMaskIntoConstraints = false
        layer.cornerRadius = Constants.cornerRadius
        layer.masksToBounds = true
        layer.borderWidth = Constants.borderWidth
        layer.borderColor = borderColor?.cgColor ?? Constants.defaultBorderColor.cgColor
        titleLabel?.font = UIFont(name: Constants.latoBoldFontName, size: Constants.titleFontSize)
        setTitleColor(Constants.normalTitleColor, for: .normal)
        backgroundColor = Constants.normalBackgroundColor
        gradientLayer.colors = [
            Constants.gradientColor1.cgColor,
            Constants.gradientColor2.cgColor,
            Constants.gradientColor3.cgColor
        ]
        gradientLayer.locations = Constants.gradientLocations
        gradientLayer.startPoint = Constants.gradientStartPoint
        gradientLayer.endPoint = Constants.gradientEndPoint
        gradientLayer.cornerRadius = Constants.cornerRadius
        gradientLayer.actions = Constants.gradientActions
        layer.insertSublayer(gradientLayer, at: 0)
        updateAppearance()
    }

    private func updateAppearance() {
        if isGradientOn {
            setTitleColor(.white, for: .normal)
            layer.borderColor = UIColor.clear.cgColor

            switch selectedFillStyle {
            case .gradient:
                backgroundColor = .clear
                gradientLayer.isHidden = false

            case .solid(let c):
                gradientLayer.isHidden = true
                backgroundColor = c
            }

        } else {
            backgroundColor = Constants.normalBackgroundColor
            setTitleColor(Constants.normalTitleColor, for: .normal)
            layer.borderColor = (borderColor ?? Constants.defaultBorderColor).cgColor
            gradientLayer.isHidden = true
        }

        titleLabel?.font = UIFont(name: Constants.latoBoldFontName, size: Constants.titleFontSize)
        setNeedsLayout()
    }
}

// MARK: - SelectedFillStyle

extension GradientStateButton {
    enum SelectedFillStyle {
        case gradient
        case solid(UIColor)
    }
}

// MARK: - Constants

private extension GradientStateButton {
    enum Constants {
        static let cornerRadius: CGFloat = 13
        static let borderWidth: CGFloat = 1

        static let latoBoldFontName = "Lato-Bold"
        static let titleFontSize: CGFloat = 12

        static let normalBackgroundColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)
        static let normalTitleColor = UIColor(red: 0.502, green: 0.502, blue: 0.502, alpha: 1)

        static let defaultBorderColor = UIColor(red: 0.376, green: 0.376, blue: 0.376, alpha: 1)

        static let gradientColor1 = UIColor(red: 0.678, green: 0.706, blue: 0.953, alpha: 1)
        static let gradientColor2 = UIColor(red: 0.412, green: 0.463, blue: 0.922, alpha: 1)
        static let gradientColor3 = UIColor(red: 0.232, green: 0.261, blue: 0.521, alpha: 1)

        static let gradientLocations: [NSNumber] = [0, 0.5, 1]
        static let gradientStartPoint = CGPoint(x: 1, y: 0.5)
        static let gradientEndPoint = CGPoint(x: 0, y: 0.5)

        static let gradientActions: [String: CAAction] = [
            "bounds": NSNull(), "position": NSNull(), "frame": NSNull(),
            "colors": NSNull(), "locations": NSNull(), "contents": NSNull()
        ]
    }
}

