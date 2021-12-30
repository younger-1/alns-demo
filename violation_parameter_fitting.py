# %%
from math import gamma
import numpy as np
import matplotlib.pyplot as plt
from scipy import optimize

# %matplotlib
plt.style.use("ggplot")

# %%
def para_of_vio_conv(init_punish: float, allow_vio: float):
    """
    init_punish : 单位违反量的初始 punish
    allow_vio : 容许的违反量
    """

    # xdata = allow_vio / 500.0 * np.array([100, 200, 300, 400, 425, 450, 475, 500], dtype=float)
    # ydata = np.array([50, 200, 450, 800, 1200, 2000, 6000, 15000], dtype=float)
    xdata = allow_vio / 500.0 * np.array([100, 200, 300, 400, 450, 500], dtype=float)
    ydata = np.array([50, 200, 450, 800, 2000, 15000], dtype=float)

    def vio_conv(x: float, a: float, b: float, c: float, d: float):
        return a * x + b * np.exp(c * (x - d))

    perr_min = np.inf
    p_best = None
    for _n in range(100):
        p, _e = optimize.curve_fit(
            vio_conv,
            xdata,
            ydata,
            bounds=(
                [0.50 * init_punish, 0.05 * init_punish, 0.020 * init_punish, 0.50 * allow_vio],
                [0.75 * init_punish, 0.07 * init_punish, 0.025 * init_punish, 0.75 * allow_vio],
            ),
            maxfev=6000,
        )
        perr = np.sum(np.power(ydata - vio_conv(xdata, *p), 2))
        if perr < perr_min:
            perr_min = perr
            p_best = p

    print("Parameters: ", p_best)
    x = np.linspace(0, allow_vio * (1.02), 1000)
    plt.plot(x, vio_conv(x, *p_best))
    return p_best


# %%
p_load = para_of_vio_conv(2, 500)

# %%
p_time = para_of_vio_conv(100, 10)

# %%
p_node = para_of_vio_conv(200, 5)
