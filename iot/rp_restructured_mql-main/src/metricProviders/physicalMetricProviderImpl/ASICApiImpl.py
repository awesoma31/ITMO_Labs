import requests


def parseMinerStats(miner_json):
    miner = miner_json["miner"]

    hashrate = miner.get("hr_realtime") or miner.get("instant_hashrate") or miner.get("hashrate") or 0

    chains = miner.get("chains", [])
    pcb_temps = []
    chip_temps = []
    for chain in chains:
        pcb_temp = (chain["pcb_temp"]["min"] + chain["pcb_temp"]["max"]) / 2
        chip_temp = (chain["chip_temp"]["min"] + chain["chip_temp"]["max"]) / 2
        pcb_temps.append(pcb_temp)
        chip_temps.append(chip_temp)
    if pcb_temps and chip_temps:
        avg_pcb = sum(pcb_temps) / len(pcb_temps)
        avg_chip = sum(chip_temps) / len(chip_temps)
        temp_c = (avg_pcb + avg_chip) / 2
    else:
        d_pcb = miner.get("pcb_temp", {})
        d_chip = miner.get("chip_temp", {})
        temp_c = (
                ((d_pcb.get("min", 0) + d_pcb.get("max", 0)) / 2 +
                 (d_chip.get("min", 0) + d_chip.get("max", 0)) / 2)
                / 2
        )

    power_consumption = miner.get("power_consumption", 0) or miner.get("power_usage", 0) or 0

    return {
        "hashrate": float(hashrate),
        "tempC": float(temp_c),
        "powerConsumption": float(power_consumption)
    }

def getToken(url: str, pw: str) -> str:
    response = requests.post(
        url,
        headers={"Content-Type": "application/json"},
        json={
            "pw": pw
        }
    )
    return response.json()["token"]

def getSummary(url: str, token: str) -> dict:
    headers = {
        "Authorization": f"Bearer {token}"
    }
    response = requests.get(url, headers=headers)
    return response.json()

def getMetric(url: str, pw: str) -> dict[str, float]:
    token = getToken(f"{url}/unlock", pw)
    summaryJson = getSummary(f"{url}/summary", token)
    return parseMinerStats(summaryJson)