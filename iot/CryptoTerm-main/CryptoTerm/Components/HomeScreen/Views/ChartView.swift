import SwiftUI
import Charts
import UIKit
import Foundation

struct HashrateChartView: View {

    // MARK: Public properties

    let series: [Points]
    let onSelectAll: (_ selectedDate: Date, _ valuesById: [String: MetricPoint]) -> Void
    let onInteractionChanged: (_ isInteracting: Bool) -> Void
    let timeRange: TimeRange

    @State private var selectedDate: Date?
    @State private var selectedPointsById: [String: MetricPoint] = [:]

    enum TimeRange { case year, month, week, day }

    // MARK: Private properties

    private static let mskTZ = TimeZone(identifier: Constants.mskTimeZoneId)!
    private static var mskCalendar: Calendar {
        var cal = Calendar(identifier: .gregorian)
        cal.timeZone = mskTZ
        return cal
    }

    private static let yearFormatter: DateFormatter = {
        let f = DateFormatter()
        f.locale = Locale(identifier: Constants.posixLocaleId)
        f.timeZone = mskTZ
        f.dateFormat = Constants.yearDateFormat
        return f
    }()

    private static let monthFormatter: DateFormatter = {
        let f = DateFormatter()
        f.locale = Locale(identifier: Constants.posixLocaleId)
        f.timeZone = mskTZ
        f.dateFormat = Constants.monthDateFormat
        return f
    }()

    private static let weekFormatter: DateFormatter = {
        let f = DateFormatter()
        f.locale = Locale(identifier: Constants.posixLocaleId)
        f.timeZone = mskTZ
        f.dateFormat = Constants.weekDateFormat
        return f
    }()

    private static let dayFormatter: DateFormatter = {
        let f = DateFormatter()
        f.locale = Locale(identifier: Constants.posixLocaleId)
        f.timeZone = mskTZ
        f.dateFormat = Constants.dayDateFormat
        return f
    }()

    // MARK: Body

    var body: some View {
        let hasAnyPoints = series.contains { !$0.points.isEmpty }

        if series.isEmpty {
            emptySelectionStub
        } else if !hasAnyPoints {
            noDataStub
        } else {

            let sortedSeries: [Points] = series.map { s in
                var copy = s
                copy.points = s.points.sorted { $0.time < $1.time }
                return copy
            }

            let colorById: [String: Color] = Dictionary(
                uniqueKeysWithValues: sortedSeries.map { ($0.id, Color(uiColor: $0.color)) }
            )

            let allPoints: [MetricPoint] = sortedSeries
                .flatMap { $0.points }
                .sorted { $0.time < $1.time }

            let now = Date()

            Chart {
                ForEach(sortedSeries) { one in
                    if one.points.count <= 1 {
                        ForEach(one.points) { p in
                            PointMark(
                                x: .value(Constants.axisTimeTitle, p.time),
                                y: .value(Constants.axisValueTitle, p.value)
                            )
                            .symbolSize(Constants.singlePointSymbolSize)
                            .foregroundStyle(colorById[one.id] ?? .white)
                        }
                    } else {
                        ForEach(one.points) { p in
                            LineMark(
                                x: .value(Constants.axisTimeTitle, p.time),
                                y: .value(Constants.axisValueTitle, p.value),
                                series: .value(Constants.axisSeriesTitle, one.id)
                            )
                            .lineStyle(StrokeStyle(
                                lineWidth: Constants.lineWidth,
                                lineCap: Constants.lineCap,
                                lineJoin: Constants.lineJoin
                            ))
                            .foregroundStyle(colorById[one.id] ?? .white)
                        }
                    }
                }

                ForEach(sortedSeries) { one in
                    if let sp = selectedPointsById[one.id] {
                        PointMark(
                            x: .value(Constants.axisTimeTitle, sp.time),
                            y: .value(Constants.axisValueTitle, sp.value)
                        )
                        .symbolSize(Constants.selectedPointSymbolSize)
                        .foregroundStyle(Color(uiColor: one.color))
                    }
                }
            }
            .environment(\.timeZone, Self.mskTZ)
            .environment(\.calendar, Self.mskCalendar)
            .chartXScale(domain: fixedXDomain(now: now))
            .chartYScale(domain: extendedYDomain(allPoints))
            .chartXAxis { buildXAxis() }
            .chartYAxis { buildYAxis() }
            .chartOverlay { proxy in
                GeometryReader { geo in
                    let plotFrame = geo[proxy.plotAreaFrame]

                    Rectangle()
                        .fill(Color.clear)
                        .contentShape(Rectangle())
                        .gesture(
                            DragGesture(minimumDistance: Constants.dragMinimumDistance)
                                .onChanged { gesture in
                                    onInteractionChanged(true)

                                    guard !sortedSeries.isEmpty, !allPoints.isEmpty else { return }

                                    let clampedX = clamp(gesture.location.x, min: plotFrame.minX, max: plotFrame.maxX)
                                    let xInPlot = clampedX - plotFrame.origin.x

                                    guard let rawDate: Date = proxy.value(atX: xInPlot) else { return }
                                    guard let anchor = nearestTime(to: rawDate, points: allPoints) else { return }

                                    var dict: [String: MetricPoint] = [:]
                                    for s in sortedSeries {
                                        if let nearest = nearestPoint(to: anchor, points: s.points) {
                                            dict[s.id] = nearest
                                        }
                                    }

                                    selectedDate = anchor
                                    selectedPointsById = dict
                                    onSelectAll(anchor, dict)
                                }
                                .onEnded { _ in
                                    onInteractionChanged(false)

                                    selectedDate = nil
                                    selectedPointsById = [:]

                                    var dict: [String: MetricPoint] = [:]
                                    for s in sortedSeries {
                                        if let last = s.points.last { dict[s.id] = last }
                                    }

                                    onSelectAll(Date(), dict)
                                }
                        )

                    if let d = selectedDate,
                       let xPos = proxy.position(forX: d) {
                        Rectangle()
                            .fill(Color.white.opacity(Constants.selectionLineOpacity))
                            .frame(width: Constants.selectionLineWidth, height: plotFrame.height)
                            .position(
                                x: plotFrame.origin.x + xPos,
                                y: plotFrame.midY
                            )
                    }
                }
            }
            .padding(.horizontal, Constants.chartPaddingHorizontal)
            .padding(.vertical, Constants.chartPaddingVertical)
        }
    }

    // MARK: Stubs

    private var emptySelectionStub: some View {
        VStack(spacing: Constants.stubVStackSpacing) {
            Image(systemName: Constants.stubIconName)
                .font(.system(size: Constants.stubIconSize, weight: Constants.stubIconWeight))
                .foregroundStyle(.secondary)

            Text(Constants.emptySelectionTitle)
                .font(.custom(Constants.latoBoldFontName, size: Constants.stubTitleFontSize))
                .foregroundStyle(Color.white.opacity(Constants.stubTitleOpacity))

            Text(Constants.emptySelectionSubtitle)
                .font(.custom(Constants.latoRegularFontName, size: Constants.stubSubtitleFontSize))
                .foregroundStyle(Color.white.opacity(Constants.stubSubtitleOpacity))
                .multilineTextAlignment(.center)
                .padding(.horizontal, Constants.stubTextHorizontalPadding)
        }
        .frame(maxWidth: .infinity, minHeight: Constants.stubMinHeight)
        .padding(.vertical, Constants.stubOuterVerticalPadding)
        .padding(.horizontal, Constants.stubOuterHorizontalPadding)
    }

    private var noDataStub: some View {
        VStack(spacing: Constants.stubVStackSpacing) {
            Image(systemName: Constants.stubIconName)
                .font(.system(size: Constants.stubIconSize, weight: Constants.stubIconWeight))
                .foregroundStyle(.secondary)

            Text(Constants.noDataTitle)
                .font(.custom(Constants.latoBoldFontName, size: Constants.stubTitleFontSize))
                .foregroundStyle(Color.white.opacity(Constants.stubTitleOpacity))

            Text(Constants.noDataSubtitle)
                .font(.custom(Constants.latoRegularFontName, size: Constants.stubSubtitleFontSize))
                .foregroundStyle(Color.white.opacity(Constants.stubSubtitleOpacity))
                .multilineTextAlignment(.center)
                .padding(.horizontal, Constants.stubTextHorizontalPadding)
        }
        .frame(maxWidth: .infinity, minHeight: Constants.stubMinHeight)
        .padding(.vertical, Constants.stubOuterVerticalPadding)
        .padding(.horizontal, Constants.stubOuterHorizontalPadding)
    }

    // MARK: Axis

    @AxisContentBuilder
    private func buildXAxis() -> some AxisContent {
        switch timeRange {
        case .year:
            AxisMarks(preset: .aligned, values: .stride(by: .month, count: Constants.xYearGridStrideMonths)) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: Constants.axisGridLineWidth))
                    .foregroundStyle(Constants.axisGridColor)
                AxisTick(centered: true, length: Constants.axisTickLengthZero)
                AxisValueLabel { }
            }
            AxisMarks(preset: .aligned, position: .bottom, values: .stride(by: .month, count: Constants.xYearLabelStrideMonths)) { value in
                AxisValueLabel {
                    if let date = value.as(Date.self) {
                        Text(Self.yearFormatter.string(from: date))
                            .foregroundStyle(Constants.axisLabelColor)
                            .font(.custom(Constants.latoRegularFontName, size: Constants.axisLabelFontSize))
                    }
                }
            }

        case .month:
            AxisMarks(preset: .aligned, values: .stride(by: .day, count: Constants.xMonthGridStrideDays)) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: Constants.axisGridLineWidth))
                    .foregroundStyle(Constants.axisGridColor)
                AxisTick(centered: true, length: Constants.axisTickLengthZero)
                AxisValueLabel { }
            }
            AxisMarks(preset: .aligned, position: .bottom, values: .stride(by: .day, count: Constants.xMonthLabelStrideDays)) { value in
                AxisValueLabel {
                    if let date = value.as(Date.self) {
                        Text(Self.monthFormatter.string(from: date))
                            .foregroundStyle(Constants.axisLabelColor)
                            .font(.custom(Constants.latoRegularFontName, size: Constants.axisLabelFontSize))
                    }
                }
            }

        case .week:
            AxisMarks(preset: .aligned, values: .stride(by: .day, count: Constants.xWeekStrideDays)) { value in
                AxisGridLine(stroke: StrokeStyle(lineWidth: Constants.axisGridLineWidth))
                    .foregroundStyle(Constants.axisGridColor)
                AxisTick(centered: true, length: Constants.axisTickLengthZero)
                AxisValueLabel {
                    if let date = value.as(Date.self) {
                        Text(Self.weekFormatter.string(from: date))
                            .foregroundStyle(Constants.axisLabelColor)
                            .font(.custom(Constants.latoRegularFontName, size: Constants.axisLabelFontSize))
                    }
                }
            }

        case .day:
            AxisMarks(preset: .aligned, values: .stride(by: .hour, count: Constants.xDayGridStrideHours)) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: Constants.axisGridLineWidth))
                    .foregroundStyle(Constants.axisGridColor)
                AxisTick(centered: true, length: Constants.axisTickLengthZero)
                AxisValueLabel { }
            }
            AxisMarks(preset: .aligned, position: .bottom, values: .stride(by: .hour, count: Constants.xDayLabelStrideHours)) { value in
                AxisValueLabel {
                    if let date = value.as(Date.self) {
                        Text(formatDateForDay(date))
                            .foregroundStyle(Constants.axisLabelColor)
                            .font(.custom(Constants.latoRegularFontName, size: Constants.axisLabelFontSize))
                    }
                }
            }
        }
    }

    @AxisContentBuilder
    private func buildYAxis() -> some AxisContent {
        AxisMarks(position: .leading, values: .automatic(desiredCount: Constants.yDesiredCount)) { _ in
            AxisGridLine(stroke: StrokeStyle(lineWidth: Constants.axisGridLineWidth))
                .foregroundStyle(Constants.axisGridColor)
            AxisTick(centered: true, length: Constants.axisTickLengthZero)
            AxisValueLabel { }
        }

        AxisMarks(position: .leading, values: .automatic(desiredCount: Constants.yDesiredCount)) { value in
            AxisValueLabel {
                if let v = value.as(Double.self) {
                    Text(formatYValue(v))
                        .foregroundStyle(Constants.axisLabelColor)
                        .font(.custom(Constants.latoRegularFontName, size: Constants.axisLabelFontSize))
                }
            }
        }
    }

    // MARK: Helpers

    private func fixedXDomain(now: Date) -> ClosedRange<Date> {
        let cal = Self.mskCalendar

        func startOfMonth(_ d: Date) -> Date {
            let comps = cal.dateComponents([.year, .month], from: d)
            return cal.date(from: comps)!
        }

        func startOfDay(_ d: Date) -> Date {
            cal.startOfDay(for: d)
        }

        func startOfHour(_ d: Date) -> Date {
            let comps = cal.dateComponents([.year, .month, .day, .hour], from: d)
            return cal.date(from: comps)!
        }

        switch timeRange {
        case .year:
            let endCore = startOfMonth(now)
            let startCore = cal.date(byAdding: .month, value: -Constants.xYearCoreMonthsBack, to: endCore)!
            let start = cal.date(byAdding: .day, value: -Constants.xYearPaddingDays, to: startCore)!
            let end = cal.date(byAdding: .day, value: Constants.xYearPaddingDays, to: endCore)!
            return start...end

        case .month:
            let endCore = startOfDay(now)
            let startCore = cal.date(byAdding: .day, value: -Constants.xMonthCoreDaysBack, to: endCore)!
            let start = cal.date(byAdding: .hour, value: -Constants.xMonthPaddingHours, to: startCore)!
            let end = cal.date(byAdding: .hour, value: Constants.xMonthPaddingHours, to: endCore)!
            return start...end

        case .week:
            let endCore = startOfDay(now)
            let startCore = cal.date(byAdding: .day, value: -Constants.xWeekCoreDaysBack, to: endCore)!
            let start = cal.date(byAdding: .hour, value: -Constants.xWeekPaddingHours, to: startCore)!
            let end = cal.date(byAdding: .hour, value: Constants.xWeekPaddingHours, to: endCore)!
            return start...end

        case .day:
            let endCore = startOfHour(now)
            let startCore = cal.date(byAdding: .hour, value: -Constants.xDayCoreHoursBack, to: endCore)!
            let start = cal.date(byAdding: .minute, value: -Constants.xDayPaddingMinutes, to: startCore)!
            let end = cal.date(byAdding: .minute, value: Constants.xDayPaddingMinutes, to: endCore)!
            return start...end
        }
    }

    private func extendedYDomain(_ points: [MetricPoint]) -> ClosedRange<Double> {
        guard
            let minVal = points.map(\.value).min(),
            let maxVal = points.map(\.value).max()
        else { return Constants.defaultYMin...Constants.defaultYMax }

        if minVal == maxVal {
            let pad = Swift.max(Constants.yPadMin, abs(maxVal) * Constants.yPadEqualFactor, Constants.yPadEqualExtraMin)
            return Swift.max(Constants.yClampMin, minVal - pad)...(maxVal + pad)
        }

        let span = maxVal - minVal

        let basePad = Swift.max(Constants.yPadMin, span * Constants.yPadSpanFactor)
        let extra = Swift.max(span * Constants.yPadExtraSpanFactor, Constants.yPadExtraMin)

        let extendedMin = minVal - basePad - extra
        let extendedMax = maxVal + basePad + extra

        return Swift.max(Constants.yClampMin, extendedMin)...extendedMax
    }

    private func nearestPoint(to date: Date, points: [MetricPoint]) -> MetricPoint? {
        points.min(by: {
            abs($0.time.timeIntervalSince(date)) < abs($1.time.timeIntervalSince(date))
        })
    }

    private func nearestTime(to date: Date, points: [MetricPoint]) -> Date? {
        points.min(by: {
            abs($0.time.timeIntervalSince(date)) < abs($1.time.timeIntervalSince(date))
        })?.time
    }

    private func formatDateForDay(_ date: Date) -> String {
        let cal = Self.mskCalendar
        let hour = cal.component(.hour, from: date)
        let minute = cal.component(.minute, from: date)
        
        if minute == Constants.zeroInt { return String(format: Constants.hourZeroFormat, hour) }
        return Self.dayFormatter.string(from: date)
    }

    private func formatYValue(_ value: Double) -> String {
        if value >= Constants.million { return String(format: Constants.formatOneDecimalM, value / Constants.million) }
        if value >= Constants.thousand { return String(format: Constants.formatNoDecimalK, value / Constants.thousand) }
        if value >= Constants.oneDouble { return String(format: Constants.formatNoDecimal, value) }
        return String(format: Constants.formatOneDecimal, value)
    }

    private func clamp(_ x: CGFloat, min: CGFloat, max: CGFloat) -> CGFloat {
        if x < min { return min }
        if x > max { return max }
        return x
    }
}

// MARK: - Constants

private extension HashrateChartView {
    enum Constants {
        static let mskTimeZoneId = "Europe/Moscow"
        static let posixLocaleId = "en_US_POSIX"

        static let yearDateFormat = "MMM"
        static let monthDateFormat = "d"
        static let weekDateFormat = "EEE"
        static let dayDateFormat = "HH:mm"

        static let axisTimeTitle = "Time"
        static let axisValueTitle = "Value"
        static let axisSeriesTitle = "Series"

        static let singlePointSymbolSize: CGFloat = 50
        static let selectedPointSymbolSize: CGFloat = 40

        static let lineWidth: CGFloat = 1
        static let lineCap: CGLineCap = .round
        static let lineJoin: CGLineJoin = .round

        static let dragMinimumDistance: CGFloat = 0

        static let selectionLineOpacity: Double = 0.55
        static let selectionLineWidth: CGFloat = 1

        static let chartPaddingHorizontal: CGFloat = 8
        static let chartPaddingVertical: CGFloat = 6

        static let stubIconName = "chart.xyaxis.line"
        static let stubIconSize: CGFloat = 34
        static let stubIconWeight: Font.Weight = .semibold

        static let emptySelectionTitle = "No devices selected"
        static let emptySelectionSubtitle = "Select one or more devices to see their metrics and compare trends on the chart."

        static let noDataTitle = "No data for selected devices"
        static let noDataSubtitle = "There are no metrics available for the current time range.Try another range or check device connectivity."

        static let latoBoldFontName = "Lato-Bold"
        static let latoRegularFontName = "Lato-Regular"

        static let stubTitleFontSize: CGFloat = 16
        static let stubSubtitleFontSize: CGFloat = 13

        static let stubTitleOpacity: Double = 0.92
        static let stubSubtitleOpacity: Double = 0.65

        static let stubTextHorizontalPadding: CGFloat = 18

        static let stubMinHeight: CGFloat = 160
        static let stubOuterVerticalPadding: CGFloat = 12
        static let stubOuterHorizontalPadding: CGFloat = 8

        static let stubVStackSpacing: CGFloat = 10

        static let axisGridLineWidth: CGFloat = 1
        static let axisGridColor = Color(red: 0.341, green: 0.341, blue: 0.341, opacity: 0.25)

        static let axisTickLengthZero: CGFloat = 0

        static let axisLabelColor = Color(red: 0.502, green: 0.502, blue: 0.502, opacity: 0.69)
        static let axisLabelFontSize: CGFloat = 12

        static let xYearGridStrideMonths = 1
        static let xYearLabelStrideMonths = 2

        static let xMonthGridStrideDays = 2
        static let xMonthLabelStrideDays = 2

        static let xWeekStrideDays = 1

        static let xDayGridStrideHours = 2
        static let xDayLabelStrideHours = 4

        static let yDesiredCount = 6

        static let xYearCoreMonthsBack = 11
        static let xYearPaddingDays = 15

        static let xMonthCoreDaysBack = 30
        static let xMonthPaddingHours = 12

        static let xWeekCoreDaysBack = 6
        static let xWeekPaddingHours = 12

        static let xDayCoreHoursBack = 23
        static let xDayPaddingMinutes = 30

        static let defaultYMin: Double = 0
        static let defaultYMax: Double = 100

        static let yPadMin: Double = 5
        static let yPadEqualFactor: Double = 0.15
        static let yPadEqualExtraMin: Double = 2
        static let yPadSpanFactor: Double = 0.15
        static let yPadExtraSpanFactor: Double = 0.30
        static let yPadExtraMin: Double = 2
        static let yClampMin: Double = 0

        static let zeroInt = 0
        static let hourZeroFormat = "%02d:00"

        static let million: Double = 1_000_000
        static let thousand: Double = 1_000
        static let oneDouble: Double = 1

        static let formatOneDecimalM = "%.1fM"
        static let formatNoDecimalK = "%.0fK"
        static let formatNoDecimal = "%.0f"
        static let formatOneDecimal = "%.1f"
    }
}

