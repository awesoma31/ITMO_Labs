import pytest
from src.booleanConditionGraph.BooleanGraph import BooleanGraph
from src.booleanConditionGraph.BooleanNode import BooleanNode

def runCommonTestCase(
        graph: BooleanGraph,
        testCases: list[tuple[dict[str, bool], dict[str, bool]]]
) -> BooleanGraph:
    for case in testCases:
        out = graph.evaluate(case[0])
        for key in case[1].keys():
            assert key in out
            assert out[key] == case[1][key]

def buildSimpleTestCase() -> BooleanGraph:
    or1Node = BooleanNode("or", ["a", "b"], "g1")
    or2Node = BooleanNode("or", ["c", "b"], "g2")
    andNode = BooleanNode("and", ["g1", "g2"], "y")
    return BooleanGraph([or1Node, andNode, or2Node])

def buildNotSimpleTestCase() -> BooleanGraph:
    notNode = BooleanNode("not", ["a"], "g1")
    andNode = BooleanNode("and", ["b", "g1"], "y")
    return BooleanGraph([notNode, andNode])

def buildTwoOutputsTestCase() -> BooleanGraph:
    and1Node = BooleanNode("and", ["a", "b"], "y1")
    and2Node = BooleanNode("and", ["b", "c"], "y2")
    return BooleanGraph([and1Node, and2Node])

def buildTwoComponentGraphTestCase() -> BooleanGraph:
    orNode = BooleanNode("or", ["a", "b"], "g1")
    andNode = BooleanNode("and", ["g1", "c"], "y")
    notNode = BooleanNode("not", ["d"], "g2")
    and2Node = BooleanNode("and", ["e", "g2"], "z")
    return BooleanGraph([orNode, and2Node, notNode, andNode])

def buildDeepGraphTestCase() -> BooleanGraph:
    orNode = BooleanNode("or", ["a", "b"], "g1")
    andNode = BooleanNode("and", ["g1", "c"], "g2")
    notNode = BooleanNode("not", ["g2"], "y")
    return BooleanGraph([orNode, andNode, notNode])

def buildPartlyConnectedGraphTestCase() -> BooleanGraph:
    orNode = BooleanNode("and", ["a", "b"], "y1")
    noneNode = BooleanNode("none", ["c"], "y2")
    return BooleanGraph([orNode, noneNode])

def buildTwoOutputsForOneNodeGraphTestCase() -> BooleanGraph:
    orNode = BooleanNode("or", ["a", "b"], "g1")
    andNode = BooleanNode("and", ["g1", "c", "g2"], "y")
    notNode = BooleanNode("not", ["d"], "g2")
    and2Node = BooleanNode("and", ["e", "g2"], "z")
    return BooleanGraph([orNode, and2Node, notNode, andNode])

def test_simple_graph():
    simpleGraph = buildSimpleTestCase()
    tetCases = [
        ({"a": False, "b": False, "c": True}, {"y": False}),
        ({"a": False, "b": True, "c": False}, {"y": True}),
    ]
    runCommonTestCase(simpleGraph, tetCases)

def test_simple_not_graph():
    simpleGraph = buildNotSimpleTestCase()
    tetCases = [
        ({"a": True, "b": True, "c": False}, {"y": False}),
        ({"a": False, "b": True, "c": True}, {"y": True}),
    ]
    runCommonTestCase(simpleGraph, tetCases)

def test_two_outputs_graph():
    twoOutputsGraph = buildTwoOutputsTestCase()
    tetCases = [
        ({"a": True, "b": False, "c": False}, {"y1": False, "y2": False}),
        ({"a": True, "b": True, "c": False}, {"y1": True, "y2": False}),
    ]
    runCommonTestCase(twoOutputsGraph, tetCases)

def test_two_components_graph():
    twoComponentsGraph = buildTwoComponentGraphTestCase()
    tetCases = [
        ({"a": True, "b": True, "c": False, "d": False, "e": True}, {"y": False, "z": True}),
        ({"a": False, "b": True, "c": True, "d": False, "e": False}, {"y": True, "z": False}),
        ({"a": False, "b": False, "c": True, "d": True, "e": True}, {"y": False, "z": False}),
    ]
    runCommonTestCase(twoComponentsGraph, tetCases)

def test_deep_graph():
    deepGraph = buildDeepGraphTestCase()
    tetCases = [
        ({"a": True, "b": False, "c": True}, {"y": False}),
        ({"a": True, "b": False, "c": False}, {"y": True}),
    ]
    runCommonTestCase(deepGraph, tetCases)

def test_partly_connected_graph():
    partlyConnectedGraph = buildPartlyConnectedGraphTestCase()
    tetCases = [
        ({"a": True, "b": True, "c": False}, {"y1": True, "y2": False}),
        ({"a": False, "b": True, "c": True}, {"y1": False, "y2": True}),
    ]
    runCommonTestCase(partlyConnectedGraph, tetCases)

def test_node_with_two_outputs_graph():
    twoComponentsGraph = buildTwoOutputsForOneNodeGraphTestCase()
    tetCases = [
        ({"a": True, "b": True, "c": False, "d": False, "e": True}, {"y": False, "z": True}),
        ({"a": False, "b": True, "c": True, "d": False, "e": False}, {"y": True, "z": False}),
        ({"a": False, "b": False, "c": True, "d": True, "e": True}, {"y": False, "z": False}),
    ]
    runCommonTestCase(twoComponentsGraph, tetCases)

if __name__ == "__main__":
    pytest.main([__file__])