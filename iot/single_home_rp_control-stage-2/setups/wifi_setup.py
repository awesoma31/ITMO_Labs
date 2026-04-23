#!/usr/bin/env python3
import subprocess
import time
import sys
import json
import os

def load_config():
    """Загружаем конфиг из JSON вместо YAML"""
    config_path = "/etc/cryptoterm/config/user-config.json"

    # Если файла нет - используем значения по умолчанию
    default_config = {
        "wifi": {
            "ssid": "YOUR_WIFI_SSID",
            "password": "YOUR_WIFI_PASSWORD"
        }
    }

    if os.path.exists(config_path):
        try:
            with open(config_path, 'r') as f:
                return json.load(f)
        except:
            return default_config
    else:
        return default_config

def start_other_services():
    try:
        subprocess.run(["systemctl", "enable", "deps-install.service"], check=True)
        subprocess.run(["systemctl", "enable", "control-loop.service"], check=True)
        subprocess.run(["systemctl", "enable", "notification-setup.service"], check=True)
        subprocess.run(["systemctl", "enable", "arduino-setup.service"], check=True)
    except Exception as e:
        print("enable failed, starting service:", e)
    subprocess.Popen(["systemctl", "start", "deps-install.service"])
    subprocess.Popen(["systemctl", "start", "notification-setup.service"])
    subprocess.Popen(["systemctl", "start", "arduino-setup.service"])
    subprocess.Popen(["systemctl", "start", "control-loop.service"])
    print("started service")

def run_cmd(cmd):
    print(f"Running: {cmd}")
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if result.returncode != 0:
        print(f"Error: {result.stderr}")
    else:
        print(f"Success: {result.stdout}")
    return result


def main():
    print("=== Starting WiFi Setup (NetworkManager) ===")

    config = load_config()
    wifi_config = config['wifi']
    ssid = wifi_config['ssid']
    password = wifi_config['password']

    print(f"Connecting to: {ssid}")

    # 1. Останавливаем wpa_supplicant если он работает
    print("1. Stopping wpa_supplicant...")
    run_cmd("sudo systemctl stop wpa_supplicant 2>/dev/null || true")
    run_cmd("sudo systemctl disable wpa_supplicant 2>/dev/null || true")

    # 2. Отключаем power management
    print("2. Disabling WiFi power management...")
    run_cmd("sudo iwconfig wlan0 power off")

    # 3. Перезапускаем NetworkManager
    print("3. Restarting NetworkManager...")
    run_cmd("sudo systemctl restart NetworkManager")
    time.sleep(5)

    # 4. Включаем WiFi интерфейс
    print("4. Enabling WiFi interface...")
    run_cmd("sudo nmcli radio wifi on")
    run_cmd("sudo nmcli device set wlan0 managed yes")

    # 5. Ждем пока интерфейс станет доступен
    print("5. Waiting for WiFi interface...")
    for i in range(10):
        result = run_cmd("nmcli device status | grep wlan0")
        if "wlan0" in result.stdout:
            break
        time.sleep(2)
    else:
        print("WiFi interface not found, trying to continue...")

    # 6. Удаляем старое подключение если есть
    print("6. Removing old connections...")
    run_cmd(f"sudo nmcli connection delete '{ssid}' 2>/dev/null || true")
    run_cmd("sudo nmcli connection delete 'preconfigured' 2>/dev/null || true")

    # 7. Сканируем сети (может занять время)
    print("7. Scanning for networks...")
    run_cmd("sudo nmcli device wifi rescan")
    time.sleep(10)

    # 8. Пытаемся подключиться через nmcli
    print(f"8. Connecting to {ssid}...")
    result = run_cmd(f"sudo nmcli device wifi connect '{ssid}' password '{password}'")

    if result.returncode != 0:
        print("First attempt failed, retrying with different approach...")
        # Альтернативный метод
        time.sleep(5)
        result = run_cmd(f"sudo nmcli device wifi connect '{ssid}' password '{password}' ifname wlan0")

    # 9. Ждем подключения
    print("9. Waiting for connection...")
    connected = False
    for i in range(30):
        # Проверяем статус подключения
        status_result = run_cmd("nmcli device status | grep wlan0")
        if "connected" in status_result.stdout:
            print(f"WiFi connected: {status_result.stdout.strip()}")
            connected = True
            break

        # Также проверяем через iwconfig
        iw_result = run_cmd("iwconfig wlan0 2>/dev/null | grep ESSID || true")
        if "off/any" not in iw_result.stdout and "ESSID" in iw_result.stdout:
            print(f"WiFi connected: {iw_result.stdout.strip()}")
            connected = True
            break

        time.sleep(2)

    if not connected:
        print("WiFi connection failed, but continuing...")

    # 10. Ждем IP адрес
    print("10. Waiting for IP address...")
    for i in range(20):
        ip_check = run_cmd("hostname -I")
        ips = ip_check.stdout.strip()
        if ips:
            # Ищем IP для wlan0
            ip_result = run_cmd("ip addr show wlan0 2>/dev/null | grep 'inet ' || true")
            if ip_result.stdout.strip():
                print(f"WiFi IP address: {ip_result.stdout.strip()}")
            else:
                print(f"IP addresses: {ips}")
            break
        time.sleep(3)
    else:
        print("No IP address received yet, but continuing...")

    # 11. Проверяем интернет
    print("11. Testing internet connectivity...")
    internet_check = run_cmd("ping -c 2 -W 3 8.8.8.8 2>/dev/null || ping -c 2 -W 3 1.1.1.1 2>/dev/null || true")

    if internet_check.returncode == 0:
        print("SUCCESS: Internet is available!")
    else:
        print("No internet access, but WiFi setup completed")

    # 12. Делаем подключение автоматическим
    print("12. Making connection persistent...")
    run_cmd(f"sudo nmcli connection modify '{ssid}' connection.autoconnect yes")

    # 13. Запускаем другие сервисы
    print("13. Starting other services...")
    start_other_services()

    print("=== WiFi Setup Completed ===")
    sys.exit(0)


if __name__ == "__main__":
    main()
