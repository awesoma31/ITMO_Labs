import matplotlib.pyplot as plt
import numpy as np
from matplotlib.animation import FuncAnimation

g = 6.67430e-11
m_sun = 1.9885e30
au = 1.495978707e11

# Уран
a = 19.19126393 * au
e = 0.04716771
rp = a * (1 - e)
vp = np.sqrt(g * m_sun * (1 + e) / (a * (1 - e)))

pos = np.array([rp, 0.0])
vel = np.array([0.0, vp])

hour_step = 6
dt = hour_step * 3600
sim_speed = 5000
period_est = 0.0
first_pass = True
prev_r = np.linalg.norm(pos)

areas = []
r_errors = []
prev_pos = pos.copy()

fig, ax = plt.subplots(figsize=(6, 6))
(traj,) = ax.plot([], [], lw=1)
(pt,) = ax.plot([], [], "o", markersize=4)
(sun,) = ax.plot(0, 0, "o", ms=10)
text_time = ax.text(0.02, 0.95, "", transform=ax.transAxes)
text_period = ax.text(0.02, 0.90, "", transform=ax.transAxes)
text_area = ax.text(0.02, 0.85, "", transform=ax.transAxes)
text_rerror = ax.text(0.02, 0.80, "", transform=ax.transAxes)

ax.set_aspect("equal")
ax.set_xlim(-22, 22)
ax.set_ylim(-22, 22)
ax.set_xlabel("x (au)")
ax.set_ylabel("y (au)")

xs, ys = [], []


def update(frame):
    global pos, vel, prev_r, period_est, first_pass, prev_pos

    for _ in range(sim_speed):
        r = np.linalg.norm(pos)
        acc = -g * m_sun / r**3 * pos
        pos_n = pos + vel * dt
        r_n = np.linalg.norm(pos_n)
        acc_n = -g * m_sun / r_n**3 * pos_n
        vel += acc_n * dt
        pos = pos_n

        if prev_r > r and r_n > r:
            if first_pass:
                first_pass = False
                period_est = 0.0
            else:
                # III
                years = period_est / (365.25 * 24 * 3600)
                print(f"Период из симуляции: {years:.3f} лет")
                lhs = years**2
                rhs = (a / au) ** 3
                print(
                    f"Сравнение T² = {lhs:.3f}, a³ = {rhs:.3f}, T²/a³ = {lhs / rhs:.3f}"
                )
                if r_errors:
                    mean_err = np.mean(r_errors)
                    max_err = np.max(r_errors)
                    print(f"Ошибка r (средн.): {mean_err:.2e}, макс: {max_err:.2e}")
                ax.set_title("Полный оборот ✔", color="tab:green")
                anim.event_source.stop()
            period_est = 0.0
        prev_r = r
        period_est += dt

        # II
        v1 = np.array([prev_pos[0], prev_pos[1], 0.0])
        v2 = np.array([pos[0], pos[1], 0.0])
        sector_area = 0.5 * np.linalg.norm(np.cross(v1, v2))
        areas.append(sector_area)
        prev_pos = pos.copy()

        # I
        r_sim = np.linalg.norm(pos)
        theta = np.arctan2(pos[1], pos[0])
        r_theory = a * (1 - e**2) / (1 + e * np.cos(theta))
        rel_error = abs(r_sim - r_theory) / r_theory
        r_errors.append(rel_error)

    xs.append(pos[0] / au)
    ys.append(pos[1] / au)
    traj.set_data(xs, ys)
    pt.set_data([pos[0] / au], [pos[1] / au])

    sim_days = len(xs) * dt * sim_speed / 86400
    text_time.set_text(f"t = {sim_days / 365.25:6.2f} yr")
    if not first_pass:
        text_period.set_text(f"period = {period_est / 365.25 / 24 / 3600:6.2f} yr")
    if len(areas) > 100:
        recent = areas[-100:]
        mean_area = np.mean(recent)
        std_area = np.std(recent)
        text_area.set_text(f"area = {mean_area:.2e} ± {std_area:.1e}")
    if len(r_errors) > 100:
        recent_r = r_errors[-100:]
        text_rerror.set_text(f"r err = {np.mean(recent_r):.1e}")
    return traj, pt, text_time, text_period, text_area, text_rerror


anim = FuncAnimation(fig, update, frames=20000000, interval=20, blit=False)
plt.show()
