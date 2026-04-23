import UIKit

protocol HardwareAccordionCellDelegate: AnyObject {
    func hardwareCellDidToggleExpand(_ cell: HardwareAccordionCell, deviceId: String, expanded: Bool)
    func hardwareCellDidTapStart(_ cell: HardwareAccordionCell, deviceId: String, selectedMode: HardwareAccordionCell.Mode, completion: @escaping () -> Void)
    func hardwareCellDidTapSchedule(_ cell: HardwareAccordionCell, deviceId: String)
    func hardwareCellDidChangeMode(_ cell: HardwareAccordionCell, deviceId: String, mode: HardwareAccordionCell.Mode)
    func hardwareCellDidChangeEditAction(_ cell: HardwareAccordionCell, deviceId: String, action: HardwareAccordionCell.EditAction)
}

final class HardwareAccordionCell: UICollectionViewCell {

    // MARK: Public properties

    static let reuseId = UIConstants.reuseId
    weak var delegate: HardwareAccordionCellDelegate?

    // MARK: Private properties

    private var deviceId: String = ""
    private var isExpanded: Bool = false
    private var selectedMode: Mode = .standart
    private var loadingStart: Bool = false
    private var detailsBottom: NSLayoutConstraint!
    private var detailsHeight: NSLayoutConstraint!
    private var actionConstraints: [NSLayoutConstraint] = []

    private let container: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.cornerRadius = UIConstants.containerCornerRadius
        view.layer.masksToBounds = UIConstants.containerMasksToBounds
        view.layer.shadowColor = UIConstants.shadowColor.cgColor
        view.layer.shadowOpacity = UIConstants.shadowOpacity
        view.layer.shadowRadius = UIConstants.shadowRadius
        view.layer.shadowOffset = UIConstants.shadowOffset
        return view
    }()

    private let headerButton: UIControl = {
        let control = UIControl()
        control.translatesAutoresizingMaskIntoConstraints = false
        return control
    }()

    private let nameLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = UIConstants.nameTextColor
        label.font = UIConstants.nameFont
        return label
    }()

    private let chevron: UIImageView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.contentMode = .scaleAspectFit
        return imageView
    }()

    let detailsContainer: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.shadowColor = UIConstants.shadowColor.cgColor
        view.layer.shadowOpacity = UIConstants.shadowOpacity
        view.layer.shadowRadius = UIConstants.shadowRadius
        view.layer.shadowOffset = UIConstants.shadowOffset
        view.layer.cornerRadius = UIConstants.detailsCornerRadius
        view.layer.maskedCorners = UIConstants.detailsMaskedCorners
        view.layer.borderWidth = UIConstants.detailsBorderWidth
        view.layer.borderColor = UIConstants.detailsDefaultBorderColor.cgColor
        view.backgroundColor = UIConstants.detailsBackgroundColor
        view.layer.masksToBounds = UIConstants.detailsMasksToBounds
        return view
    }()

    private let detailsInner: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    private let divider: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = UIConstants.dividerColor
        return view
    }()

    private let startButton: GradientButton = {
        GradientButton(iconName: UIConstants.startButtonIconName, title: UIConstants.startButtonTitle)
    }()

    private let scheduleButton: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.layer.shadowColor = UIConstants.shadowColor.cgColor
        button.layer.shadowOpacity = UIConstants.shadowOpacity
        button.layer.shadowRadius = UIConstants.shadowRadius
        button.layer.shadowOffset = UIConstants.shadowOffset
        button.layer.cornerRadius = UIConstants.scheduleButtonCornerRadius
        button.backgroundColor = UIConstants.scheduleButtonBackgroundColor
        button.setImage(UIImage(named: UIConstants.scheduleButtonImageName), for: .normal)
        return button
    }()

    private let consumptionTitle = makeGrayLabel(UIConstants.consumptionTitleText)
    private let hashrateTitle = makeGrayLabel(UIConstants.hashrateTitleText)

    private let consumptionValue = makeGrayLabel(UIConstants.placeholderValueText)
    private let hashrateValue = makeGrayLabel(UIConstants.placeholderValueText)

    private let consumptionUnit = makeGrayLabel(UIConstants.consumptionUnitText)
    private let hashrateUnit = makeGrayLabel(UIConstants.hashrateUnitText)

    private lazy var selectButton: UIButton = makeOutlineActionButton(
        title: UIConstants.selectButtonTitle,
        borderColor: UIConstants.selectButtonBorderColor
    )

    private lazy var deselectButton: UIButton = makeOutlineActionButton(
        title: UIConstants.deselectButtonTitle,
        borderColor: UIConstants.deselectButtonBorderColor
    )

    private lazy var actionsRow: UIStackView = {
        let s = UIStackView()
        s.translatesAutoresizingMaskIntoConstraints = false
        s.axis = .horizontal
        s.spacing = UIConstants.actionsRowSpacing
        s.alignment = .fill
        s.distribution = .fill
        return s
    }()

    private lazy var economyButton = makeModeButton(title: Mode.economy.rawValue)
    private lazy var standartButton = makeModeButton(title: Mode.standart.rawValue)
    private lazy var maximalButton = makeModeButton(title: Mode.maximal.rawValue)

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
        contentView.backgroundColor = .clear

        contentView.addSubview(container)
        container.addSubview(headerButton)
        headerButton.addSubview(nameLabel)
        headerButton.addSubview(chevron)

        container.addSubview(detailsContainer)
        detailsContainer.addSubview(detailsInner)

        headerButton.addTarget(self, action: #selector(toggleExpand), for: .touchUpInside)

        setupDetailsUI()
        setupConstraints()

        startButton.addTarget(self, action: #selector(didTapStart), for: .touchUpInside)
        scheduleButton.addTarget(self, action: #selector(didTapSchedule), for: .touchUpInside)

        selectButton.addTarget(self, action: #selector(didTapSelect), for: .touchUpInside)
        deselectButton.addTarget(self, action: #selector(didTapDeselect), for: .touchUpInside)

        economyButton.addTarget(self, action: #selector(didTapEconomy), for: .touchUpInside)
        standartButton.addTarget(self, action: #selector(didTapStandart), for: .touchUpInside)
        maximalButton.addTarget(self, action: #selector(didTapMaximal), for: .touchUpInside)

        applyExpanded(false, false)
        applyMode(.standart, animated: false)
        applyActionStyle(.startSchedule, editAction: .select, animated: false)
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    // MARK: Lifecycle

    override func prepareForReuse() {
        super.prepareForReuse()

        detailsContainer.alpha = 1
        detailsContainer.isHidden = false

        delegate = nil
        deviceId = ""
        isExpanded = false
        selectedMode = .standart

        loadingStart = false
        applyModePalette(nil)
        stopButtonBreathingAnimation()

        applyExpanded(false, false)
        applyMode(.standart, animated: false)
        applyActionStyle(.startSchedule, editAction: .select, animated: false)
    }

    override func preferredLayoutAttributesFitting(_ layoutAttributes: UICollectionViewLayoutAttributes) -> UICollectionViewLayoutAttributes {
        setNeedsLayout()
        layoutIfNeeded()

        let size = contentView.systemLayoutSizeFitting(
            CGSize(width: layoutAttributes.size.width, height: UIView.layoutFittingCompressedSize.height),
            withHorizontalFittingPriority: .required,
            verticalFittingPriority: .fittingSizeLevel
        )

        let attrs = layoutAttributes
        attrs.size.height = size.height
        return attrs
    }

    // MARK: Public Methods

    func configure(
        row: DeviceRow,
        expanded: Bool,
        selectedMode: Mode = .standart,
        actionStyle: ActionStyle = .startSchedule,
        editAction: EditAction = .select,
        modePalette: ModePalette? = nil
    ) {
        deviceId = row.deviceId
        nameLabel.text = row.name

        container.backgroundColor = row.accentColor
        detailsContainer.layer.borderColor = row.accentColor.cgColor

        consumptionValue.text = String(format: UIConstants.valueFormat, row.kw)
        hashrateValue.text = String(format: UIConstants.valueFormat, row.thr)

        applyModePalette(modePalette)
        self.selectedMode = selectedMode
        applyMode(selectedMode, animated: false)

        applyActionStyle(actionStyle, editAction: editAction, animated: false)

        detailsHeight.constant = expanded ? UIConstants.detailsExpandedHeight : UIConstants.detailsCollapsedHeight
        detailsContainer.isHidden = !expanded
        detailsContainer.alpha = expanded ? UIConstants.expandedAlpha : UIConstants.collapsedAlpha
        chevron.image = UIImage(named: expanded ? UIConstants.chevronDownImageName : UIConstants.chevronUpImageName)
        isExpanded = expanded
    }

    func setEditAction(_ action: EditAction, animated: Bool) {
        applyEditActionVisual(action, animated: animated)
    }

    func setSelectedMode(_ mode: Mode, animated: Bool) {
        applyMode(mode, animated: animated)
    }

    func setHeaderInteractionEnabled(_ enabled: Bool) {
        headerButton.isUserInteractionEnabled = enabled
    }

    func setExpandedAnimated(_ expanded: Bool) {
        if expanded {
            detailsContainer.isHidden = false
            detailsContainer.alpha = UIConstants.expandedAlpha
        }

        contentView.layoutIfNeeded()

        detailsHeight.constant = expanded ? UIConstants.detailsExpandedHeight : UIConstants.detailsCollapsedHeight
        chevron.image = UIImage(named: expanded ? UIConstants.chevronDownImageName : UIConstants.chevronUpImageName)
        isExpanded = expanded

        let finalyDuration: TimeInterval = expanded ? UIConstants.expandAnimationDuration : UIConstants.collapseAnimationDuration

        UIView.animate(withDuration: finalyDuration, delay: 0, options: []) {
            self.contentView.layoutIfNeeded()
        } completion: { _ in
            if !expanded {
                self.detailsContainer.isHidden = true
            }
        }
    }

    // MARK: Private methods

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            container.topAnchor.constraint(equalTo: contentView.topAnchor),
            container.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            container.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            container.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),

            headerButton.topAnchor.constraint(equalTo: container.topAnchor),
            headerButton.leadingAnchor.constraint(equalTo: container.leadingAnchor),
            headerButton.trailingAnchor.constraint(equalTo: container.trailingAnchor),
            headerButton.heightAnchor.constraint(equalToConstant: UIConstants.headerHeight),

            nameLabel.leadingAnchor.constraint(equalTo: headerButton.leadingAnchor, constant: UIConstants.headerHorizontalInset),
            nameLabel.centerYAnchor.constraint(equalTo: headerButton.centerYAnchor),

            chevron.trailingAnchor.constraint(equalTo: headerButton.trailingAnchor, constant: -UIConstants.headerHorizontalInset),
            chevron.centerYAnchor.constraint(equalTo: headerButton.centerYAnchor),

            detailsContainer.topAnchor.constraint(equalTo: headerButton.bottomAnchor),
            detailsContainer.leadingAnchor.constraint(equalTo: container.leadingAnchor),
            detailsContainer.trailingAnchor.constraint(equalTo: container.trailingAnchor),

            detailsInner.topAnchor.constraint(equalTo: detailsContainer.topAnchor),
            detailsInner.leadingAnchor.constraint(equalTo: detailsContainer.leadingAnchor),
            detailsInner.trailingAnchor.constraint(equalTo: detailsContainer.trailingAnchor),
        ])

        detailsHeight = detailsContainer.heightAnchor.constraint(equalToConstant: UIConstants.detailsCollapsedHeight)
        detailsHeight.priority = .required
        detailsHeight.isActive = true

        detailsBottom = detailsContainer.bottomAnchor.constraint(equalTo: container.bottomAnchor)
        detailsBottom.priority = UIConstants.detailsBottomPriority
        detailsBottom.isActive = true

        detailsInner.setContentCompressionResistancePriority(.required, for: .vertical)
        detailsInner.setContentHuggingPriority(.required, for: .vertical)
    }

    private func setupDetailsUI() {
        let modesRow = UIStackView(arrangedSubviews: [economyButton, standartButton, maximalButton])
        modesRow.translatesAutoresizingMaskIntoConstraints = false
        modesRow.axis = .horizontal
        modesRow.alignment = .fill
        modesRow.distribution = .fillEqually
        modesRow.spacing = UIConstants.modesRowSpacing
        detailsInner.addSubview(modesRow)

        let leftCol = UIStackView(arrangedSubviews: [consumptionTitle, hashrateTitle])
        leftCol.axis = .vertical
        leftCol.spacing = UIConstants.metricsRowsSpacing

        let rightCol = UIStackView(arrangedSubviews: [
            makeValueRow(value: consumptionValue, unit: consumptionUnit),
            makeValueRow(value: hashrateValue, unit: hashrateUnit)
        ])
        rightCol.axis = .vertical
        rightCol.spacing = UIConstants.metricsRowsSpacing
        rightCol.alignment = .trailing

        let metricsContainer = UIStackView(arrangedSubviews: [leftCol, rightCol])
        metricsContainer.translatesAutoresizingMaskIntoConstraints = false
        metricsContainer.axis = .horizontal
        metricsContainer.distribution = .fillEqually
        metricsContainer.alignment = .top
        detailsInner.addSubview(metricsContainer)

        detailsInner.addSubview(divider)
        detailsInner.addSubview(actionsRow)

        NSLayoutConstraint.activate([
            modesRow.topAnchor.constraint(equalTo: detailsInner.topAnchor, constant: UIConstants.modesRowTopInset),
            modesRow.leadingAnchor.constraint(equalTo: detailsInner.leadingAnchor, constant: UIConstants.modesRowSideInset),
            modesRow.trailingAnchor.constraint(equalTo: detailsInner.trailingAnchor, constant: -UIConstants.modesRowSideInset),

            metricsContainer.topAnchor.constraint(equalTo: modesRow.bottomAnchor, constant: UIConstants.metricsTopSpacing),
            metricsContainer.leadingAnchor.constraint(equalTo: detailsInner.leadingAnchor, constant: UIConstants.metricsSideInset),
            metricsContainer.trailingAnchor.constraint(equalTo: detailsInner.trailingAnchor, constant: -UIConstants.metricsSideInset),

            divider.topAnchor.constraint(equalTo: metricsContainer.bottomAnchor, constant: UIConstants.dividerTopSpacing),
            divider.leadingAnchor.constraint(equalTo: detailsInner.leadingAnchor, constant: UIConstants.metricsSideInset),
            divider.trailingAnchor.constraint(equalTo: detailsInner.trailingAnchor, constant: -UIConstants.metricsSideInset),
            divider.heightAnchor.constraint(equalToConstant: UIConstants.dividerHeight),

            actionsRow.topAnchor.constraint(equalTo: divider.bottomAnchor, constant: UIConstants.actionsTopSpacing),
            actionsRow.leadingAnchor.constraint(equalTo: detailsInner.leadingAnchor, constant: UIConstants.actionsSideInset),
            actionsRow.trailingAnchor.constraint(equalTo: detailsInner.trailingAnchor, constant: -UIConstants.actionsSideInset),
            actionsRow.heightAnchor.constraint(equalToConstant: UIConstants.actionsRowHeight),
            actionsRow.bottomAnchor.constraint(equalTo: detailsInner.bottomAnchor, constant: -UIConstants.actionsBottomInset),
        ])
    }

    private func applyExpanded(_ expanded: Bool, _ animated: Bool) {
        isExpanded = expanded
        chevron.image = UIImage(named: expanded ? UIConstants.chevronDownImageName : UIConstants.chevronUpImageName)
        detailsHeight.constant = expanded ? UIConstants.detailsExpandedHeight : UIConstants.detailsCollapsedHeight
    }

    private func applyMode(_ mode: Mode, animated: Bool) {
        selectedMode = mode

        let apply = {
            self.economyButton.isGradientOn  = (mode == .economy)
            self.standartButton.isGradientOn = (mode == .standart)
            self.maximalButton.isGradientOn  = (mode == .maximal)
            self.layoutIfNeeded()
        }

        if animated {
            UIView.animate(withDuration: UIConstants.modeAnimationDuration, delay: 0, options: [.curveEaseInOut]) { apply() }
        } else {
            apply()
        }
    }

    private func applyActionStyle(_ style: ActionStyle, editAction: EditAction, animated: Bool) {
        NSLayoutConstraint.deactivate(actionConstraints)
        actionConstraints.removeAll()

        actionsRow.arrangedSubviews.forEach { v in
            actionsRow.removeArrangedSubview(v)
            v.removeFromSuperview()
        }

        actionsRow.addArrangedSubview(startButton)

        startButton.setContentCompressionResistancePriority(.required, for: .horizontal)
        startButton.setContentHuggingPriority(.defaultLow, for: .horizontal)

        switch style {
        case .startSchedule:
            actionsRow.addArrangedSubview(scheduleButton)

            scheduleButton.setContentHuggingPriority(.required, for: .horizontal)
            scheduleButton.setContentCompressionResistancePriority(.required, for: .horizontal)

            let c = scheduleButton.widthAnchor.constraint(equalTo: scheduleButton.heightAnchor)
            c.priority = .required
            actionConstraints.append(c)

        case .selectDeselect:
            actionsRow.addArrangedSubview(selectButton)
            actionsRow.addArrangedSubview(deselectButton)

            let eq = selectButton.widthAnchor.constraint(equalTo: deselectButton.widthAnchor)
            eq.priority = .required
            actionConstraints.append(eq)

            let startBigger = startButton.widthAnchor.constraint(greaterThanOrEqualTo: selectButton.widthAnchor, multiplier: UIConstants.startButtonBiggerMultiplier)
            startBigger.priority = UIConstants.softRequiredPriority
            actionConstraints.append(startBigger)

            let minStart = startButton.widthAnchor.constraint(greaterThanOrEqualToConstant: UIConstants.startButtonMinWidth)
            minStart.priority = UIConstants.softRequiredPriority
            actionConstraints.append(minStart)

            let minSel = selectButton.widthAnchor.constraint(greaterThanOrEqualToConstant: UIConstants.selectButtonMinWidth)
            minSel.priority = UIConstants.softRequiredPriority
            actionConstraints.append(minSel)

            let minDes = deselectButton.widthAnchor.constraint(greaterThanOrEqualToConstant: UIConstants.deselectButtonMinWidth)
            minDes.priority = UIConstants.softRequiredPriority
            actionConstraints.append(minDes)

            selectButton.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
            deselectButton.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        }

        NSLayoutConstraint.activate(actionConstraints)

        applyEditActionVisual(editAction, animated: animated)
        updateInteractivityForLoading()
    }

    private func applyEditActionVisual(_ action: EditAction, animated: Bool) {
        let active: CGFloat = UIConstants.editActionActiveAlpha
        let inactive: CGFloat = UIConstants.editActionInactiveAlpha

        let apply = {
            self.selectButton.alpha = (action == .select) ? active : inactive
            self.deselectButton.alpha = (action == .deselect) ? active : inactive
        }

        if animated {
            UIView.animate(withDuration: UIConstants.editActionAnimationDuration, delay: 0, options: [.curveEaseInOut, .allowUserInteraction]) { apply() }
        } else {
            apply()
        }
    }

    private func updateInteractivityForLoading() {
        let enabled = !loadingStart
        startButton.isUserInteractionEnabled = enabled
        scheduleButton.isUserInteractionEnabled = enabled
        selectButton.isUserInteractionEnabled = enabled
        deselectButton.isUserInteractionEnabled = enabled
    }

    private func makeModeButton(title: String) -> GradientStateButton {
        let button = GradientStateButton(type: .system)
        button.setTitle(title, for: .normal)
        return button
    }

    private func makeValueRow(value: UILabel, unit: UILabel) -> UIStackView {
        let stack = UIStackView(arrangedSubviews: [value, unit])
        stack.axis = .horizontal
        stack.spacing = UIConstants.valueRowSpacing
        stack.alignment = .center
        return stack
    }

    private func makeOutlineActionButton(title: String, borderColor: UIColor) -> UIButton {
        let b = UIButton(type: .system)
        b.translatesAutoresizingMaskIntoConstraints = false
        b.setTitle(title, for: .normal)
        b.setTitleColor(UIConstants.actionButtonTitleColor, for: .normal)
        b.titleLabel?.font = UIConstants.actionButtonFont

        b.backgroundColor = UIConstants.actionButtonBackgroundColor
        b.layer.cornerRadius = UIConstants.actionButtonCornerRadius
        b.layer.borderWidth = UIConstants.actionButtonBorderWidth
        b.layer.borderColor = borderColor.cgColor

        b.layer.shadowColor = UIConstants.shadowColor.cgColor
        b.layer.shadowOpacity = UIConstants.shadowOpacity
        b.layer.shadowRadius = UIConstants.shadowRadius
        b.layer.shadowOffset = UIConstants.shadowOffset

        return b
    }

    private static func makeGrayLabel(_ text: String) -> UILabel {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = text
        label.textColor = UIConstants.grayLabelColor
        label.font = UIConstants.grayLabelFont
        return label
    }

    private func applyLoadingState(_ loading: Bool) {
        guard loadingStart != loading else { return }
        loadingStart = loading

        updateInteractivityForLoading()

        if loading {
            startButtonBreathingAnimation()
        } else {
            stopButtonBreathingAnimation()
            startButton.alpha = 1
            startButton.transform = .identity
        }
    }

    private func startButtonBreathingAnimation() {
        let anim = CABasicAnimation(keyPath: UIConstants.breathingKeyPath)
        anim.fromValue = UIConstants.breathingFromOpacity
        anim.toValue = UIConstants.breathingToOpacity
        anim.duration = UIConstants.breathingDuration
        anim.autoreverses = true
        anim.repeatCount = .infinity
        anim.timingFunction = CAMediaTimingFunction(name: .easeInEaseOut)
        startButton.layer.add(anim, forKey: UIConstants.breathingAnimationKey)
    }

    private func stopButtonBreathingAnimation() {
        startButton.layer.removeAnimation(forKey: UIConstants.breathingAnimationKey)
    }

    private func applyModePalette(_ palette: ModePalette?) {
        if let p = palette {
            economyButton.selectedFillStyle = .solid(p.economy)
            standartButton.selectedFillStyle = .solid(p.standart)
            maximalButton.selectedFillStyle = .solid(p.maximal)

            economyButton.borderColor = p.economy.withAlphaComponent(UIConstants.modeButtonBorderAlpha)
            standartButton.borderColor = p.standart.withAlphaComponent(UIConstants.modeButtonBorderAlpha)
            maximalButton.borderColor = p.maximal.withAlphaComponent(UIConstants.modeButtonBorderAlpha)

        } else {
            economyButton.selectedFillStyle = .gradient
            standartButton.selectedFillStyle = .gradient
            maximalButton.selectedFillStyle = .gradient

            economyButton.borderColor = nil
            standartButton.borderColor = nil
            maximalButton.borderColor = nil
        }
    }

    @objc private func didTapEconomy() {
        applyMode(.economy, animated: true)
        delegate?.hardwareCellDidChangeMode(self, deviceId: deviceId, mode: .economy)
    }

    @objc private func didTapStandart() {
        applyMode(.standart, animated: true)
        delegate?.hardwareCellDidChangeMode(self, deviceId: deviceId, mode: .standart)
    }

    @objc private func didTapMaximal() {
        applyMode(.maximal, animated: true)
        delegate?.hardwareCellDidChangeMode(self, deviceId: deviceId, mode: .maximal)
    }

    @objc private func didTapSelect() {
        applyEditActionVisual(.select, animated: true)
        delegate?.hardwareCellDidChangeEditAction(self, deviceId: deviceId, action: .select)
    }

    @objc private func didTapDeselect() {
        applyEditActionVisual(.deselect, animated: true)
        delegate?.hardwareCellDidChangeEditAction(self, deviceId: deviceId, action: .deselect)
    }

    @objc private func didTapStart() {
        applyLoadingState(true)
        delegate?.hardwareCellDidTapStart(self, deviceId: deviceId, selectedMode: selectedMode) { [weak self] in
            self?.applyLoadingState(false)
        }
    }

    @objc private func didTapSchedule() {
        delegate?.hardwareCellDidTapSchedule(self, deviceId: deviceId)
    }

    @objc private func toggleExpand() {
        delegate?.hardwareCellDidToggleExpand(self, deviceId: deviceId, expanded: !isExpanded)
    }
}

// MARK: - Mode

extension HardwareAccordionCell {
    enum Mode: String {
        case economy = "ECONOMY"
        case standart = "STANDART"
        case maximal = "MAXIMAL"
    }
}

// MARK: - ActionStyle

extension HardwareAccordionCell {
    enum ActionStyle {
        case startSchedule
        case selectDeselect
    }
}

// MARK: - EditAction

extension HardwareAccordionCell {
    enum EditAction {
        case select
        case deselect
    }
}

// MARK: - ModePalette

extension HardwareAccordionCell {
    struct ModePalette {
        let economy: UIColor
        let standart: UIColor
        let maximal: UIColor
    }
}

// MARK: - UIConstants

private extension HardwareAccordionCell {
    enum UIConstants {

        // General
        static let reuseId = "HardwareAccordionCell"

        // Sizes / Layout
        static let containerCornerRadius: CGFloat = 15
        static let detailsCornerRadius: CGFloat = 15
        static let scheduleButtonCornerRadius: CGFloat = 15
        static let actionButtonCornerRadius: CGFloat = 15

        static let headerHeight: CGFloat = 50
        static let headerHorizontalInset: CGFloat = 20

        static let detailsExpandedHeight: CGFloat = 235
        static let detailsCollapsedHeight: CGFloat = 0

        static let modesRowTopInset: CGFloat = 30
        static let modesRowSideInset: CGFloat = 16
        static let modesRowSpacing: CGFloat = 34

        static let metricsTopSpacing: CGFloat = 20
        static let metricsSideInset: CGFloat = 33
        static let metricsRowsSpacing: CGFloat = 16

        static let dividerTopSpacing: CGFloat = 20
        static let dividerHeight: CGFloat = 1

        static let actionsTopSpacing: CGFloat = 19
        static let actionsSideInset: CGFloat = 16
        static let actionsRowHeight: CGFloat = 52
        static let actionsBottomInset: CGFloat = 26
        static let actionsRowSpacing: CGFloat = 16

        static let valueRowSpacing: CGFloat = 18

        // Priorities
        static let detailsBottomPriority: UILayoutPriority = UILayoutPriority(999)
        static let softRequiredPriority: UILayoutPriority = UILayoutPriority(999)

        // Alpha
        static let expandedAlpha: CGFloat = 1
        static let collapsedAlpha: CGFloat = 0
        static let editActionActiveAlpha: CGFloat = 1.0
        static let editActionInactiveAlpha: CGFloat = 0.25

        // Animation
        static let modeAnimationDuration: TimeInterval = 0.15
        static let editActionAnimationDuration: TimeInterval = 0.15
        static let expandAnimationDuration: TimeInterval = 0.22
        static let collapseAnimationDuration: TimeInterval = 0.47

        // Shadows
        static let shadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.03)
        static let shadowOpacity: Float = 1
        static let shadowRadius: CGFloat = 28.6
        static let shadowOffset = CGSize(width: 0, height: 23)

        // Masks
        static let containerMasksToBounds: Bool = true
        static let detailsMasksToBounds: Bool = true

        // Colors
        static let nameTextColor: UIColor = .white
        static let dividerColor = UIColor(red: 0.231, green: 0.231, blue: 0.231, alpha: 1)
        static let detailsBackgroundColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)
        static let detailsDefaultBorderColor = UIColor(red: 0.17, green: 0.211, blue: 0.583, alpha: 1)

        static let selectButtonBorderColor = UIColor(red: 0, green: 0.522, blue: 0.052, alpha: 1)
        static let deselectButtonBorderColor = UIColor(red: 0.894, green: 0.208, blue: 0.184, alpha: 1)

        static let scheduleButtonBackgroundColor = UIColor(red: 0.411, green: 0.462, blue: 0.921, alpha: 1)

        static let actionButtonTitleColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        static let actionButtonBackgroundColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)

        static let grayLabelColor = UIColor(red: 0.502, green: 0.502, blue: 0.502, alpha: 1)

        // Fonts
        static let nameFont = UIFont(name: "Lato-Bold", size: 16)
        static let actionButtonFont = UIFont(name: "Lato-Bold", size: 16)
        static let grayLabelFont = UIFont(name: "Lato-Bold", size: 12)

        // Details Container
        static let detailsBorderWidth: CGFloat = 1
        static let detailsMaskedCorners: CACornerMask = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]

        // Titles / Strings
        static let startButtonIconName = "play"
        static let startButtonTitle = "Start"
        static let scheduleButtonImageName = "calendar"

        static let consumptionTitleText = "Consumption"
        static let hashrateTitleText = "Hash rate"
        static let placeholderValueText = "~ 999"
        static let consumptionUnitText = "Kw/h"
        static let hashrateUnitText = "Th/h"

        static let selectButtonTitle = "Select"
        static let deselectButtonTitle = "Deselect"
        static let actionButtonBorderWidth: CGFloat = 1

        static let valueFormat = "~ %.2f"

        static let chevronDownImageName = "chevron_down"
        static let chevronUpImageName = "chevron_up"

        // Layout multipliers / minimums
        static let startButtonBiggerMultiplier: CGFloat = 1.25
        static let startButtonMinWidth: CGFloat = 120
        static let selectButtonMinWidth: CGFloat = 110
        static let deselectButtonMinWidth: CGFloat = 110

        // Breathing animation
        static let breathingKeyPath = "opacity"
        static let breathingFromOpacity: CGFloat = 1.0
        static let breathingToOpacity: CGFloat = 0.65
        static let breathingDuration: TimeInterval = 0.75
        static let breathingAnimationKey = "breathingOpacity"

        static let modeButtonBorderAlpha: CGFloat = 0.45
    }
}
