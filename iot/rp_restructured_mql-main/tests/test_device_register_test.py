import pytest
from src.deviceRegistration.registerDevice import registerDevice
from src.registrys.CommonRegistry import CommonRegistry
from src.mqlInitializing.mqlInitializing import initializeMQL

def test_registerDevice():
    registerDevice(
        CommonRegistry("./mockSystemConfig.yaml" ),
        CommonRegistry("./mockUserConfig.yaml"),
        "./mockResultingInternalConfig.yaml"
    )
    mql = initializeMQL(CommonRegistry("./mockResultingInternalConfig.yaml"))

    mql.run(0, 2)

if __name__ == "__main__":
    pytest.main([__file__])