import UIKit

final class CurrencyRowButton: UIControl {

    // MARK: Private properties

    private let bg: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = Constants.backgroundColor
        view.layer.cornerRadius = Constants.cornerRadius
        view.layer.borderWidth = Constants.borderWidth
        view.layer.borderColor = Constants.borderColor.cgColor
        view.isUserInteractionEnabled = false
        return view
    }()

    private let iconView: UIImageView = {
        let iv = UIImageView()
        iv.translatesAutoresizingMaskIntoConstraints = false
        iv.contentMode = .scaleAspectFit
        return iv
    }()

    private let titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.titleColor
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.titleFontSize)
        label.isUserInteractionEnabled = false
        return label
    }()

    private let chevron: UIImageView = {
        let iv = UIImageView(image: UIImage(named: Constants.chevronImageName))
        iv.translatesAutoresizingMaskIntoConstraints = false
        iv.isUserInteractionEnabled = false
        return iv
    }()

    // MARK: Init

    init(iconName: String, title: String) {
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        backgroundColor = .clear

        addSubview(bg)
        bg.addSubview(iconView)
        bg.addSubview(titleLabel)
        bg.addSubview(chevron)

        iconView.image = UIImage(named: iconName)
        titleLabel.text = title

        NSLayoutConstraint.activate([
            bg.topAnchor.constraint(equalTo: topAnchor),
            bg.leadingAnchor.constraint(equalTo: leadingAnchor),
            bg.trailingAnchor.constraint(equalTo: trailingAnchor),
            bg.bottomAnchor.constraint(equalTo: bottomAnchor),

            iconView.leadingAnchor.constraint(equalTo: bg.leadingAnchor, constant: Constants.iconLeading),
            iconView.centerYAnchor.constraint(equalTo: bg.centerYAnchor),

            titleLabel.centerYAnchor.constraint(equalTo: bg.centerYAnchor),
            titleLabel.leadingAnchor.constraint(equalTo: iconView.trailingAnchor, constant: Constants.titleLeading),

            chevron.centerYAnchor.constraint(equalTo: bg.centerYAnchor),
            chevron.trailingAnchor.constraint(equalTo: bg.trailingAnchor, constant: Constants.chevronTrailing),
        ])
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    // MARK: Public methods

    func setTitle(_ text: String) {
        titleLabel.text = text
    }

    func setIcon(_ iconName: String) {
        iconView.image = UIImage(named: iconName)
    }
}

// MARK: - Constants

private extension CurrencyRowButton {
    enum Constants {
        static let backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 1)

        static let cornerRadius: CGFloat = 15
        static let borderWidth: CGFloat = 1
        static let borderColor = UIColor(red: 0.231, green: 0.231, blue: 0.231, alpha: 1)

        static let titleColor = UIColor(red: 0.502, green: 0.502, blue: 0.502, alpha: 1)
        static let latoBoldFontName = "Lato-Bold"
        static let titleFontSize: CGFloat = 12

        static let chevronImageName = "chevron_right_big"

        static let iconLeading: CGFloat = 16
        static let titleLeading: CGFloat = 9
        static let chevronTrailing: CGFloat = -7
    }
}
