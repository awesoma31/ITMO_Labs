import UIKit

final class LoadingOverlayView: UIView {

    // MARK: Private properties

    private let spinner: UIActivityIndicatorView = {
        let spinner = UIActivityIndicatorView(style: .large)
        spinner.translatesAutoresizingMaskIntoConstraints = false
        spinner.color = .white
        spinner.hidesWhenStopped = true
        return spinner
    }()

    private let label: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = UIColor.white.withAlphaComponent(Constants.labelAlpha)
        label.font = UIFont(name: Constants.fontName, size: Constants.fontSize)
        label.textAlignment = .center
        label.numberOfLines = Constants.labelNumberOfLines
        return label
    }()

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupUI()
    }

    // MARK: Public methods

    func show(text: String, animated: Bool = true) {
        label.text = text
        isHidden = false
        spinner.startAnimating()

        guard animated else {
            alpha = Constants.visibleAlpha
            return
        }

        UIView.animate(
            withDuration: Constants.showAnimationDuration,
            delay: Constants.animationDelay,
            options: [.curveEaseOut]
        ) {
            self.alpha = Constants.visibleAlpha
        }
    }

    func hide(animated: Bool = true) {
        guard !isHidden else { return }

        let finish = {
            self.spinner.stopAnimating()
            self.isHidden = true
        }

        guard animated else {
            alpha = Constants.hiddenAlpha
            finish()
            return
        }

        UIView.animate(
            withDuration: Constants.hideAnimationDuration,
            delay: Constants.animationDelay,
            options: [.curveEaseIn]
        ) {
            self.alpha = Constants.hiddenAlpha
        } completion: { _ in
            finish()
        }
    }

    // MARK: Private methods

    private func setupUI() {
        translatesAutoresizingMaskIntoConstraints = false
        backgroundColor = .black
        isHidden = true
        alpha = Constants.hiddenAlpha
        isUserInteractionEnabled = true

        addSubview(spinner)
        addSubview(label)

        NSLayoutConstraint.activate([
            spinner.centerXAnchor.constraint(equalTo: centerXAnchor),
            spinner.centerYAnchor.constraint(equalTo: centerYAnchor),

            label.topAnchor.constraint(equalTo: spinner.bottomAnchor, constant: Constants.labelTopInset),
            label.leadingAnchor.constraint(greaterThanOrEqualTo: leadingAnchor, constant: Constants.labelHorizontalInset),
            label.trailingAnchor.constraint(lessThanOrEqualTo: trailingAnchor, constant: -Constants.labelHorizontalInset),
            label.centerXAnchor.constraint(equalTo: centerXAnchor)
        ])
    }
}

// MARK: - Constants

private extension LoadingOverlayView {
    enum Constants {
        static let hiddenAlpha: CGFloat = 0
        static let visibleAlpha: CGFloat = 1

        static let labelAlpha: CGFloat = 0.75
        static let fontName: String = "Lato-Regular"
        static let fontSize: CGFloat = 14
        static let labelNumberOfLines: Int = 0

        static let labelTopInset: CGFloat = 12
        static let labelHorizontalInset: CGFloat = 20

        static let showAnimationDuration: TimeInterval = 0.2
        static let hideAnimationDuration: TimeInterval = 0.2
        static let animationDelay: TimeInterval = 0
    }
}
