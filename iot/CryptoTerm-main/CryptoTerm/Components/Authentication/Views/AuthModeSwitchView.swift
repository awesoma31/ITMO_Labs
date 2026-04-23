import UIKit

final class AuthModeSwitchView: UIControl {

    // MARK: Public properties

    var mode: Mode = .logIn {
        didSet {
            updateSelection(animated: true)
        }
    }

    var onModeChanged: ((Mode) -> Void)?

    // MARK: Private properties

    private let selectedPill: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = Constants.selectedPillColor
        view.layer.cornerRadius = Constants.cornerRadius
        view.layer.masksToBounds = false
        view.layer.shadowColor = Constants.shadowColor.cgColor
        view.layer.shadowOpacity = Constants.shadowOpacity
        view.layer.shadowRadius = Constants.shadowRadius
        view.layer.shadowOffset = Constants.shadowOffset
        return view
    }()

    private let container: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    private let loginButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setTitle(Constants.loginButtonTitle, for: .normal)
        button.titleLabel?.font = UIFont(name: Constants.fontName, size: Constants.fontSize)
        button.setTitleColor(Constants.buttonTextColor, for: .normal)
        button.backgroundColor = Constants.buttonBackgroundColor
        return button
    }()

    private let signInButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setTitle(Constants.signInButtonTitle, for: .normal)
        button.titleLabel?.font = UIFont(name: Constants.fontName, size: Constants.fontSize)
        button.setTitleColor(Constants.buttonTextColor, for: .normal)
        button.backgroundColor = Constants.buttonBackgroundColor
        return button
    }()

    private var selectedConstraint: NSLayoutConstraint?

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
        updateSelection(animated: false)
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupView()
        updateSelection(animated: false)
    }

    // MARK: Lifecycle

    override var intrinsicContentSize: CGSize {
        let loginSize = loginButton.intrinsicContentSize
        let signInSize = signInButton.intrinsicContentSize

        let width =
        Constants.leftInset +
        loginSize.width +
        Constants.labelSpacing +
        signInSize.width +
        Constants.rightInset

        let height =
        Constants.topInset +
        max(loginSize.height, signInSize.height) +
        Constants.bottomInset

        return CGSize(width: width, height: height)
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        let path = UIBezierPath(roundedRect: selectedPill.bounds, cornerRadius: Constants.cornerRadius)
        selectedPill.layer.shadowPath = path.cgPath
    }

    // MARK: Private Methods

    private func setupView() {
        backgroundColor = Constants.backgroundColor
        layer.cornerRadius = Constants.cornerRadius
        layer.borderWidth = Constants.borderWidth
        layer.borderColor = Constants.borderColor.cgColor
        clipsToBounds = false

        addSubview(selectedPill)
        addSubview(container)

        loginButton.addTarget(self, action: #selector(tapLogin), for: .touchUpInside)
        container.addSubview(loginButton)

        signInButton.addTarget(self, action: #selector(tapSignIn), for: .touchUpInside)
        container.addSubview(signInButton)

        NSLayoutConstraint.activate([
            container.topAnchor.constraint(equalTo: topAnchor),
            container.bottomAnchor.constraint(equalTo: bottomAnchor),
            container.leadingAnchor.constraint(equalTo: leadingAnchor),
            container.trailingAnchor.constraint(equalTo: trailingAnchor),

            selectedPill.topAnchor.constraint(equalTo: topAnchor),
            selectedPill.bottomAnchor.constraint(equalTo: bottomAnchor),
            selectedPill.widthAnchor.constraint(equalTo: widthAnchor, multiplier: Constants.selectedPillWidthMultiplier),

            loginButton.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: Constants.leftInset),
            loginButton.topAnchor.constraint(equalTo: container.topAnchor),
            loginButton.bottomAnchor.constraint(equalTo: container.bottomAnchor),

            signInButton.topAnchor.constraint(equalTo: container.topAnchor),
            signInButton.bottomAnchor.constraint(equalTo: container.bottomAnchor),
            signInButton.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: -Constants.rightInset)
        ])
    }

    private func updateSelection(animated: Bool) {
        selectedConstraint?.isActive = false

        (mode == .logIn)
        ? (selectedConstraint = selectedPill.leadingAnchor.constraint(equalTo: leadingAnchor))
        : (selectedConstraint = selectedPill.trailingAnchor.constraint(equalTo: trailingAnchor))

        selectedConstraint?.isActive = true

        if animated {
            UIView.animate(withDuration: Constants.animationDuration, delay: Constants.animationDelay, options: Constants.animationOptions, animations: layoutIfNeeded)
        } else {
            layoutIfNeeded()
        }

        onModeChanged?(mode)
    }

    @objc private func tapLogin() {
        loginButton.isEnabled = false
        signInButton.isEnabled = true
        mode = .logIn
    }

    @objc private func tapSignIn() {
        loginButton.isEnabled = true
        signInButton.isEnabled = false
        mode = .signIn
    }
}

// MARK: - Mode

extension AuthModeSwitchView {
    enum Mode: Int {
        case logIn = 0
        case signIn = 1
    }
}

// MARK: - Constants

private extension AuthModeSwitchView {
    enum Constants {
        // Layout
        static let leftInset: CGFloat = 70
        static let rightInset: CGFloat = 70
        static let topInset: CGFloat = 7
        static let bottomInset: CGFloat = 7
        static let labelSpacing: CGFloat = 140
        static let cornerRadius: CGFloat = 15
        static let borderWidth: CGFloat = 1
        static let selectedPillWidthMultiplier: CGFloat = 0.5

        // Colors
        static let backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 1)
        static let borderColor = UIColor(red: 0.17, green: 0.211, blue: 0.583, alpha: 1)
        static let selectedPillColor = UIColor(red: 0.411, green: 0.462, blue: 0.921, alpha: 1)
        static let shadowColor = UIColor(white: 0, alpha: 0.03)
        static let buttonTextColor = UIColor.white
        static let buttonBackgroundColor = UIColor.clear

        // Shadow
        static let shadowOpacity: Float = 1
        static let shadowRadius: CGFloat = 28.6
        static let shadowOffset = CGSize(width: 0, height: 23)

        // Font
        static let fontName = "Lato-Bold"
        static let fontSize: CGFloat = 16

        // Button
        static let loginButtonTitle = "Sign up"
        static let signInButtonTitle = "Sign In"

        // Animation
        static let animationDuration: TimeInterval = 0.22
        static let animationDelay: TimeInterval = 0
        static let animationOptions: UIView.AnimationOptions = [.curveEaseInOut]
    }
}
