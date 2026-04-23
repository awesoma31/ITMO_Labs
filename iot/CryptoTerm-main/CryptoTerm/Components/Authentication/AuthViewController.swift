import UIKit

protocol AuthViewControllerInput: AnyObject {
    func successLogInOrSignIn()
    func setEmailState(newState: AuthTextFieldView.State)
    func setUsernameState(newState: AuthTextFieldView.State)
    func setPasswordState(newState: AuthTextFieldView.State)
    func setLoading(_ isLoading: Bool)
}

typealias AuthViewControllerOutput = AuthInteractorInput

final class AuthViewController: UIViewController {

    // MARK: Public properties

    var currentState: AuthModeSwitchView.Mode = .logIn {
        didSet {
            setupButton()
            clearTextFields()
            currentState == .logIn ? showFields() : hideFields()
            updateScrollView()
        }
    }

    var interactor: AuthViewControllerOutput?
    weak var delegate: Coordinator?

    // MARK: Private properties

    private let topContainer: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    private let scrollView: UIScrollView = {
        let scrollView = UIScrollView()
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.showsVerticalScrollIndicator = false
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.alwaysBounceVertical = true
        return scrollView
    }()

    private let contentView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    private let helloLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = "Hello!"
        label.textColor = Constants.helloLabelTextColor
        label.font = Constants.helloLabelFont
        label.textAlignment = .center
        return label
    }()

    private let emailField: AuthTextFieldView = {
        let emailField = AuthTextFieldView()
        emailField.translatesAutoresizingMaskIntoConstraints = false
        return emailField
    }()

    private let usernameField: AuthTextFieldView = {
        let usernameField = AuthTextFieldView()
        usernameField.translatesAutoresizingMaskIntoConstraints = false
        usernameField.configure(
            buttonImage: nil,
            placeholderText: "User name",
            isEnabled: false
        )
        return usernameField
    }()

    private let passwordField: AuthTextFieldView = {
        let passwordField = AuthTextFieldView()
        passwordField.translatesAutoresizingMaskIntoConstraints = false
        passwordField.configure(
            buttonImage: UIImage(named: "view_hide")!,
            placeholderText: "Password",
            isSecurity: true,
            buttonAction: {
                passwordField.toggleSecirity()
                passwordField.isSecurity
                ? passwordField.setButtonImage(image: UIImage(named: "view_hide")!)
                : passwordField.setButtonImage(image: UIImage(named: "view")!)
            }
        )
        return passwordField
    }()

    private let repeatPasswordField: AuthTextFieldView = {
        let repeatPasswordField = AuthTextFieldView()
        repeatPasswordField.translatesAutoresizingMaskIntoConstraints = false
        repeatPasswordField.configure(
            buttonImage: UIImage(named: "view_hide")!,
            placeholderText: "Repeat password",
            isSecurity: true,
            buttonAction: {
                repeatPasswordField.toggleSecirity()
                repeatPasswordField.isSecurity
                ? repeatPasswordField.setButtonImage(image: UIImage(named: "view_hide")!)
                : repeatPasswordField.setButtonImage(image: UIImage(named: "view")!)
            }
        )
        return repeatPasswordField
    }()

    private let finallyButton: UIButton = {
        let button = UIButton(type: .custom)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.titleLabel?.font = Constants.finallyButtonFont
        button.setTitleColor(Constants.finallyButtonTextColor, for: .normal)
        button.layer.cornerRadius = Constants.finallyButtonCornerRadius
        button.layer.shadowColor = Constants.finallyButtonShadowColor.cgColor
        button.layer.shadowOpacity = Constants.finallyButtonShadowOpacity
        button.layer.shadowRadius = Constants.finallyButtonShadowRadius
        button.layer.shadowOffset = Constants.finallyButtonShadowOffset
        return button
    }()

    private var emailTost: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = "Enter the email address you used when connecting ASIK"
        label.textColor = Constants.toastTextColor
        label.font = Constants.toastFont
        label.layer.cornerRadius = Constants.toastCornerRadius
        label.layer.shadowColor = Constants.toastShadowColor.cgColor
        label.layer.shadowOpacity = Constants.toastShadowOpacity
        label.layer.shadowRadius = Constants.toastShadowRadius
        label.layer.shadowOffset = Constants.toastShadowOffset
        label.backgroundColor = Constants.toastBackgroundColor
        label.clipsToBounds = true
        label.isHidden = true
        label.textAlignment = .center
        return label
    }()

    private lazy var tabsContainer: AuthModeSwitchView = {
        let container = AuthModeSwitchView()
        container.translatesAutoresizingMaskIntoConstraints = false
        container.onModeChanged = { [weak self] mode in
            self?.currentState = mode
        }
        return container
    }()

    private var toastTimer: Timer?
    private var isToastVisible = false
    private var topFinallyButtonConstraint: NSLayoutConstraint?
    private var topEmailFieldConstraint: NSLayoutConstraint?
    private var activeField: UIView?
    private var keyboardHeight: CGFloat = 0
    private var contentViewHeightConstraint: NSLayoutConstraint?
    private var isLoading = false

    // MARK: Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = Constants.viewBackgroundColor
        setupLayout()
        setupButton()
        setupKeyboardObservers()
        setupTapGesture()
        setupTextFieldDelegates()
        emailField.configure(
            buttonImage: UIImage(named: "info")!,
            placeholderText: "Email",
            isEnabled: true,
            buttonAction: { [weak self] in
                self?.showTost()
            }
        )
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        updateScrollView()
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
        toastTimer?.invalidate()
    }

    // MARK: Private Methods

    private func setupLayout() {
        view.addSubview(topContainer)
        view.addSubview(emailTost)
        topContainer.addSubview(helloLabel)
        topContainer.addSubview(tabsContainer)

        view.addSubview(scrollView)
        scrollView.addSubview(contentView)
        contentView.addSubview(emailField)
        contentView.addSubview(usernameField)
        contentView.addSubview(passwordField)
        contentView.addSubview(repeatPasswordField)

        finallyButton.addTarget(self, action: #selector(tapButton), for: .touchUpInside)
        finallyButton.addTarget(self, action: #selector(buttonTouchDown), for: .touchDown)
        finallyButton.addTarget(self, action: #selector(buttonTouchUp), for: [.touchUpInside, .touchUpOutside, .touchCancel])
        contentView.addSubview(finallyButton)

        topFinallyButtonConstraint = finallyButton.topAnchor.constraint(equalTo: repeatPasswordField.bottomAnchor, constant: Constants.finallyButtonTopOffset)
        topEmailFieldConstraint = emailField.topAnchor.constraint(equalTo: usernameField.bottomAnchor, constant: 20)

        NSLayoutConstraint.activate([
            topContainer.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            topContainer.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            topContainer.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            topContainer.heightAnchor.constraint(equalToConstant: 300),

            helloLabel.topAnchor.constraint(equalTo: topContainer.topAnchor, constant: Constants.helloLabelTopOffset),
            helloLabel.centerXAnchor.constraint(equalTo: topContainer.centerXAnchor),

            tabsContainer.topAnchor.constraint(equalTo: helloLabel.bottomAnchor, constant: Constants.tabsContainerTopOffset),
            tabsContainer.centerXAnchor.constraint(equalTo: topContainer.centerXAnchor),
            tabsContainer.bottomAnchor.constraint(lessThanOrEqualTo: topContainer.bottomAnchor, constant: -20),
            tabsContainer.widthAnchor.constraint(equalToConstant: 370),

            scrollView.topAnchor.constraint(equalTo: topContainer.bottomAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.bottomAnchor),

            contentView.topAnchor.constraint(equalTo: scrollView.contentLayoutGuide.topAnchor),
            contentView.leadingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.leadingAnchor),
            contentView.trailingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.trailingAnchor),
            contentView.bottomAnchor.constraint(equalTo: scrollView.contentLayoutGuide.bottomAnchor),
            contentView.widthAnchor.constraint(equalTo: scrollView.frameLayoutGuide.widthAnchor),

            usernameField.topAnchor.constraint(equalTo: contentView.topAnchor),
            usernameField.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            usernameField.widthAnchor.constraint(equalToConstant: Constants.fieldWidth),
            usernameField.heightAnchor.constraint(equalToConstant: Constants.fieldHeight),

            topEmailFieldConstraint!,
            emailField.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            emailField.widthAnchor.constraint(equalToConstant: Constants.fieldWidth),
            emailField.heightAnchor.constraint(equalToConstant: Constants.fieldHeight),

            passwordField.topAnchor.constraint(equalTo: emailField.bottomAnchor, constant: 20),
            passwordField.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            passwordField.widthAnchor.constraint(equalToConstant: Constants.fieldWidth),
            passwordField.heightAnchor.constraint(equalToConstant: Constants.fieldHeight),

            repeatPasswordField.topAnchor.constraint(equalTo: passwordField.bottomAnchor, constant: 20),
            repeatPasswordField.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            repeatPasswordField.widthAnchor.constraint(equalToConstant: Constants.fieldWidth),
            repeatPasswordField.heightAnchor.constraint(equalToConstant: Constants.fieldHeight),

            topFinallyButtonConstraint!,
            finallyButton.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            finallyButton.widthAnchor.constraint(equalToConstant: Constants.finallyButtonWidth),
            finallyButton.heightAnchor.constraint(equalToConstant: Constants.finallyButtonHeight),
            finallyButton.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -Constants.finallyButtonVerticalOffset),

            emailTost.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            emailTost.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            emailTost.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            emailTost.heightAnchor.constraint(greaterThanOrEqualToConstant: Constants.toastHeight)
        ])
    }

    private func updateScrollView() {
        view.layoutIfNeeded()

        let availableHeight = view.bounds.height - topContainer.frame.height - view.safeAreaInsets.bottom

        let contentHeight = contentView.subviews
            .filter { ($0 is AuthTextFieldView && !$0.isHidden) || $0 === finallyButton }
            .map { $0.frame.maxY }
            .max() ?? 0

        let totalContentHeight = contentHeight + 20

        scrollView.isScrollEnabled = totalContentHeight > availableHeight
        scrollView.alwaysBounceVertical = scrollView.isScrollEnabled

        if scrollView.isScrollEnabled {
            contentViewHeightConstraint?.isActive = false
            contentViewHeightConstraint = contentView.heightAnchor.constraint(greaterThanOrEqualToConstant: totalContentHeight)
            contentViewHeightConstraint?.priority = .defaultLow
            contentViewHeightConstraint?.isActive = true
        } else {
            contentViewHeightConstraint?.isActive = false
            contentViewHeightConstraint = nil
        }
    }

    private func setupTextFieldDelegates() {
        emailField.onReturn = { [weak self] in
            if self?.currentState == .logIn {
                self?.usernameField.becomeActive()
            } else {
                self?.passwordField.becomeActive()
            }
        }

        usernameField.onReturn = { [weak self] in
            self?.passwordField.becomeActive()
        }

        passwordField.onReturn = { [weak self] in
            guard let self = self else { return }
            
            if self.currentState == .logIn {
                self.repeatPasswordField.becomeActive()
            } else {
                self.passwordField.resignActive()
                self.tapButton()
            }
        }

        repeatPasswordField.onReturn = { [weak self] in
            self?.repeatPasswordField.resignActive()
            self?.tapButton()
        }
    }

    private func setupKeyboardObservers() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillShow(_:)),
            name: UIResponder.keyboardWillShowNotification,
            object: nil
        )

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillHide(_:)),
            name: UIResponder.keyboardWillHideNotification,
            object: nil
        )
    }

    private func setupTapGesture() {
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(handleTap(_:)))
        tapGesture.cancelsTouchesInView = false
        view.addGestureRecognizer(tapGesture)
    }

    private func setupButton() {
        if currentState == .logIn {
            finallyButton.setTitle("Sign up", for: .normal)
            finallyButton.backgroundColor = Constants.loginButtonBackgroundColor
        } else {
            finallyButton.setTitle("Sign In", for: .normal)
            finallyButton.backgroundColor = Constants.signinButtonBackgroundColor
        }
    }

    private func hideFields() {
        repeatPasswordField.isHidden = true
        usernameField.isHidden = true

        topEmailFieldConstraint?.isActive = false
        topFinallyButtonConstraint?.isActive = false

        topFinallyButtonConstraint = finallyButton.topAnchor.constraint(equalTo: passwordField.bottomAnchor, constant: Constants.finallyButtonTopOffset)
        topEmailFieldConstraint = emailField.topAnchor.constraint(equalTo: contentView.topAnchor)

        topFinallyButtonConstraint?.isActive = true
        topEmailFieldConstraint?.isActive = true

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            self.updateScrollView()
        }
        emailField.setButtonImage(image: nil)
        passwordField.setButtonImage(image: nil)
        passwordField.setSecurity(isSecurity: true)
        passwordField.setButtonEnabled(isEnabled: false)
        emailField.setButtonEnabled(isEnabled: false)
    }

    private func showFields() {
        repeatPasswordField.isHidden = false
        usernameField.isHidden = false

        topFinallyButtonConstraint?.isActive = false
        topEmailFieldConstraint?.isActive = false

        topFinallyButtonConstraint = finallyButton.topAnchor.constraint(equalTo: repeatPasswordField.bottomAnchor, constant: Constants.finallyButtonTopOffset)
        topEmailFieldConstraint = emailField.topAnchor.constraint(equalTo: usernameField.bottomAnchor, constant: 20)

        topEmailFieldConstraint?.isActive = true
        topFinallyButtonConstraint?.isActive = true

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            self.updateScrollView()
        }
        emailField.setButtonImage(image: UIImage(named: "info"))
        passwordField.setButtonImage(image: UIImage(named: "view_hide"))
        repeatPasswordField.setButtonImage(image: UIImage(named: "view_hide"))
        passwordField.setSecurity(isSecurity: true)
        repeatPasswordField.setSecurity(isSecurity: true)
        passwordField.setButtonEnabled(isEnabled: true)
        emailField.setButtonEnabled(isEnabled: true)
    }

    private func clearTextFields() {
        emailField.text = ""
        usernameField.text = ""
        passwordField.text = ""
        repeatPasswordField.text = ""
        emailField.setState(newState: .normal)
        usernameField.setState(newState: .normal)
        passwordField.setState(newState: .normal)
        repeatPasswordField.setState(newState: .normal)
    }

    private func onLogIn(email: String, password: String, username: String) {
        interactor?.onLogIn(email: email, password: password, username: username)
    }

    private func onSignIn(email: String, password: String) {
        interactor?.onSignIn(email: email, password: password)
    }

    private func showTost() {
        guard !isToastVisible else { return }

        isToastVisible = true
        emailTost.alpha = 0

        UIView.animate(withDuration: 0.3) {
            self.emailTost.isHidden = false
            self.emailTost.alpha = 1
        }

        toastTimer?.invalidate()
        toastTimer = Timer.scheduledTimer(timeInterval: 3.0, target: self, selector: #selector(hideTost), userInfo: nil, repeats: false)
    }

    private func applyLoadingState(_ loading: Bool) {
        guard isLoading != loading else { return }
        isLoading = loading

        finallyButton.isUserInteractionEnabled = !loading
        emailField.isUserInteractionEnabled = !loading
        usernameField.isUserInteractionEnabled = !loading
        passwordField.isUserInteractionEnabled = !loading
        repeatPasswordField.isUserInteractionEnabled = !loading
        tabsContainer.isUserInteractionEnabled = !loading

        if loading {
            startButtonBreathingAnimation()
            if isToastVisible { hideTost() }
            view.endEditing(true)
        } else {
            stopButtonBreathingAnimation()
            finallyButton.alpha = Constants.buttonNormalAlpha
            finallyButton.transform = .identity
        }
    }

    private func startButtonBreathingAnimation() {
        let anim = CABasicAnimation(keyPath: "opacity")
        anim.fromValue = 1.0
        anim.toValue = 0.65
        anim.duration = 0.75
        anim.autoreverses = true
        anim.repeatCount = .infinity
        anim.timingFunction = CAMediaTimingFunction(name: .easeInEaseOut)
        finallyButton.layer.add(anim, forKey: "breathingOpacity")
    }

    private func stopButtonBreathingAnimation() {
        finallyButton.layer.removeAnimation(forKey: "breathingOpacity")
    }

    @objc private func hideTost() {
        guard isToastVisible else { return }

        UIView.animate(withDuration: 0.3) {
            self.emailTost.alpha = 0
        } completion: { _ in
            self.emailTost.isHidden = true
            self.isToastVisible = false
            self.toastTimer?.invalidate()
            self.toastTimer = nil
        }
    }

    @objc private func tapButton() {
        var wrong: Bool = false

        if emailField.text.isEmpty {
            emailField.setState(newState: .error(message: "This field is required!"))
            wrong = true
        }

        if passwordField.text.isEmpty {
            passwordField.setState(newState: .error(message: "This field is required!"))
            wrong = true
        }

        if currentState == .logIn {
            if usernameField.text.isEmpty {
                usernameField.setState(newState: .error(message: "This field is required!"))
                wrong = true
            }

            if repeatPasswordField.text.isEmpty {
                repeatPasswordField.setState(newState: .error(message: "This field is required!"))
                wrong = true
                return
            }

            if repeatPasswordField.text != passwordField.text {
                repeatPasswordField.setState(newState: .error(message: "Passwords don't match!"))
                repeatPasswordField.text = ""
                return
            }
            if wrong { return }
            applyLoadingState(true)
            onLogIn(email: emailField.text, password: passwordField.text, username: usernameField.text)
        } else {
            if wrong { return }
            applyLoadingState(true)
            onSignIn(email: emailField.text, password: passwordField.text)
        }
    }

    @objc private func buttonTouchDown() {
        guard !isLoading else { return }
        UIView.animate(withDuration: Constants.buttonAnimationDuration) {
            self.finallyButton.alpha = Constants.buttonPressedAlpha
            self.finallyButton.transform = CGAffineTransform(scaleX: Constants.buttonPressedScale, y: Constants.buttonPressedScale)
        }
    }

    @objc private func buttonTouchUp() {
        guard !isLoading else { return }
        UIView.animate(withDuration: Constants.buttonAnimationDuration) {
            self.finallyButton.alpha = Constants.buttonNormalAlpha
            self.finallyButton.transform = .identity
        }
    }

    @objc private func keyboardWillShow(_ notification: Notification) {
        guard let userInfo = notification.userInfo,
              let keyboardFrame = userInfo[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect else { return }

        keyboardHeight = keyboardFrame.height

        let textFields = [emailField, usernameField, passwordField, repeatPasswordField]
        for field in textFields {
            if let textField = field.getTextField(), textField.isFirstResponder {
                activeField = textField
                break
            }
        }

        if let activeField = activeField {
            let contentInsets = UIEdgeInsets(
                top: 0,
                left: 0,
                bottom: keyboardHeight,
                right: 0
            )
            scrollView.contentInset = contentInsets
            scrollView.scrollIndicatorInsets = contentInsets

            let fieldFrameInScrollView = activeField.convert(activeField.bounds, to: scrollView)
            scrollView.scrollRectToVisible(fieldFrameInScrollView, animated: true)
        }

        if isToastVisible {
            hideTost()
        }
    }

    @objc private func keyboardWillHide(_ notification: Notification) {
        scrollView.contentInset = .zero
        scrollView.scrollIndicatorInsets = .zero
        keyboardHeight = 0
    }

    @objc private func handleTap(_ gesture: UITapGestureRecognizer) {
        view.endEditing(true)
        if isToastVisible {
            hideTost()
        }
    }
}

// MARK: - AuthViewControllerInput

extension AuthViewController: AuthViewControllerInput {
    func successLogInOrSignIn() {
        delegate?.showMainScreen()
    }

    func setEmailState(newState: AuthTextFieldView.State) {
        emailField.text = ""
        emailField.setState(newState: newState)
    }

    func setUsernameState(newState: AuthTextFieldView.State) {
        usernameField.text = ""
        usernameField.setState(newState: newState)
    }
    
    func setPasswordState(newState: AuthTextFieldView.State) {
        passwordField.text = ""
        passwordField.setState(newState: newState)
    }

    func setLoading(_ isLoading: Bool) {
        applyLoadingState(isLoading)
    }
}

// MARK: - Constants

private extension AuthViewController {
    enum Constants {
        // Colors
        static let viewBackgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 1)
        static let helloLabelTextColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        static let finallyButtonTextColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        static let privacyPolicyButtonTextColor = UIColor(red: 0.411, green: 0.462, blue: 0.921, alpha: 1)
        static let finallyButtonShadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.03)
        static let loginButtonBackgroundColor = UIColor(red: 0.231, green: 0.231, blue: 0.231, alpha: 1)
        static let signinButtonBackgroundColor = UIColor(red: 0.411, green: 0.462, blue: 0.921, alpha: 1)
        static let toastTextColor = UIColor(red: 0.231, green: 0.231, blue: 0.231, alpha: 1)
        static let toastBackgroundColor = UIColor(red: 0.411, green: 0.462, blue: 0.921, alpha: 1)
        static let toastShadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.03)

        // Fonts
        static let helloLabelFont = UIFont(name: "Lato-Bold", size: 32)!
        static let finallyButtonFont = UIFont(name: "Lato-Bold", size: 16)!
        static let privacyPolicyButtonFont = UIFont(name: "Lato-Bold", size: 10)!
        static let toastFont = UIFont(name: "Lato-Regular", size: 14)!

        // Layout
        static let helloLabelTopOffset: CGFloat = 90
        static let tabsContainerTopOffset: CGFloat = 90
        static let emailFieldTopOffset: CGFloat = 40
        static let usernameFieldTopOffset: CGFloat = 30
        static let passwordFieldTopOffset: CGFloat = 30
        static let repeatPasswordFieldTopOffset: CGFloat = 30
        static let finallyButtonTopOffset: CGFloat = 40
        static let fieldHeight: CGFloat = 50
        static let fieldWidth: CGFloat = 371
        static let finallyButtonWidth: CGFloat = 185
        static let finallyButtonHeight: CGFloat = 50
        static let finallyButtonVerticalOffset: CGFloat = 20
        static let toastHeight: CGFloat = 80

        // Button
        static let finallyButtonCornerRadius: CGFloat = 15
        static let finallyButtonShadowOpacity: Float = 1
        static let finallyButtonShadowRadius: CGFloat = 28.6
        static let finallyButtonShadowOffset = CGSize(width: 0, height: 23)

        // Toast
        static let toastCornerRadius: CGFloat = 15
        static let toastShadowOpacity: Float = 1
        static let toastShadowRadius: CGFloat = 28.6
        static let toastShadowOffset = CGSize(width: 0, height: 23)

        // Animation
        static let buttonAnimationDuration: TimeInterval = 0.1
        static let buttonNormalAlpha: CGFloat = 1.0
        static let buttonPressedAlpha: CGFloat = 0.8
        static let buttonPressedScale: CGFloat = 0.98
        static let keyboardAnimationDuration: TimeInterval = 0.3
        static let keyboardOffsetPadding: CGFloat = 20
    }
}
