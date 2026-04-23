from src.booleanConditionGraph.BooleanNode import BooleanNode
from src.exceptions.ConfigError import ConfigError


class BooleanGraph:
    def __init__(self, nodes: list[BooleanNode]) -> None:
        self.nodes = nodes
        self.inputToNode: dict[str, list[BooleanNode]] = {}
        self.outputToNode: dict[str, BooleanNode] = {}
        for node in nodes:
            for inpId in node.inputIds:
                self.inputToNode.setdefault(inpId, []).append(node)
            self.outputToNode[node.outputId] = node
        self.outputNames: set[str] = \
            {i for i in self.outputToNode.keys() if i not in self.inputToNode}

    def evaluate(self, input: dict[str, bool]) -> dict[str, bool]:
        executionQueue: list[BooleanNode] = []
        result: dict[str, bool] = {}
        # just filling inputs
        for inp in input:
            self.__setInputs__(inp, input[inp], executionQueue)

        # check if smth gonna be executed
        if not executionQueue:
            return result
        # executing
        while executionQueue:
            currentNode = executionQueue.pop(0)
            nodeOutput = currentNode.evaluate()
            self.__setInputs__(currentNode.outputId, nodeOutput, executionQueue)
            self.__setToOutputs__(currentNode.outputId, nodeOutput, result)
            currentNode.dropInput()

        # тк у нод не сопоставляется вход и id входа
        # может быть такое, что при первом исполнении
        # нода частично заполнилась и не попала в
        # queue и при след исполнении ее входы
        # заполнятся и она попадет в queue
        # и будет работать с мусором, по этому
        # насильно сбрасываем все входы
        for node in self.nodes:
            node.dropInput()

        return result

    def __setInputs__(self, inputId: str, value: bool, exQueue: list) -> None:
        if inputId not in self.inputToNode:
            return
        for node in self.inputToNode[inputId]:
            node.setInput(value)
            if node.isInputFilled():
                exQueue.append(node)

    def __setToOutputs__(self, outputId: str, value: bool, result: dict[str, bool]) -> None:
        if outputId in self.outputNames:
            result[outputId] = value


