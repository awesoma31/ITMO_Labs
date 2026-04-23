import UIKit

protocol HomeViewControllerInput: AnyObject {
    func configureDevices(devices: [DeviceRow])
    func configurePoints(points: [MetricPoint], deviceId: String)
    func configureUsername(username: String)
    func showStartScreen()
    func successChangeMode(isSuccess: Bool)
    func showProft(exchangeValue: Double, withoutValue: Double, withValue: Double)
}

typealias HomeViewControllerOutput = HomeInteractorInput

final class HomeViewController: UIViewController {

    // MARK: Public properties

    var interactor: HomeViewControllerOutput?
    var delegate: Coordinator?

    // MARK: Private properties

    private var hardwareAnimatingIds = Set<String>()
    private var seriesByDeviceId: [String: Points] = [:]
    private var currentMetric: Metrics = .hashrate
    private var devices: [DeviceRow] = []
    private var isLoadingDevices = false
    private var keyboardHeight: CGFloat = Constants.zeroCGFloat
    private var activeTextField: UITextField?
    private var selectedPeriod: Period = .hour
    private var selectedPrice: Double = Constants.zeroDouble
    private var profitTimer: Timer?
    private let timerInterval: TimeInterval = Constants.profitTimerInterval
    private let loadingOverlay = LoadingOverlayView()
    private var pendingRelayout = false
    private var pendingRelayoutDuration: TimeInterval = Constants.zeroTimeInterval
    private var relayoutCtx = RelayoutAnimCtx()
    private var expandedDeviceIds: Set<String> = []

    private var selectedRange: TimeRange = .year {
        didSet {
            guard oldValue != selectedRange else { return }
            updatePoints()
        }
    }

    private let greetingLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.numberOfLines = 1
        label.textColor = Constants.greetingTextColor
        return label
    }()

    private let profileButton: UIButton = {
        let button = UIButton(type: .system)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setImage(UIImage(systemName: Constants.profileImageName), for: .normal)
        button.tintColor = Constants.profileTintColor
        return button
    }()

    private lazy var collectionView: UICollectionView = {
        let cv = UICollectionView(frame: .zero, collectionViewLayout: makeLayout())
        cv.translatesAutoresizingMaskIntoConstraints = false
        cv.backgroundColor = .clear
        cv.alwaysBounceVertical = true
        cv.contentInset = UIEdgeInsets(
            top: Constants.collectionTopInset,
            left: Constants.collectionLeftInset,
            bottom: Constants.collectionBottomInset,
            right: Constants.collectionRightInset
        )
        cv.dataSource = self
        cv.delegate = self
        cv.showsVerticalScrollIndicator = false
        cv.register(ChartCardCell.self, forCellWithReuseIdentifier: ChartCardCell.reuseId)
        cv.register(TimeRangeSelectorCell.self, forCellWithReuseIdentifier: TimeRangeSelectorCell.reuseId)
        cv.register(HardwareAccordionCell.self, forCellWithReuseIdentifier: HardwareAccordionCell.reuseId)
        cv.register(
            HardwaresHeaderView.self,
            forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader,
            withReuseIdentifier: HardwaresHeaderView.reuseId
        )
        cv.register(ProfitCalculatorCell.self, forCellWithReuseIdentifier: ProfitCalculatorCell.reuseId)
        cv.register(
            SectionSeparatorView.self,
            forSupplementaryViewOfKind: UICollectionView.elementKindSectionFooter,
            withReuseIdentifier: SectionSeparatorView.reuseId
        )
        return cv
    }()

    // MARK: Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = Constants.viewBackgroundColor

        navigationItem.backBarButtonItem = UIBarButtonItem(title: Constants.emptyBackTitle, style: .plain, target: nil, action: nil)

        view.addSubview(greetingLabel)
        view.addSubview(profileButton)
        view.addSubview(collectionView)
        view.addSubview(loadingOverlay)

        profileButton.addTarget(self, action: #selector(tapLogoutButton), for: .touchUpInside)

        setupKeyboardDismissal()
        setupKeyboardObservers()

        interactor?.getUsername()

        isLoadingDevices = true
        collectionView.reloadSections(IndexSet(integer: Section.chart.rawValue))

        loadingOverlay.show(text: Constants.loadingDevicesText)
        greetingLabel.isHidden = true
        profileButton.isHidden = true
        collectionView.isHidden = true

        interactor?.getDevices() { [weak self] in
            self?.greetingLabel.isHidden = false
            self?.profileButton.isHidden = false
            self?.collectionView.isHidden = false
            self?.loadingOverlay.hide()
        }
        setupConstraints()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setupProfitTimer()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        stopProfitTimer()
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
        stopProfitTimer()
    }

    // MARK: Private methods

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            greetingLabel.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            greetingLabel.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: Constants.greetingLeading),

            profileButton.centerYAnchor.constraint(equalTo: greetingLabel.centerYAnchor),
            profileButton.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: Constants.profileTrailing),
            profileButton.widthAnchor.constraint(equalToConstant: 30),
            profileButton.heightAnchor.constraint(equalToConstant: 30),

            collectionView.topAnchor.constraint(equalTo: greetingLabel.bottomAnchor, constant: Constants.collectionTopToGreetingBottom),
            collectionView.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: Constants.collectionLeading),
            collectionView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: Constants.collectionTrailing),
            collectionView.bottomAnchor.constraint(equalTo: view.bottomAnchor),

            loadingOverlay.topAnchor.constraint(equalTo: view.topAnchor),
            loadingOverlay.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            loadingOverlay.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            loadingOverlay.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }

    private func makeLayout() -> UICollectionViewLayout {
        UICollectionViewCompositionalLayout { [weak self] sectionIndex, env in
            guard self != nil else { return nil }

            guard let section = Section(rawValue: sectionIndex) else { return nil }

            switch section {
            case .chart:
                let itemSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .estimated(Constants.chartEstimatedHeight)
                )
                let item = NSCollectionLayoutItem(layoutSize: itemSize)

                let groupSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .estimated(Constants.chartEstimatedHeight)
                )
                let group = NSCollectionLayoutGroup.vertical(layoutSize: groupSize, subitems: [item])

                let section = NSCollectionLayoutSection(group: group)
                section.contentInsets = .init(
                    top: Constants.zeroCGFloat,
                    leading: Constants.zeroCGFloat,
                    bottom: Constants.chartSectionBottomInset,
                    trailing: Constants.zeroCGFloat
                )
                return section

            case .range:
                let itemSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .absolute(Constants.rangeAbsoluteHeight)
                )
                let item = NSCollectionLayoutItem(layoutSize: itemSize)

                let groupSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .absolute(Constants.rangeAbsoluteHeight)
                )
                let group = NSCollectionLayoutGroup.vertical(layoutSize: groupSize, subitems: [item])

                let section = NSCollectionLayoutSection(group: group)
                section.contentInsets = NSDirectionalEdgeInsets(
                    top: Constants.zeroCGFloat,
                    leading: Constants.zeroCGFloat,
                    bottom: Constants.rangeSectionBottomInset,
                    trailing: Constants.zeroCGFloat
                )

                return section

            case .hardwares:
                let itemSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .estimated(Constants.hardwaresEstimatedHeight)
                )
                let item = NSCollectionLayoutItem(layoutSize: itemSize)

                let groupSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .estimated(Constants.hardwaresEstimatedHeight)
                )
                let group = NSCollectionLayoutGroup.vertical(layoutSize: groupSize, subitems: [item])

                let section = NSCollectionLayoutSection(group: group)
                section.interGroupSpacing = Constants.hardwaresInterGroupSpacing

                section.contentInsets = NSDirectionalEdgeInsets(
                    top: Constants.hardwaresSectionTopInset,
                    leading: Constants.zeroCGFloat,
                    bottom: Constants.hardwaresSectionBottomInset,
                    trailing: Constants.zeroCGFloat
                )

                let headerSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .absolute(Constants.hardwaresHeaderHeight)
                )

                let header = NSCollectionLayoutBoundarySupplementaryItem(
                    layoutSize: headerSize,
                    elementKind: UICollectionView.elementKindSectionHeader,
                    alignment: .top
                )

                let footerSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .absolute(Constants.hardwaresFooterHeight)
                )

                let footer = NSCollectionLayoutBoundarySupplementaryItem(
                    layoutSize: footerSize,
                    elementKind: UICollectionView.elementKindSectionFooter,
                    alignment: .bottom
                )

                section.boundarySupplementaryItems = [header, footer]

                return section

            case .profit:
                let itemSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .estimated(Constants.profitEstimatedHeight)
                )
                let item = NSCollectionLayoutItem(layoutSize: itemSize)

                let groupSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(Constants.fractionalWidthFull),
                    heightDimension: .estimated(Constants.profitEstimatedHeight)
                )
                let group = NSCollectionLayoutGroup.vertical(layoutSize: groupSize, subitems: [item])

                let section = NSCollectionLayoutSection(group: group)
                section.contentInsets = .init(
                    top: Constants.profitSectionTopInset,
                    leading: Constants.zeroCGFloat,
                    bottom: Constants.zeroCGFloat,
                    trailing: Constants.zeroCGFloat
                )
                return section
            }
        }
    }

    private func updatePoints() {
        let deviceIds = seriesByDeviceId.keys
        deviceIds.forEach { deviceId in
            interactor?.getPoints(
                timeRange: selectedRange,
                currentMetric: currentMetric,
                deviceId: deviceId
            )
        }
    }

    private func addPoints(points: [MetricPoint], deviceId: String) {
        guard let row = devices.first(where: { $0.deviceId == deviceId }) else { return }

        seriesByDeviceId[deviceId] = Points(
            id: deviceId,
            name: row.name,
            color: row.accentColor,
            points: points
        )

        updateChartCellSeries()
    }

    private func deletePoints(deviceId: String) {
        seriesByDeviceId.removeValue(forKey: deviceId)
        updateChartCellSeries()
    }

    private func updateChartCellSeries() {
        let ip = IndexPath(item: Constants.firstItem, section: Section.chart.rawValue)
        guard let cell = collectionView.cellForItem(at: ip) as? ChartCardCell else {
            collectionView.reloadItems(at: [ip])
            return
        }
        cell.updateSeries(rebuildSeriesArray(), timeRange: selectedRange)
    }

    private func rebuildSeriesArray() -> [Points] {
        devices.compactMap { seriesByDeviceId[$0.deviceId] }
    }

    private func scrollToTextFieldIfNeeded(_ textField: UITextField) {
        guard let cell = textField.superview?.superview as? ProfitCalculatorCell,
              let indexPath = collectionView.indexPath(for: cell) else { return }

        guard let cellFrame = collectionView.layoutAttributesForItem(at: indexPath)?.frame else { return }

        let cellRectInView = collectionView.convert(cellFrame, to: view)

        let bottomOfCell = cellRectInView.maxY
        let topOfKeyboard = view.bounds.height - keyboardHeight
        let overlap = bottomOfCell - topOfKeyboard

        if overlap > 0 {
            let targetOffset = CGPoint(
                x: Constants.zeroCGFloat,
                y: collectionView.contentOffset.y + overlap + Constants.scrollExtraOffset
            )

            UIView.animate(withDuration: Constants.scrollAnimationDuration) {
                self.collectionView.contentOffset = targetOffset
            }
        }
    }

    private func performCollectionRelayout(duration: TimeInterval) {
        if duration <= Constants.zeroTimeInterval {
            UIView.performWithoutAnimation {
                self.collectionView.performBatchUpdates(nil)
                self.collectionView.layoutIfNeeded()
            }
            return
        }

        UIView.animate(withDuration: duration, delay: Constants.zeroTimeInterval, options: []) {
            self.collectionView.performBatchUpdates(nil)
            self.collectionView.layoutIfNeeded()
        }
    }

    @objc private func tapLogoutButton() {
        interactor?.logout()
    }
}

// MARK: - UICollectionViewDataSource

extension HomeViewController: UICollectionViewDataSource {

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        Section.allCases.count
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        guard let s = Section(rawValue: section) else { return 0 }
        switch s {
        case .chart, .range, .profit:
            return 1
        case .hardwares:
            return devices.count
        }
    }

    func collectionView(_ collectionView: UICollectionView,
                        cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {

        guard let s = Section(rawValue: indexPath.section) else {
            return UICollectionViewCell()
        }

        switch s {
        case .chart:
            let cell = collectionView.dequeueReusableCell(
                withReuseIdentifier: ChartCardCell.reuseId,
                for: indexPath
            ) as! ChartCardCell

            cell.configure(
                dateText: Date.todayMoscow,
                rows: devices,
                series: rebuildSeriesArray(),
                timeRange: selectedRange,
                isLoadingDevices: isLoadingDevices
            )
            cell.delegate = self
            collectionView.panGestureRecognizer.require(toFail: cell.handlerPanGesture)
            collectionView.panGestureRecognizer.require(toFail: cell.tablePanGesture)
            return cell

        case .range:
            let cell = collectionView.dequeueReusableCell(
                withReuseIdentifier: TimeRangeSelectorCell.reuseId,
                for: indexPath
            ) as! TimeRangeSelectorCell

            cell.configure(selected: selectedRange)
            cell.delegate = self
            return cell

        case .hardwares:
            let cell = collectionView.dequeueReusableCell(
                withReuseIdentifier: HardwareAccordionCell.reuseId,
                for: indexPath
            ) as! HardwareAccordionCell

            let row = devices[indexPath.item]
            let expanded = expandedDeviceIds.contains(row.deviceId)
            cell.configure(row: row, expanded: expanded)
            cell.delegate = self
            return cell

        case .profit:
            let cell = collectionView.dequeueReusableCell(
                withReuseIdentifier: ProfitCalculatorCell.reuseId,
                for: indexPath
            ) as! ProfitCalculatorCell

            cell.delegate = self
            return cell
        }
    }

    func collectionView(_ collectionView: UICollectionView,
                        viewForSupplementaryElementOfKind kind: String,
                        at indexPath: IndexPath) -> UICollectionReusableView {

        if kind == UICollectionView.elementKindSectionHeader {
            guard let s = Section(rawValue: indexPath.section), s == .hardwares else {
                return UICollectionReusableView()
            }

            let header = collectionView.dequeueReusableSupplementaryView(
                ofKind: kind,
                withReuseIdentifier: HardwaresHeaderView.reuseId,
                for: indexPath
            ) as! HardwaresHeaderView

            header.configure(title: Constants.hardwaresHeaderTitle, backgroundColor: Constants.viewBackgroundColor)
            return header
        }

        if kind == UICollectionView.elementKindSectionFooter {
            guard let s = Section(rawValue: indexPath.section), s == .hardwares else {
                return UICollectionReusableView()
            }

            let footer = collectionView.dequeueReusableSupplementaryView(
                ofKind: kind,
                withReuseIdentifier: SectionSeparatorView.reuseId,
                for: indexPath
            ) as! SectionSeparatorView

            return footer
        }

        return UICollectionReusableView()
    }
}

// MARK: - ChartCardCellDelegate

extension HomeViewController: ChartCardCellDelegate {
    func chartCardCellDidRequestLayoutUpdate(_ cell: ChartCardCell) {
        collectionView.performBatchUpdates(nil)
    }

    func chartCardCellDidTapExpand(_ cell: ChartCardCell) { }

    func chartCardCellDidTapTitle(_ currentMetrics: Metrics, _ ids: [String]) {
        currentMetric = currentMetrics
        for deviceId in ids {
            interactor?.getPoints(timeRange: selectedRange, currentMetric: currentMetric, deviceId: deviceId)
        }
    }

    func chartCardCellRequestCollectionRelayout(_ cell: ChartCardCell, duration: TimeInterval, delay: TimeInterval) {

        if duration <= Constants.zeroTimeInterval {
            if collectionView.isDragging || collectionView.isDecelerating {
                pendingRelayout = true
                pendingRelayoutDuration = max(pendingRelayoutDuration, Constants.zeroTimeInterval)
                return
            }
            performCollectionRelayout(duration: Constants.zeroTimeInterval)
            return
        }

        if collectionView.isDecelerating {
            collectionView.setContentOffset(collectionView.contentOffset, animated: false)
        }

        if collectionView.isDragging {
            pendingRelayout = true
            pendingRelayoutDuration = max(pendingRelayoutDuration, duration)
            return
        }

        performCollectionRelayout(duration: duration)
    }

    func chartCardCellDidTapTableCell(deviceId: String, selected: Bool) {
        if selected {
            interactor?.getPoints(timeRange: selectedRange, currentMetric: currentMetric, deviceId: deviceId)
        } else {
            deletePoints(deviceId: deviceId)
        }
    }

    func chartCardCellDidRequestRefresh(_ cell: ChartCardCell) {
        interactor?.getDevices() { [weak cell] in
            cell?.endRefreshing()
        }
        updatePoints()
    }
}

// MARK: - Section

private extension HomeViewController {
    enum Section: Int, CaseIterable {
        case chart = 0
        case range = 1
        case hardwares = 2
        case profit = 3
    }
}

// MARK: - RelayoutAnimCtx

private extension HomeViewController {
    struct RelayoutAnimCtx {
        var active = false
        var duration: TimeInterval = Constants.zeroTimeInterval
        var appearShiftY: CGFloat = Constants.zeroCGFloat
    }
}

// MARK: - TimeRangeSelectorCellDelegate

extension HomeViewController: TimeRangeSelectorCellDelegate {
    func timeRangeSelectorCell(_ cell: TimeRangeSelectorCell, didSelect range: TimeRange) {
        selectedRange = range
        cell.configure(selected: selectedRange)
    }
}

// MARK: - HardwareAccordionCellDelegate

extension HomeViewController: HardwareAccordionCellDelegate {
    func hardwareCellDidChangeEditAction(_ cell: HardwareAccordionCell, deviceId: String, action: HardwareAccordionCell.EditAction) { }

    func hardwareCellDidChangeMode(_ cell: HardwareAccordionCell, deviceId: String, mode: HardwareAccordionCell.Mode) { }

    func hardwareCellDidToggleExpand(_ cell: HardwareAccordionCell, deviceId: String, expanded: Bool) {
        let duration: TimeInterval = Constants.hardwareToggleDuration

        guard !hardwareAnimatingIds.contains(deviceId) else { return }
        hardwareAnimatingIds.insert(deviceId)

        if expanded { expandedDeviceIds.insert(deviceId) }
        else { expandedDeviceIds.remove(deviceId) }

        cell.setExpandedAnimated(expanded)

        CATransaction.begin()
        CATransaction.setAnimationDuration(duration)
        CATransaction.setAnimationTimingFunction(CAMediaTimingFunction(name: .easeInEaseOut))
        CATransaction.setCompletionBlock { [weak self] in
            self?.hardwareAnimatingIds.remove(deviceId)
        }

        collectionView.performBatchUpdates({
            collectionView.collectionViewLayout.invalidateLayout()
        }, completion: nil)

        CATransaction.commit()
    }

    func hardwareCellDidTapStart(_ cell: HardwareAccordionCell, deviceId: String, selectedMode: HardwareAccordionCell.Mode, completion: @escaping () -> Void) {
        let mode = switch selectedMode {
        case .economy:
            Constants.modeECO
        case .standart:
            Constants.modeSTANDARD
        case .maximal:
            Constants.modeOVERCLOCK
        }

        let deviceRow = devices
            .filter { $0.deviceId == deviceId }
            .first
        interactor?.changeMode(minerIds: deviceRow?.asicIds ?? [], mode: mode, completion: completion)
    }

    func hardwareCellDidTapSchedule(_ cell: HardwareAccordionCell, deviceId: String) {
        delegate?.showMimingCalendar(devices: devices, openedDeviceId: deviceId)
    }
}

// MARK: - ProfitCalculatorCellDelegate

extension HomeViewController: ProfitCalculatorCellDelegate {
    func profitCalculatorCellDidChangeCurrency(_ cell: ProfitCalculatorCell) {
        delegate?.showCoinsScreen()
    }

    func profitCalculatorCellDidChangePeriod(_ cell: ProfitCalculatorCell, selectedPeriod: Period) {
        self.selectedPeriod = selectedPeriod
        restartProfitTimer()
        cell.setLoading(true)
        interactor?.calculateProfit(price: selectedPrice, period: selectedPeriod) { cell.setLoading(false) }
    }

    func profitCalculatorCellDidChangePrice(_ cell: ProfitCalculatorCell, newPrice: Double) {
        self.selectedPrice = newPrice
        restartProfitTimer()
        cell.setLoading(true)
        interactor?.calculateProfit(price: selectedPrice, period: selectedPeriod) { cell.setLoading(false) }
    }

    func setCoinsButton(title: String, iconName: String) {
        let cell = collectionView.cellForItem(at: IndexPath(item: Constants.firstItem, section: Constants.profitSectionIndex)) as! ProfitCalculatorCell
        cell.setCoinsButton(title: title, iconName: iconName)
        restartProfitTimer()
        cell.setLoading(true)
        interactor?.calculateProfit(price: selectedPrice, period: selectedPeriod) { cell.setLoading(false) }
    }

    func profitCalculatorCellDidBeginEditing(_ cell: ProfitCalculatorCell, textField: UITextField) {
        activeTextField = textField
        scrollToTextFieldIfNeeded(textField)
    }

    func profitCalculatorCellDidEndEditing(_ cell: ProfitCalculatorCell, textField: UITextField) {
        activeTextField = nil
    }
}

// MARK: - HomeViewControllerInput

extension HomeViewController: HomeViewControllerInput {

    func showStartScreen() {
        delegate?.showAuthScreen()
    }

    func configureDevices(devices: [DeviceRow]) {
        self.devices = devices
        isLoadingDevices = false
        expandedDeviceIds.removeAll()
        collectionView.reloadData()
    }

    func configurePoints(points: [MetricPoint], deviceId: String) {
        addPoints(points: points, deviceId: deviceId)
    }

    func configureUsername(username: String) {
        let first = Constants.greetingFirst
        let second = "\(Constants.greetingSecondPrefix)\(username)\(Constants.greetingSecondSuffix)"
        let regulatFont = UIFont(name: Constants.latoRegularFontName, size: Constants.greetingFontSize)!
        let boldFont = UIFont(name: Constants.latoBoldFontName, size: Constants.greetingFontSize)!
        let attr = NSMutableAttributedString(
            string: first,
            attributes: [.font: regulatFont]
        )
        attr.append(NSAttributedString(
            string: second,
            attributes: [.font: boldFont]
        ))
        greetingLabel.attributedText = attr
    }

    func successChangeMode(isSuccess: Bool) {
        let message: String
        let style: TopToastStyle

        if isSuccess {
            style = .success
            message = Constants.toastSuccessMessage
        } else {
            style = .error
            message = Constants.toastErrorMessage
        }

        TopToast.show(style: style, message: message, duration: Constants.toastDuration, fontSize: Constants.toastFontSize)
    }

    func showProft(exchangeValue: Double, withoutValue: Double, withValue: Double) {
        guard let cell = collectionView.cellForItem(at: IndexPath(row: Constants.firstRow, section: Constants.profitSectionIndex)) as? ProfitCalculatorCell else {
            return
        }
        cell.configure(
            exchangeRateText: String(exchangeValue),
            withoutExpenses: String(withoutValue),
            withExpenses: String(withValue)
        )
    }
}

// MARK: - UICollectionViewDelegate

extension HomeViewController: UICollectionViewDelegate {
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        if !decelerate { flushPendingRelayoutIfNeeded() }
    }

    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        flushPendingRelayoutIfNeeded()
    }

    private func flushPendingRelayoutIfNeeded() {
        guard pendingRelayout else { return }
        pendingRelayout = false
        let d = pendingRelayoutDuration
        pendingRelayoutDuration = Constants.zeroTimeInterval
        performCollectionRelayout(duration: d > Constants.zeroTimeInterval ? d : Constants.zeroTimeInterval)
    }

    func collectionView(_ collectionView: UICollectionView,
                        willDisplay cell: UICollectionViewCell,
                        forItemAt indexPath: IndexPath) {
        let v = cell.contentView
        v.alpha = Constants.alphaHidden
        v.transform = CGAffineTransform(translationX: Constants.zeroCGFloat, y: Constants.zeroCGFloat)

        UIView.animate(withDuration: Constants.zeroTimeInterval, delay: Constants.zeroTimeInterval, options: [.curveEaseInOut, .allowUserInteraction]) {
            v.alpha = Constants.alphaVisible
            v.transform = .identity
        }
    }
}

// MARK: - Simplified Keyboard Handling

extension HomeViewController {
    private func setupKeyboardDismissal() {
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        tapGesture.cancelsTouchesInView = false
        view.addGestureRecognizer(tapGesture)
    }

    @objc private func dismissKeyboard() {
        view.endEditing(true)
    }

    private func setupKeyboardObservers() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillShow),
            name: UIResponder.keyboardWillShowNotification,
            object: nil
        )
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillHide),
            name: UIResponder.keyboardWillHideNotification,
            object: nil
        )
    }

    @objc private func keyboardWillShow(notification: NSNotification) {
        guard let userInfo = notification.userInfo,
              let keyboardFrame = userInfo[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect,
              let animationDuration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? TimeInterval else { return }
        
        let keyboardHeight = keyboardFrame.height - view.safeAreaInsets.bottom
        self.keyboardHeight = keyboardHeight
        
        if let activeTextField = activeTextField {
            scrollToTextFieldImmediately(activeTextField)
        }

        UIView.animate(withDuration: animationDuration) {
            let bottomInset = keyboardHeight + Constants.keyboardExtraBottomInset
            self.collectionView.contentInset = UIEdgeInsets(
                top: Constants.collectionTopInset,
                left: Constants.collectionLeftInset,
                bottom: bottomInset,
                right: Constants.collectionRightInset
            )
            self.collectionView.scrollIndicatorInsets = UIEdgeInsets(
                top: Constants.collectionTopInset,
                left: Constants.collectionLeftInset,
                bottom: bottomInset,
                right: Constants.collectionRightInset
            )
        }
    }

    private func scrollToTextFieldImmediately(_ textField: UITextField) {
        guard let cell = textField.superview?.superview?.superview as? ProfitCalculatorCell,
              let indexPath = collectionView.indexPath(for: cell) else {
            return
        }

        collectionView.scrollToItem(at: indexPath, at: .top, animated: false)

        DispatchQueue.main.async {
            guard let cellRect = self.collectionView.layoutAttributesForItem(at: indexPath)?.frame else { return }
            let _ = self.collectionView.convert(cellRect, to: self.view)

            let textFieldRectInCell = textField.convert(textField.bounds, to: cell)
            let textFieldRectInView = cell.convert(textFieldRectInCell, to: self.view)

            let bottomOfTextField = textFieldRectInView.maxY
            let topOfKeyboard = self.view.bounds.height - self.keyboardHeight
            let overlap = bottomOfTextField - topOfKeyboard

            if overlap > 0 {
                let currentOffset = self.collectionView.contentOffset
                let newOffset = CGPoint(x: currentOffset.x, y: currentOffset.y + overlap + Constants.keyboardScrollExtraOffset)

                self.collectionView.setContentOffset(newOffset, animated: false)
            }
        }
    }

    @objc private func keyboardWillHide(notification: NSNotification) {
        guard let userInfo = notification.userInfo,
              let animationDuration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? TimeInterval else { return }

        activeTextField = nil
        keyboardHeight = Constants.zeroCGFloat

        UIView.animate(withDuration: animationDuration) {
            self.collectionView.contentInset = UIEdgeInsets(
                top: Constants.collectionTopInset,
                left: Constants.collectionLeftInset,
                bottom: Constants.collectionBottomInset,
                right: Constants.collectionRightInset
            )
            self.collectionView.scrollIndicatorInsets = UIEdgeInsets(
                top: Constants.collectionTopInset,
                left: Constants.collectionLeftInset,
                bottom: Constants.collectionBottomInset,
                right: Constants.collectionRightInset
            )
        }
    }
}

// MARK: - Timer Methods

extension HomeViewController {

    private func setupProfitTimer() {
        stopProfitTimer()

        profitTimer = Timer.scheduledTimer(
            timeInterval: timerInterval,
            target: self,
            selector: #selector(profitTimerFired),
            userInfo: nil,
            repeats: true
        )

        RunLoop.current.add(profitTimer!, forMode: .common)

        DispatchQueue.main.asyncAfter(deadline: .now() + Constants.profitTimerInitialDelay) {
            self.profitTimerFired()
        }
    }

    private func stopProfitTimer() {
        profitTimer?.invalidate()
        profitTimer = nil
    }

    private func restartProfitTimer() {
        stopProfitTimer()
        setupProfitTimer()
    }

    @objc private func profitTimerFired() {
        interactor?.calculateProfit(price: selectedPrice, period: selectedPeriod) { }
    }
}

// MARK: - Date

extension Date {
    func formattedInMoscow(format: String = Constants.moscowDefaultFormat) -> String {
        let moscowTimeZone = TimeZone(identifier: Constants.moscowTimeZoneId)!
        let dateFormatter = DateFormatter()
        dateFormatter.timeZone = moscowTimeZone
        dateFormatter.dateFormat = format
        dateFormatter.locale = Locale(identifier: Constants.moscowLocaleId)
        return dateFormatter.string(from: self)
    }

    static var todayMoscow: String {
        return Date().formattedInMoscow()
    }

    func formattedInMoscowWithTime() -> String {
        return formattedInMoscow(format: Constants.moscowDefaultFormat)
    }

    func formattedInMoscowWithoutTime() -> String {
        return formattedInMoscow(format: Constants.moscowDateOnlyFormat)
    }
}

private extension Date {
    enum Constants {
        static let moscowTimeZoneId = "Europe/Moscow"
        static let moscowLocaleId = "ru_RU"
        static let moscowDefaultFormat = "dd.MM.yyyy HH:mm"
        static let moscowDateOnlyFormat = "dd.MM.yyyy"
    }
}

// MARK: - Constants

private extension HomeViewController {
    enum Constants {
        static let zeroCGFloat: CGFloat = 0
        static let zeroDouble: Double = 0
        static let zeroTimeInterval: TimeInterval = 0

        static let firstItem = 0
        static let firstRow = 0
        static let profitSectionIndex = 3

        static let viewBackgroundColor: UIColor = .black

        static let emptyBackTitle = ""

        static let greetingTextColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        static let greetingLeading: CGFloat = 30

        static let profileImageName = "person.slash.fill"
        static let profileTintColor = UIColor(red: 0.992, green: 0.992, blue: 0.992, alpha: 1)
        static let profileTrailing: CGFloat = -35

        static let collectionTopToGreetingBottom: CGFloat = 26
        static let collectionLeading: CGFloat = 20
        static let collectionTrailing: CGFloat = -20

        static let collectionTopInset: CGFloat = 16
        static let collectionLeftInset: CGFloat = 0
        static let collectionBottomInset: CGFloat = 24
        static let collectionRightInset: CGFloat = 0

        static let loadingDevicesText = "Loading your devices"

        static let fractionalWidthFull: CGFloat = 1.0

        static let chartEstimatedHeight: CGFloat = 480
        static let chartSectionBottomInset: CGFloat = 10

        static let rangeAbsoluteHeight: CGFloat = 60
        static let rangeSectionBottomInset: CGFloat = -20

        static let hardwaresEstimatedHeight: CGFloat = 84
        static let hardwaresInterGroupSpacing: CGFloat = 10
        static let hardwaresSectionTopInset: CGFloat = 10
        static let hardwaresSectionBottomInset: CGFloat = 10
        static let hardwaresHeaderHeight: CGFloat = 40
        static let hardwaresFooterHeight: CGFloat = 16

        static let profitEstimatedHeight: CGFloat = 390
        static let profitSectionTopInset: CGFloat = 5

        static let hardwaresHeaderTitle = "Hardwares"

        static let scrollExtraOffset: CGFloat = 20
        static let scrollAnimationDuration: TimeInterval = 0.3

        static let hardwareToggleDuration: TimeInterval = 0.3

        static let modeECO = "ECO"
        static let modeSTANDARD = "STANDARD"
        static let modeOVERCLOCK = "OVERCLOCK"

        static let greetingFirst = "Hey"
        static let greetingSecondPrefix = ", "
        static let greetingSecondSuffix = "!"
        static let latoRegularFontName = "Lato-Regular"
        static let latoBoldFontName = "Lato-Bold"
        static let greetingFontSize: CGFloat = 32

        static let toastSuccessMessage = "All miners switched mode successfully."
        static let toastErrorMessage = "Some miners couldn’t switch mode. Please try again."
        static let toastDuration: TimeInterval = 3.0
        static let toastFontSize: CGFloat = 14

        static let alphaHidden: CGFloat = 0
        static let alphaVisible: CGFloat = 1

        static let keyboardExtraBottomInset: CGFloat = 10
        static let keyboardScrollExtraOffset: CGFloat = 10

        static let profitTimerInterval: TimeInterval = 60
        static let profitTimerInitialDelay: TimeInterval = 0.1
    }
}
