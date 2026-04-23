import UIKit

final class DeviceRowCell: UITableViewCell {

    // MARK: Public properties

    var deviceId: String?
    var deviceColor: UIColor?

    static let reuseId = Constants.reuseId

    // MARK: Private properties

    private let background: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.cornerRadius = Constants.backgroundCornerRadius
        return view
    }()

    private let colorSquare: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.cornerRadius = Constants.colorSquareCornerRadius
        view.layer.masksToBounds = Constants.colorSquareMasksToBounds
        return view
    }()

    private let nameLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.textColorWhite
        label.font = Constants.textFont
        label.textAlignment = Constants.nameTextAlignment
        return label
    }()

    private let thrLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.textColorWhite
        label.font = Constants.textFont
        return label
    }()

    private let kwLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.textColorWhite
        label.font = Constants.textFont
        return label
    }()

    private let tempLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.textColorWhite
        label.font = Constants.textFont
        return label
    }()

    private let statusDot: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.cornerRadius = Constants.statusDotCornerRadius
        view.layer.masksToBounds = Constants.statusDotMasksToBounds
        return view
    }()

    // MARK: Init

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        backgroundColor = Constants.cellBackgroundColor
        contentView.backgroundColor = Constants.cellBackgroundColor
        selectionStyle = .none

        contentView.addSubview(background)
        background.addSubview(colorSquare)
        background.addSubview(nameLabel)
        background.addSubview(thrLabel)
        background.addSubview(kwLabel)
        background.addSubview(tempLabel)
        background.addSubview(statusDot)

        NSLayoutConstraint.activate([
            background.topAnchor.constraint(equalTo: contentView.topAnchor, constant: Constants.backgroundTopInset),
            background.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            background.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            background.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
            background.heightAnchor.constraint(equalToConstant: Constants.backgroundHeight),

            colorSquare.leadingAnchor.constraint(equalTo: background.leadingAnchor, constant: Constants.colorSquareLeadingInset),
            colorSquare.widthAnchor.constraint(equalToConstant: Constants.colorSquareSize),
            colorSquare.heightAnchor.constraint(equalToConstant: Constants.colorSquareSize),
            colorSquare.centerYAnchor.constraint(equalTo: background.centerYAnchor),

            nameLabel.leadingAnchor.constraint(equalTo: colorSquare.trailingAnchor, constant: Constants.nameLeadingSpacing),
            nameLabel.centerYAnchor.constraint(equalTo: background.centerYAnchor),
            nameLabel.widthAnchor.constraint(equalToConstant: Constants.nameWidth),

            thrLabel.leadingAnchor.constraint(equalTo: nameLabel.trailingAnchor, constant: Constants.thrLeadingSpacing),
            thrLabel.centerYAnchor.constraint(equalTo: background.centerYAnchor),
            thrLabel.widthAnchor.constraint(equalToConstant: Constants.thrWidth),

            kwLabel.leadingAnchor.constraint(equalTo: thrLabel.trailingAnchor, constant: Constants.kwLeadingSpacing),
            kwLabel.centerYAnchor.constraint(equalTo: background.centerYAnchor),
            kwLabel.widthAnchor.constraint(equalToConstant: Constants.kwWidth),

            tempLabel.leadingAnchor.constraint(equalTo: kwLabel.trailingAnchor, constant: Constants.tempLeadingSpacing),
            tempLabel.centerYAnchor.constraint(equalTo: background.centerYAnchor),
            tempLabel.widthAnchor.constraint(equalToConstant: Constants.tempWidth),

            statusDot.trailingAnchor.constraint(equalTo: background.trailingAnchor, constant: -Constants.statusDotTrailingInset),
            statusDot.widthAnchor.constraint(equalToConstant: Constants.statusDotSize),
            statusDot.heightAnchor.constraint(equalToConstant: Constants.statusDotSize),
            statusDot.centerYAnchor.constraint(equalTo: background.centerYAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    // MARK: Public methods

    func configure(_ row: DeviceRow, highlighted: Bool) {
        background.backgroundColor = highlighted
        ? Constants.backgroundColorHighlighted
        : Constants.backgroundColorNormal

        colorSquare.backgroundColor = highlighted
        ? row.accentColor
        : Constants.colorSquareColorNormal

        deviceColor = row.accentColor

        nameLabel.text = row.name
        nameLabel.textColor = highlighted
        ? Constants.textColorWhite
        : Constants.textColorDimmed

        thrLabel.text = String(format: Constants.formatThr, row.thr)
        thrLabel.textColor = highlighted
        ? Constants.textColorWhite
        : Constants.textColorDimmed

        kwLabel.text = String(format: Constants.formatKw, row.kw)
        kwLabel.textColor = highlighted
        ? Constants.textColorWhite
        : Constants.textColorDimmed

        tempLabel.text = String(format: Constants.formatTemp, row.temp)
        tempLabel.textColor = highlighted
        ? Constants.textColorWhite
        : Constants.textColorDimmed

        statusDot.backgroundColor = row.statusColor
        deviceId = row.deviceId
    }
}

// MARK: - Constants

private extension DeviceRowCell {
    enum Constants {

        // MARK: Identifiers

        static let reuseId: String = "DeviceRowCell"

        // MARK: Layout

        static let backgroundCornerRadius: CGFloat = 12
        static let backgroundTopInset: CGFloat = 5
        static let backgroundHeight: CGFloat = 25

        static let colorSquareCornerRadius: CGFloat = 2
        static let colorSquareMasksToBounds: Bool = true
        static let colorSquareLeadingInset: CGFloat = 7
        static let colorSquareSize: CGFloat = 15

        static let nameLeadingSpacing: CGFloat = 10
        static let nameWidth: CGFloat = 60

        static let thrLeadingSpacing: CGFloat = 5
        static let thrWidth: CGFloat = 60

        static let kwLeadingSpacing: CGFloat = 2
        static let kwWidth: CGFloat = 58

        static let tempLeadingSpacing: CGFloat = 2
        static let tempWidth: CGFloat = 60

        static let statusDotCornerRadius: CGFloat = 5
        static let statusDotMasksToBounds: Bool = true
        static let statusDotTrailingInset: CGFloat = 25
        static let statusDotSize: CGFloat = 10

        // MARK: Formatting

        static let formatThr: String = "%.1f"
        static let formatKw: String = "%.2f"
        static let formatTemp: String = "%.3f"

        // MARK: Fonts

        static let textFont: UIFont? = UIFont(name: "Lato-Bold", size: 12)

        // MARK: Colors

        static let cellBackgroundColor: UIColor = .clear

        static let textColorWhite: UIColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        static let textColorDimmed: UIColor = UIColor(red: 0.341, green: 0.341, blue: 0.341, alpha: 1)

        static let backgroundColorHighlighted: UIColor = UIColor(red: 0.341, green: 0.341, blue: 0.341, alpha: 1)
        static let backgroundColorNormal: UIColor = UIColor(red: 0, green: 0, blue: 0, alpha: 1)

        static let colorSquareColorNormal: UIColor = UIColor(red: 0.341, green: 0.341, blue: 0.341, alpha: 1)

        // MARK: Text alignment

        static let nameTextAlignment: NSTextAlignment = .left
    }
}

