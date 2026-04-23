import UIKit

final class HardwaresHeaderView: UICollectionReusableView {

    // MARK: Public properties

    static let reuseId = Constants.reuseId

    // MARK: Private properties

    private let stroke: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.borderWidth = Constants.strokeBorderWidth
        view.layer.borderColor = Constants.strokeBorderColor.cgColor
        return view
    }()

    private let titleContainer: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.cornerRadius = Constants.titleContainerCornerRadius
        view.layer.masksToBounds = true
        return view
    }()

    private let titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.titleTextColor
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.titleFontSize)
        label.textAlignment = .left
        return label
    }()

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .clear

        addSubview(stroke)
        addSubview(titleContainer)
        titleContainer.addSubview(titleLabel)
        titleContainer.backgroundColor = Constants.defaultTitleBackgroundColor

        NSLayoutConstraint.activate([
            stroke.leadingAnchor.constraint(equalTo: leadingAnchor),
            stroke.trailingAnchor.constraint(equalTo: trailingAnchor),
            stroke.centerYAnchor.constraint(equalTo: centerYAnchor),
            stroke.heightAnchor.constraint(equalToConstant: Constants.strokeHeight),

            titleContainer.centerYAnchor.constraint(equalTo: stroke.centerYAnchor),
            titleContainer.leadingAnchor.constraint(equalTo: leadingAnchor, constant: Constants.titleContainerLeading),

            titleLabel.topAnchor.constraint(equalTo: titleContainer.topAnchor, constant: Constants.titleLabelTop),
            titleLabel.bottomAnchor.constraint(equalTo: titleContainer.bottomAnchor, constant: Constants.titleLabelBottom),
            titleLabel.leadingAnchor.constraint(equalTo: titleContainer.leadingAnchor, constant: Constants.titleLabelLeading),
            titleLabel.trailingAnchor.constraint(equalTo: titleContainer.trailingAnchor, constant: Constants.titleLabelTrailing)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    // MARK: Public methods

    func configure(title: String, backgroundColor: UIColor = Constants.defaultTitleBackgroundColor) {
        titleLabel.text = title
        titleContainer.backgroundColor = backgroundColor
    }
}

// MARK: - Constants

private extension HardwaresHeaderView {
    enum Constants {
        static let reuseId = "HardwaresHeaderView"

        static let strokeBorderWidth: CGFloat = 1
        static let strokeHeight: CGFloat = 1
        static let strokeBorderColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)

        static let titleContainerCornerRadius: CGFloat = 6
        static let titleContainerLeading: CGFloat = 14

        static let titleTextColor = UIColor(red: 0.502, green: 0.502, blue: 0.502, alpha: 1)
        static let latoBoldFontName = "Lato-Bold"
        static let titleFontSize: CGFloat = 10

        static let defaultTitleBackgroundColor: UIColor = .black

        static let titleLabelTop: CGFloat = 2
        static let titleLabelBottom: CGFloat = -2
        static let titleLabelLeading: CGFloat = 10
        static let titleLabelTrailing: CGFloat = -10
    }
}

