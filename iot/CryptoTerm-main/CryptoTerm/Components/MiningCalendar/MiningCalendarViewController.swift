import UIKit
import FSCalendar

protocol MiningCalendarViewControllerInput: AnyObject {
    func showStartScreen()
    func successCheduleRegimeChange(isSuccess: Bool)
    func applyPlannedModeDates(_ modeDates: [HardwareAccordionCell.Mode: Set<Date>], forDeviceId: String)
}

typealias MiningCalendarViewControllerOutput = MiningCalendarInteractorInput

final class MiningCalendarViewController: UIViewController {

    // MARK: Public properties

    var interactor: MiningCalendarViewControllerOutput?
    weak var delegate: Coordinator?

    // MARK: Private properties

    private var hardwareAnimatingIds = Set<String>()
    private var editAction: HardwareAccordionCell.EditAction = .select
    private var isSyncingCalendarSelection = false
    private let devices: [DeviceRow]
    private var currentCalendarPage: Date = Date()
    private var openDeviceId: String? = nil
    private var stateByDeviceId: [String: DeviceCalendarState] = [:]
    private var currentMode: HardwareAccordionCell.Mode = .standart
    private var modeDates: [HardwareAccordionCell.Mode: Set<Date>] = [
        .economy: [],
        .standart: [],
        .maximal: []
    ]
    private var dragStart: Date?
    private var dragEnd: Date?
    private var dragAction: DragAction?
    private let modeColorEconomy  = UIColor(
        red: Constants.modeEconomyRed,
        green: Constants.modeEconomyGreen,
        blue: Constants.modeEconomyBlue,
        alpha: Constants.modeColorAlpha
    )
    private let modeColorStandart = UIColor(
        red: Constants.modeStandartRed,
        green: Constants.modeStandartGreen,
        blue: Constants.modeStandartBlue,
        alpha: Constants.modeColorAlpha
    )
    private let modeColorMaximal  = UIColor(
        red: Constants.modeMaximalRed,
        green: Constants.modeMaximalGreen,
        blue: Constants.modeMaximalBlue,
        alpha: Constants.modeColorAlpha
    )

    private var todayStart: Date {
        gregorian.startOfDay(for: Date())
    }

    private var tomorrowStart: Date {
        gregorian.date(byAdding: .day, value: 1, to: todayStart)!
    }

    private let headerContainer: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    private let monthLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .white
        label.font = UIFont(name: Constants.monthLabelFontName, size: Constants.monthLabelFontSize)
        label.textAlignment = .center
        return label
    }()

    private let prevButton: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setImage(UIImage(named: Constants.prevButtonImageName), for: .normal)
        return button
    }()

    private let nextButton: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setImage(UIImage(named: Constants.nextButtonImageName), for: .normal)
        return button
    }()

    private let divider: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = UIColor(
            red: Constants.dividerColorRed,
            green: Constants.dividerColorGreen,
            blue: Constants.dividerColorBlue,
            alpha: Constants.dividerColorAlpha
        )
        return view
    }()

    private let calendarView: FSCalendar = {
        let calendarView = FSCalendar()

        calendarView.translatesAutoresizingMaskIntoConstraints = false

        calendarView.placeholderType = .none
        calendarView.backgroundColor = .black
        calendarView.headerHeight = Constants.calendarHeaderHeight

        calendarView.weekdayHeight = Constants.calendarWeekdayHeight
        calendarView.appearance.weekdayTextColor = .white
        calendarView.appearance.weekdayFont = UIFont(name: Constants.weekdayFontName, size: Constants.weekdayFontSize)

        calendarView.appearance.titleDefaultColor = .white
        calendarView.appearance.titleSelectionColor = .white
        calendarView.appearance.titleFont = UIFont(name: Constants.titleFontName, size: Constants.titleFontSize)

        calendarView.appearance.selectionColor = .clear
        calendarView.appearance.todayColor = .clear

        calendarView.firstWeekday = Constants.calendarFirstWeekday
        calendarView.allowsMultipleSelection = true

        calendarView.register(RangeCalendarCell.self, forCellReuseIdentifier: Constants.rangeCalendarCellReuseId)

        return calendarView
    }()

    private lazy var collectionView: UICollectionView = {
        let cv = UICollectionView(frame: .zero, collectionViewLayout: makeLayout())
        cv.translatesAutoresizingMaskIntoConstraints = false
        cv.backgroundColor = .clear
        cv.alwaysBounceVertical = true
        cv.showsVerticalScrollIndicator = false
        cv.dataSource = self
        cv.delegate = self
        cv.register(CalendarContainerCell.self, forCellWithReuseIdentifier: CalendarContainerCell.reuseId)
        cv.register(HardwareAccordionCell.self, forCellWithReuseIdentifier: HardwareAccordionCell.reuseId)
        cv.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: Constants.collectionBottomInset, right: 0)
        return cv
    }()

    private lazy var dragGesture: UILongPressGestureRecognizer = {
        let g = UILongPressGestureRecognizer(target: self, action: #selector(handleDrag(_:)))
        g.minimumPressDuration = Constants.dragMinimumPressDuration
        g.cancelsTouchesInView = false
        return g
    }()

    private lazy var gregorian: Calendar = {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone(identifier: Constants.gregorianTimeZoneId) ?? .current
        calendar.locale = Locale(identifier: Constants.gregorianLocaleId)
        return calendar
    }()

    private lazy var monthFormatter: DateFormatter = {
        let df = DateFormatter()
        df.calendar = gregorian
        df.locale = gregorian.locale
        df.timeZone = gregorian.timeZone
        df.dateFormat = Constants.monthDateFormat
        return df
    }()

    private lazy var legendStack: UIStackView = {
        let s = UIStackView(arrangedSubviews: [
            makeLegendItem(color: modeColorEconomy, title: "ECONOMY"),
            makeLegendItem(color: modeColorStandart, title: "STANDART"),
            makeLegendItem(color: modeColorMaximal, title: "MAXIMAL")
        ])
        s.translatesAutoresizingMaskIntoConstraints = false
        s.axis = .horizontal
        s.alignment = .center
        s.distribution = .equalCentering
        s.spacing = Constants.legendSpacing
        s.isLayoutMarginsRelativeArrangement = true
        s.layoutMargins = UIEdgeInsets(
            top: 0,
            left: Constants.legendHorizontalMargin,
            bottom: 0,
            right: Constants.legendHorizontalMargin
        )
        return s
    }()

    // MARK: Init

    init(devices: [DeviceRow], openedDeviceId: String) {
        self.devices = devices
        super.init(nibName: nil, bundle: nil)

        if !openedDeviceId.isEmpty {
            self.openDeviceId = openedDeviceId
            self.currentMode = .standart
        }
    }

    convenience init() {
        self.init(devices: [], openedDeviceId: "")
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    // MARK: Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        setupView()
        setupHeader()
        setupCalendar()
        setupNavBarAppearance()
        setupConstraints()

        updateMonthLabel(for: currentCalendarPage)

        if let id = openDeviceId, devices.contains(where: { $0.deviceId == id }) {
            loadState(for: id)
            syncFSCalendarSelectionFromModeDates()
        } else if let firstId = devices.first?.deviceId {
            openDeviceId = firstId
            loadState(for: firstId)
            syncFSCalendarSelectionFromModeDates()
        }

        if let id = openDeviceId,
           let deviceRow = devices.first(where: { $0.deviceId == id } ),
           deviceRow.asicIds.count > 0
        {
            interactor?.loadPlannedCalendar(rpId: id, minerId: deviceRow.asicIds[0])
        }

        updateWeekdayLabelsToUppercase()
        calendarView.reloadData()
    }

    // MARK: Private methods

    private func setupView() {
        view.backgroundColor = .black
        title = Constants.screenTitle
        navigationItem.largeTitleDisplayMode = .never

        view.addSubview(collectionView)
    }

    private func setupHeader() {
        prevButton.addTarget(self, action: #selector(didTapPrev), for: .touchUpInside)
        nextButton.addTarget(self, action: #selector(didTapNext), for: .touchUpInside)

        headerContainer.addSubview(monthLabel)
        headerContainer.addSubview(prevButton)
        headerContainer.addSubview(nextButton)
    }

    private func setupCalendar() {
        calendarView.dataSource = self
        calendarView.delegate = self
        calendarView.addGestureRecognizer(dragGesture)
    }

    private func setupNavBarAppearance() {
        guard let navBar = navigationController?.navigationBar else { return }

        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .black
        appearance.shadowColor = .clear
        appearance.titleTextAttributes = [
            .foregroundColor: UIColor.white,
            .font: UIFont(name: Constants.navBarTitleFontName, size: Constants.navBarTitleFontSize) as Any
        ]

        if let img = UIImage(named: Constants.navBarBackChevronImageName)?.withRenderingMode(.alwaysTemplate) {
            appearance.setBackIndicatorImage(img, transitionMaskImage: img)
        }

        navBar.standardAppearance = appearance
        navBar.scrollEdgeAppearance = appearance
        navBar.compactAppearance = appearance
        navBar.tintColor = UIColor(
            red: Constants.navBarTintRed,
            green: Constants.navBarTintGreen,
            blue: Constants.navBarTintBlue,
            alpha: Constants.navBarTintAlpha
        )
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            collectionView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            collectionView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            collectionView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            collectionView.bottomAnchor.constraint(equalTo: view.bottomAnchor),

            monthLabel.centerXAnchor.constraint(equalTo: headerContainer.centerXAnchor),
            monthLabel.centerYAnchor.constraint(equalTo: headerContainer.centerYAnchor),

            prevButton.trailingAnchor.constraint(equalTo: monthLabel.leadingAnchor),
            prevButton.centerYAnchor.constraint(equalTo: headerContainer.centerYAnchor),

            nextButton.leadingAnchor.constraint(equalTo: monthLabel.trailingAnchor),
            nextButton.centerYAnchor.constraint(equalTo: headerContainer.centerYAnchor)
        ])
    }

    private func makeLayout() -> UICollectionViewLayout {
        UICollectionViewCompositionalLayout { [weak self] sectionIndex, _ in
            guard self != nil else { return nil }
            guard let section = Section(rawValue: sectionIndex) else { return nil }

            switch section {
            case .calendar:
                let itemSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(1.0),
                    heightDimension: .estimated(Constants.calendarSectionEstimatedHeight)
                )
                let item = NSCollectionLayoutItem(layoutSize: itemSize)

                let groupSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(1.0),
                    heightDimension: .estimated(Constants.calendarSectionEstimatedHeight)
                )
                let group = NSCollectionLayoutGroup.vertical(layoutSize: groupSize, subitems: [item])

                let s = NSCollectionLayoutSection(group: group)
                s.contentInsets = .init(top: 0, leading: 0, bottom: 0, trailing: 0)
                return s

            case .devices:
                let itemSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(1.0),
                    heightDimension: .estimated(Constants.devicesSectionEstimatedHeight)
                )
                let item = NSCollectionLayoutItem(layoutSize: itemSize)

                let groupSize = NSCollectionLayoutSize(
                    widthDimension: .fractionalWidth(1.0),
                    heightDimension: .estimated(Constants.devicesSectionEstimatedHeight)
                )
                let group = NSCollectionLayoutGroup.vertical(layoutSize: groupSize, subitems: [item])

                let s = NSCollectionLayoutSection(group: group)
                s.interGroupSpacing = Constants.devicesSectionInterGroupSpacing
                s.contentInsets = .init(
                    top: Constants.devicesSectionTopInset,
                    leading: Constants.devicesSectionHorizontalInset,
                    bottom: 0,
                    trailing: Constants.devicesSectionHorizontalInset
                )
                return s
            }
        }
    }

    private func updateWeekdayLabelsToUppercase() {
        let weekdayLabels = calendarView.calendarWeekdayView.weekdayLabels
        let currentFont = calendarView.appearance.weekdayFont
        let currentColor = calendarView.appearance.weekdayTextColor

        for label in weekdayLabels {
            if let t = label.text { label.text = t.uppercased() }
            label.font = currentFont
            label.textColor = currentColor
        }
    }

    private func color(for mode: HardwareAccordionCell.Mode) -> UIColor {
        switch mode {
        case .economy:  return modeColorEconomy
        case .standart: return modeColorStandart
        case .maximal:  return modeColorMaximal
        }
    }

    private func updateMonthLabel(for page: Date) {
        monthLabel.text = monthFormatter.string(from: page)
    }

    private func indexPath(for deviceId: String) -> IndexPath? {
        guard let idx = devices.firstIndex(where: { $0.deviceId == deviceId }) else { return nil }
        return IndexPath(item: idx, section: Section.devices.rawValue)
    }

    private func persistCurrentState() {
        guard let id = openDeviceId else { return }
        stateByDeviceId[id] = DeviceCalendarState(currentMode: currentMode, modeDates: modeDates)
    }

    private func loadState(for deviceId: String) {
        if let s = stateByDeviceId[deviceId] {
            currentMode = s.currentMode
            modeDates = s.modeDates
        } else {
            currentMode = .standart
            modeDates = [.economy: [], .standart: [], .maximal: []]
        }
        prunePastDates()
    }

    private func clearFSCalendarSelection() {
        isSyncingCalendarSelection = true
        let selected = calendarView.selectedDates
        for d in selected { calendarView.deselect(d) }
        isSyncingCalendarSelection = false
    }

    private func syncFSCalendarSelectionFromModeDates() {
        isSyncingCalendarSelection = true
        defer { isSyncingCalendarSelection = false }

        let page = calendarView.currentPage
        clearFSCalendarSelection()

        let all = modeDates.values.reduce(into: Set<Date>()) { $0.formUnion($1) }
        for d in all {
            calendarView.select(d, scrollToDate: false)
        }

        calendarView.setCurrentPage(page, animated: false)
    }

    private func openOnly(deviceId: String, tappedCell: HardwareAccordionCell?) {
        let prev = openDeviceId

        persistCurrentState()

        if let prev, prev != deviceId, let ipPrev = indexPath(for: prev),
           let prevCell = collectionView.cellForItem(at: ipPrev) as? HardwareAccordionCell {
            prevCell.setExpandedAnimated(false)
        }

        openDeviceId = deviceId

        dragStart = nil
        dragEnd = nil
        dragAction = nil

        loadState(for: deviceId)
        syncFSCalendarSelectionFromModeDates()

        calendarView.reloadData()

        if let id = openDeviceId,
           let deviceRow = devices.first(where: { $0.deviceId == id } ),
           deviceRow.asicIds.count > 0
        {
            interactor?.loadPlannedCalendar(rpId: id, minerId: deviceRow.asicIds[0])
        }

        if let cell = tappedCell {
            cell.setSelectedMode(currentMode, animated: false)
            cell.setEditAction(editAction, animated: false)
        } else if let ip = indexPath(for: deviceId) {
            collectionView.reloadItems(at: [ip])
        }
    }

    private func commitDragRange() {
        guard openDeviceId != nil else { return }
        guard let a = dragStart, let b = dragEnd else { return }
        guard let action = dragAction else { return }

        let start = min(a, b)
        let end = max(a, b)

        var cur = start
        while cur <= end {
            let d = day(cur)

            if d < tomorrowStart {
                cur = gregorian.date(byAdding: .day, value: 1, to: cur)!
                continue
            }

            switch action {
            case .add:
                if canSelect(date: d, for: currentMode) {
                    modeDates[currentMode, default: []].insert(d)
                }
            case .remove:
                if let owner = allModesDateOwner(for: d) {
                    modeDates[owner]?.remove(d)
                }
            }

            cur = gregorian.date(byAdding: .day, value: 1, to: cur)!
        }

        persistCurrentState()
        syncFSCalendarSelectionFromModeDates()
    }

    private func day(_ date: Date) -> Date {
        gregorian.startOfDay(for: date)
    }

    private func allModesDateOwner(for date: Date) -> HardwareAccordionCell.Mode? {
        let d = day(date)
        if modeDates[.economy, default: []].contains(d) { return .economy }
        if modeDates[.standart, default: []].contains(d) { return .standart }
        if modeDates[.maximal, default: []].contains(d) { return .maximal }
        return nil
    }

    private func canSelect(date: Date, for mode: HardwareAccordionCell.Mode) -> Bool {
        let d = day(date)
        guard d >= tomorrowStart else { return false }
        guard let owner = allModesDateOwner(for: d) else { return true }
        return owner == mode
    }

    private func isInDragRange(_ date: Date) -> Bool {
        guard let a = dragStart, let b = dragEnd else { return false }
        let start = min(a, b)
        let end = max(a, b)
        let d = day(date)
        return (d >= start && d <= end)
    }

    private func isStartOfWeek(_ d: Date) -> Bool {
        gregorian.component(.weekday, from: d) == calendarView.firstWeekday
    }

    private func isEndOfWeek(_ d: Date) -> Bool {
        let fw = calendarView.firstWeekday
        let last = ((Int(fw) + Constants.daysInWeekMinusOne) % Constants.daysInWeek) + 1
        return gregorian.component(.weekday, from: d) == last
    }

    private func selectionStyle(
        for date: Date,
        mode: HardwareAccordionCell.Mode,
        includeDrag: Bool
    ) -> SelectionStyle {

        let d = day(date)

        guard d >= tomorrowStart else { return .none }

        let set = modeDates[mode, default: []]
        let action = dragAction ?? .add
        let isCurrentMode = (mode == currentMode)

        func effectiveSelected(_ dd: Date) -> Bool {
            let base = set.contains(dd)
            guard includeDrag, isCurrentMode, isInDragRange(dd) else { return base }

            switch action {
            case .add:
                return base || canSelect(date: dd, for: mode)
            case .remove:
                return false
            }
        }

        let selected = effectiveSelected(d)
        guard selected else { return .none }

        let prev = gregorian.date(byAdding: .day, value: -1, to: d)!
        let next = gregorian.date(byAdding: .day, value: 1, to: d)!

        let prevSelected = !isStartOfWeek(d) && effectiveSelected(prev)
        let nextSelected = !isEndOfWeek(d) && effectiveSelected(next)

        switch (prevSelected, nextSelected) {
        case (false, false): return .single
        case (false, true):  return .start
        case (true, false):  return .end
        case (true, true):   return .middle
        }
    }

    private func isBlockedDate(_ date: Date) -> Bool {
        day(date) < tomorrowStart
    }

    private func prunePastDates() {
        let min = tomorrowStart
        for mode in [HardwareAccordionCell.Mode.economy, .standart, .maximal] {
            guard var set = modeDates[mode] else { continue }
            set = Set(set.filter { $0 >= min })
            modeDates[mode] = set
        }
    }

    private func makeLegendItem(color: UIColor, title: String) -> UIView {
        let dot = UIView()
        dot.translatesAutoresizingMaskIntoConstraints = false
        dot.backgroundColor = color
        dot.layer.cornerRadius = Constants.legendDotCornerRadius
        dot.layer.masksToBounds = true
        dot.layer.borderWidth = Constants.legendDotBorderWidth
        dot.layer.borderColor = UIColor.white.withAlphaComponent(Constants.legendDotBorderAlpha).cgColor

        NSLayoutConstraint.activate([
            dot.widthAnchor.constraint(equalToConstant: Constants.legendDotSize),
            dot.heightAnchor.constraint(equalToConstant: Constants.legendDotSize)
        ])

        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = title
        label.textColor = UIColor.white.withAlphaComponent(Constants.legendTextAlpha)
        label.font = UIFont(name: Constants.legendFontName, size: Constants.legendFontSize)
        label.setContentCompressionResistancePriority(.required, for: .horizontal)

        let wrap = UIStackView(arrangedSubviews: [dot, label])
        wrap.translatesAutoresizingMaskIntoConstraints = false
        wrap.axis = .horizontal
        wrap.alignment = .center
        wrap.spacing = Constants.legendItemSpacing
        return wrap
    }

    @objc private func didTapPrev() {
        let prev = gregorian.date(byAdding: .month, value: -1, to: currentCalendarPage)!
        calendarView.setCurrentPage(prev, animated: true)
        updateMonthLabel(for: prev)
        currentCalendarPage = prev
    }

    @objc private func didTapNext() {
        let next = gregorian.date(byAdding: .month, value: 1, to: currentCalendarPage)!
        calendarView.setCurrentPage(next, animated: true)
        updateMonthLabel(for: next)
        currentCalendarPage = next
    }

    @objc private func handleDrag(_ g: UILongPressGestureRecognizer) {
        guard openDeviceId != nil else { return }

        switch g.state {
        case .began, .changed:
            let p = g.location(in: calendarView.collectionView)

            guard
                let indexPath = calendarView.collectionView.indexPathForItem(at: p),
                let cell = calendarView.collectionView.cellForItem(at: indexPath) as? FSCalendarCell,
                let date = calendarView.date(for: cell)
            else { return }

            let d = day(date)

            if isBlockedDate(d) {
                return
            }

            if g.state == .began {

                if editAction == .select {
                    if let owner = allModesDateOwner(for: d), owner != currentMode {
                        dragStart = nil
                        dragEnd = nil
                        dragAction = nil
                        return
                    }
                    dragAction = .add
                } else {
                    dragAction = .remove
                }

                dragStart = d
                dragEnd = d

            } else {
                dragEnd = d
            }

            calendarView.reloadData()

        case .ended, .cancelled, .failed:
            commitDragRange()
            dragStart = nil
            dragEnd = nil
            dragAction = nil
            calendarView.reloadData()

        default:
            break
        }
    }
}

// MARK: - Section

private extension MiningCalendarViewController {
    enum Section: Int, CaseIterable {
        case calendar = 0
        case devices  = 1
    }
}

// MARK: - DeviceCalendarState

private extension MiningCalendarViewController {
    struct DeviceCalendarState {
        var currentMode: HardwareAccordionCell.Mode
        var modeDates: [HardwareAccordionCell.Mode: Set<Date>]
    }
}

// MARK: - DragAction

private extension MiningCalendarViewController {
    enum DragAction {
        case add
        case remove
    }
}

// MARK: - UICollectionViewDataSource

extension MiningCalendarViewController: UICollectionViewDataSource {

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        Section.allCases.count
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        guard let s = Section(rawValue: section) else { return 0 }
        switch s {
        case .calendar: return 1
        case .devices:  return devices.count
        }
    }

    func collectionView(
        _ collectionView: UICollectionView,
        cellForItemAt indexPath: IndexPath
    ) -> UICollectionViewCell {

        guard let s = Section(rawValue: indexPath.section) else { return UICollectionViewCell() }

        switch s {
        case .calendar:
            let cell = collectionView.dequeueReusableCell(
                withReuseIdentifier: CalendarContainerCell.reuseId,
                for: indexPath
            ) as! CalendarContainerCell

            cell.embed(header: headerContainer, divider: divider, calendar: calendarView, legend: legendStack)
            return cell

        case .devices:
            let cell = collectionView.dequeueReusableCell(
                withReuseIdentifier: HardwareAccordionCell.reuseId,
                for: indexPath
            ) as! HardwareAccordionCell

            let row = devices[indexPath.item]
            let expanded = row.deviceId == openDeviceId

            let modeForThisDevice: HardwareAccordionCell.Mode = {
                if openDeviceId == row.deviceId { return currentMode }
                return stateByDeviceId[row.deviceId]?.currentMode ?? .standart
            }()

            let palette = HardwareAccordionCell.ModePalette(
                economy: modeColorEconomy,
                standart: modeColorStandart,
                maximal: modeColorMaximal
            )

            cell.configure(
                row: row,
                expanded: expanded,
                selectedMode: modeForThisDevice,
                actionStyle: .selectDeselect,
                editAction: editAction,
                modePalette: palette
            )

            cell.delegate = self
            return cell
        }
    }
}

// MARK: - UICollectionViewDelegate

extension MiningCalendarViewController: UICollectionViewDelegate { }

// MARK: - HardwareAccordionCellDelegate

extension MiningCalendarViewController: HardwareAccordionCellDelegate {

    func hardwareCellDidToggleExpand(_ cell: HardwareAccordionCell, deviceId: String, expanded: Bool) {

        let duration: TimeInterval = Constants.hardwareToggleAnimationDuration

        if expanded == false {
            return
        }

        guard !hardwareAnimatingIds.contains(deviceId) else { return }
        hardwareAnimatingIds.insert(deviceId)

        openOnly(deviceId: deviceId, tappedCell: cell)

        cell.setExpandedAnimated(true)

        UIView.animate(withDuration: duration,
                       delay: 0,
                       options: [.curveEaseInOut, .allowUserInteraction]) {
            self.collectionView.collectionViewLayout.invalidateLayout()
            self.collectionView.performBatchUpdates(nil)
            self.collectionView.layoutIfNeeded()
        } completion: { [weak self] _ in
            self?.hardwareAnimatingIds.remove(deviceId)
        }
    }

    func hardwareCellDidTapStart(
        _ cell: HardwareAccordionCell,
        deviceId: String,
        selectedMode: HardwareAccordionCell.Mode,
        completion: @escaping () -> Void
    ) {
        let deviceRow = devices.first(where: { $0.deviceId == deviceId })

        interactor?.cheduleRegimeChange(
            minerIds: deviceRow?.asicIds ?? [],
            dates: modeDates,
            completion: completion
        )
    }

    func hardwareCellDidTapSchedule(_ cell: HardwareAccordionCell, deviceId: String) { }

    func hardwareCellDidChangeMode(_ cell: HardwareAccordionCell, deviceId: String, mode: HardwareAccordionCell.Mode) {
        guard openDeviceId == deviceId else { return }
        currentMode = mode
        persistCurrentState()
        calendarView.reloadData()
    }

    func hardwareCellDidChangeEditAction(_ cell: HardwareAccordionCell, deviceId: String, action: HardwareAccordionCell.EditAction) {
        guard openDeviceId == deviceId else { return }
        editAction = action
    }
}

// MARK: - FSCalendarDataSource

extension MiningCalendarViewController: FSCalendarDataSource { }

// MARK: - FSCalendarDelegate

extension MiningCalendarViewController: FSCalendarDelegate {

    func calendar(_ calendar: FSCalendar, didSelect date: Date, at monthPosition: FSCalendarMonthPosition) {
        guard openDeviceId != nil else {
            isSyncingCalendarSelection = true
            calendar.deselect(date)
            isSyncingCalendarSelection = false
            return
        }

        if isSyncingCalendarSelection { return }

        let d = day(date)

        if let owner = allModesDateOwner(for: d), owner != currentMode {
            isSyncingCalendarSelection = true
            calendar.deselect(date)
            isSyncingCalendarSelection = false
            return
        }

        switch editAction {
        case .select:
            if canSelect(date: d, for: currentMode) {
                modeDates[currentMode, default: []].insert(d)
                persistCurrentState()
                calendar.reloadData()
            } else {
                isSyncingCalendarSelection = true
                calendar.deselect(date)
                isSyncingCalendarSelection = false
            }

        case .deselect:
            isSyncingCalendarSelection = true
            calendar.deselect(date)
            isSyncingCalendarSelection = false
        }
    }

    func calendar(_ calendar: FSCalendar, didDeselect date: Date, at monthPosition: FSCalendarMonthPosition) {
        guard openDeviceId != nil else { return }
        if isSyncingCalendarSelection { return }

        let d = day(date)

        switch editAction {
        case .select:
            isSyncingCalendarSelection = true
            calendar.select(d)
            isSyncingCalendarSelection = false

        case .deselect:
            if let owner = allModesDateOwner(for: d) {
                modeDates[owner]?.remove(d)
                persistCurrentState()
            }
            calendar.reloadData()
        }
    }

    func calendarCurrentPageDidChange(_ calendar: FSCalendar) {
        updateMonthLabel(for: calendar.currentPage)
        currentCalendarPage = calendar.currentPage
    }

    func calendar(_ calendar: FSCalendar, shouldSelect date: Date, at monthPosition: FSCalendarMonthPosition) -> Bool {
        return !isBlockedDate(date) && openDeviceId != nil
    }

    func calendar(_ calendar: FSCalendar, shouldDeselect date: Date, at monthPosition: FSCalendarMonthPosition) -> Bool {
        return !isBlockedDate(date) && openDeviceId != nil
    }
}

// MARK: - FSCalendarDelegateAppearance

extension MiningCalendarViewController: FSCalendarDelegateAppearance {

    func calendar(_ calendar: FSCalendar,
                  cellFor date: Date,
                  at position: FSCalendarMonthPosition) -> FSCalendarCell {

        let cell = calendar.dequeueReusableCell(
            withIdentifier: Constants.rangeCalendarCellReuseId,
            for: date,
            at: position
        ) as! RangeCalendarCell

        if isBlockedDate(date) {
            cell.setDisabled(true)
            cell.applySelection(style: .none, color: .clear)
         
        } else {
            cell.setDisabled(false)
        }

        guard openDeviceId != nil else {
            cell.applySelection(style: .none, color: .clear)
            return cell
        }

        if let ownerMode = allModesDateOwner(for: date) {
            let style = selectionStyle(for: date, mode: ownerMode, includeDrag: true)
            cell.applySelection(style: style, color: color(for: ownerMode))
            return cell
        }

        let canPaintDrag = isInDragRange(date) && canSelect(date: date, for: currentMode)
        if canPaintDrag {
            let style = selectionStyle(for: date, mode: currentMode, includeDrag: true)
            cell.applySelection(style: style, color: color(for: currentMode))
        } else {
            cell.applySelection(style: .none, color: .clear)
        }

        return cell
    }

    func calendar(_ calendar: FSCalendar,
                  appearance: FSCalendarAppearance,
                  titleDefaultColorFor date: Date) -> UIColor? {
        return isBlockedDate(date) ? UIColor(white: 1.0, alpha: 0.25) : .white
    }

    func calendar(_ calendar: FSCalendar,
                  appearance: FSCalendarAppearance,
                  titleSelectionColorFor date: Date) -> UIColor? {
        return isBlockedDate(date) ? UIColor(white: 1.0, alpha: 0.25) : .white
    }

}

// MARK: - MiningCalendarViewControllerInput

extension MiningCalendarViewController: MiningCalendarViewControllerInput {

    func showStartScreen() {
        delegate?.showAuthScreen()
    }

    func successCheduleRegimeChange(isSuccess: Bool) {
        let message: String
        let style: TopToastStyle

        if isSuccess {
            style = .success
            message = Constants.successToastMessage
        } else {
            style = .error
            message = Constants.errorToastMessage
        }

        TopToast.show(
            style: style,
            message: message,
            duration: Constants.toastDuration,
            fontSize: Constants.toastFontSize
        )
    }

    func applyPlannedModeDates(_ modeDates: [HardwareAccordionCell.Mode : Set<Date>], forDeviceId: String) {
        guard openDeviceId == forDeviceId else { return }

        self.modeDates = modeDates
        prunePastDates()
        persistCurrentState()
        syncFSCalendarSelectionFromModeDates()
        calendarView.reloadData()
    }
}

// MARK: - Constants

private extension MiningCalendarViewController {
    enum Constants {

        static let screenTitle: String = "Select period"

        static let monthLabelFontName: String = "Lato-SemiBold"
        static let monthLabelFontSize: CGFloat = 16

        static let prevButtonImageName: String = "chevron_left_small"
        static let nextButtonImageName: String = "chevron_right_small"

        static let dividerColorRed: CGFloat = 0.498
        static let dividerColorGreen: CGFloat = 0.498
        static let dividerColorBlue: CGFloat = 0.498
        static let dividerColorAlpha: CGFloat = 1

        static let calendarHeaderHeight: CGFloat = 0
        static let calendarWeekdayHeight: CGFloat = 40

        static let weekdayFontName: String = "Lato-Bold"
        static let weekdayFontSize: CGFloat = 12

        static let titleFontName: String = "Lato-Regular"
        static let titleFontSize: CGFloat = 12

        static let calendarFirstWeekday: UInt = 2

        static let rangeCalendarCellReuseId: String = "RangeCalendarCell"

        static let collectionBottomInset: CGFloat = 24

        static let dragMinimumPressDuration: TimeInterval = 0.1

        static let gregorianTimeZoneId: String = "Europe/Moscow"
        static let gregorianLocaleId: String = "en_US_POSIX"
        static let monthDateFormat: String = "LLLL yyyy"

        static let calendarSectionEstimatedHeight: CGFloat = 420
        static let devicesSectionEstimatedHeight: CGFloat = 84

        static let devicesSectionInterGroupSpacing: CGFloat = 10
        static let devicesSectionTopInset: CGFloat = 16
        static let devicesSectionHorizontalInset: CGFloat = 20

        static let hardwareToggleAnimationDuration: TimeInterval = 0.30

        static let navBarTitleFontName: String = "Lato-Bold"
        static let navBarTitleFontSize: CGFloat = 12
        static let navBarBackChevronImageName: String = "chevron_back"

        static let navBarTintRed: CGFloat = 0.671
        static let navBarTintGreen: CGFloat = 0.671
        static let navBarTintBlue: CGFloat = 0.671
        static let navBarTintAlpha: CGFloat = 1

        static let modeColorAlpha: CGFloat = 1

        static let modeEconomyRed: CGFloat = 0.624
        static let modeEconomyGreen: CGFloat = 0.702
        static let modeEconomyBlue: CGFloat = 1.000

        static let modeStandartRed: CGFloat = 0.184
        static let modeStandartGreen: CGFloat = 0.357
        static let modeStandartBlue: CGFloat = 1.000

        static let modeMaximalRed: CGFloat = 0.427
        static let modeMaximalGreen: CGFloat = 0.157
        static let modeMaximalBlue: CGFloat = 0.851

        static let legendHorizontalMargin: CGFloat = 16
        static let legendSpacing: CGFloat = 14

        static let legendDotSize: CGFloat = 10
        static let legendDotCornerRadius: CGFloat = 4
        static let legendDotBorderWidth: CGFloat = 1
        static let legendDotBorderAlpha: CGFloat = 0.12

        static let legendItemSpacing: CGFloat = 6

        static let legendTextAlpha: CGFloat = 0.70
        static let legendFontName: String = "Lato-Bold"
        static let legendFontSize: CGFloat = 10

        static let toastDuration: TimeInterval = 3.0
        static let toastFontSize: CGFloat = 14

        static let successToastMessage: String = "All miners switched mode successfully."
        static let errorToastMessage: String = "Some miners couldn’t switch mode. Please try again."

        static let daysInWeek: Int = 7
        static let daysInWeekMinusOne: Int = 5
    }
}
