import pytest
from src.alerts.AlertChecker import AlertChecker

# just make sure, I ain't fucked up with interface

class AlertCheckerMock(AlertChecker):

    def __init__(self):
        self.lastMetric = {}

    def getRequestId(self):
        return "AlertCheckerMock"

    def check(self, metric: dict[str, float]) -> None:
        self.lastMetric = metric

def test_straightForwardInterfaceTest():
    alertChecker = AlertCheckerMock()
    _id = alertChecker.getRequestId()
    alertChecker.check({"metric": 1})
    assert alertChecker.lastMetric["metric"] == 1

if __name__ == "__main__":
    pytest.main([__file__])