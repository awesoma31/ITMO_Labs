import UIKit

final class GradientButton: UIControl {

    // MARK: Private methods

    private let gradientLayer: CAGradientLayer = {
        let gradientLayer = CAGradientLayer()
        gradientLayer.colors = [
            Constants.gradientColor1.cgColor,
            Constants.gradientColor2.cgColor,
            Constants.gradientColor3.cgColor
        ]
        gradientLayer.locations = Constants.gradientLocations
        gradientLayer.startPoint = Constants.gradientStartPoint
        gradientLayer.endPoint = Constants.gradientEndPoint
        gradientLayer.cornerRadius = Constants.cornerRadius
        return gradientLayer
    }()

    private let iconImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.contentMode = .scaleAspectFit
        return imageView
    }()

    private let titleLabel: UILabel = {
        let label = UILabel()
        label.textColor = .white
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.titleFontSize)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()

    // MARK: Init

    init(iconName: String, title: String) {
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        setupUI(iconName: iconName, title: title)
    }

    required init?(coder: NSCoder) {
        fatalError(Constants.fatalInitMessage)
    }

    // MARK: Private methods

    private func setupUI(iconName: String, title: String) {
        layer.insertSublayer(gradientLayer, at: 0)

        layer.cornerRadius = Constants.cornerRadius
        layer.shadowColor = Constants.shadowColor.cgColor
        layer.shadowOpacity = Constants.shadowOpacity
        layer.shadowRadius = Constants.shadowRadius
        layer.shadowOffset = Constants.shadowOffset

        iconImageView.image = UIImage(named: iconName)
        addSubview(iconImageView)

        titleLabel.text = title
        addSubview(titleLabel)

        NSLayoutConstraint.activate([
            iconImageView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: Constants.iconLeading),
            iconImageView.centerYAnchor.constraint(equalTo: centerYAnchor),

            titleLabel.leadingAnchor.constraint(equalTo: iconImageView.trailingAnchor, constant: Constants.titleLeading),
            titleLabel.centerYAnchor.constraint(equalTo: centerYAnchor)
        ])
    }

    // MARK: Lifecycle

    override func layoutSubviews() {
        super.layoutSubviews()
        gradientLayer.frame = bounds
    }
}

// MARK: - Constants

private extension GradientButton {
    enum Constants {
        static let gradientColor1 = UIColor(red: 0.678, green: 0.706, blue: 0.953, alpha: 1)
        static let gradientColor2 = UIColor(red: 0.412, green: 0.463, blue: 0.922, alpha: 1)
        static let gradientColor3 = UIColor(red: 0.232, green: 0.261, blue: 0.521, alpha: 1)

        static let gradientLocations: [NSNumber] = [0, 0.5, 1]
        static let gradientStartPoint = CGPoint(x: 1, y: 0.5)
        static let gradientEndPoint = CGPoint(x: 0, y: 0.5)

        static let cornerRadius: CGFloat = 15

        static let shadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.03)
        static let shadowOpacity: Float = 1
        static let shadowRadius: CGFloat = 28.6
        static let shadowOffset = CGSize(width: 0, height: 23)

        static let iconLeading: CGFloat = 13
        static let titleLeading: CGFloat = 1

        static let latoBoldFontName = "Lato-Bold"
        static let titleFontSize: CGFloat = 16

        static let fatalInitMessage = "init(coder:) has not been implemented"
    }
}

