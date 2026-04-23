class InternalError(Exception):
    """Raised when there's an error, which might happen in production.
    if you catched one, means smth gone terribbly wrong"""
    def __init__(self, message="default developer error message"):
        super().__init__(message)