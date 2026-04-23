class DeveloperError(Exception):
    """Raised when there's an error, which shouldn't happen in production.
    if you catched one, means, I made some mistakes in validation or smth"""
    def __init__(self, message="default developer error message"):
        super().__init__(message)