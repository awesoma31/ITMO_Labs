from typing import Any
import yaml

from src.exceptions.InternalError import InternalError

class CommonRegistry:
    """!
    @brief Documentation for the class CommonRegistry.

    Common registry class stores all basic registrys centralized, allowing to
    get some fields of objects, set 'em and synchronize registry state with file
    """

    def __init__(self, configPath: str) -> None:
        self.path = configPath
        with open(configPath) as conf:
            self.data = yaml.safe_load(conf)

        self.deviceId = self.data.get("deviceId")

    def getParameterByPath(self, key: str, default: Any = None) -> Any:
        try:
            if key == ".":
                return self.data[key]

            if "." not in key and key in self.data:
                return self.data[key]

            keys = key.split(".")
            node = self.__resolveRoot(keys)
            return self.__traceGet(node, keys[2:])
        except (InternalError, KeyError):
            return default

    def setParameterByPath(self, key: str, value: Any) -> None:
        keys = key.split(".")
        node = self.__resolveRoot(keys)

        parent, lastKey = self.__traceGetParent(node, keys[2:])
        parent[lastKey] = value

        self.__flushToFile()

    def __resolveRoot(self, keys: list[str]) -> dict:
        if len(keys) < 2:
            return self.data[keys[0]]

        group, id_ = keys[0], keys[1]

        if group not in self.data:
            raise InternalError(f"group '{group}' not found in registry")

        groupData = self.data[group]

        # dict case
        if isinstance(groupData, dict):
            if id_ not in groupData:
                raise InternalError(f"id '{id_}' not found in group '{group}'")
            return groupData[id_]

        # list case
        for entry in groupData:
            if isinstance(entry, dict) and entry.get("id") == id_:
                return entry

        raise InternalError(f"id '{id_}' not found in group '{group}'")

    def __traceGet(self, node: dict, keys: list[str]) -> Any:
        current = node
        for key in keys:
            if not isinstance(current, dict) or key not in current:
                raise InternalError(f"key '{key}' not found in registry path")
            current = current[key]
        return current

    def __traceGetParent(self, node: dict, keys: list[str]) -> tuple[dict, str]:
        if not keys:
            raise InternalError("cannot set root object directly")

        current = node
        for key in keys[:-1]:
            if key not in current or not isinstance(current[key], dict):
                raise InternalError(f"key '{key}' not found in registry path")
            current = current[key]

        return current, keys[-1]

    def __flushToFile(self) -> None:
        with open(self.path, "w") as conf:
            yaml.safe_dump(self.data, conf, sort_keys=False)
