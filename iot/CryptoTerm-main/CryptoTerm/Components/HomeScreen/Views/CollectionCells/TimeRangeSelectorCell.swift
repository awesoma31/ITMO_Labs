import UIKit

protocol TimeRangeSelectorCellDelegate: AnyObject {
    func timeRangeSelectorCell(_ cell: TimeRangeSelectorCell, didSelect range: TimeRange)
}

final class TimeRangeSelectorCell: UICollectionViewCell {

    // MARK: Public properties

    static let reuseId = Constants.reuseId
    weak var delegate: TimeRangeSelectorCellDelegate?

    // MARK: Private properties

    private var selectedRange: TimeRange = .year
    private var buttons: [TimeRange: UIButton] = [:]
    private var gradientLayers: [UIButton: CAGradientLayer] = [:]

    private let stack: UIStackView = {
        let stack = UIStackView()
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = Constants.stackAxis
        stack.alignment = Constants.stackAlignment
        stack.distribution = Constants.stackDistribution
        stack.spacing = Constants.stackSpacing
        return stack
    }()

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
        contentView.backgroundColor = Constants.contentBackgroundColor
        contentView.addSubview(stack)

        NSLayoutConstraint.activate([
            stack.topAnchor.constraint(equalTo: contentView.topAnchor),
            stack.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            stack.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            stack.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
            contentView.heightAnchor.constraint(equalToConstant: Constants.contentHeight)
        ])

        TimeRange.allCases.forEach { range in
            let button = makeButton(title: range.title)
            button.tag = range.rawValue
            button.addTarget(self, action: #selector(didTapButton(_:)), for: .touchUpInside)

            stack.addArrangedSubview(button)
            buttons[range] = button
        }
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // MARK: Lifecycle

    override func layoutSubviews() {
        super.layoutSubviews()
        if gradientLayers.isEmpty {
            DispatchQueue.main.async {
                self.applySelection(self.selectedRange, notify: false)
            }
        } else {
            updateGradientFrames()
        }
    }

    // MARK: Public methods

    func configure(selected: TimeRange) {
        selectedRange = selected
        setNeedsLayout()
    }

    // MARK: Private methods

    private func makeButton(title: String) -> UIButton {
        let button = UIButton(type: .system)
        button.setTitle(title, for: .normal)
        button.titleLabel?.font = Constants.buttonFont
        button.layer.cornerRadius = Constants.buttonCornerRadius
        button.layer.masksToBounds = Constants.buttonMasksToBounds
        button.backgroundColor = Constants.buttonBackgroundColorNormal
        button.setTitleColor(Constants.buttonTitleColorNormal, for: .normal)
        return button
    }

    private func applySelection(_ range: TimeRange, notify: Bool) {
        selectedRange = range

        for (r, button) in buttons {
            let isSelected = (r == range)

            if isSelected {
                button.backgroundColor = Constants.buttonBackgroundColorSelected
                button.setTitleColor(Constants.buttonTitleColorSelected, for: .normal)
                button.layer.shadowOpacity = Constants.buttonShadowOpacitySelected

                let gradientLayer: CAGradientLayer
                if let existingLayer = gradientLayers[button] {
                    gradientLayer = existingLayer
                } else {
                    gradientLayer = CAGradientLayer()
                    gradientLayers[button] = gradientLayer

                    gradientLayer.colors = Constants.gradientColors
                    gradientLayer.locations = Constants.gradientLocations
                    gradientLayer.startPoint = Constants.gradientStartPoint
                    gradientLayer.endPoint = Constants.gradientEndPoint
                    gradientLayer.cornerRadius = Constants.gradientCornerRadius

                    button.layer.insertSublayer(gradientLayer, at: 0)
                }

                gradientLayer.frame = button.bounds

            } else {
                button.backgroundColor = Constants.buttonBackgroundColorNormal
                button.setTitleColor(Constants.buttonTitleColorNormal, for: .normal)
                button.layer.shadowOpacity = Constants.buttonShadowOpacityNormal

                if let gradientLayer = gradientLayers[button] {
                    gradientLayer.removeFromSuperlayer()
                    gradientLayers[button] = nil
                }
            }
        }

        if notify {
            delegate?.timeRangeSelectorCell(self, didSelect: range)
        }
    }

    private func updateGradientFrames() {
        for (button, gradientLayer) in gradientLayers {
            gradientLayer.frame = button.bounds
        }
    }

    // MARK: Actions

    @objc private func didTapButton(_ sender: UIButton) {
        guard let range = TimeRange(rawValue: sender.tag) else { return }
        applySelection(range, notify: true)
    }
}

// MARK: - Constants

private extension TimeRangeSelectorCell {
    enum Constants {

        // MARK: Identifiers

        static let reuseId: String = "SimpleTimeRangeCell"

        // MARK: Layout

        static let contentHeight: CGFloat = 35

        static let stackAxis: NSLayoutConstraint.Axis = .horizontal
        static let stackAlignment: UIStackView.Alignment = .fill
        static let stackDistribution: UIStackView.Distribution = .fillEqually
        static let stackSpacing: CGFloat = 10

        // MARK: Button

        static let buttonFont: UIFont? = UIFont(name: "Lato-Bold", size: 12)
        static let buttonCornerRadius: CGFloat = 15
        static let buttonMasksToBounds: Bool = false

        static let buttonShadowOpacitySelected: Float = 1
        static let buttonShadowOpacityNormal: Float = 0

        // MARK: Colors

        static let contentBackgroundColor: UIColor = .clear

        static let buttonBackgroundColorNormal: UIColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)
        static let buttonBackgroundColorSelected: UIColor = .clear

        static let buttonTitleColorNormal: UIColor = UIColor(red: 0.502, green: 0.502, blue: 0.502, alpha: 1)
        static let buttonTitleColorSelected: UIColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)

        // MARK: Gradient

        static let gradientColors: [CGColor] = [
            UIColor(red: 0.678, green: 0.706, blue: 0.953, alpha: 1).cgColor,
            UIColor(red: 0.412, green: 0.463, blue: 0.922, alpha: 1).cgColor,
            UIColor(red: 0.232, green: 0.261, blue: 0.521, alpha: 1).cgColor
        ]

        static let gradientLocations: [NSNumber] = [0, 0.5, 1]
        static let gradientStartPoint: CGPoint = CGPoint(x: 1, y: 0.5)
        static let gradientEndPoint: CGPoint = CGPoint(x: 0, y: 0.5)
        static let gradientCornerRadius: CGFloat = 15
    }
}

