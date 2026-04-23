from src.exceptions.ConfigError import ConfigError
from src.floatCondition.floatCondition import FloatCondition

class FloatConditionMoreThen(FloatCondition):
    def eval(self, a: float, b: float) -> bool:
        return a > b

class FloatConditionLessThen(FloatCondition):
    def eval(self, a: float, b: float) -> bool:
        return a < b

class FloatConditionMoreThenOrEq(FloatCondition):
    def eval(self, a: float, b: float) -> bool:
        return a >= b

class FloatConditionLessThenOrEq(FloatCondition):
    def eval(self, a: float, b: float) -> bool:
        return a <= b

class FloatConditionEq(FloatCondition):
    def eval(self, a: float, b: float) -> bool:
        return a == b

class FloatConditionNotEq(FloatCondition):
    def eval(self, a: float, b: float) -> bool:
        return a == b

def FloatConditionFabric(condition: str) -> FloatCondition:
    condition = condition.strip()
    if condition == ">=":
        return FloatConditionMoreThenOrEq()
    if condition == "<=":
        return FloatConditionLessThenOrEq()
    if condition == ">":
        return FloatConditionMoreThen()
    if condition == "<":
        return FloatConditionLessThen()
    if condition == "==":
        return FloatConditionNotEq()
    raise ConfigError(f"trying to parse non existing condition {condition}")
