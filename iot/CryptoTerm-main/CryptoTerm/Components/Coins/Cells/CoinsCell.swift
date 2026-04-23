import UIKit

final class CoinsCell: UITableViewCell {

    // MARK: Public properties

    static let reuseId = Constants.reuseId

    // MARK: Private properties

    private var iconView: UIImageView = {
        let imageView = UIImageView(image: UIImage(systemName: Constants.defaultIconSystemName))
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.contentMode = .scaleAspectFit
        return imageView
    }()

    private let titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .white
        label.font = UIFont(name: Constants.fontName, size: Constants.titleFontSize)
        return label
    }()

    private let rowBackground: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.shadowColor = UIColor(
            red: Constants.shadowColorRed,
            green: Constants.shadowColorGreen,
            blue: Constants.shadowColorBlue,
            alpha: Constants.shadowColorAlpha
        ).cgColor
        view.layer.shadowOpacity = Constants.shadowOpacity
        view.layer.shadowRadius = Constants.shadowRadius
        view.layer.shadowOffset = Constants.shadowOffset
        view.backgroundColor = UIColor(
            red: Constants.backgroundColorRed,
            green: Constants.backgroundColorGreen,
            blue: Constants.backgroundColorBlue,
            alpha: Constants.backgroundColorAlpha
        )
        return view
    }()

    // MARK: Init

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        selectionStyle = .none
        backgroundColor = .clear
        contentView.backgroundColor = .clear

        contentView.addSubview(rowBackground)
        rowBackground.addSubview(iconView)
        rowBackground.addSubview(titleLabel)

        NSLayoutConstraint.activate([
            rowBackground.topAnchor.constraint(equalTo: contentView.topAnchor),
            rowBackground.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            rowBackground.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            rowBackground.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),

            iconView.leadingAnchor.constraint(equalTo: rowBackground.leadingAnchor, constant: Constants.iconLeadingInset),
            iconView.centerYAnchor.constraint(equalTo: rowBackground.centerYAnchor),

            titleLabel.leadingAnchor.constraint(equalTo: iconView.trailingAnchor, constant: Constants.titleLeadingInset),
            titleLabel.centerYAnchor.constraint(equalTo: rowBackground.centerYAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    // MARK: Public methods

    func configure(title: String, iconName: String) {
        titleLabel.text = title
        iconView.image = UIImage(named: iconName)
    }
}

// MARK: - Constants

private extension CoinsCell {
    enum Constants {
        static let reuseId: String = "CoinsCell"

        static let defaultIconSystemName: String = "info.circle"

        static let fontName: String = "Lato-Bold"
        static let titleFontSize: CGFloat = 12

        static let shadowColorRed: CGFloat = 0
        static let shadowColorGreen: CGFloat = 0
        static let shadowColorBlue: CGFloat = 0
        static let shadowColorAlpha: CGFloat = 0.03

        static let shadowOpacity: Float = 1
        static let shadowRadius: CGFloat = 28.6
        static let shadowOffset: CGSize = CGSize(width: 0, height: 23)

        static let backgroundColorRed: CGFloat = 0.102
        static let backgroundColorGreen: CGFloat = 0.102
        static let backgroundColorBlue: CGFloat = 0.102
        static let backgroundColorAlpha: CGFloat = 1

        static let iconLeadingInset: CGFloat = 15
        static let titleLeadingInset: CGFloat = 16
    }
}

