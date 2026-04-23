class ConfigError(Exception):
    """Raised when there's an error in configuration"""
    def __init__(self, message="default developer error message"):
        super().__init__(message)