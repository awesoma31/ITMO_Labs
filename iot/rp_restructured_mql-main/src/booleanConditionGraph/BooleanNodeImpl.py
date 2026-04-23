from src.booleanConditionGraph.BooleanNodeMethod import BooleanNodeMethod
from src.exceptions.ConfigError import ConfigError

class BooleanNodeAnd(BooleanNodeMethod):
    def evaluate(self, input: list[bool]) -> bool:
        for i in input:
            if not i:
                return False
        return True

class BooleanNodeOr(BooleanNodeMethod):
    def evaluate(self, input: list[bool]) -> bool:
        for i in input:
            if i:
                return True
        return False

class BooleanNodeNot(BooleanNodeMethod):
    def evaluate(self, input: list[bool]) -> bool:
        if len(input) != 1:
            raise ConfigError("Not boolean can only have one input.")
        return not input[0]

class BooleanNodeNothing(BooleanNodeMethod):
    def evaluate(self, input: list[bool]) -> bool:
        if len(input) != 1:
            raise ConfigError("Nothing boolean can only have one input.")
        return input[0]

def BooleanNodeFabric(method: str) -> BooleanNodeMethod:
    method = method.lower()
    if method == "and":
        return BooleanNodeAnd()
    elif method == "or":
        return BooleanNodeOr()
    if method == "not":
        return BooleanNodeNot()
    if method == "nothing" or method == "none":
        return BooleanNodeNothing()
    raise ConfigError("No such aggregation method: {}".format(method))
