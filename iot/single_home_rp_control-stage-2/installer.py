import sys
import yaml
from PySide6.QtWidgets import (QApplication, QMainWindow, QWidget, QVBoxLayout,
                               QHBoxLayout, QTreeWidget, QTreeWidgetItem, QFormLayout,
                               QLineEdit, QComboBox, QSpinBox, QPushButton, QTabWidget,
                               QMessageBox, QGroupBox, QScrollArea, QInputDialog)
from PySide6.QtCore import Qt, QTimer

# --- ЦЕНТРАЛИЗОВАННАЯ СХЕМА ---
SCHEMA = {
    "metricProviders": {
        "ASICMock": {
            "address": "str", "port": "int", "model": ["default", "S19", "M30S"],
            "user": "str", "password": "str"
        },
        "TemperatureSensorMock": {
            "pin": ["A1", "A2", "A3", "B1"]
        }
    },
    "metricAggregators": {
        "VirtualMetricProviderAggregated": {
            "input": "list_ref", "type": ["avg", "min", "max", "sum"], "metricName": "str"
        }
    },
    "conditions": {
        "ConditionObject": {
            "input": "ref", "metricName": "str", "type": [">=", "<=", "==", ">", "<"], "value": "int"
        }
    },
    "alerts": {
        "AlertObject": {
            "input": "ref", "metricName": "str", "type": [">=", "<=", "==", ">", "<"], "value": "int"
        }
    },
    "conditionGrouping": {
        "LogicNode": {"type": ["OR", "AND"], "input": "list_ref"}
    },
    "actuators": {
        "BypassValveMock": {"input": "ref"}
    }
}


class ConfigApp(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("CryptoTerm Configurator (Ultra Stable)")
        self.resize(1100, 800)

        self.system_data = {cat: [] for cat in SCHEMA.keys()}
        self.system_data["mqlTimeOut"] = 10
        self.global_settings = {
            "userEmail": "ZaluppaEzha@gmail.com", "apiKey": "default-device-key",
            "backendUrl": "https://cryptoterm.duckdns.org",
            "mqttHost": "158.160.185.120", "telegram": "@default_user", "ipAddress": "192.168.1.100",
            "ledNotification": {"pin": 11, "port": 1234}
        }

        self.init_ui()

    def init_ui(self):
        self.tabs = QTabWidget()

        # Topology
        self.topo_tab = QWidget()
        layout = QHBoxLayout(self.topo_tab)

        left_panel = QVBoxLayout()
        self.tree = QTreeWidget()
        self.tree.setHeaderLabel("Nodes Hierarchy")
        self.tree.itemClicked.connect(self.load_node_editor)

        btn_add = QPushButton("+ Add Node")
        btn_add.clicked.connect(self.add_node_dialog)
        btn_del = QPushButton("- Delete Selected")
        btn_del.clicked.connect(self.delete_node)

        left_panel.addWidget(self.tree)
        left_panel.addWidget(btn_add)
        left_panel.addWidget(btn_del)

        self.scroll = QScrollArea()
        self.scroll.setWidgetResizable(True)
        self.editor_widget = QWidget()
        self.scroll.setWidget(self.editor_widget)

        layout.addLayout(left_panel, 1)
        layout.addWidget(self.scroll, 2)
        self.tabs.addTab(self.topo_tab, "System Topology")

        # Settings
        self.settings_tab = QWidget()
        self.setup_settings_tab()
        self.tabs.addTab(self.settings_tab, "General Settings")

        btn_save = QPushButton("💾 SAVE ALL CONFIGS")
        btn_save.clicked.connect(self.save_all)
        btn_save.setStyleSheet("background-color: #27ae60; color: white; font-weight: bold; height: 40px;")

        main_layout = QVBoxLayout()
        main_layout.addWidget(self.tabs)
        main_layout.addWidget(btn_save)

        container = QWidget()
        container.setLayout(main_layout)
        self.setCentralWidget(container)
        self.refresh_tree()

    def clear_editor(self):
        """Безопасная очистка редактора"""
        # Сначала отвязываем виджет от scroll area
        old_widget = self.scroll.takeWidget()
        if old_widget:
            old_widget.deleteLater()

        # Создаем новый
        self.editor_widget = QWidget()
        self.editor_layout = QFormLayout(self.editor_widget)
        self.scroll.setWidget(self.editor_widget)

    def refresh_tree(self):
        self.tree.clear()
        for cat in SCHEMA.keys():
            cat_item = QTreeWidgetItem([cat])
            self.tree.addTopLevelItem(cat_item)
            for node in self.system_data.get(cat, []):
                name = node.get("name", "unnamed")
                node_item = QTreeWidgetItem([name])
                node_item.setData(0, Qt.UserRole, (cat, node))
                cat_item.addChild(node_item)
        self.tree.expandAll()

    def add_node_dialog(self):
        categories = list(SCHEMA.keys())
        cat, ok = QInputDialog.getItem(self, "Select Category", "Category:", categories, 0, False)
        if ok and cat:
            available_types = list(SCHEMA[cat].keys())
            node_type = available_types[0] if available_types else "default"
            new_node = {"name": f"New_{cat}_{len(self.system_data[cat]) + 1}", "type": node_type}
            self.system_data[cat].append(new_node)
            self.refresh_tree()

    def delete_node(self):
        item = self.tree.currentItem()
        if item and item.parent():
            cat, node_ref = item.data(0, Qt.UserRole)
            self.system_data[cat].remove(node_ref)
            self.clear_editor()
            self.refresh_tree()

    def load_node_editor(self, item):
        """Загрузка полей ноды. Вызывается либо по клику, либо через QTimer"""
        if not item or not item.parent():
            self.clear_editor()
            return

        cat, node_ref = item.data(0, Qt.UserRole)
        self.clear_editor()

        # 1. Unique Name
        name_edit = QLineEdit(node_ref.get("name", ""))
        name_edit.textChanged.connect(lambda t: self.update_name(node_ref, t, item))
        self.editor_layout.addRow("Unique Name (ID):", name_edit)

        # 2. Type Selection (Implementation)
        type_combo = QComboBox()
        available_types = list(SCHEMA[cat].keys())
        type_combo.addItems(available_types)

        # Блокируем сигналы при установке текста, чтобы не вызвать рекурсию
        type_combo.blockSignals(True)
        type_combo.setCurrentText(node_ref.get("type", ""))
        type_combo.blockSignals(False)

        # ВАЖНО: используем lambda с QTimer для смены типа
        type_combo.currentTextChanged.connect(lambda t: self.deferred_type_change(node_ref, cat, t, item))
        self.editor_layout.addRow("Implementation Type:", type_combo)

        # 3. Dynamic Fields
        current_type = node_ref.get("type")
        fields_schema = SCHEMA[cat].get(current_type, {})

        for field, f_type in fields_schema.items():
            val = node_ref.get(field, "")
            label_str = field.capitalize()

            if f_type == "str":
                w = QLineEdit(str(val))
                w.textChanged.connect(lambda t, f=field: node_ref.update({f: t}))
            elif f_type == "int":
                w = QSpinBox()
                w.setMaximum(999999)
                w.setValue(int(val) if val else 0)
                w.valueChanged.connect(lambda v, f=field: node_ref.update({f: v}))
            elif isinstance(f_type, list):
                w = QComboBox()
                w.addItems(f_type)
                w.setCurrentText(str(val))
                w.currentTextChanged.connect(lambda t, f=field: node_ref.update({f: t}))
            elif f_type == "ref":
                w = QComboBox()
                w.addItems([""] + self.get_all_names())
                w.setCurrentText(str(val))
                w.currentTextChanged.connect(lambda t, f=field: node_ref.update({f: t}))
            elif f_type == "list_ref":
                w = QLineEdit(", ".join(val) if isinstance(val, list) else str(val))
                w.setPlaceholderText("ID1, ID2")
                w.textChanged.connect(
                    lambda t, f=field: node_ref.update({f: [x.strip() for x in t.split(",") if x.strip()]}))

            self.editor_layout.addRow(label_str, w)

    def deferred_type_change(self, node_ref, cat, new_type, item):
        """Метод для безопасной смены типа через очередь событий"""
        if node_ref.get("type") == new_type:
            return

        node_ref["type"] = new_type
        # Очистка старых данных
        name = node_ref["name"]
        node_ref.clear()
        node_ref["name"] = name
        node_ref["type"] = new_type

        # Самое важное: откладываем выполнение load_node_editor
        QTimer.singleShot(0, lambda: self.load_node_editor(item))

    def update_name(self, node_ref, new_name, item):
        node_ref["name"] = new_name
        item.setText(0, new_name)

    def get_all_names(self):
        names = []
        for cat in SCHEMA.keys():
            for node in self.system_data[cat]:
                names.append(node.get("name", ""))
        return names

    def setup_settings_tab(self):
        layout = QFormLayout(self.settings_tab)
        self.setting_inputs = {}
        for key, val in self.global_settings.items():
            if isinstance(val, dict):
                grp = QGroupBox(key);
                gl = QFormLayout()
                for sk, sv in val.items():
                    le = QLineEdit(str(sv))
                    gl.addRow(sk, le);
                    self.setting_inputs[f"{key}.{sk}"] = le
                grp.setLayout(gl);
                layout.addRow(grp)
            else:
                le = QLineEdit(str(val))
                layout.addRow(key, le);
                self.setting_inputs[key] = le

    def validate_graph(self):
        adj = {}
        for cat in SCHEMA.keys():
            for n in self.system_data[cat]:
                name = n.get("name")
                inp = n.get("input", [])
                adj[name] = [inp] if isinstance(inp, str) else inp

        visited, stack = set(), set()

        def has_cycle(v):
            if v in stack: return True
            if v in visited: return False
            visited.add(v);
            stack.add(v)
            for neighbor in adj.get(v, []):
                if neighbor in adj and has_cycle(neighbor): return True
            stack.remove(v);
            return False

        for node in adj:
            if node not in visited:
                if has_cycle(node): return False
        return True

    def save_all(self):
        if not self.validate_graph():
            QMessageBox.critical(self, "Cycle Error", "Circular dependency detected!")
            return

        for key, widget in self.setting_inputs.items():
            v = widget.text()
            if "." in key:
                p, s = key.split(".");
                self.global_settings[p][s] = int(v) if v.isdigit() else v
            else:
                self.global_settings[key] = int(v) if v.isdigit() else v

        try:
            with open("metric_config.yaml", "w") as f:
                yaml.dump(self.system_data, f, sort_keys=False)
            with open("user_config.yaml", "w") as f:
                yaml.dump(self.global_settings, f, sort_keys=False)
            QMessageBox.information(self, "Success", "Configs saved!")
        except Exception as e:
            QMessageBox.critical(self, "Error", str(e))


if __name__ == "__main__":
    app = QApplication(sys.argv)
    app.setStyle("Fusion")
    gui = ConfigApp()
    gui.show()
    sys.exit(app.exec())