import UIKit
import SwiftUI

protocol ChartCardCellDelegate: AnyObject {
    func chartCardCellDidTapExpand(_ cell: ChartCardCell)
    func chartCardCellDidRequestRefresh(_ cell: ChartCardCell)
    func chartCardCellDidTapTitle(_ currentMetric: Metrics, _ ids: [String])
    func chartCardCellRequestCollectionRelayout(_ cell: ChartCardCell, duration: TimeInterval, delay: TimeInterval)
    func chartCardCellDidTapTableCell(deviceId: String, selected: Bool)
}

final class ChartCardCell: UICollectionViewCell {

    // MARK: Public properties

    static let reuseId = Constants.reuseId

    weak var delegate: ChartCardCellDelegate?

    var handlerPanGesture: UIPanGestureRecognizer { handlerPan }

    var currentMetric: Metrics = .hashrate {
        didSet {
            titleButton.setTitle("\(currentMetric.rawValue)", for: .normal)
            setupMetricMenu()
        }
    }

    var tablePanGesture: UIPanGestureRecognizer { tableView.panGestureRecognizer }

    // MARK: Private properties

    private var clockTimer: Timer?
    private var isChartInteracting = false

    private var indexByDeviceId: [String: Int] = [:]
    private var selectedDeviceIds: Set<String> = []
    private var chartHost: UIHostingController<HashrateChartView>?
    private var deviceRows: [DeviceRow] = []

    private var isExpanded = true
    private var isAnimating = false
    private let animDuration: TimeInterval = Constants.animDuration

    private var isLoadingDevices = true

    private var tableSectionTopConstraint: NSLayoutConstraint!
    private var tableSectionHeightConstraint: NSLayoutConstraint!
    private var handlerTopConstraint: NSLayoutConstraint!

    private var tableHeightConstraint: NSLayoutConstraint!
    private var emptyStateHeightConstraint: NSLayoutConstraint!

    private var sectionHeightExpanded: CGFloat {
        Constants.tableHeaderHeight + Constants.tableHeaderToTableSpacing + Constants.tableVisibleHeight
    }

    private let myRefreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.tintColor = Constants.refreshTintColor
        return refreshControl
    }()

    private let cardView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.cornerRadius = Constants.cardCornerRadius
        view.layer.shadowColor = Constants.cardShadowColor.cgColor
        view.layer.shadowOpacity = Constants.cardShadowOpacity
        view.layer.shadowRadius = Constants.cardShadowRadius
        view.layer.shadowOffset = Constants.cardShadowOffset
        view.backgroundColor = Constants.cardBackgroundColor
        return view
    }()

    private let headerContainer: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    private let titleButton: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setTitle(Constants.titleInitialText, for: .normal)
        button.setTitleColor(Constants.titleTextColor, for: .normal)
        button.titleLabel?.font = Constants.titleFont
        return button
    }()

    private let titleImage: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.isUserInteractionEnabled = Constants.titleImageUserInteractionEnabled
        button.setImage(UIImage(named: Constants.titleImageName), for: .normal)
        return button
    }()

    private let dateLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = Constants.dateTextColor
        label.font = Constants.dateFont
        label.textAlignment = Constants.dateTextAlignment
        return label
    }()

    let chartContainer: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = Constants.chartContainerBackgroundColor
        view.layer.masksToBounds = Constants.chartContainerMasksToBounds
        view.isHidden = Constants.chartContainerInitiallyHidden
        return view
    }()

    private let dividerView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = Constants.dividerColor
        return view
    }()

    private let tableSectionContainer: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.clipsToBounds = Constants.tableSectionClipsToBounds
        view.backgroundColor = Constants.tableSectionBackgroundColor
        return view
    }()

    private let tableHeaderRow: UIStackView = {
        func makeLabel(_ text: String) -> UILabel {
            let label = UILabel()
            label.text = text
            label.textColor = Constants.tableHeaderTextColor
            label.font = Constants.tableHeaderFont
            label.textAlignment = Constants.tableHeaderTextAlignment
            return label
        }

        let stack = UIStackView(arrangedSubviews: [
            makeLabel(Constants.tableHeaderName),
            makeLabel(Constants.tableHeaderThr),
            makeLabel(Constants.tableHeaderKw),
            makeLabel(Constants.tableHeaderTemp),
            makeLabel(Constants.tableHeaderStatus)
        ])
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = Constants.tableHeaderAxis
        stack.alignment = Constants.tableHeaderAlignment
        stack.distribution = Constants.tableHeaderDistribution
        return stack
    }()

    private lazy var tableView: UITableView = {
        let tableView = UITableView(frame: .zero, style: .plain)
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.backgroundColor = Constants.tableBackgroundColor
        tableView.separatorStyle = Constants.tableSeparatorStyle
        tableView.showsVerticalScrollIndicator = Constants.tableShowsVerticalScrollIndicator
        tableView.isScrollEnabled = Constants.tableIsScrollEnabledDefault
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(DeviceRowCell.self, forCellReuseIdentifier: DeviceRowCell.reuseId)
        return tableView
    }()

    private let devicesLoader: UIActivityIndicatorView = {
        let view = UIActivityIndicatorView(style: Constants.devicesLoaderStyle)
        view.translatesAutoresizingMaskIntoConstraints = false
        view.color = Constants.devicesLoaderColor
        view.hidesWhenStopped = Constants.devicesLoaderHidesWhenStopped
        return view
    }()

    private let loaderLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = Constants.loaderText
        label.textColor = Constants.loaderTextColor
        label.font = Constants.loaderFont
        label.textAlignment = Constants.loaderTextAlignment
        return label
    }()

    private let handlerArea: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = Constants.handlerAreaBackgroundColor
        return view
    }()

    private let handlerLine: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.shadowColor = Constants.handlerLineShadowColor.cgColor
        view.layer.shadowOpacity = Constants.handlerLineShadowOpacity
        view.layer.shadowRadius = Constants.handlerLineShadowRadius
        view.layer.shadowOffset = Constants.handlerLineShadowOffset
        view.backgroundColor = Constants.handlerLineBackgroundColor
        view.layer.borderWidth = Constants.handlerLineBorderWidth
        view.layer.borderColor = Constants.handlerLineBorderColor.cgColor
        view.layer.masksToBounds = Constants.handlerLineMasksToBounds
        return view
    }()

    private let emptyStateView: UIView = {
        let container = UIView()
        container.translatesAutoresizingMaskIntoConstraints = false
        container.backgroundColor = Constants.emptyStateBackgroundColor

        let title = UILabel()
        title.translatesAutoresizingMaskIntoConstraints = false
        title.text = Constants.emptyStateTitleText
        title.textColor = Constants.emptyStateTitleColor
        title.font = Constants.emptyStateTitleFont
        title.textAlignment = Constants.emptyStateTitleAlignment

        let subtitle = UILabel()
        subtitle.translatesAutoresizingMaskIntoConstraints = false
        subtitle.text = Constants.emptyStateSubtitleText
        subtitle.textColor = Constants.emptyStateSubtitleColor
        subtitle.font = Constants.emptyStateSubtitleFont
        subtitle.textAlignment = Constants.emptyStateSubtitleAlignment
        subtitle.numberOfLines = Constants.emptyStateSubtitleNumberOfLines

        container.addSubview(title)
        container.addSubview(subtitle)

        NSLayoutConstraint.activate([
            title.topAnchor.constraint(equalTo: container.topAnchor),
            title.leadingAnchor.constraint(equalTo: container.leadingAnchor),
            title.trailingAnchor.constraint(equalTo: container.trailingAnchor),

            subtitle.topAnchor.constraint(equalTo: title.bottomAnchor, constant: Constants.emptyStateSubtitleTopSpacing),
            subtitle.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: Constants.emptyStateSubtitleSideInset),
            subtitle.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: -Constants.emptyStateSubtitleSideInset),
            subtitle.bottomAnchor.constraint(equalTo: container.bottomAnchor)
        ])

        return container
    }()

    private lazy var handlerPan: UIPanGestureRecognizer = {
        let pan = UIPanGestureRecognizer(target: self, action: #selector(handleHandlerPan(_:)))
        pan.cancelsTouchesInView = Constants.handlerPanCancelsTouchesInView
        return pan
    }()

    // MARK: Init

    override init(frame: CGRect) {
        super.init(frame: frame)
        contentView.backgroundColor = Constants.contentBackgroundColor

        contentView.addSubview(cardView)
        cardView.addSubview(headerContainer)

        headerContainer.addSubview(titleButton)
        headerContainer.addSubview(titleImage)
        headerContainer.addSubview(dateLabel)

        chartContainer.isHidden = Constants.chartContainerInitiallyHidden
        cardView.addSubview(chartContainer)

        cardView.addSubview(dividerView)

        cardView.addSubview(tableSectionContainer)
        tableSectionContainer.addSubview(tableHeaderRow)
        tableSectionContainer.addSubview(tableView)
        tableSectionContainer.addSubview(emptyStateView)
        tableSectionContainer.addSubview(devicesLoader)
        tableSectionContainer.addSubview(loaderLabel)

        cardView.addSubview(handlerArea)
        handlerArea.addSubview(handlerLine)
        handlerArea.addGestureRecognizer(handlerPan)

        tableView.refreshControl = myRefreshControl
        tableView.rowHeight = Constants.tableRowHeight
        tableView.estimatedRowHeight = Constants.tableEstimatedRowHeight

        titleButton.addTarget(self, action: #selector(didTapTitle), for: .touchUpInside)
        myRefreshControl.addTarget(self, action: #selector(handleRefresh), for: .valueChanged)

        setupConstraints()
        setupMetricMenu()
        applyState(expanded: true, animated: false)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // MARK: Lifecycle

    override func didMoveToWindow() {
        super.didMoveToWindow()
        if window != nil { startClock() }
        else { stopClock() }
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        isChartInteracting = false
        isLoadingDevices = true
        devicesLoader.startAnimating()
        updateTableLayout()
    }

    override func preferredLayoutAttributesFitting(_ layoutAttributes: UICollectionViewLayoutAttributes)
    -> UICollectionViewLayoutAttributes {
        setNeedsLayout()
        layoutIfNeeded()

        let size = contentView.systemLayoutSizeFitting(
            CGSize(
                width: layoutAttributes.size.width,
                height: UIView.layoutFittingCompressedSize.height
            ),
            withHorizontalFittingPriority: .required,
            verticalFittingPriority: .fittingSizeLevel
        )

        let attrs = layoutAttributes
        attrs.size.height = ceil(size.height)
        return attrs
    }

    // MARK: Public methods

    func configure(
        dateText: String,
        rows: [DeviceRow],
        series: [Points],
        timeRange: TimeRange,
        isLoadingDevices: Bool
    ) {
        dateLabel.text = dateText

        deviceRows = rows
        indexByDeviceId = Dictionary(uniqueKeysWithValues: rows.enumerated().map { ($0.element.deviceId, $0.offset) })

        tableView.reloadData()

        setDevicesLoading(isLoadingDevices)

        setChart(series: series, timeRange: timeRange) { [weak self] _, dict in
            guard let self else { return }
            for (deviceId, point) in dict {
                self.applyPoint(point, forDeviceId: deviceId)
            }
        }
    }

    func updateSeries(_ series: [Points], timeRange: TimeRange) {
        updateTableLayout()

        if isExpanded {
            delegate?.chartCardCellRequestCollectionRelayout(self, duration: 0, delay: 0)
        }

        setChart(series: series, timeRange: timeRange) { [weak self] selectedDate, dict in
            guard let self else { return }
            for (deviceId, point) in dict {
                self.applyPoint(point, forDeviceId: deviceId)
            }
        }
    }

    func setDevicesLoading(_ loading: Bool) {
        isLoadingDevices = loading
        if loading { devicesLoader.startAnimating() }
        else { devicesLoader.stopAnimating() }

        updateTableLayout()

        if isExpanded {
            delegate?.chartCardCellRequestCollectionRelayout(self, duration: 0, delay: 0)
        }
    }

    func endRefreshing() {
        myRefreshControl.endRefreshing()
    }

    // MARK: Private methods

    private func setupConstraints() {
        tableSectionTopConstraint = tableSectionContainer.topAnchor.constraint(
            equalTo: dividerView.bottomAnchor,
            constant: Constants.tableSectionTopExpanded
        )
        tableSectionHeightConstraint = tableSectionContainer.heightAnchor.constraint(
            equalToConstant: sectionHeightExpanded
        )

        handlerTopConstraint = handlerArea.topAnchor.constraint(
            equalTo: tableSectionContainer.bottomAnchor,
            constant: Constants.handlerTopExpanded
        )

        tableHeightConstraint = tableView.heightAnchor.constraint(
            equalToConstant: Constants.tableVisibleHeight
        )
        emptyStateHeightConstraint = emptyStateView.heightAnchor.constraint(
            equalToConstant: Constants.emptyStateHeight
        )

        NSLayoutConstraint.activate([
            cardView.topAnchor.constraint(equalTo: contentView.topAnchor),
            cardView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            cardView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            cardView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),

            headerContainer.topAnchor.constraint(equalTo: cardView.topAnchor, constant: Constants.headerTopInset),
            headerContainer.leadingAnchor.constraint(equalTo: cardView.leadingAnchor, constant: Constants.sideInset),
            headerContainer.trailingAnchor.constraint(equalTo: cardView.trailingAnchor, constant: -Constants.sideInset),
            headerContainer.heightAnchor.constraint(equalToConstant: Constants.headerContainerHeight),

            titleButton.leadingAnchor.constraint(equalTo: headerContainer.leadingAnchor),
            titleButton.centerYAnchor.constraint(equalTo: headerContainer.centerYAnchor, constant: Constants.titleCenterYOffset),

            titleImage.leadingAnchor.constraint(equalTo: titleButton.trailingAnchor, constant: Constants.titleImageLeadingSpacing),
            titleImage.centerYAnchor.constraint(equalTo: titleButton.centerYAnchor, constant: Constants.titleCenterYOffset),

            dateLabel.centerXAnchor.constraint(equalTo: headerContainer.centerXAnchor, constant: Constants.dateLabelCenterXOffset),
            dateLabel.bottomAnchor.constraint(equalTo: headerContainer.bottomAnchor),

            chartContainer.topAnchor.constraint(equalTo: headerContainer.bottomAnchor, constant: Constants.chartTopSpacing),
            chartContainer.leadingAnchor.constraint(equalTo: cardView.leadingAnchor, constant: Constants.chartSideInset),
            chartContainer.trailingAnchor.constraint(equalTo: cardView.trailingAnchor, constant: -Constants.chartSideInset),
            chartContainer.heightAnchor.constraint(equalToConstant: Constants.chartHeight),

            dividerView.topAnchor.constraint(equalTo: chartContainer.bottomAnchor, constant: Constants.dividerTopSpacing),
            dividerView.leadingAnchor.constraint(equalTo: cardView.leadingAnchor, constant: Constants.sideInset),
            dividerView.trailingAnchor.constraint(equalTo: cardView.trailingAnchor, constant: -Constants.sideInset),
            dividerView.heightAnchor.constraint(equalToConstant: Constants.dividerHeight),

            handlerTopConstraint,
            handlerArea.leadingAnchor.constraint(equalTo: cardView.leadingAnchor),
            handlerArea.trailingAnchor.constraint(equalTo: cardView.trailingAnchor),
            handlerArea.bottomAnchor.constraint(equalTo: cardView.bottomAnchor),
            handlerArea.heightAnchor.constraint(equalToConstant: Constants.handlerAreaHeight),

            handlerLine.centerXAnchor.constraint(equalTo: handlerArea.centerXAnchor),
            handlerLine.centerYAnchor.constraint(equalTo: handlerArea.centerYAnchor),
            handlerLine.widthAnchor.constraint(equalToConstant: Constants.handlerLineWidth),
            handlerLine.heightAnchor.constraint(equalToConstant: Constants.handlerLineHeight),

            tableSectionTopConstraint,
            tableSectionHeightConstraint,
            tableSectionContainer.leadingAnchor.constraint(equalTo: cardView.leadingAnchor, constant: Constants.sideInset),
            tableSectionContainer.trailingAnchor.constraint(equalTo: cardView.trailingAnchor, constant: -Constants.sideInset),

            tableHeaderRow.topAnchor.constraint(equalTo: tableSectionContainer.topAnchor),
            tableHeaderRow.leadingAnchor.constraint(equalTo: tableSectionContainer.leadingAnchor, constant: Constants.tableHeaderLeadingInset),
            tableHeaderRow.trailingAnchor.constraint(equalTo: tableSectionContainer.trailingAnchor),
            tableHeaderRow.heightAnchor.constraint(equalToConstant: Constants.tableHeaderHeight),

            tableView.topAnchor.constraint(equalTo: tableHeaderRow.bottomAnchor, constant: Constants.tableHeaderToTableSpacing),
            tableView.leadingAnchor.constraint(equalTo: tableSectionContainer.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: tableSectionContainer.trailingAnchor),
            tableHeightConstraint,

            emptyStateView.centerXAnchor.constraint(equalTo: tableSectionContainer.centerXAnchor),
            emptyStateView.centerYAnchor.constraint(equalTo: tableSectionContainer.centerYAnchor),
            emptyStateView.leadingAnchor.constraint(greaterThanOrEqualTo: tableSectionContainer.leadingAnchor),
            emptyStateView.trailingAnchor.constraint(lessThanOrEqualTo: tableSectionContainer.trailingAnchor),
            emptyStateHeightConstraint,

            devicesLoader.centerXAnchor.constraint(equalTo: tableSectionContainer.centerXAnchor),
            devicesLoader.centerYAnchor.constraint(equalTo: tableSectionContainer.centerYAnchor, constant: Constants.devicesLoaderCenterYOffset),

            loaderLabel.topAnchor.constraint(equalTo: devicesLoader.bottomAnchor, constant: Constants.loaderLabelTopSpacing),
            loaderLabel.centerXAnchor.constraint(equalTo: tableSectionContainer.centerXAnchor)
        ])
    }

    private func setupMetricMenu() {
        let actions: [UIAction] = Metrics.allCases.map { metric in
            UIAction(title: metric.rawValue, state: metric == currentMetric ? .on : .off) { [weak self] _ in
                guard let self else { return }
                self.currentMetric = metric
                self.delegate?.chartCardCellDidTapTitle(metric, Array(self.selectedDeviceIds))
            }
        }

        titleButton.menu = UIMenu(title: "", options: .displayInline, children: actions)
        titleButton.showsMenuAsPrimaryAction = true
    }

    private func updateTableLayout() {
        if !isExpanded {
            tableHeaderRow.isHidden = true
            tableView.isHidden = true
            emptyStateView.isHidden = true
            devicesLoader.isHidden = true
            loaderLabel.isHidden = true

            tableView.isScrollEnabled = false
            tableHeightConstraint.constant = 0
            tableSectionHeightConstraint.constant = Constants.sectionHeightCollapsed
            return
        }

        if isLoadingDevices {
            tableHeaderRow.isHidden = true
            tableView.isHidden = true
            emptyStateView.isHidden = true

            devicesLoader.isHidden = false
            loaderLabel.isHidden = false

            tableHeightConstraint.constant = 0
            tableView.isScrollEnabled = false

            tableSectionHeightConstraint.constant =
            Constants.tableHeaderHeight + Constants.tableHeaderToTableSpacing + Constants.emptySectionHeight
            return
        }

        let isEmpty = deviceRows.isEmpty

        devicesLoader.isHidden = true
        loaderLabel.isHidden = true

        tableHeaderRow.isHidden = isEmpty
        tableView.isHidden = isEmpty
        emptyStateView.isHidden = !isEmpty

        if isEmpty {
            tableHeightConstraint.constant = 0
            tableView.isScrollEnabled = false
            tableSectionHeightConstraint.constant =
            Constants.tableHeaderHeight + Constants.tableHeaderToTableSpacing + Constants.emptySectionHeight
            return
        }

        let rows = CGFloat(deviceRows.count)
        let desired = rows * Constants.tableRowHeight
        let visible = min(desired, Constants.tableVisibleHeight)

        tableHeightConstraint.constant = visible
        tableView.isScrollEnabled = desired > Constants.tableVisibleHeight

        tableSectionHeightConstraint.constant =
        Constants.tableHeaderHeight + Constants.tableHeaderToTableSpacing + visible
    }

    private func startClock() {
        stopClock()
        updateDateLabelToNowMSK()

        let t = Timer(timeInterval: Constants.clockInterval, repeats: true) { [weak self] _ in
            guard let self else { return }
            guard !self.isChartInteracting else { return }
            self.updateDateLabelToNowMSK()
        }
        clockTimer = t
        RunLoop.main.add(t, forMode: .common)
    }

    private func stopClock() {
        clockTimer?.invalidate()
        clockTimer = nil
    }

    private func updateDateLabelToNowMSK() {
        dateLabel.text = Constants.mskDateTimeFormatter.string(from: Date())
    }

    private func toggle(expand: Bool) {
        isAnimating = true
        isExpanded = expand
        applyState(expanded: expand, animated: true)
    }

    private func applyState(expanded: Bool, animated: Bool) {
        if animated { layoutIfNeeded() }

        tableSectionTopConstraint.constant = expanded ? Constants.tableSectionTopExpanded : Constants.tableSectionTopCollapsed
        handlerTopConstraint.constant = expanded
        ? Constants.handlerTopExpanded
        : Constants.handlerTopCollapsed - Constants.handlerCollapsedExtraOffset

        if expanded {
            updateTableLayout()
        } else {
            tableSectionHeightConstraint.constant = Constants.sectionHeightCollapsed
        }

        tableView.isUserInteractionEnabled = expanded

        let animations = {
            let a: CGFloat = expanded ? 1 : 0
            self.tableSectionContainer.alpha = a
            self.dividerView.alpha = a
            self.contentView.layoutIfNeeded()
        }

        let dur1: TimeInterval
        let dur2: TimeInterval
        let del2: TimeInterval

        if expanded {
            dur1 = Constants.animDuration
            dur2 = Constants.animDuration
            del2 = Constants.animDuration
        } else {
            dur1 = Constants.animDuration
            dur2 = 0
            del2 = 0
        }

        delegate?.chartCardCellRequestCollectionRelayout(self, duration: dur1, delay: 0)

        if animated {
            UIView.animate(withDuration: dur2, delay: del2, options: []) {
                animations()
            } completion: { _ in
                self.isAnimating = false
            }
        } else {
            animations()
            isAnimating = false
        }
    }

    private func setChart(
        series: [Points],
        timeRange: TimeRange,
        onSelect: @escaping (_ selectedDate: Date, _ valuesById: [String: MetricPoint]) -> Void
    ) {
        chartContainer.isHidden = false

        let range: HashrateChartView.TimeRange
        switch timeRange {
        case .day: range = .day
        case .week: range = .week
        case .month: range = .month
        case .year: range = .year
        }

        let root = HashrateChartView(
            series: series,
            onSelectAll: { [weak self] selectedDate, dict in
                guard let self else { return }

                if self.isChartInteracting {
                    self.dateLabel.text = Constants.mskDateTimeFormatter.string(from: selectedDate)
                }

                for (deviceId, point) in dict {
                    self.applyPoint(point, forDeviceId: deviceId)
                }

                onSelect(selectedDate, dict)
            },
            onInteractionChanged: { [weak self] isInteracting in
                guard let self else { return }
                self.isChartInteracting = isInteracting
            },
            timeRange: range
        )

        if let chartHost {
            chartHost.rootView = root
            chartHost.view.invalidateIntrinsicContentSize()
            return
        }

        let host = UIHostingController(rootView: root)
        host.view.backgroundColor = Constants.hostBackgroundColor
        host.view.translatesAutoresizingMaskIntoConstraints = false

        chartContainer.addSubview(host.view)
        NSLayoutConstraint.activate([
            host.view.topAnchor.constraint(equalTo: chartContainer.topAnchor),
            host.view.leadingAnchor.constraint(equalTo: chartContainer.leadingAnchor),
            host.view.trailingAnchor.constraint(equalTo: chartContainer.trailingAnchor),
            host.view.bottomAnchor.constraint(equalTo: chartContainer.bottomAnchor)
        ])

        chartHost = host
    }

    private func applyPoint(_ point: MetricPoint, forDeviceId deviceId: String) {
        if !Thread.isMainThread {
            DispatchQueue.main.async { [weak self] in
                self?.applyPoint(point, forDeviceId: deviceId)
            }
            return
        }

        guard let rowIndex = indexByDeviceId[deviceId] else { return }

        let old = deviceRows[rowIndex]
        let updated = updateRow(old, with: point.value, metric: currentMetric)
        deviceRows[rowIndex] = updated

        let indexPath = IndexPath(row: rowIndex, section: 0)

        if let cell = tableView.cellForRow(at: indexPath) as? DeviceRowCell {
            let highlighted = selectedDeviceIds.contains(deviceId)
            cell.configure(updated, highlighted: highlighted)
        } else {
            UIView.performWithoutAnimation {
                tableView.reloadRows(at: [indexPath], with: .none)
            }
        }
    }

    private func updateRow(_ row: DeviceRow, with value: Double, metric: Metrics) -> DeviceRow {
        var r = row
        switch metric {
        case .hashrate:
            r.thr = value
        case .temperature:
            r.temp = value
        case .consumption:
            r.kw = value
        }
        return r
    }

    // MARK: Actions

    @objc private func handleHandlerPan(_ recognizer: UIPanGestureRecognizer) {
        guard !isAnimating else { return }

        if recognizer.state == .changed {
            let v = recognizer.velocity(in: handlerArea)
            guard abs(v.y) > Constants.handlerPanVelocityThreshold else { return }

            if v.y < 0 {
                if isExpanded {
                    toggle(expand: false)
                }
            } else {
                if !isExpanded {
                    toggle(expand: true)
                }
            }
        }
    }

    @objc private func handleRefresh() {
        delegate?.chartCardCellDidRequestRefresh(self)
    }

    @objc private func didTapTitle() {
        delegate?.chartCardCellDidTapTitle(currentMetric, selectedDeviceIds.map { $0 })
    }
}

// MARK: - UITableViewDataSource

extension ChartCardCell: UITableViewDataSource {

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        deviceRows.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: DeviceRowCell.reuseId, for: indexPath) as! DeviceRowCell
        let row = deviceRows[indexPath.row]

        let highlighted = selectedDeviceIds.contains(row.deviceId)
        cell.configure(row, highlighted: highlighted)
        return cell
    }
}

// MARK: - UITableViewDelegate

extension ChartCardCell: UITableViewDelegate {

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let row = deviceRows[indexPath.row]
        let id = row.deviceId

        let nowSelected: Bool
        if selectedDeviceIds.contains(id) {
            selectedDeviceIds.remove(id)
            nowSelected = false
        } else {
            selectedDeviceIds.insert(id)
            nowSelected = true
        }

        UIView.performWithoutAnimation {
            tableView.reloadRows(at: [indexPath], with: .none)
        }

        delegate?.chartCardCellDidTapTableCell(deviceId: id, selected: nowSelected)
    }
}

// MARK: - Constants

private extension ChartCardCell {
    enum Constants {

        // MARK: Identifiers

        static let reuseId: String = "ChartCardCell"

        // MARK: Clock

        static let clockInterval: TimeInterval = 1.0

        static let mskTimeZoneId: String = "Europe/Moscow"
        static let mskLocaleId: String = "ru_RU"
        static let mskDateFormat: String = "dd.MM.yyyy HH:mm"

        static let mskDateTimeFormatter: DateFormatter = {
            let f = DateFormatter()
            f.locale = Locale(identifier: mskLocaleId)
            f.timeZone = TimeZone(identifier: mskTimeZoneId)!
            f.dateFormat = mskDateFormat
            return f
        }()

        // MARK: Animation

        static let animDuration: TimeInterval = 0.28

        // MARK: Layout

        static let sideInset: CGFloat = 18
        static let headerTopInset: CGFloat = 26
        static let headerContainerHeight: CGFloat = 24

        static let titleCenterYOffset: CGFloat = 2
        static let titleImageLeadingSpacing: CGFloat = 4
        static let dateLabelCenterXOffset: CGFloat = 15

        static let chartTopSpacing: CGFloat = 14
        static let chartSideInset: CGFloat = 10
        static let chartHeight: CGFloat = 200

        static let dividerTopSpacing: CGFloat = 21
        static let dividerHeight: CGFloat = 1

        static let tableSectionTopExpanded: CGFloat = 21
        static let tableSectionTopCollapsed: CGFloat = 0
        static let sectionHeightCollapsed: CGFloat = 0

        static let tableHeaderLeadingInset: CGFloat = 18
        static let tableHeaderHeight: CGFloat = 18
        static let tableHeaderToTableSpacing: CGFloat = 13
        static let tableVisibleHeight: CGFloat = 100

        static let emptyStateHeight: CGFloat = 86
        static let emptySectionHeight: CGFloat = 90

        static let handlerTopExpanded: CGFloat = 0
        static let handlerTopCollapsed: CGFloat = 0
        static let handlerCollapsedExtraOffset: CGFloat = 25

        static let handlerAreaHeight: CGFloat = 34
        static let handlerLineWidth: CGFloat = 65
        static let handlerLineHeight: CGFloat = 2

        static let devicesLoaderCenterYOffset: CGFloat = -8
        static let loaderLabelTopSpacing: CGFloat = 8

        static let tableRowHeight: CGFloat = 30
        static let tableEstimatedRowHeight: CGFloat = 0

        // MARK: Gestures

        static let handlerPanCancelsTouchesInView: Bool = true
        static let handlerPanVelocityThreshold: CGFloat = 80

        // MARK: Text

        static let titleInitialText: String = "Hash rate"

        static let tableHeaderName: String = "Name"
        static let tableHeaderThr: String = "Th/r"
        static let tableHeaderKw: String = "kW"
        static let tableHeaderTemp: String = "t°C"
        static let tableHeaderStatus: String = "Status"

        static let loaderText: String = "Loading devices…"

        static let emptyStateTitleText: String = "No devices yet"
        static let emptyStateSubtitleText: String = "Add a device to your account to see metrics and statistics here."

        // MARK: Images

        static let titleImageName: String = "vector"

        // MARK: Fonts

        static let titleFont: UIFont? = UIFont(name: "Lato-Bold", size: 18)
        static let dateFont: UIFont? = UIFont(name: "Lato-Bold", size: 12)

        static let tableHeaderFont: UIFont? = UIFont(name: "Lato-Bold", size: 12)

        static let loaderFont: UIFont? = UIFont(name: "Lato-Regular", size: 12)

        static let emptyStateTitleFont: UIFont? = UIFont(name: "Lato-Bold", size: 14)
        static let emptyStateSubtitleFont: UIFont? = UIFont(name: "Lato-Regular", size: 12)

        // MARK: Colors

        static let contentBackgroundColor: UIColor = .clear

        static let refreshTintColor: UIColor = .white

        static let cardBackgroundColor: UIColor = UIColor(red: 0.102, green: 0.102, blue: 0.102, alpha: 1)
        static let cardShadowColor: UIColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.03)
        static let cardShadowOpacity: Float = 1
        static let cardShadowRadius: CGFloat = 28.6
        static let cardShadowOffset: CGSize = CGSize(width: 0, height: 23)
        static let cardCornerRadius: CGFloat = 15

        static let titleTextColor: UIColor = .white

        static let dateTextColor: UIColor = .white

        static let chartContainerBackgroundColor: UIColor = .clear
        static let hostBackgroundColor: UIColor = .clear

        static let dividerColor: UIColor = UIColor(red: 0.231, green: 0.231, blue: 0.231, alpha: 1)

        static let tableSectionBackgroundColor: UIColor = .clear

        static let tableBackgroundColor: UIColor = .clear

        static let tableHeaderTextColor: UIColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)

        static let devicesLoaderColor: UIColor = .white

        static let loaderTextColor: UIColor = UIColor.white.withAlphaComponent(0.65)

        static let handlerAreaBackgroundColor: UIColor = .clear

        static let handlerLineShadowColor: UIColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.25)
        static let handlerLineShadowOpacity: Float = 1
        static let handlerLineShadowRadius: CGFloat = 1
        static let handlerLineShadowOffset: CGSize = CGSize(width: 0, height: 2)
        static let handlerLineBackgroundColor: UIColor = UIColor(red: 0.376, green: 0.376, blue: 0.376, alpha: 1)
        static let handlerLineBorderWidth: CGFloat = 1
        static let handlerLineBorderColor: UIColor = UIColor(red: 0.376, green: 0.376, blue: 0.376, alpha: 1)

        static let emptyStateBackgroundColor: UIColor = .clear
        static let emptyStateTitleColor: UIColor = UIColor.white.withAlphaComponent(0.92)
        static let emptyStateSubtitleColor: UIColor = UIColor.white.withAlphaComponent(0.65)

        // MARK: Misc

        static let titleImageUserInteractionEnabled: Bool = false

        static let chartContainerMasksToBounds: Bool = false
        static let chartContainerInitiallyHidden: Bool = false

        static let tableSectionClipsToBounds: Bool = true

        static let tableSeparatorStyle: UITableViewCell.SeparatorStyle = .none
        static let tableShowsVerticalScrollIndicator: Bool = false
        static let tableIsScrollEnabledDefault: Bool = true

        static let devicesLoaderStyle: UIActivityIndicatorView.Style = .medium
        static let devicesLoaderHidesWhenStopped: Bool = true

        static let dateTextAlignment: NSTextAlignment = .center
        static let tableHeaderTextAlignment: NSTextAlignment = .center
        static let loaderTextAlignment: NSTextAlignment = .center
        static let emptyStateTitleAlignment: NSTextAlignment = .center
        static let emptyStateSubtitleAlignment: NSTextAlignment = .center

        static let tableHeaderAxis: NSLayoutConstraint.Axis = .horizontal
        static let tableHeaderAlignment: UIStackView.Alignment = .center
        static let tableHeaderDistribution: UIStackView.Distribution = .fillEqually

        static let emptyStateSubtitleNumberOfLines: Int = 0
        static let emptyStateSubtitleTopSpacing: CGFloat = 6
        static let emptyStateSubtitleSideInset: CGFloat = 10

        static let handlerLineMasksToBounds: Bool = true
    }
}
