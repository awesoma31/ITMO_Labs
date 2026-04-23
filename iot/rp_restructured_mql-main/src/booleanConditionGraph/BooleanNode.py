from src.booleanConditionGraph.BooleanNodeMethod import BooleanNodeMethod
from src.booleanConditionGraph.BooleanNodeImpl import BooleanNodeFabric
from src.exceptions.DeveloperError import DeveloperError


class BooleanNode:
    """!
    @brief BooleanNode
    describes a boolean node
    you gotta set inputs and then you are able to execute it
    """

    def __init__(self, method: str, inputIds: list[str], outputId: str) -> None:
        self.method: BooleanNodeMethod = BooleanNodeFabric(method)
        self.inputIds: set[str] = set(inputIds)
        self.outputId: str = outputId
        self.inputs: list[bool] = []

    def evaluate(self) -> bool:
        if not self.isInputFilled():
            raise DeveloperError(f"Asked to execute node {self.outputId} without inputs filled!")
        return self.method.evaluate(self.inputs)

    def containsInput(self, inputId) -> bool:
        return inputId in self.inputIds

    def getOutputId(self):
        return self.outputId

    def setInput(self, value: bool) -> None:
        self.inputs.append(value)

    def isInputFilled(self) -> bool:
        return len(self.inputs) == len(self.inputIds)

    def dropInput(self) -> None:
        self.inputs.clear()