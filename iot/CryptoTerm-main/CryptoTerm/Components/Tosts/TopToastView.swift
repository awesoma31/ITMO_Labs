import UIKit

final class TopToastView: UIView {

    // MARK: Public properties

    var onDismiss: (() -> Void)?

    // MARK: Private properties

    private let iconView: UIImageView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.tintColor = .white
        imageView.contentMode = .scaleAspectFit
        imageView.setContentHuggingPriority(.required, for: .horizontal)
        return imageView
    }()

    private let messageLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .white
        label.numberOfLines = 0
        return label
    }()

    // MARK: Init

    init(style: TopToastStyle, message: String, fontSize: CGFloat) {
        super.init(frame: .zero)
        setupUI(style: style, message: message, fontSize: fontSize)
        setupGestures()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // MARK: Private methods

    private func setupUI(style: TopToastStyle, message: String, fontSize: CGFloat) {
        backgroundColor = style.backgroundColor
        layer.cornerRadius = Constants.cornerRadius
        layer.masksToBounds = false
        layer.shadowColor = UIColor.black.cgColor
        layer.shadowOpacity = Constants.shadowOpacity
        layer.shadowRadius = Constants.shadowRadius
        layer.shadowOffset = Constants.shadowOffset

        iconView.image = style.icon
        messageLabel.text = message
        messageLabel.font = UIFont(name: Constants.fontName, size: fontSize)

        let stack = UIStackView(arrangedSubviews: [iconView, messageLabel])
        stack.axis = .horizontal
        stack.alignment = .center
        stack.spacing = Constants.stackSpacing

        addSubview(stack)
        stack.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            iconView.widthAnchor.constraint(equalToConstant: Constants.iconSize),
            iconView.heightAnchor.constraint(equalToConstant: Constants.iconSize),

            stack.topAnchor.constraint(equalTo: topAnchor, constant: Constants.stackTopInset),
            stack.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -Constants.stackBottomInset),
            stack.leadingAnchor.constraint(equalTo: leadingAnchor, constant: Constants.stackLeadingInset),
            stack.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -Constants.stackTrailingInset)
        ])
    }

    private func setupGestures() {
        let swipeUp = UISwipeGestureRecognizer(target: self, action: #selector(handleDismissGesture))
        swipeUp.direction = .up
        addGestureRecognizer(swipeUp)

        let tap = UITapGestureRecognizer(target: self, action: #selector(handleDismissGesture))
        addGestureRecognizer(tap)
    }

    @objc private func handleDismissGesture() {
        onDismiss?()
    }
}

// MARK: - Constants

private extension TopToastView {
    enum Constants {
        static let cornerRadius: CGFloat = 16
        static let shadowOpacity: Float = 0.18
        static let shadowRadius: CGFloat = 10
        static let shadowOffset: CGSize = CGSize(width: 0, height: 6)

        static let fontName: String = "Lato-Bold"

        static let iconSize: CGFloat = 22

        static let stackSpacing: CGFloat = 10
        static let stackTopInset: CGFloat = 12
        static let stackBottomInset: CGFloat = 12
        static let stackLeadingInset: CGFloat = 14
        static let stackTrailingInset: CGFloat = 14
    }
}
