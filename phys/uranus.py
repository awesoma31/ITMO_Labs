import matplotlib.pyplot as plt
import numpy as np
from matplotlib.animation import FuncAnimation

G = 6.67430e-11
M_sun = 1.9885e30  # масса Солнца, кг
AU = 1.495978707e11  # астрономическая единица, м

# УРАН
a = 19.19126393 * AU  # большая полуось
e = 0.04716771  # эксцентриситет
rp = a * (1 - e)  # перигелий

# Скорость в перигелии
multiplier = 1.0
vp = multiplier * np.sqrt(G * M_sun * (1 + e) / (a * (1 - e)))

pos = np.array([rp, 0.0])  # Уран в перигелии на оси X
vel = np.array([0.0, vp])  # скорость вдоль +Y

HOUR_STEP = 6
dt = HOUR_STEP * 3600  # шаг
sim_speed = 5000  # ускорение времени
period_est = 0.0
first_pass = True
prev_r = np.linalg.norm(pos)

fig, ax = plt.subplots(figsize=(6, 6))
(traj,) = ax.plot([], [], lw=1)
(pt,) = ax.plot([], [], "o", markersize=4)
(sun,) = ax.plot(0, 0, "o", ms=10)

ax.set_aspect("equal")
ax.set_xlim(-22, 22)
ax.set_ylim(-22, 22)
ax.set_xlabel("x (AU)")
ax.set_ylabel("y (AU)")
text_time = ax.text(0.02, 0.95, "", transform=ax.transAxes)
text_period = ax.text(0.02, 0.90, "", transform=ax.transAxes)

xs, ys = [], []


def update(frame):
    global pos, vel, prev_r, period_est, first_pass

    for _ in range(sim_speed):
        r = np.linalg.norm(pos)  # Расстояние до Солнца
        acc = -G * M_sun / r**3 * pos  # Текущее ускорение
        pos_n = pos + vel * dt
        r_n = np.linalg.norm(pos_n)
        acc_n = -G * M_sun / r_n**3 * pos_n
        vel += acc_n * dt
        pos = pos_n

        if prev_r > r and r_n > r:
            if first_pass:
                first_pass = False
                period_est = 0.0
            else:
                print(f"Период из симуляции: {period_est/365.25/24/3600:.3f} лет")
                ax.set_title("Полный оборот ✔", color="tab:green")
                # anim.event_source.stop()
                # break
            period_est = 0.0
        prev_r = r
        period_est += dt

    xs.append(pos[0] / AU)
    ys.append(pos[1] / AU)
    traj.set_data(xs, ys)
    pt.set_data([pos[0] / AU], [pos[1] / AU])

    sim_days = len(xs) * dt * sim_speed / 86400
    text_time.set_text(f"t = {sim_days/365.25:6.2f} yr")
    if not first_pass:
        text_period.set_text(f"period = {period_est/365.25/24/3600:6.2f} yr")
    return traj, pt, text_time, text_period


anim = FuncAnimation(fig, update, frames=20000000, interval=20, blit=False)
plt.show()
