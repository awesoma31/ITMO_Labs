import UIKit

protocol ProfitCalculatorCellDelegate: AnyObject {
    func profitCalculatorCellDidChangePeriod(_ cell: ProfitCalculatorCell, selectedPeriod: Period)
    func profitCalculatorCellDidChangePrice(_ cell: ProfitCalculatorCell, newPrice: Double)
    func profitCalculatorCellDidChangeCurrency(_ cell: ProfitCalculatorCell)
    func profitCalculatorCellDidBeginEditing(_ cell: ProfitCalculatorCell, textField: UITextField)
    func profitCalculatorCellDidEndEditing(_ cell: ProfitCalculatorCell, textField: UITextField)
}

final class ProfitCalculatorCell: UICollectionViewCell {

    // MARK: Public properties

    static let reuseId = Constants.reuseId

    weak var delegate: ProfitCalculatorCellDelegate?

    // MARK: Private properties

    private var periodButtons: [UIButton] = []
    private var selectedPeriod: Period = .hour
    private var lastSentPrice: Double = Constants.zeroDouble

    private let currencyButton = CurrencyRowButton(iconName: Constants.defaultCurrencyIconName, title: Constants.defaultCurrencyTitle)
    private let priceRow = PriceInputRowView()
    private var isLoading = false

    private let container: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = Constants.containerBackgroundColor
        view.layer.cornerRadius = Constants.cornerRadius
        return view
    }()

    private let titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = Constants.titleText
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.titleFontSize)
        label.textColor = Constants.whiteTextColor
        return label
    }()

    private let periodScroll: UIScrollView = {
        let s = UIScrollView()
        s.translatesAutoresizingMaskIntoConstraints = false
        s.showsHorizontalScrollIndicator = false
        s.alwaysBounceHorizontal = true
        return s
    }()

    private let periodStack: UIStackView = {
        let st = UIStackView()
        st.translatesAutoresizingMaskIntoConstraints = false
        st.axis = .horizontal
        st.spacing = Constants.periodStackSpacing
        return st
    }()

    private let exchangeTitle: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.secondaryTextColor
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.smallFontSize)
        label.text = Constants.exchangeRateTitleText
        return label
    }()

    private let exchangeValue: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.secondaryTextColor
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.smallFontSize)
        label.textAlignment = .right
        label.text = Constants.zeroUsdText
        return label
    }()

    private let innerSeparator: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = Constants.separatorColor
        return view
    }()

    private let withoutTitle: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.secondaryTextColor
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.smallFontSize)
        label.text = Constants.withoutExpensesTitleText
        return label
    }()

    private let withTitle: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.secondaryTextColor
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.smallFontSize)
        label.text = Constants.withExpensesTitleText
        return label
    }()

    private let withoutValue: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.secondaryTextColor
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.smallFontSize)
        label.textAlignment = .right
        label.text = Constants.zeroUsdText
        return label
    }()

    private let withValue: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.secondaryTextColor
        label.font = UIFont(name: Constants.latoBoldFontName, size: Constants.smallFontSize)
        label.textAlignment = .right
        label.text = Constants.zeroUsdText
        return label
    }()

    private let timerImageView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: Constants.timerImageName)!)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        return imageView
    }()

    private let loadingOverlay: UIView = {
        let v = UIView()
        v.translatesAutoresizingMaskIntoConstraints = false
        v.backgroundColor = Constants.overlayBackgroundColor
        v.layer.cornerRadius = Constants.cornerRadius
        v.clipsToBounds = true
        v.alpha = Constants.overlayInitialAlpha
        v.isHidden = true
        v.isUserInteractionEnabled = true
        return v
    }()

    private let loadingSpinner: UIActivityIndicatorView = {
        let a = UIActivityIndicatorView(style: .medium)
        a.translatesAutoresizingMaskIntoConstraints = false
        a.hidesWhenStopped = true
        return a
    }()

    private let loadingLabel: UILabel = {
        let l = UILabel()
        l.translatesAutoresizingMaskIntoConstraints = false
        l.text = Constants.loadingText
        l.font = UIFont(name: Constants.latoBoldFontName, size: Constants.smallFontSize)
        l.textColor = Constants.loadingTextColor
        return l
    }()

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
    
        backgroundColor = .clear
        contentView.backgroundColor = .clear
    
        contentView.addSubview(container)
    
        container.addSubview(titleLabel)
        container.addSubview(currencyButton)
        container.addSubview(priceRow)
    
        container.addSubview(periodScroll)
        periodScroll.addSubview(periodStack)
    
        container.addSubview(exchangeTitle)
        container.addSubview(exchangeValue)
        container.addSubview(innerSeparator)
    
        container.addSubview(withoutTitle)
        container.addSubview(withoutValue)
    
        container.addSubview(withTitle)
        container.addSubview(withValue)
    
        container.addSubview(timerImageView)

        container.addSubview(loadingOverlay)
        loadingOverlay.addSubview(loadingSpinner)
        loadingOverlay.addSubview(loadingLabel)

        NSLayoutConstraint.activate([
            container.topAnchor.constraint(equalTo: contentView.topAnchor),
            container.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            container.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            container.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),

            titleLabel.topAnchor.constraint(equalTo: container.topAnchor, constant: Constants.titleTop),
            titleLabel.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: Constants.sideInset),

            currencyButton.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: Constants.currencyTop),
            currencyButton.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: Constants.sideInset),
            currencyButton.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: Constants.sideInsetNegative),
            currencyButton.heightAnchor.constraint(equalToConstant: Constants.rowHeight),

            priceRow.topAnchor.constraint(equalTo: currencyButton.bottomAnchor, constant: Constants.priceTop),
            priceRow.leadingAnchor.constraint(equalTo: currencyButton.leadingAnchor),
            priceRow.trailingAnchor.constraint(equalTo: currencyButton.trailingAnchor),
            priceRow.heightAnchor.constraint(equalToConstant: Constants.rowHeight),

            periodScroll.topAnchor.constraint(equalTo: priceRow.bottomAnchor, constant: Constants.periodTop),
            periodScroll.leadingAnchor.constraint(equalTo: currencyButton.leadingAnchor),
            periodScroll.trailingAnchor.constraint(equalTo: currencyButton.trailingAnchor),
            periodScroll.heightAnchor.constraint(equalToConstant: Constants.periodScrollHeight),

            periodStack.leadingAnchor.constraint(equalTo: periodScroll.contentLayoutGuide.leadingAnchor),
            periodStack.trailingAnchor.constraint(equalTo: periodScroll.contentLayoutGuide.trailingAnchor),
            periodStack.topAnchor.constraint(equalTo: periodScroll.contentLayoutGuide.topAnchor),
            periodStack.bottomAnchor.constraint(equalTo: periodScroll.contentLayoutGuide.bottomAnchor),
            periodStack.heightAnchor.constraint(equalTo: periodScroll.frameLayoutGuide.heightAnchor),

            exchangeTitle.topAnchor.constraint(equalTo: periodScroll.bottomAnchor, constant: Constants.exchangeTop),
            exchangeTitle.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: Constants.sideInset),

            exchangeValue.centerYAnchor.constraint(equalTo: exchangeTitle.centerYAnchor),
            exchangeValue.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: Constants.sideInsetNegative),

            timerImageView.centerYAnchor.constraint(equalTo: exchangeTitle.centerYAnchor, constant: Constants.timerCenterYOffset),
            timerImageView.trailingAnchor.constraint(equalTo: exchangeValue.leadingAnchor, constant: Constants.timerTrailingConstant),

            innerSeparator.topAnchor.constraint(equalTo: exchangeTitle.bottomAnchor, constant: Constants.innerSeparatorTop),
            innerSeparator.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: Constants.innerSeparatorInset),
            innerSeparator.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: Constants.innerSeparatorTrailingConstant),
            innerSeparator.heightAnchor.constraint(equalToConstant: Constants.innerSeparatorHeight),

            withTitle.topAnchor.constraint(equalTo: innerSeparator.bottomAnchor, constant: Constants.withTitleTop),
            withTitle.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: Constants.sideInset),

            withValue.centerYAnchor.constraint(equalTo: withTitle.centerYAnchor),
            withValue.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: Constants.sideInsetNegative),

            withoutTitle.topAnchor.constraint(equalTo: withTitle.bottomAnchor, constant: Constants.withoutTitleTop),
            withoutTitle.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: Constants.sideInset),

            withoutValue.centerYAnchor.constraint(equalTo: withoutTitle.centerYAnchor),
            withoutValue.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: Constants.sideInsetNegative),

            loadingOverlay.topAnchor.constraint(equalTo: container.topAnchor),
            loadingOverlay.leadingAnchor.constraint(equalTo: container.leadingAnchor),
            loadingOverlay.trailingAnchor.constraint(equalTo: container.trailingAnchor),
            loadingOverlay.bottomAnchor.constraint(equalTo: container.bottomAnchor),

            loadingSpinner.centerXAnchor.constraint(equalTo: loadingOverlay.centerXAnchor),
            loadingSpinner.centerYAnchor.constraint(equalTo: loadingOverlay.centerYAnchor, constant: Constants.spinnerCenterYOffset),

            loadingLabel.topAnchor.constraint(equalTo: loadingSpinner.bottomAnchor, constant: Constants.loadingLabelTop),
            loadingLabel.centerXAnchor.constraint(equalTo: loadingOverlay.centerXAnchor)
        ])

        currencyButton.addTarget(self, action: #selector(didTapCurrency), for: .touchUpInside)
        priceRow.textField.delegate = self

        buildPeriods()
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    // MARK: Lifecycle

    override func prepareForReuse() {
        super.prepareForReuse()
        alpha = Constants.cellVisibleAlpha
        transform = .identity
        lastSentPrice = Constants.zeroDouble
        setLoading(false, animated: false)
    }

    // MARK: Public methods

    func configure(
        exchangeRateText: String,
        withoutExpenses: String,
        withExpenses: String
    ) {
        exchangeValue.text = "\(exchangeRateText)\(Constants.usdSuffix)"
        withoutValue.attributedText = configureValue(value: withoutExpenses)
        withValue.attributedText = configureValue(value: withExpenses)
    }

    func setLoading(_ loading: Bool, animated: Bool = true) {
        guard isLoading != loading else { return }
        isLoading = loading

        if loading, priceRow.textField.isFirstResponder {
            priceRow.textField.resignFirstResponder()
        }

        currencyButton.isEnabled = !loading
        priceRow.textField.isEnabled = !loading
        periodButtons.forEach { $0.isEnabled = !loading }

        let show = {
            self.loadingOverlay.alpha = loading ? Constants.overlayShownAlpha : Constants.overlayHiddenAlpha
            self.container.alpha = loading ? Constants.containerLoadingAlpha : Constants.containerNormalAlpha
        }

        if loading {
            loadingOverlay.isHidden = false
            loadingSpinner.startAnimating()
        }

        if animated {
            UIView.animate(withDuration: Constants.loadingAnimationDuration, animations: show) { _ in
                if !loading {
                    self.loadingSpinner.stopAnimating()
                    self.loadingOverlay.isHidden = true
                }
            }
        } else {
            show()
            if !loading {
                loadingSpinner.stopAnimating()
                loadingOverlay.isHidden = true
            }
        }
    }

    func setCoinsButton(title: String, iconName: String) {
        currencyButton.setTitle(title)
        currencyButton.setIcon(iconName)
    }

    // MARK: Private methods

    private func configureValue(value: String) -> NSMutableAttributedString {
        let first = value
        let second = Constants.usdSuffix
        let font = UIFont(name: Constants.latoBoldFontName, size: Constants.smallFontSize)
        let firstColor: UIColor = Constants.positiveValueColor
        let secondColor = Constants.secondaryTextColor

        let attr = NSMutableAttributedString(
            string: first,
            attributes: [.font: font as Any, .foregroundColor: firstColor]
        )
        attr.append(NSMutableAttributedString(
            string: second,
            attributes: [.font: font as Any, .foregroundColor: secondColor]
        ))
        return attr
    }

    private func buildPeriods() {
        periodButtons.forEach { $0.removeFromSuperview() }
        periodButtons.removeAll()

        for (idx, p) in Period.allCases.enumerated() {
            let b = GradientStateButton()
            b.borderColor = Constants.periodButtonBorderColor
            b.translatesAutoresizingMaskIntoConstraints = false
            b.setTitle(p.rawValue, for: .normal)

            b.tag = idx
            b.addTarget(self, action: #selector(didTapPeriod(_:)), for: .touchUpInside)

            NSLayoutConstraint.activate([
                b.heightAnchor.constraint(equalToConstant: Constants.periodButtonHeight),
                b.widthAnchor.constraint(greaterThanOrEqualToConstant: Constants.periodButtonMinWidth)
            ])

            periodStack.addArrangedSubview(b)
            periodButtons.append(b)
        }

        updatePeriodSelection()
    }

    private func updatePeriodSelection() {
        for (idx, b) in periodButtons.enumerated() {
            let gb = b as! GradientStateButton
            let p = Period.allCases[idx]
            let isSel = (p == selectedPeriod)
            isSel ? (gb.isGradientOn = true) : (gb.isGradientOn = false)
        }
    }

    private func parseDouble(_ input: String) -> Double? {
        let trimmed = input.trimmingCharacters(in: .whitespacesAndNewlines)
        let normalized = trimmed
            .replacingOccurrences(of: Constants.parseWhitespace, with: Constants.parseEmpty)
            .replacingOccurrences(of: Constants.parseComma, with: Constants.parseDot)
        return Double(normalized)
    }

    @objc private func didTapPeriod(_ sender: UIButton) {
        let p = Period.allCases[sender.tag]
        guard selectedPeriod != p else { return }
        selectedPeriod = p
        updatePeriodSelection()
        delegate?.profitCalculatorCellDidChangePeriod(self, selectedPeriod: p)
    }

    @objc private func didTapCurrency() {
        delegate?.profitCalculatorCellDidChangeCurrency(self)
    }
}

// MARK: - UITextFieldDelegate

extension ProfitCalculatorCell: UITextFieldDelegate {
    func textFieldDidBeginEditing(_ textField: UITextField) {
        delegate?.profitCalculatorCellDidBeginEditing(self, textField: textField)
    }

    func textFieldDidEndEditing(_ textField: UITextField) {
        delegate?.profitCalculatorCellDidEndEditing(self, textField: textField)
        sendPriceIfNeeded()
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    private func sendPriceIfNeeded() {
        let text = priceRow.textField.text ?? ""

        if text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            if lastSentPrice != Constants.zeroDouble {
                lastSentPrice = Constants.zeroDouble
                delegate?.profitCalculatorCellDidChangePrice(self, newPrice: Constants.zeroDouble)
            }
            return
        }

        guard let value = parseDouble(text) else { return }

        if value != lastSentPrice {
            lastSentPrice = value
            delegate?.profitCalculatorCellDidChangePrice(self, newPrice: value)
        }
    }
}

// MARK: - Constants

private extension ProfitCalculatorCell {
    enum Constants {
        static let reuseId = "ProfitCalculatorCell"

        static let defaultCurrencyIconName = "bitcoin"
        static let defaultCurrencyTitle = "Bitcoin"

        static let zeroDouble: Double = 0

        static let titleText = "Calculate profit"
        static let exchangeRateTitleText = "Exchange rate"
        static let withoutExpensesTitleText = "Without expenses"
        static let withExpensesTitleText = "With expenses"
        static let zeroUsdText = "0 USD"
        static let usdSuffix = " USD"

        static let timerImageName = "timer"

        static let loadingText = "Calculating..."

        static let latoBoldFontName = "Lato-Bold"
        static let titleFontSize: CGFloat = 16
        static let smallFontSize: CGFloat = 12

        static let containerBackgroundColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)
        static let whiteTextColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        static let secondaryTextColor = UIColor(red: 0.502, green: 0.502, blue: 0.502, alpha: 1)
        static let separatorColor = UIColor(red: 0.231, green: 0.231, blue: 0.231, alpha: 1)
        static let periodButtonBorderColor = UIColor(red: 0.376, green: 0.376, blue: 0.376, alpha: 1)
        static let positiveValueColor: UIColor = UIColor(red: 0.133, green: 0.545, blue: 0.133, alpha: 1)

        static let overlayBackgroundColor = UIColor(white: 0, alpha: 0.45)
        static let loadingTextColor = UIColor(white: 1, alpha: 0.9)

        static let cornerRadius: CGFloat = 15
        static let periodStackSpacing: CGFloat = 20

        static let titleTop: CGFloat = 25
        static let sideInset: CGFloat = 23
        static let sideInsetNegative: CGFloat = -23

        static let currencyTop: CGFloat = 21
        static let priceTop: CGFloat = 14
        static let periodTop: CGFloat = 27
        static let exchangeTop: CGFloat = 25

        static let rowHeight: CGFloat = 50

        static let periodScrollHeight: CGFloat = 35

        static let timerCenterYOffset: CGFloat = 22
        static let timerTrailingConstant: CGFloat = 22

        static let innerSeparatorTop: CGFloat = 20
        static let innerSeparatorInset: CGFloat = 15
        static let innerSeparatorTrailingConstant: CGFloat = 15
        static let innerSeparatorHeight: CGFloat = 1

        static let withTitleTop: CGFloat = 20
        static let withoutTitleTop: CGFloat = 16

        static let overlayInitialAlpha: CGFloat = 0
        static let overlayShownAlpha: CGFloat = 1.0
        static let overlayHiddenAlpha: CGFloat = 0.0

        static let containerLoadingAlpha: CGFloat = 0.85
        static let containerNormalAlpha: CGFloat = 1.0

        static let loadingAnimationDuration: TimeInterval = 0.2

        static let spinnerCenterYOffset: CGFloat = -6
        static let loadingLabelTop: CGFloat = 8

        static let periodButtonHeight: CGFloat = 35
        static let periodButtonMinWidth: CGFloat = 100

        static let cellVisibleAlpha: CGFloat = 1

        static let parseWhitespace = " "
        static let parseEmpty = ""
        static let parseComma = ","
        static let parseDot = "."
    }
}

