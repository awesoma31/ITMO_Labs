import pytest

from src.mqlInitializing.mqlInitializing import initializeMQL
from src.registrys.CommonRegistry import CommonRegistry

def test_initializeFromConfig():
    configPath = "./mockInternalConfig.yaml"

    reg = CommonRegistry(configPath)
    mql = initializeMQL(reg)

    mql.run(0, 2)

if __name__ == "__main__":
    pytest.main([__file__])